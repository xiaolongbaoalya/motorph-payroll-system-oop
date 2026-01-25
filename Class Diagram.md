# Class Categories

| Category        | Classes                          |
|-----------------|----------------------------------|
| **Core**        | Person, Employee, Admin          |
| **Support**     | ContactInfo, GovernmentID, Position, Compensation        |
| **Functional**  | Attendance, Payroll  |
| **Utility**     | LoginManager         |

---

# Class Details

## Person *(Base Class)*
- **Attributes**:
  - `firstName: String`
  - `lastName: String`
- **Methods**:
  - `getFullName(): String`

## Employee *(extends Person)*
- **Attributes**:
  - `employeeNumber: int`
  - `birthday: LocalDate`
  - `contact: ContactInfo`
  - `governmentID: GovernmentID`
  - `status: String`
  - `position: Position`
  - `compensation: Compensation`
  - `password: String`
- **Methods**:
  - `getEmployeeNumber(): int`
  - `getSupervisor(): Employee`
  - `getHourlyRate(): double`
  - `getInfoSummary(): String`

## Admin *(extends Employee)*
- **Attributes**:
  - `username: String`
- **Methods**:
  - `authenticate(inputUser: String, inputPass: String): boolean`
  - `getAdminInfo(): String`

## ContactInfo
- **Attributes**:
  - `address: String`
  - `phoneNumber: String`
- **Notes**: Stores personal contact details

## GovernmentID
- **Attributes**:
  - `sss: String`
  - `philhealth: String`
  - `tin: String`
  - `pagibig: String`
- **Notes**: Government IDs for payroll deductions

## Position
- **Attributes**:
  - `position: String`
  - `supervisor: Employee`
- **Notes**: Job title and supervisor

## Compensation
- **Attributes**:
  - `basicSalary: double`
  - `riceSubsidy: double`
  - `phoneAllowance: double`
  - `clothingAllowance: double`
  - `hourlyRate: double`
- **Notes**: Pay structure and allowances

## Attendance
- **Attributes**:
  - `employee: Employee`
  - `date: LocalDate`
  - `timeIn: LocalTime`
  - `timeOut: LocalTime`
- **Methods**:
  - `getWorkHours(): double`

## Payroll
- **Attributes**:
  - `employee: Employee`
  - `sss: double`
  - `philhealth: double`
  - `pagibig: double`
  - `tax: double`
  - `netSalary: double`
  - `grossSalary: double`
- **Methods**:
  - `computeSemiGross(): double`
  - `computeDeductions(): double`
  - `generatePayslip(): void`
- **Notes**: Payroll computations

## LoginManager
- **Attributes**:
  - `empNumberInput: String`
  - `passwordInput: String`
  - `adminUsernameInput: String`
  - `adminPasswordInput: String`
- **Methods**:
  - `authenticateEmployee(empNumber: int, password: String): boolean`
  - `authenticateAdmin(username: String, password: String): boolean`
- **Notes**: Handles authentication for both admin and employee
