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
import com.compprog1282025.model.user.HRUser;
import com.compprog1282025.model.user.OperationsUser;
import com.compprog1282025.model.user.Role;
import com.compprog1282025.model.user.Session;
import com.compprog1282025.model.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationAndStressTest {
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
    void dataValidationRulesRejectBadEmployeePayloadsAndAcceptValidOnes() {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        EmployeeService employeeService = new EmployeeService(employeeDAO, new AttendanceDAO(), new RequestDAO(), new UserDAO());
        Session adminSession = sessionFor(new AdminUser("admin", "x", 10001, Role.ADMIN, true), employeeDAO);

        Employee invalidPhone = newEmployee(98100);
        invalidPhone.getContact().setPhone("0917_123_4567");
        assertThrows(IllegalArgumentException.class, () -> employeeService.addEmployee(invalidPhone, adminSession));

        Employee invalidTin = newEmployee(98101);
        invalidTin.getGovernmentID().setTin("123456789012");
        assertThrows(IllegalArgumentException.class, () -> employeeService.addEmployee(invalidTin, adminSession));

        Employee invalidPagIbig = newEmployee(98102);
        invalidPagIbig.getGovernmentID().setPagIbig("ABCDEFGHIJKL");
        assertThrows(IllegalArgumentException.class, () -> employeeService.addEmployee(invalidPagIbig, adminSession));

        Employee unsafeSupervisor = newEmployee(98103);
        unsafeSupervisor.setSupervisorName("DROP TABLE employees");
        assertThrows(IllegalArgumentException.class, () -> employeeService.addEmployee(unsafeSupervisor, adminSession));

        Employee validEmployee = newEmployee(98104);
        validEmployee.getContact().setPhone("02-8123-4567");
        validEmployee.getGovernmentID().setSss("49-1632020-8");
        validEmployee.getGovernmentID().setPhilHealth("123456789012");
        validEmployee.getGovernmentID().setTin("442-605-657-000");
        validEmployee.getGovernmentID().setPagIbig("123456789012");

        assertDoesNotThrow(() -> employeeService.addEmployee(validEmployee, adminSession));
        assertNotNull(employeeDAO.findById(98104));
    }

    @Test
    void stressTestEmployeeCrudLoopMaintainsExpectedCounts() {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        EmployeeService employeeService = new EmployeeService(employeeDAO, new AttendanceDAO(), new RequestDAO(), new UserDAO());
        Session adminSession = sessionFor(new AdminUser("admin", "x", 10001, Role.ADMIN, true), employeeDAO);

        int initialCount = employeeDAO.getAll().size();
        int baseEmployeeNumber = 98200;
        int employeeCount = 20;

        String[] firstNames = {
                "Liam", "Noah", "Mason", "Ethan", "Lucas",
                "Jacob", "Aiden", "Logan", "Caleb", "Wyatt",
                "Nolan", "Owen", "Isaac", "Levi", "Julian",
                "Aaron", "Carter", "Dylan", "Gavin", "Miles"
        };

        for (int i = 0; i < employeeCount; i++) {
            Employee employee = newEmployee(baseEmployeeNumber + i);
            employee.setFirstName(firstNames[i]);
            employee.setLastName("User");
            employee.getPosition().setDepartment(i % 2 == 0 ? "HR" : "Finance");
            assertDoesNotThrow(() -> employeeService.addEmployee(employee, adminSession));
        }

        assertEquals(initialCount + employeeCount, employeeDAO.getAll().size());

        for (int i = 0; i < employeeCount; i++) {
            int employeeNumber = baseEmployeeNumber + i;
            Employee employee = employeeDAO.findById(employeeNumber);
            assertNotNull(employee);
            employee.getContact().setPhone("0917-000-0" + String.format("%03d", i));
            employee.getPosition().setJobTitle("Updated Staff " + i);
            assertDoesNotThrow(() -> employeeService.updateEmployee(employeeNumber, employee, adminSession));
        }

        for (int i = 0; i < employeeCount / 2; i++) {
            int employeeNumber = baseEmployeeNumber + i;
            assertDoesNotThrow(() -> employeeService.archiveEmployee(employeeNumber, adminSession));
        }

        List<Employee> remainingEmployees = employeeDAO.getAll();
        assertEquals(initialCount + (employeeCount / 2), remainingEmployees.size());
        assertTrue(remainingEmployees.stream().noneMatch(emp -> emp.getEmployeeNumber() < baseEmployeeNumber + (employeeCount / 2) && emp.getEmployeeNumber() >= baseEmployeeNumber));
    }

    @Test
    void stressTestRequestSubmissionAndApprovalPersistsAllStatuses() {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        RequestDAO requestDAO = new RequestDAO();
        RequestService requestService = new RequestService(requestDAO);

        Session employeeSession = sessionFor(new OperationsUser("emp", "x", 10005, Role.OPERATIONS, true), employeeDAO);
        Session hrSession = sessionFor(new HRUser("hr", "x", 10002, Role.HR, true), employeeDAO);

        int initialCount = requestDAO.getAll().size();
        int requestCount = 30;

        for (int i = 0; i < requestCount; i++) {
            Request request = new Request();
            request.setEmployeeNumber(10005);
            request.setCategory(i % 2 == 0 ? "Vacation Leave" : "Sick Leave");
            request.setStartDate(LocalDate.now().plusDays(i + 1L));
            request.setEndDate(LocalDate.now().plusDays(i + 1L));
            request.setReason("Load test request " + i);
            assertDoesNotThrow(() -> requestService.submitRequest(request, employeeSession));
        }

        List<Request> submittedRequests = requestDAO.getAll().stream()
                .filter(req -> req.getReason() != null && req.getReason().startsWith("Load test request "))
                .toList();

        assertEquals(requestCount, submittedRequests.size());
        assertEquals(initialCount + requestCount, requestDAO.getAll().size());

        for (int i = 0; i < submittedRequests.size(); i++) {
            Request request = submittedRequests.get(i);
            if (i % 2 == 0) {
                assertDoesNotThrow(() -> requestService.approveRequest(request.getRequestId(), hrSession));
            } else {
                assertDoesNotThrow(() -> requestService.rejectRequest(request.getRequestId(), hrSession));
            }
        }

        RequestDAO reloadedDao = new RequestDAO();
        List<Request> reloadedRequests = reloadedDao.getAll().stream()
                .filter(req -> req.getReason() != null && req.getReason().startsWith("Load test request "))
                .toList();

        assertEquals(requestCount, reloadedRequests.size());
        assertEquals(requestCount / 2, reloadedRequests.stream().filter(req -> "APPROVED".equals(req.getStatus())).count());
        assertEquals(requestCount / 2, reloadedRequests.stream().filter(req -> "REJECTED".equals(req.getStatus())).count());
    }

    private Session sessionFor(User user, EmployeeDAO employeeDAO) {
        user.setEmployee(employeeDAO.findById(user.getEmployeeNumber()));
        return new Session(user);
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
