package com.compprog1282025.model.employee;

import java.time.LocalDate;

public class Request {
    private String requestId;
    private int employeeNumber;
    private String type;        // Leave request type
    private String category;    // e.g., "Sick Leave"
    private LocalDate startDate;
    private LocalDate endDate;
    private String startTime;
    private String endTime;
    private String reason;
    private String status;
    private LocalDate dateFiled;
    private String approvedByFullName;
    private String approvedByEmployeeNo;

    public Request() {} // Essential for DAO

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public int getEmployeeNumber() { return employeeNumber; }
    public void setEmployeeNumber(int employeeNumber) { this.employeeNumber = employeeNumber; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getDateFiled() { return dateFiled; }
    public void setDateFiled(LocalDate dateFiled) { this.dateFiled = dateFiled; }

    public String getApprovedByFullName() { return approvedByFullName; }
    public void setApprovedByFullName(String approvedByFullName) { this.approvedByFullName = approvedByFullName; }

    public String getApprovedByEmployeeNo() { return approvedByEmployeeNo; }
    public void setApprovedByEmployeeNo(String approvedByEmployeeNo) { this.approvedByEmployeeNo = approvedByEmployeeNo; }
}

