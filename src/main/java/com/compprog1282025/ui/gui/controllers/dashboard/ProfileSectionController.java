package com.compprog1282025.ui.gui.controllers.dashboard;

import com.compprog1282025.model.employee.Employee;
import com.compprog1282025.model.user.Session;
import javafx.scene.control.Label;

public class ProfileSectionController {
    private final Label lblWelcome;
    private final Label lblFullName;
    private final Label lblEmpId;
    private final Label lblProfileId;
    private final Label lblProfileName;
    private final Label lblProfileBirthday;
    private final Label lblProfilePhone;
    private final Label lblProfileAddress;
    private final Label lblProfilePosition;
    private final Label lblProfileDepartment;
    private final Label lblProfileStatus;
    private final Label lblProfileSupervisor;
    private final Label lblProfileSSS;
    private final Label lblProfilePhilHealth;
    private final Label lblProfileTIN;
    private final Label lblProfilePagIbig;

    public ProfileSectionController(
            Label lblWelcome,
            Label lblFullName,
            Label lblEmpId,
            Label lblProfileId,
            Label lblProfileName,
            Label lblProfileBirthday,
            Label lblProfilePhone,
            Label lblProfileAddress,
            Label lblProfilePosition,
            Label lblProfileDepartment,
            Label lblProfileStatus,
            Label lblProfileSupervisor,
            Label lblProfileSSS,
            Label lblProfilePhilHealth,
            Label lblProfileTIN,
            Label lblProfilePagIbig
    ) {
        this.lblWelcome = lblWelcome;
        this.lblFullName = lblFullName;
        this.lblEmpId = lblEmpId;
        this.lblProfileId = lblProfileId;
        this.lblProfileName = lblProfileName;
        this.lblProfileBirthday = lblProfileBirthday;
        this.lblProfilePhone = lblProfilePhone;
        this.lblProfileAddress = lblProfileAddress;
        this.lblProfilePosition = lblProfilePosition;
        this.lblProfileDepartment = lblProfileDepartment;
        this.lblProfileStatus = lblProfileStatus;
        this.lblProfileSupervisor = lblProfileSupervisor;
        this.lblProfileSSS = lblProfileSSS;
        this.lblProfilePhilHealth = lblProfilePhilHealth;
        this.lblProfileTIN = lblProfileTIN;
        this.lblProfilePagIbig = lblProfilePagIbig;
    }

    public void loadUserData(Session currentSession) {
        if (currentSession == null || currentSession.getUser() == null) {
            return;
        }

        Employee emp = currentSession.getUser().getEmployee();

        if (lblWelcome != null) lblWelcome.setText("Welcome, " + valueOrNA(emp.getFirstName()) + "!");
        if (lblFullName != null) lblFullName.setText(valueOrNA(emp.getFirstName()) + " " + valueOrNA(emp.getLastName()));
        if (lblEmpId != null) lblEmpId.setText(String.valueOf(emp.getEmployeeNumber()));

        if (lblProfileId != null) lblProfileId.setText(String.valueOf(emp.getEmployeeNumber()));
        if (lblProfileName != null) lblProfileName.setText(valueOrNA(emp.getFirstName()) + " " + valueOrNA(emp.getLastName()));
        if (lblProfileBirthday != null) lblProfileBirthday.setText(emp.getBirthday() != null ? emp.getBirthday().toString() : "N/A");

        if (lblProfilePhone != null) lblProfilePhone.setText(emp.getContact() != null ? valueOrNA(emp.getContact().getPhone()) : "N/A");
        if (lblProfileAddress != null) lblProfileAddress.setText(emp.getContact() != null ? valueOrNA(emp.getContact().getAddress()) : "N/A");

        if (lblProfilePosition != null) lblProfilePosition.setText(emp.getPosition() != null ? valueOrNA(emp.getPosition().getJobTitle()) : "N/A");
        if (lblProfileDepartment != null) lblProfileDepartment.setText(emp.getPosition() != null ? valueOrNA(emp.getPosition().getDepartment()) : "N/A");
        if (lblProfileStatus != null) lblProfileStatus.setText(valueOrNA(emp.getStatus()));
        if (lblProfileSupervisor != null) lblProfileSupervisor.setText(valueOrNA(emp.getSupervisorName()));

        if (lblProfileSSS != null) lblProfileSSS.setText(emp.getGovernmentID() != null ? valueOrNA(emp.getGovernmentID().getSss()) : "N/A");
        if (lblProfilePhilHealth != null) lblProfilePhilHealth.setText(emp.getGovernmentID() != null ? valueOrNA(emp.getGovernmentID().getPhilHealth()) : "N/A");
        if (lblProfileTIN != null) lblProfileTIN.setText(emp.getGovernmentID() != null ? valueOrNA(emp.getGovernmentID().getTin()) : "N/A");
        if (lblProfilePagIbig != null) lblProfilePagIbig.setText(emp.getGovernmentID() != null ? valueOrNA(emp.getGovernmentID().getPagIbig()) : "N/A");
    }

    private String valueOrNA(String value) {
        return (value == null || value.isBlank()) ? "N/A" : value;
    }
}
