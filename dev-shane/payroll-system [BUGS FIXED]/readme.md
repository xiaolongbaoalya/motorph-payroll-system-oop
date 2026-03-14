
# Access Control Audit & Cleanup

This audit tracks the transition from a messy, tangled security setup to a clean, centralized system. The goal was to stop repeating the same rules in 10 different files and create a "Single Source of Truth."

---

## The "Before" State

Access checks were scattered everywhere:

* `AccessControlService` did some work.
* `DashboardController` had its own rules for buttons.
* Other controllers were checking roles directly.
* Rules were duplicated in both the UI and the backend.

---

## Major Changes

### 1. One Central Security Guard

`AccessControlService` is now the only place that decides who can do what.

* New shared methods like `canManageEmployees()` and `canApproveRequests()` were added.
* **Security Guards:** Services now use "Require" methods (like `requireLeaveApproval()`) to stop unauthorized users immediately.

### 2. Shrinking the Dashboard

The `DashboardController` was way too big. It was cut from ~2,000 lines down to under 1,000 lines by giving specific jobs to new helpers.

| Job | Responsibility | New Helper |
| --- | --- | --- |
| **Security** | Who sees what menu | `DashboardAccessHelper` |
| **HR Forms** | Adding/Updating employees | `HrEmployeeFormHelper` |
| **Finance** | Payroll & History | `FinanceBulkPayslipHelper` |
| **IT Tools** | Backups & Health checks | `ITDashboardController` |

---

## Making Data Stable

### 1. Better Typing (Segmented Boxes)

To stop typos in SSS or TIN numbers from crashing the app, the UI was changed. Instead of one long box, there are now small, separate boxes for each section of the ID.

* **Result:** The code joins them perfectly every time, preventing "bad data" errors.

### 2. Request IDs

Request IDs were messy (timestamp-based). They now follow a clean, sequential system: `REQ-00001`, `REQ-00002`, etc.

---

## Interface Cleanup

* **Dead Buttons:** Removed "Cancel" and "Refresh" buttons that didn't actually do anything.
* **Terminal Update:** Added an `IT Menu` to the terminal version so IT/Admin users can run health checks without the GUI.
* **Unified IT:** The IT section is now a smooth part of the main dashboard instead of feeling like a separate app.

---

## Final Check

* **Tests:** `mvn -q test` passed after all these changes.
* **Windows Fix:** Added "retry" logic for saving CSVs to prevent errors when Windows locks a file too quickly.
