package ui.user;

import dao.OrderDAO;
import model.Order;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class OrderManagementPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;

    public OrderManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 248, 255)); // Light blue background

        // Title
        JLabel title = new JLabel("订单管理", SwingConstants.CENTER);
        title.setFont(new Font("宋体", Font.BOLD, 28));
        title.setForeground(new Color(70, 130, 180)); // Steel blue text
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Add padding
        add(title, BorderLayout.NORTH);

        // Toolbar
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false); // Disable toolbar dragging
        toolBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        toolBar.setBackground(new Color(240, 248, 255)); // Match background color

        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("宋体", Font.PLAIN, 18));
        JButton searchBtn = createStyledButton("查询");
        JButton addBtn = createStyledButton("新增");
        JButton editBtn = createStyledButton("编辑");
        JButton deleteBtn = createStyledButton("删除");

        toolBar.add(new JLabel("搜索:"));
        toolBar.add(searchField);
        toolBar.add(searchBtn);
        toolBar.addSeparator();
        toolBar.add(addBtn);
        toolBar.add(editBtn);
        toolBar.add(deleteBtn);

        add(toolBar, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"订单ID", "客户ID", "订单日期", "配送日期", "金额", "状态", "备注"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("宋体", Font.PLAIN, 18));
        table.getTableHeader().setFont(new Font("宋体", Font.BOLD, 18));
        table.getTableHeader().setBackground(new Color(70, 130, 180)); // Steel blue header
        table.getTableHeader().setForeground(Color.WHITE); // White text for header

        // Alternate row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 248, 255)); // Alternate row colors
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        add(scrollPane, BorderLayout.CENTER);

        // Load data
        loadData("");

        // Button actions
        searchBtn.addActionListener(e -> loadData(searchField.getText().trim()));
        addBtn.addActionListener(e -> showAddDialog());
        editBtn.addActionListener(e -> showEditDialog());
        deleteBtn.addActionListener(e -> deleteOrder());
    }

    private void loadData(String keyword) {
        model.setRowCount(0);
        List<Order> orders = new OrderDAO().getAllOrders(keyword); // Pass keyword to filter orders
        for (Order order : orders) {
            model.addRow(new Object[]{
                    order.getId(),
                    order.getCustomerId(),
                    order.getOrderDate(),
                    order.getDeliveryDate(),
                    "¥" + order.getTotalPrice(),
                    order.getStatus() == 0 ? "待配送" : "已完成",
                    order.getOrderNote()
            });
        }
    }

    private void showAddDialog() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding
        panel.setBackground(new Color(240, 248, 255)); // Match background color

        JTextField customerIdField = new JTextField();
        JTextField deliveryDateField = new JTextField();
        JTextField totalPriceField = new JTextField();
        JTextField noteField = new JTextField();
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"待配送", "已完成"});

        panel.add(createStyledLabel("客户ID:"));
        panel.add(customerIdField);
        panel.add(createStyledLabel("配送日期:"));
        panel.add(deliveryDateField);
        panel.add(createStyledLabel("金额:"));
        panel.add(totalPriceField);
        panel.add(createStyledLabel("备注:"));
        panel.add(noteField);
        panel.add(createStyledLabel("状态:"));
        panel.add(statusCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "新增订单", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Order order = new Order();
                order.setCustomerId(Integer.parseInt(customerIdField.getText()));
                order.setDeliveryDate(Timestamp.valueOf(deliveryDateField.getText()));
                order.setTotalPrice(Double.parseDouble(totalPriceField.getText()));
                order.setOrderNote(noteField.getText());
                order.setStatus(statusCombo.getSelectedIndex());

                new OrderDAO().addOrder(order);
                loadData(""); // Refresh the table
                JOptionPane.showMessageDialog(this, "新增成功！");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "新增失败: " + e.getMessage());
            }
        }
    }

    private void showEditDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要编辑的订单！");
            return;
        }

        int id = (int) model.getValueAt(selectedRow, 0);
        Order order = new OrderDAO().getOrderById(id);
        if (order == null) {
            JOptionPane.showMessageDialog(this, "订单不存在！");
            return;
        }

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding
        panel.setBackground(new Color(240, 248, 255)); // Match background color

        JTextField customerIdField = new JTextField(String.valueOf(order.getCustomerId()));
        JTextField deliveryDateField = new JTextField(order.getDeliveryDate().toString());
        JTextField totalPriceField = new JTextField(String.valueOf(order.getTotalPrice()));
        JTextField noteField = new JTextField(order.getOrderNote());
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"待配送", "已完成"});
        statusCombo.setSelectedIndex(order.getStatus());

        panel.add(createStyledLabel("客户ID:"));
        panel.add(customerIdField);
        panel.add(createStyledLabel("配送日期:"));
        panel.add(deliveryDateField);
        panel.add(createStyledLabel("金额:"));
        panel.add(totalPriceField);
        panel.add(createStyledLabel("备注:"));
        panel.add(noteField);
        panel.add(createStyledLabel("状态:"));
        panel.add(statusCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "编辑订单", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                order.setCustomerId(Integer.parseInt(customerIdField.getText()));
                order.setDeliveryDate(Timestamp.valueOf(deliveryDateField.getText()));
                order.setTotalPrice(Double.parseDouble(totalPriceField.getText()));
                order.setOrderNote(noteField.getText());
                order.setStatus(statusCombo.getSelectedIndex());

                new OrderDAO().updateOrder(order);
                loadData("");
                JOptionPane.showMessageDialog(this, "编辑成功！");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "编辑失败: " + e.getMessage());
            }
        }
    }

    private void deleteOrder() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要删除的订单！");
            return;
        }

        int id = (int) model.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除选中的订单吗?", "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                new OrderDAO().deleteOrder(id);
                loadData("");
                JOptionPane.showMessageDialog(this, "删除成功！");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "删除失败: " + e.getMessage());
            }
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("宋体", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 130, 180)); // Steel blue background
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        return button;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.RIGHT);
        label.setFont(new Font("宋体", Font.BOLD, 18));
        label.setForeground(new Color(70, 130, 180)); // Steel blue text
        return label;
    }
}