package yumProxy.server.user;

import yumProxy.net.mysql.MySQLUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

public class EmailVerificationViewer extends JFrame {
    private JTextField emailField;
    private JButton queryButton;
    private JTable resultTable;
    private DefaultTableModel tableModel;

    public EmailVerificationViewer() {
        setTitle("濡ょ姴鐭侀惁澶愭儘娴ｈ娈堕柟璇″枛缁ㄩ亶寮婚妷顭戝殑鐎规悶鍎遍崣?);
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("闂侇収鍠氶?"));
        emailField = new JTextField(25);
        topPanel.add(emailField);
        queryButton = new JButton("闁哄被鍎撮?);
        topPanel.add(queryButton);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"id", "email", "code", "status", "create_time"};
        tableModel = new DefaultTableModel(columns, 0);
        resultTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultTable);
        add(scrollPane, BorderLayout.CENTER);

        queryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                queryEmailVerification();
            }
        });
    }

    private void queryEmailVerification() {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "閻犲洨鏌夌欢顓㈠礂閵夆晛浠忕紒鐘冲敾缁?);
            return;
        }
        String sql = "SELECT * FROM email_verification WHERE email=? ORDER BY create_time DESC LIMIT 10";
        List<Map<String, Object>> res = MySQLUtils.executeQuery(sql, email);
        tableModel.setRowCount(0);
        for (Map<String, Object> row : res) {
            Object[] rowData = new Object[] {
                row.get("id"),
                row.get("email"),
                row.get("code"),
                row.get("status"),
                row.get("create_time")
            };
            tableModel.addRow(rowData);
        }
        if (res.isEmpty()) {
            JOptionPane.showMessageDialog(this, "婵炲备鍓濆﹢渚€寮婚妷銉ョ厒闁烩晝顭堥崣褏鎷嬮弶璺ㄧЭ闁?);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new EmailVerificationViewer().setVisible(true);
        });
    }
} 
