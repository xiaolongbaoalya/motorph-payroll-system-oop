package com.compprog1282025.service;

import com.compprog1282025.dao.AttendanceDAO;
import com.compprog1282025.dao.EmployeeDAO;
import com.compprog1282025.dao.RequestDAO;
import com.compprog1282025.dao.UserDAO;
import com.compprog1282025.model.employee.Compensation;
import com.compprog1282025.model.employee.ContactInfo;
import com.compprog1282025.model.employee.Employee;
import com.compprog1282025.model.employee.GovernmentID;
import com.compprog1282025.model.employee.Position;
import com.compprog1282025.model.employee.Request;
import com.compprog1282025.model.user.AdminUser;
import com.compprog1282025.model.user.FinanceUser;
import com.compprog1282025.model.user.HRUser;
import com.compprog1282025.model.user.ITUser;
import com.compprog1282025.model.user.OperationsUser;
import com.compprog1282025.model.user.Role;
import com.compprog1282025.model.user.Session;
import com.compprog1282025.model.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RbacAndRequestPersistenceTest {
    private static final int FILE_RESTORE_RETRY_ATTEMPTS = 5;
    private static final long FILE_RESTORE_RETRY_DELAY_MS = 100L;

    private static final Path DATA_DIR = Path.of("data");
    private static final Path EMPLOYEES_CSV = DATA_DIR.resolve("employees.csv");
    private static final Path ATTENDANCE_CSV = DATA_DIR.resolve("attendance.csv");
    private static final Path REQUESTS_CSV = DATA_DIR.resolve("requests.csv");
    private static final Path USERS_CSV = DATA_DIR.resolve("users.csv");

    private byte[] employeesSnapshot;
    private byte[] attendanceSnapshot;
    private byte[] requestsSnapshot;
    private byte[] usersSnapshot;

    @BeforeEach
    void snapshotData() throws IOException {
        employeesSnapshot = Files.readAllBytes(EMPLOYEES_CSV);
        attendanceSnapshot = Files.readAllBytes(ATTENDANCE_CSV);
        requestsSnapshot = Files.readAllBytes(REQUESTS_CSV);
        usersSnapshot = Files.readAllBytes(USERS_CSV);
    }

    @AfterEach
    void restoreData() throws IOException {
        writeSnapshotWithRetry(EMPLOYEES_CSV, employeesSnapshot);
        writeSnapshotWithRetry(ATTENDANCE_CSV, attendanceSnapshot);
        writeSnapshotWithRetry(REQUESTS_CSV, requestsSnapshot);
        writeSnapshotWithRetry(USERS_CSV, usersSnapshot);
    }

    @Test
    void employeeCannotAccessHrOrFinanceActions() {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        RequestService requestService = new RequestService(new RequestDAO());
        FinanceService financeService = new FinanceService(new AttendanceService(new AttendanceDAO()));

        Session employeeSession = sessionFor(new OperationsUser("emp", "x", 10005, Role.OPERATIONS, true), employeeDAO);
        Employee anotherEmployee = employeeDAO.findById(10001);

        assertThrows(InvalidAccessException.class,
                () -> requestService.getPendingLeaveRequests(employeeSession));
        assertThrows(InvalidAccessException.class,
                () -> financeService.computePayslip(employeeSession, anotherEmployee, 2026, 3));
    }

    @Test
    void hrCannotRunPayrollOrPayslipForOthers() {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        FinanceService financeService = new FinanceService(new AttendanceService(new AttendanceDAO()));

        Session hrSession = sessionFor(new HRUser("hr", "x", 10002, Role.HR, true), employeeDAO);
        Employee anotherEmployee = employeeDAO.findById(10001);

        assertThrows(InvalidAccessException.class,
                () -> financeService.calculateMonthSalary(hrSession, anotherEmployee, 2026, 3));
        assertThrows(InvalidAccessException.class,
                () -> financeService.computePayslip(hrSession, anotherEmployee, 2026, 3));
    }

    @Test
    void financeCannotDeleteEmployees() {
        EmployeeService employeeService = new EmployeeService(new EmployeeDAO(), new AttendanceDAO(), new RequestDAO(), new UserDAO());
        EmployeeDAO employeeDAO = new EmployeeDAO();
        Session financeSession = sessionFor(new FinanceUser("fin", "x", 10004, Role.FINANCE, true), employeeDAO);

        assertThrows(InvalidAccessException.class,
                () -> employeeService.removeEmployee(10001, financeSession));
    }

    @Test
    void adminCanAccessAllCriticalOperationsIncludingDelete() {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        EmployeeService employeeService = new EmployeeService(employeeDAO, new AttendanceDAO(), new RequestDAO(), new UserDAO());
        RequestService requestService = new RequestService(new RequestDAO());
        FinanceService financeService = new FinanceService(new AttendanceService(new AttendanceDAO()));
        Session adminSession = sessionFor(new AdminUser("admin", "x", 10001, Role.ADMIN, true), employeeDAO);

        assertDoesNotThrow(() -> employeeService.getAllEmployees(adminSession));
        assertDoesNotThrow(() -> requestService.getPendingLeaveRequests(adminSession));
        assertDoesNotThrow(() -> financeService.computePayslip(adminSession, employeeDAO.findById(10002), 2026, 3));

        Employee temp = newEmployee(99991);
        assertDoesNotThrow(() -> employeeService.addEmployee(temp, adminSession));
        assertNotNull(employeeDAO.findById(99991));

        assertDoesNotThrow(() -> employeeService.removeEmployee(99991, adminSession));
        assertEquals(null, employeeDAO.findById(99991));
    }

    @Test
    void unknownRoleFallsBackToEmployeePermissions() {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        EmployeeService employeeService = new EmployeeService(employeeDAO, new AttendanceDAO(), new RequestDAO(), new UserDAO());
        RequestService requestService = new RequestService(new RequestDAO());

        Session unknownRoleSession = sessionFor(new ITUser("it", "x", 10003, Role.IT, true), employeeDAO);

        assertThrows(InvalidAccessException.class,
                () -> employeeService.getEmployeeDirectory(unknownRoleSession));
        assertThrows(InvalidAccessException.class,
                () -> requestService.getPendingLeaveRequests(unknownRoleSession));
    }

    @Test
    void centralizedAccessHelpersMatchExpectedPanelsAndActions() {
        EmployeeDAO employeeDAO = new EmployeeDAO();

        Session adminSession = sessionFor(new AdminUser("admin", "x", 10001, Role.ADMIN, true), employeeDAO);
        Session hrSession = sessionFor(new HRUser("hr", "x", 10002, Role.HR, true), employeeDAO);
        Session itSession = sessionFor(new ITUser("it", "x", 10003, Role.IT, true), employeeDAO);
        Session financeSession = sessionFor(new FinanceUser("fin", "x", 10004, Role.FINANCE, true), employeeDAO);
        Session employeeSession = sessionFor(new OperationsUser("emp", "x", 10005, Role.OPERATIONS, true), employeeDAO);

        assertTrue(AccessControlService.canAccessHrPanel(adminSession));
        assertTrue(AccessControlService.canAccessHrPanel(hrSession));
        assertTrue(AccessControlService.canAccessFinancePanel(adminSession));
        assertTrue(AccessControlService.canAccessFinancePanel(financeSession));
        assertTrue(AccessControlService.canAccessItPanel(adminSession));
        assertTrue(AccessControlService.canAccessItPanel(itSession));

        assertTrue(AccessControlService.canDeleteEmployees(adminSession));
        assertTrue(AccessControlService.canApproveRequests(hrSession));
        assertTrue(AccessControlService.canProcessPayroll(financeSession));
        assertTrue(AccessControlService.canApplyLeave(employeeSession));

        assertThrows(InvalidAccessException.class,
                () -> AccessControlService.requireEmployeeDelete(hrSession, "Delete employee"));
        assertThrows(InvalidAccessException.class,
                () -> AccessControlService.requireRequestApproval(employeeSession, "Approve request"));
        assertThrows(InvalidAccessException.class,
                () -> AccessControlService.requirePayrollProcessing(hrSession, "Run payroll"));
    }

    @Test
    void employeeCreateRejectsInvalidNameAndUnsafeAddress() {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        EmployeeService employeeService = new EmployeeService(employeeDAO, new AttendanceDAO(), new RequestDAO(), new UserDAO());
        Session adminSession = sessionFor(new AdminUser("admin", "x", 10001, Role.ADMIN, true), employeeDAO);

        Employee invalidName = newEmployee(99981);
        invalidName.setFirstName("John1");
        assertThrows(IllegalArgumentException.class, () -> employeeService.addEmployee(invalidName, adminSession));

        Employee unsafeAddress = newEmployee(99982);
        unsafeAddress.getContact().setAddress("DROP TABLE employees");
        assertThrows(IllegalArgumentException.class, () -> employeeService.addEmployee(unsafeAddress, adminSession));
    }


    @Test
    void employeeCreateRejectsNumericOnlyPosition() {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        EmployeeService employeeService = new EmployeeService(employeeDAO, new AttendanceDAO(), new RequestDAO(), new UserDAO());
        Session adminSession = sessionFor(new AdminUser("admin", "x", 10001, Role.ADMIN, true), employeeDAO);

        Employee numericPosition = newEmployee(99980);
        numericPosition.getPosition().setJobTitle("12345");

        assertThrows(IllegalArgumentException.class, () -> employeeService.addEmployee(numericPosition, adminSession));
    }
    @Test
    void employeeCreateRejectsVeryLongAddress() {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        EmployeeService employeeService = new EmployeeService(employeeDAO, new AttendanceDAO(), new RequestDAO(), new UserDAO());
        Session adminSession = sessionFor(new AdminUser("admin", "x", 10001, Role.ADMIN, true), employeeDAO);

        Employee longAddress = newEmployee(99983);
        longAddress.getContact().setAddress("A".repeat(300));
        assertThrows(IllegalArgumentException.class, () -> employeeService.addEmployee(longAddress, adminSession));
    }

    @Test
    void employeeCreateAcceptsPhoneNumberWithDashes() {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        EmployeeService employeeService = new EmployeeService(employeeDAO, new AttendanceDAO(), new RequestDAO(), new UserDAO());
        Session adminSession = sessionFor(new AdminUser("admin", "x", 10001, Role.ADMIN, true), employeeDAO);

        Employee employee = newEmployee(99970);
        employee.getContact().setPhone("0917-123-4567");

        assertDoesNotThrow(() -> employeeService.addEmployee(employee, adminSession));
    }

    @Test
    void employeeCreateRejectsPhoneNumberWithInvalidCharacters() {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        EmployeeService employeeService = new EmployeeService(employeeDAO, new AttendanceDAO(), new RequestDAO(), new UserDAO());
        Session adminSession = sessionFor(new AdminUser("admin", "x", 10001, Role.ADMIN, true), employeeDAO);

        Employee employee = newEmployee(99971);
        employee.getContact().setPhone("0917_123_4567");

        assertThrows(IllegalArgumentException.class, () -> employeeService.addEmployee(employee, adminSession));
    }

    @Test
    void employeeCreateRejectsGovernmentIdsOutsideStandardFormats() {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        EmployeeService employeeService = new EmployeeService(employeeDAO, new AttendanceDAO(), new RequestDAO(), new UserDAO());
        Session adminSession = sessionFor(new AdminUser("admin", "x", 10001, Role.ADMIN, true), employeeDAO);

        Employee invalidSss = newEmployee(99972);
        invalidSss.getGovernmentID().setSss("123456");
        assertThrows(IllegalArgumentException.class, () -> employeeService.addEmployee(invalidSss, adminSession));

        Employee invalidPhilHealth = newEmployee(99973);
        invalidPhilHealth.getGovernmentID().setPhilHealth("1234-5678-9012");
        assertThrows(IllegalArgumentException.class, () -> employeeService.addEmployee(invalidPhilHealth, adminSession));

        Employee invalidTin = newEmployee(99974);
        invalidTin.getGovernmentID().setTin("123-456-789");
        assertThrows(IllegalArgumentException.class, () -> employeeService.addEmployee(invalidTin, adminSession));

        Employee invalidPagIbig = newEmployee(99975);
        invalidPagIbig.getGovernmentID().setPagIbig("1234-5678-9012");
        assertThrows(IllegalArgumentException.class, () -> employeeService.addEmployee(invalidPagIbig, adminSession));
    }

    @Test
    void employeeCreateRejectsFutureBirthday() {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        EmployeeService employeeService = new EmployeeService(employeeDAO, new AttendanceDAO(), new RequestDAO(), new UserDAO());
        Session adminSession = sessionFor(new AdminUser("admin", "x", 10001, Role.ADMIN, true), employeeDAO);

        Employee futureBirthday = newEmployee(99984);
        futureBirthday.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(IllegalArgumentException.class, () -> employeeService.addEmployee(futureBirthday, adminSession));
    }

    @Test
    void employeeCreateRejectsUnderMinimumAge() {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        EmployeeService employeeService = new EmployeeService(employeeDAO, new AttendanceDAO(), new RequestDAO(), new UserDAO());
        Session adminSession = sessionFor(new AdminUser("admin", "x", 10001, Role.ADMIN, true), employeeDAO);

        Employee underage = newEmployee(99985);
        underage.setBirthday(LocalDate.now().minusYears(15));
        assertThrows(IllegalArgumentException.class, () -> employeeService.addEmployee(underage, adminSession));
    }
    @Test
    void minimumEmploymentAgeDefaultsTo16WhenNotConfigured() {
        String original = System.getProperty("motorph.minEmploymentAge");
        try {
            System.clearProperty("motorph.minEmploymentAge");
            assertEquals(16, ValidationConfig.minEmploymentAge());
        } finally {
            restoreMinAgeProperty(original);
        }
    }

    @Test
    void minimumEmploymentAgeCanBeConfigured() {
        String original = System.getProperty("motorph.minEmploymentAge");
        try {
            System.setProperty("motorph.minEmploymentAge", "18");
            assertEquals(18, ValidationConfig.minEmploymentAge());
        } finally {
            restoreMinAgeProperty(original);
        }
    }

    @Test
    void employeeCreateUsesConfiguredMinimumEmploymentAge() {
        String original = System.getProperty("motorph.minEmploymentAge");
        try {
            System.setProperty("motorph.minEmploymentAge", "18");
            EmployeeDAO employeeDAO = new EmployeeDAO();
            EmployeeService employeeService = new EmployeeService(employeeDAO, new AttendanceDAO(), new RequestDAO(), new UserDAO());
            Session adminSession = sessionFor(new AdminUser("admin", "x", 10001, Role.ADMIN, true), employeeDAO);

            Employee seventeen = newEmployee(99986);
            seventeen.setBirthday(LocalDate.now().minusYears(17));
            assertThrows(IllegalArgumentException.class, () -> employeeService.addEmployee(seventeen, adminSession));
        } finally {
            restoreMinAgeProperty(original);
        }
    }
    @Test
    void leaveRequestRejectsUnsafeInput() {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        RequestService requestService = new RequestService(new RequestDAO());
        Session employeeSession = sessionFor(new OperationsUser("emp", "x", 10005, Role.OPERATIONS, true), employeeDAO);

        Request unsafe = new Request();
        unsafe.setRequestId("REQ-T-UNSAFE");
        unsafe.setEmployeeNumber(10005);
        unsafe.setCategory("Vacation Leave");
        unsafe.setStartDate(LocalDate.now().plusDays(1));
        unsafe.setEndDate(LocalDate.now().plusDays(2));
        unsafe.setReason("' OR 1=1");

        assertThrows(IllegalArgumentException.class, () -> requestService.submitRequest(unsafe, employeeSession));
    }

    @Test
    void leaveRequestRejectsVeryLongReason() {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        RequestService requestService = new RequestService(new RequestDAO());
        Session employeeSession = sessionFor(new OperationsUser("emp", "x", 10005, Role.OPERATIONS, true), employeeDAO);

        Request longReason = new Request();
        longReason.setRequestId("REQ-T-LONG");
        longReason.setEmployeeNumber(10005);
        longReason.setCategory("Vacation Leave");
        longReason.setStartDate(LocalDate.now().plusDays(1));
        longReason.setEndDate(LocalDate.now().plusDays(2));
        longReason.setReason("B".repeat(700));

        assertThrows(IllegalArgumentException.class, () -> requestService.submitRequest(longReason, employeeSession));
    }

    @Test
    void leaveRequestGeneratesCollisionSafeIdWhenBlank() {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        RequestService requestService = new RequestService(new RequestDAO());
        Session employeeSession = sessionFor(new OperationsUser("emp", "x", 10005, Role.OPERATIONS, true), employeeDAO);

        Request generatedIdReq = new Request();
        generatedIdReq.setRequestId("");
        generatedIdReq.setEmployeeNumber(10005);
        generatedIdReq.setCategory("Vacation Leave");
        generatedIdReq.setStartDate(LocalDate.now().plusDays(1));
        generatedIdReq.setEndDate(LocalDate.now().plusDays(2));
        generatedIdReq.setReason("Auto id");

        assertDoesNotThrow(() -> requestService.submitRequest(generatedIdReq, employeeSession));
        assertTrue(generatedIdReq.getRequestId() != null && generatedIdReq.getRequestId().matches("REQ-\\d{5}"));

        RequestDAO reloaded = new RequestDAO();
        assertNotNull(reloaded.findById(generatedIdReq.getRequestId()));
    }

    @Test
    void moneyParserAcceptsCommaSeparatedValues() {
        assertEquals(1000.0, MonetaryInputParser.parseAmount("1,000", "Basic Salary"), 0.0001);
        assertEquals(1234567.89, MonetaryInputParser.parseAmount("1,234,567.89", "Basic Salary"), 0.0001);
    }

    @Test
    void employeeMoneyIsPersistedWithTwoDecimalPlacesInCsv() throws Exception {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        EmployeeService employeeService = new EmployeeService(employeeDAO, new AttendanceDAO(), new RequestDAO(), new UserDAO());
        Session adminSession = sessionFor(new AdminUser("admin", "x", 10001, Role.ADMIN, true), employeeDAO);

        int employeeNumber = 99000;
        while (employeeDAO.findById(employeeNumber) != null) {
            employeeNumber++;
        }

        Employee moneyFormat = newEmployee(employeeNumber);
        moneyFormat.setSalary(new Compensation(1000, 2000.5, 3000.567, 0));

        assertDoesNotThrow(() -> employeeService.addEmployee(moneyFormat, adminSession));

        String[] cols = null;
        try (CSVReader reader = new CSVReader(new FileReader(EMPLOYEES_CSV.toFile()))) {
            String[] line;
            reader.readNext(); // header
            while ((line = reader.readNext()) != null) {
                if (String.valueOf(employeeNumber).equals(line[0])) {
                    cols = line;
                    break;
                }
            }
        }

        assertNotNull(cols);
        assertEquals("1000.00", cols[14]);
        assertEquals("2000.50", cols[15]);
        assertEquals("3000.57", cols[16]);
        assertEquals("0.00", cols[17]);
    }
    @Test
    void requestSubmitApproveRejectPersistAndReload() {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        RequestService requestService = new RequestService(new RequestDAO());
        Session employeeSession = sessionFor(new OperationsUser("emp", "x", 10005, Role.OPERATIONS, true), employeeDAO);
        Session hrSession = sessionFor(new HRUser("hr", "x", 10002, Role.HR, true), employeeDAO);

        String approveId = "REQ-T-" + UUID.randomUUID().toString().substring(0, 8);
        Request approveReq = new Request();
        approveReq.setRequestId(approveId);
        approveReq.setEmployeeNumber(10005);
        approveReq.setCategory("Vacation Leave");
        approveReq.setStartDate(LocalDate.now().plusDays(1));
        approveReq.setEndDate(LocalDate.now().plusDays(2));
        approveReq.setReason("Test approve");

        assertDoesNotThrow(() -> requestService.submitRequest(approveReq, employeeSession));

        RequestDAO reloadedAfterSubmit = new RequestDAO();
        Request persistedSubmit = reloadedAfterSubmit.findById(approveId);
        assertNotNull(persistedSubmit);
        assertEquals("LEAVE", persistedSubmit.getType());
        assertEquals("PENDING", persistedSubmit.getStatus());

        assertDoesNotThrow(() -> requestService.approveRequest(approveId, hrSession));

        RequestDAO reloadedAfterApprove = new RequestDAO();
        Request persistedApprove = reloadedAfterApprove.findById(approveId);
        assertNotNull(persistedApprove);
        assertEquals("APPROVED", persistedApprove.getStatus());
        assertTrue(persistedApprove.getApprovedByFullName() != null && !persistedApprove.getApprovedByFullName().isBlank());
        assertEquals("10002", persistedApprove.getApprovedByEmployeeNo());

        String rejectId = "REQ-T-" + UUID.randomUUID().toString().substring(0, 8);
        Request rejectReq = new Request();
        rejectReq.setRequestId(rejectId);
        rejectReq.setEmployeeNumber(10005);
        rejectReq.setCategory("Sick Leave");
        rejectReq.setStartDate(LocalDate.now().plusDays(3));
        rejectReq.setEndDate(LocalDate.now().plusDays(3));
        rejectReq.setReason("Test reject");

        assertDoesNotThrow(() -> requestService.submitRequest(rejectReq, employeeSession));
        assertDoesNotThrow(() -> requestService.rejectRequest(rejectId, hrSession));

        RequestDAO reloadedAfterReject = new RequestDAO();
        Request persistedReject = reloadedAfterReject.findById(rejectId);
        assertNotNull(persistedReject);
        assertEquals("REJECTED", persistedReject.getStatus());
        assertEquals("10002", persistedReject.getApprovedByEmployeeNo());
    }

    private Session sessionFor(User user, EmployeeDAO employeeDAO) {
        user.setEmployee(employeeDAO.findById(user.getEmployeeNumber()));
        return new Session(user);
    }
    private void restoreMinAgeProperty(String original) {
        if (original == null) {
            System.clearProperty("motorph.minEmploymentAge");
        } else {
            System.setProperty("motorph.minEmploymentAge", original);
        }
    }
    private Employee newEmployee(int employeeNumber) {
        return new Employee(
                employeeNumber,
                "Test",
                "User",
                LocalDate.of(1990, 1, 1),
                new ContactInfo("Test Address", "09123456789"),
                new GovernmentID("12-3456789-0", "123456789012", "123-456-789-012", "123456789012"),
                new Position("Staff", "HR"),
                new Compensation(30000, 1000, 1000, 1000),
                "Regular",
                "None"
        );
    }

    private void writeSnapshotWithRetry(Path path, byte[] data) throws IOException {
        IOException lastException = null;
        for (int attempt = 1; attempt <= FILE_RESTORE_RETRY_ATTEMPTS; attempt++) {
            try {
                Files.write(path, data);
                return;
            } catch (IOException ex) {
                lastException = ex;
                if (attempt == FILE_RESTORE_RETRY_ATTEMPTS) {
                    throw ex;
                }
                try {
                    Thread.sleep(FILE_RESTORE_RETRY_DELAY_MS);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    ex.addSuppressed(interruptedException);
                    throw ex;
                }
            }
        }
        if (lastException != null) {
            throw lastException;
        }
    }
}











