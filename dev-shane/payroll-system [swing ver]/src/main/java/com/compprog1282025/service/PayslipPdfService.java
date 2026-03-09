package com.compprog1282025.service;

import com.compprog1282025.model.employee.Employee;
import com.compprog1282025.model.employee.Payslip;
import com.compprog1282025.service.dto.PayslipComputation;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

public class PayslipPdfService {
    private static final DateTimeFormatter PERIOD_FORMAT = DateTimeFormatter.ofPattern("MMMM yyyy");

    public Path generatePayslipPdf(Employee employee, PayslipComputation computation) {
        if (employee == null || computation == null) {
            throw new IllegalArgumentException("Employee and payslip data are required.");
        }

        Payslip payslip = computation.getPayslip();
        String period = payslip.getYearMonth().toString();
        Path outputDir = Paths.get("generated", "payslips", period);
        String filename = buildFilename(employee, period);
        Path outputPath = resolveUniqueOutputPath(outputDir.resolve(filename));

        try {
            Files.createDirectories(outputDir);
            if (Files.exists(outputPath)) {
                Files.delete(outputPath);
            }
            writePdf(outputPath, employee, computation);
            return outputPath;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to generate PDF payslip: " + ex.getMessage(), ex);
        }
    }


    private Path resolveUniqueOutputPath(Path basePath) {
        if (!Files.exists(basePath)) {
            return basePath;
        }

        String fileName = basePath.getFileName().toString();
        int dot = fileName.lastIndexOf('.');
        String stem = dot >= 0 ? fileName.substring(0, dot) : fileName;
        String ext = dot >= 0 ? fileName.substring(dot) : "";

        int counter = 2;
        Path candidate;
        do {
            candidate = basePath.getParent().resolve(stem + "_" + counter + ext);
            counter++;
        } while (Files.exists(candidate));

        return candidate;
    }

    private String buildFilename(Employee employee, String period) {
        String empNo = String.valueOf(employee.getEmployeeNumber());
        String first = sanitizeNamePart(employee.getFirstName());
        String middle = "NA";
        String last = sanitizeNamePart(employee.getLastName());
        return String.format("%s_%s_%s_%s_%s.pdf", empNo, first, middle, last, period);
    }

    private String sanitizeNamePart(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return "NA";
        }
        String value = raw.trim().replaceAll("\\s+", "_");
        value = value.replaceAll("[^A-Za-z0-9_-]", "");
        value = value.replaceAll("_+", "_");
        value = value.replaceAll("^_+|_+$", "");
        return value.isEmpty() ? "NA" : value;
    }
    private void writePdf(Path outputPath, Employee employee, PayslipComputation computation) throws Exception {
        Document document = new Document(PageSize.A4, 40, 40, 32, 32);
        PdfWriter.getInstance(document, Files.newOutputStream(outputPath));
        document.open();

        addHeader(document, computation.getPayslip());
        addSectionTitle(document, "Employee Information");
        addEmployeeInfoSection(document, employee);

        addSectionTitle(document, "Earnings");
        addTwoColumnTable(document, new String[][]{
                {"Basic Salary", money(computation.getBasicSalary())},
                {"Rice Subsidy", money(computation.getRiceSubsidy())},
                {"Phone Allowance", money(computation.getPhoneAllowance())},
                {"Clothing Allowance", money(computation.getClothingAllowance())}
        });

        addSectionTitle(document, "Deductions");
        addTwoColumnTable(document, new String[][]{
                {"SSS", money(computation.getPayslip().getSss())},
                {"PhilHealth", money(computation.getPayslip().getPhilhealth())},
                {"Pag-IBIG", money(computation.getPayslip().getPagibig())},
                {"Withholding Tax", money(computation.getPayslip().getWithholding())}
        });

        addSectionTitle(document, "Payroll Summary");
        addPayrollSummary(document, computation);

        addFooter(document);
        document.close();
    }

    private void addHeader(Document document, Payslip payslip) throws Exception {
        PdfPTable header = new PdfPTable(new float[]{1.1f, 3f});
        header.setWidthPercentage(100);
        header.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);
        Image logo = loadLogo();
        if (logo != null) {
            logo.scaleToFit(88, 88);
            logoCell.addElement(logo);
        }
        header.addCell(logoCell);

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font subFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
        PdfPCell textCell = new PdfPCell();
        textCell.setBorder(Rectangle.NO_BORDER);
        textCell.addElement(new Paragraph("MotorPH Employee Payslip", titleFont));
        textCell.addElement(new Paragraph("Payroll Period: " + payslip.getYearMonth().format(PERIOD_FORMAT), subFont));
        textCell.addElement(new Paragraph("Generated by MotorPH Payroll System", subFont));
        header.addCell(textCell);

        document.add(header);
        document.add(new Paragraph(" "));
    }

    private void addSectionTitle(Document document, String title) throws Exception {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Paragraph p = new Paragraph(title, sectionFont);
        p.setSpacingBefore(6f);
        p.setSpacingAfter(4f);
        document.add(p);
    }

    private void addEmployeeInfoSection(Document document, Employee employee) throws Exception {
        addTwoColumnTable(document, new String[][]{
                {"Employee #", String.valueOf(employee.getEmployeeNumber())},
                {"Employee Name", employee.getFirstName() + " " + employee.getLastName()},
                {"Position", employee.getPosition().getJobTitle()},
                {"Department", employee.getPosition().getDepartment()}
        });
    }

    private void addTwoColumnTable(Document document, String[][] rows) throws Exception {
        PdfPTable table = new PdfPTable(new float[]{2.2f, 1.2f});
        table.setWidthPercentage(100);
        table.setSpacingBefore(4f);
        for (String[] row : rows) {
            addLabelValueRow(table, row[0], row[1]);
        }
        document.add(table);
    }

    private void addPayrollSummary(Document document, PayslipComputation computation) throws Exception {
        PdfPTable table = new PdfPTable(new float[]{2.2f, 1.2f});
        table.setWidthPercentage(100);
        table.setSpacingBefore(4f);

        addLabelValueRow(table, "Total Earnings (Gross)", money(computation.getTotalEarnings()));
        addLabelValueRow(table, "Total Deductions", money(computation.getTotalDeductions()));

        Font emphasis = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        PdfPCell label = new PdfPCell(new Phrase("Final Net Pay", emphasis));
        label.setPadding(8f);
        label.setBackgroundColor(new Color(235, 252, 241));

        PdfPCell value = new PdfPCell(new Phrase(money(computation.getPayslip().getNetSalary()), emphasis));
        value.setPadding(8f);
        value.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
        value.setBackgroundColor(new Color(235, 252, 241));

        table.addCell(label);
        table.addCell(value);

        document.add(table);
    }

    private void addFooter(Document document) throws Exception {
        document.add(new Paragraph(" "));
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9);
        document.add(new Paragraph("This is a system-generated payslip.", footerFont));
    }

    private void addLabelValueRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label));
        labelCell.setPadding(7f);

        PdfPCell valueCell = new PdfPCell(new Phrase(value));
        valueCell.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
        valueCell.setPadding(7f);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private Image loadLogo() {
        try (InputStream in = getClass().getResourceAsStream("/com/compprog1282025/assets/motorph_logo.png")) {
            if (in == null) {
                return null;
            }
            return Image.getInstance(in.readAllBytes());
        } catch (IOException ignored) {
            return null;
        }
    }

    private String money(double amount) {
        return String.format("PHP %,.2f", amount);
    }
}
