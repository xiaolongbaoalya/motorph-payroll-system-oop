package com.compprog1282025.ui.swing.modules;

import com.compprog1282025.model.employee.Employee;
import com.compprog1282025.model.user.Session;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

public class PersonalProfilePanel extends JPanel implements RefreshablePanel {
    private final Session session;

    private final JLabel lblId = new JLabel("-");
    private final JLabel lblName = new JLabel("-");
    private final JLabel lblBirthday = new JLabel("-");
    private final JLabel lblAddress = new JLabel("-");
    private final JLabel lblPhone = new JLabel("-");

    private final JLabel lblPosition = new JLabel("-");
    private final JLabel lblDepartment = new JLabel("-");
    private final JLabel lblStatus = new JLabel("-");
    private final JLabel lblSupervisor = new JLabel("-");

    private final JLabel lblSss = new JLabel("-");
    private final JLabel lblPhil = new JLabel("-");
    private final JLabel lblTin = new JLabel("-");
    private final JLabel lblPagIbig = new JLabel("-");

    public PersonalProfilePanel(Session session) {
        this.session = session;
        setLayout(new BorderLayout());
        setBackground(new Color(242, 244, 247));
        setBorder(BorderFactory.createEmptyBorder(16, 18, 18, 18));

        JPanel main = new JPanel();
        main.setOpaque(false);
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

        main.add(sectionCard("Personal Information", new String[]{
                "Employee #", "Name", "Birthday", "Address", "Phone"
        }, new JLabel[]{
                lblId, lblName, lblBirthday, lblAddress, lblPhone
        }));

        main.add(spacer());

        main.add(sectionCard("Work Details", new String[]{
                "Position", "Department", "Status", "Supervisor"
        }, new JLabel[]{
                lblPosition, lblDepartment, lblStatus, lblSupervisor
        }));

        main.add(spacer());

        main.add(sectionCard("Government Details", new String[]{
                "SSS", "PhilHealth", "TIN", "Pag-IBIG"
        }, new JLabel[]{
                lblSss, lblPhil, lblTin, lblPagIbig
        }));

        add(main, BorderLayout.NORTH);
    }

    private JPanel sectionCard(String title, String[] labels, JLabel[] values) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(new Color(248, 249, 251));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(228, 232, 237)),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JLabel heading = new JLabel(title);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 15));
        heading.setForeground(new Color(50, 62, 80));

        JPanel rows = new JPanel(new GridLayout(labels.length, 2, 10, 8));
        rows.setOpaque(false);

        for (int i = 0; i < labels.length; i++) {
            JLabel key = new JLabel(labels[i]);
            key.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            values[i].setFont(new Font("Segoe UI", Font.BOLD, 13));
            values[i].setForeground(new Color(35, 46, 62));

            rows.add(key);
            rows.add(values[i]);
        }

        card.add(heading, BorderLayout.NORTH);
        card.add(rows, BorderLayout.CENTER);
        return card;
    }

    private JPanel spacer() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        return panel;
    }

    @Override
    public void refreshData() {
        Employee e = session.getUser().getEmployee();
        if (e == null) {
            return;
        }
        lblId.setText(String.valueOf(e.getEmployeeNumber()));
        lblName.setText(e.getFirstName() + " " + e.getLastName());
        lblBirthday.setText(String.valueOf(e.getBirthday()));
        lblAddress.setText(e.getContact() != null ? nvl(e.getContact().getAddress()) : "");
        lblPhone.setText(e.getContact() != null ? nvl(e.getContact().getPhone()) : "");

        lblPosition.setText(e.getPosition() != null ? nvl(e.getPosition().getJobTitle()) : "");
        lblDepartment.setText(e.getPosition() != null ? nvl(e.getPosition().getDepartment()) : "");
        lblStatus.setText(nvl(e.getStatus()));
        lblSupervisor.setText(nvl(e.getSupervisorName()));

        lblSss.setText(e.getGovernmentID() != null ? nvl(e.getGovernmentID().getSss()) : "");
        lblPhil.setText(e.getGovernmentID() != null ? nvl(e.getGovernmentID().getPhilHealth()) : "");
        lblTin.setText(e.getGovernmentID() != null ? nvl(e.getGovernmentID().getTin()) : "");
        lblPagIbig.setText(e.getGovernmentID() != null ? nvl(e.getGovernmentID().getPagIbig()) : "");
    }

    private String nvl(String value) {
        return value == null ? "" : value;
    }
}