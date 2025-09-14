package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import db.DBConnection;

public class FlowerClassDialog extends JDialog {
    private boolean confirmed = false;
    private JTextField nameField, priceField, quantityField;
    private JTextArea descriptionArea;
    private JComboBox<String> stateCombo;
    private int editId = -1;

    public FlowerClassDialog(JFrame parent, String title) {
        this(parent, title, -1);
    }

    public FlowerClassDialog(JFrame parent, String title, int id) {
        super(parent, title, true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        this.editId = id;

        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));

        panel.add(new JLabel("名称:"));
        nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("单价:"));
        priceField = new JTextField();
        panel.add(priceField);

        panel.add(new JLabel("库存数量:"));
        quantityField = new JTextField();
        panel.add(quantityField);

        panel.add(new JLabel("描述:"));
        descriptionArea = new JTextArea(3, 20);
        panel.add(new JScrollPane(descriptionArea));

        panel.add(new JLabel("状态:"));
        stateCombo = new JComboBox<>(new String[]{"上架", "下架"});
        panel.add(stateCombo);

        // 如果是编辑模式，加载数据
        if (editId != -1) {
            loadData();
        }

        JButton confirmBtn = new JButton("确认");
        confirmBtn.addActionListener(this::saveData);

        JButton cancelBtn = new JButton("取消");
        cancelBtn.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(confirmBtn);
        buttonPanel.add(cancelBtn);

        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM FlowerClass_Tbl WHERE id = ?")) {

            ps.setInt(1, editId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                priceField.setText(String.valueOf(rs.getDouble("price")));
                quantityField.setText(String.valueOf(rs.getInt("quantity")));
                descriptionArea.setText(rs.getString("description"));
                stateCombo.setSelectedIndex(rs.getInt("state") == 1 ? 0 : 1);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "数据加载失败: " + e.getMessage());
        }
    }

    private void saveData(ActionEvent e) {
        try {
            String name = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            int quantity = Integer.parseInt(quantityField.getText());
            String description = descriptionArea.getText();
            int state = stateCombo.getSelectedIndex() == 0 ? 1 : 0;

            String sql;
            if (editId == -1) {
                sql = "INSERT INTO FlowerClass_Tbl (name, price, quantity, description, state) VALUES (?, ?, ?, ?, ?)";
            } else {
                sql = "UPDATE FlowerClass_Tbl SET name = ?, price = ?, quantity = ?, description = ?, state = ? WHERE id = ?";
            }

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, name);
                ps.setDouble(2, price);
                ps.setInt(3, quantity);
                ps.setString(4, description);
                ps.setInt(5, state);

                if (editId != -1) {
                    ps.setInt(6, editId);
                }

                if (ps.executeUpdate() > 0) {
                    confirmed = true;
                    dispose();
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "请输入有效的数字");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "保存失败: " + ex.getMessage());
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}