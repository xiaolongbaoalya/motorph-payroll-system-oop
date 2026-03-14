# 📄 MotorPH Payroll System (OOP)

A GUI-based payroll system for MotorPH developed as an **Object-Oriented Programming (OOP)** continuation of the MotorPH Payroll System originally created in **Computer Programming 2**.

This version reflects the team’s **Milestone 2 (MS2)** submission and represents the system’s current implemented state at this stage of development.

> ⚠️ This project may still be subject to change depending on mentor feedback and further revisions.

---

## 👨‍💻 Developed By

The MotorPH Payroll System Team:
- Joemel Bataga
- Maria Alissa Bernales
- Grazielle Felice Canoza
- Shane Rivera

---

## 🎯 Project Objectives

The objectives of this project are to:
- Refactor the existing payroll system into a more structured OOP design
- Apply core OOP principles such as encapsulation, abstraction, inheritance, and polymorphism
- Improve code organization, readability, and maintainability
- Enhance the original payroll system with a more modular and scalable architecture

---

## 🚧 Project Status

This repository contains the team’s **MS2 version** of the MotorPH Payroll System (OOP).

Current progress includes:
- Refactoring of the original CP2 payroll system into OOP-based classes
- Development of the system’s GUI-based structure
- Implementation of core payroll and employee management functionalities for Milestone 2

> Note: Additional revisions may still be made after mentor consultation and feedback.

---

## Quick Start

### GUI
Run the JavaFX application through:
- `com.compprog1282025.MainGUI`

### Terminal
Run the terminal version through:
- `com.compprog1282025.main.Main`

## Access Note for Mentor

All active employees in `data/employees.csv` have a matching login in `data/users.csv`.

Credential format:
- `username`: `lastname.firstletteroffirstname`
- `password`: `password<EmployeeNumber>`

Examples:
- `garcia.m` / `password10001`
- `villanueva.a` / `password10006`
- `aquino.b` / `password10003`
- `hernandez.e` / `password10005`
- `romualdez.f` / `password10015`

Username formatting notes:
- usernames are lowercase
- spaces and punctuation are removed from last names
- only the first letter of the first name is used

Examples for multi-word surnames:
- `San Jose, Brad` -> `sanjose.b`
- `De Leon, Selena` -> `deleon.s`
- `Del Rosario, Tomas` -> `delrosario.t`

### Recommended Accounts by Role

| Role | Employee | Username | Password |
|---|---|---|---|
| Admin | Manuel III Garcia (`10001`) | `garcia.m` | `password10001` |
| HR | Andrea Mae Villanueva (`10006`) | `villanueva.a` | `password10006` |
| Finance | Bianca Sofia Aquino (`10003`) | `aquino.b` | `password10003` |
| IT | Eduard Hernandez (`10005`) | `hernandez.e` | `password10005` |
| Employee / Operations | Fredrick Romualdez (`10015`) | `romualdez.f` | `password10015` |

---
## Main Features

### 1. Login and Role-Based Access
- CSV-backed authentication
- role detection after login
- restricted access per user role
- support for Admin, HR, Finance, IT, and Employee/Operations users

### 2. Employee Management
- add employee
- view employee directory
- update employee
- archive employee
- admin-only delete employee

### 3. Attendance
- time in
- time out
- attendance summary and monthly records
- HR team attendance and attendance history views

### 4. Payroll and Payslips
- gross salary computation
- SSS deduction
- PhilHealth deduction
- Pag-IBIG deduction
- withholding tax computationa
- net pay computation
- employee payslip view
- finance bulk payslip generation
- PDF payslip export

### 5. Requests and Leave
- employee leave/request submission
- request history
- HR/Admin approval and rejection
- standardized request IDs in `REQ-00001` format

### 6. IT Tools
- integrity audit
- system health checks
- data backup
- password reset
- maintenance utilities

## System Architecture

The project follows a layered architecture:

`UI -> Service -> DAO -> CSV Data`

### Layers
- **Model Layer**: employee, user, request, leave, payroll, and interface contracts
- **DAO Layer**: CSV data access and persistence
- **Service Layer**: business logic, validation, RBAC, payroll, and utilities
- **UI Layer**: JavaFX GUI controllers/helpers and terminal menu classes
- **Application Layer**: GUI and terminal entry points

### OOP Principles Used
- encapsulation
- abstraction
- inheritance
- polymorphism
- interfaces
- composition

## Technology Stack

- Java 17
- JavaFX 17
- Maven
- OpenCSV
- jBCrypt
- OpenPDF
- JUnit 5

## Project Structure

```text
src
├── main
│   ├── java
│   │   └── com
│   │       └── compprog1282025
│   │           ├── MainGUI.java
│   │           ├── main
│   │           │   └── Main.java
│   │           ├── dao
│   │           ├── model
│   │           ├── service
│   │           └── ui
│   └── resources
│       └── com
│           └── compprog1282025
│               └── ui
│                   └── gui
└── test
    └── java
```
---

## 📚 Course Context

This project was developed as part of an **Object-Oriented Programming** course and builds upon the original MotorPH Payroll System from **Computer Programming 2**.

Original CP2 repository (for reference):  
[MotorPH Payroll System - CP2](https://github.com/xiaolongbaoalya/motorph-payroll-system)

---

## 🙏 Acknowledgements

This project was developed as part of a course conducted by **Glenn Baluyot**, with the help of **IT Coach Edmund Nietes**.
