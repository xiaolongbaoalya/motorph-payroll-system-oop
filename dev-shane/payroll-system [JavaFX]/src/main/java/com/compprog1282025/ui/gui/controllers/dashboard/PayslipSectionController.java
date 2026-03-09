package com.compprog1282025.ui.gui.controllers.dashboard;

import com.compprog1282025.model.employee.Employee;
import com.compprog1282025.model.user.Session;
import com.compprog1282025.service.FinanceService;
import com.compprog1282025.service.dto.PayslipComputation;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class PayslipSectionController {
    private static final String CURRENCY_PREFIX = "PHP ";

    private final FinanceService financeService;
    private final ComboBox<String> cmbPayslipMonth;
    private final ComboBox<Integer> cmbPayslipYear;
    private final Label lblNetPay;
    private final Label lblGrossIncome;
    private final Label lblBasicSalary;
    private final Label lblRiceSubsidy;
    private final Label lblPhoneAllowance;
    private final Label lblClothingAllowance;
    private final Label lblSSS;
    private final Label lblPhilHealth;
    private final Label lblPagIbig;
    private final Label lblTax;
    private final Label lblTotalEarnings;
    private final Label lblTotalContribs;
    private final Label lblTotalTax;
    private final Label lblFinalNetPay;

    public PayslipSectionController(
            FinanceService financeService,
            ComboBox<String> cmbPayslipMonth,
            ComboBox<Integer> cmbPayslipYear,
            Label lblNetPay,
            Label lblGrossIncome,
            Label lblBasicSalary,
            Label lblRiceSubsidy,
            Label lblPhoneAllowance,
            Label lblClothingAllowance,
            Label lblSSS,
            Label lblPhilHealth,
            Label lblPagIbig,
            Label lblTax,
            Label lblTotalEarnings,
            Label lblTotalContribs,
            Label lblTotalTax,
            Label lblFinalNetPay
    ) {
        this.financeService = financeService;
        this.cmbPayslipMonth = cmbPayslipMonth;
        this.cmbPayslipYear = cmbPayslipYear;
        this.lblNetPay = lblNetPay;
        this.lblGrossIncome = lblGrossIncome;
        this.lblBasicSalary = lblBasicSalary;
        this.lblRiceSubsidy = lblRiceSubsidy;
        this.lblPhoneAllowance = lblPhoneAllowance;
        this.lblClothingAllowance = lblClothingAllowance;
        this.lblSSS = lblSSS;
        this.lblPhilHealth = lblPhilHealth;
        this.lblPagIbig = lblPagIbig;
        this.lblTax = lblTax;
        this.lblTotalEarnings = lblTotalEarnings;
        this.lblTotalContribs = lblTotalContribs;
        this.lblTotalTax = lblTotalTax;
        this.lblFinalNetPay = lblFinalNetPay;
    }

    public void refreshPayslipData(Session session) {
        if (session == null || session.getUser() == null) {
            resetPayslipUI();
            return;
        }

        Integer selectedYear = cmbPayslipYear.getValue();
        int selectedMonthIndex = cmbPayslipMonth.getSelectionModel().getSelectedIndex();
        if (selectedYear == null || selectedMonthIndex < 0) {
            resetPayslipUI();
            return;
        }

        Employee employee = session.getUser().getEmployee();
        PayslipComputation computation;
        try {
            computation = financeService.computePayslip(session, employee, selectedYear, selectedMonthIndex + 1);
        } catch (Exception e) {
            resetPayslipUI();
            return;
        }
        if (computation == null) {
            resetPayslipUI();
            return;
        }

        if (lblNetPay != null) lblNetPay.setText(formatCurrency(computation.getPayslip().getNetSalary()));
        if (lblGrossIncome != null) lblGrossIncome.setText(formatCurrency(computation.getTotalEarnings()));

        if (lblBasicSalary != null) lblBasicSalary.setText(formatCurrency(computation.getBasicSalary()));
        if (lblRiceSubsidy != null) lblRiceSubsidy.setText(formatCurrency(computation.getRiceSubsidy()));
        if (lblPhoneAllowance != null) lblPhoneAllowance.setText(formatCurrency(computation.getPhoneAllowance()));
        if (lblClothingAllowance != null) lblClothingAllowance.setText(formatCurrency(computation.getClothingAllowance()));

        if (lblSSS != null) lblSSS.setText(formatDeduction(computation.getPayslip().getSss()));
        if (lblPhilHealth != null) lblPhilHealth.setText(formatDeduction(computation.getPayslip().getPhilhealth()));
        if (lblPagIbig != null) lblPagIbig.setText(formatDeduction(computation.getPayslip().getPagibig()));
        if (lblTax != null) lblTax.setText(formatDeduction(computation.getPayslip().getWithholding()));

        if (lblTotalEarnings != null) lblTotalEarnings.setText(formatCurrency(computation.getTotalEarnings()));
        if (lblTotalContribs != null) lblTotalContribs.setText(formatDeduction(computation.getTotalContributions()));
        if (lblTotalTax != null) lblTotalTax.setText(formatDeduction(computation.getPayslip().getWithholding()));
        if (lblFinalNetPay != null) lblFinalNetPay.setText(formatCurrency(computation.getPayslip().getNetSalary()));
    }

    private void resetPayslipUI() {
        String zeroCurrency = formatCurrency(0);
        Label[] labels = {
                lblNetPay, lblGrossIncome, lblBasicSalary, lblRiceSubsidy,
                lblPhoneAllowance, lblClothingAllowance, lblSSS, lblPhilHealth,
                lblPagIbig, lblTax, lblTotalEarnings, lblTotalContribs,
                lblTotalTax, lblFinalNetPay
        };
        for (Label label : labels) {
            if (label != null) {
                label.setText(zeroCurrency);
            }
        }
    }

    private String formatCurrency(double amount) {
        return String.format(CURRENCY_PREFIX + "%,.2f", amount);
    }

    private String formatDeduction(double amount) {
        return "- " + formatCurrency(amount);
    }
}