package ui;

import dao.CustomerDAO;
import model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CustomerPanel extends JPanel {
    private JPanel customerListPanel;
    private JTextField searchField;

    public CustomerPanel() {
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("客户信息管理", SwingConstants.CENTER);
        title.setFont(new Font("宋体", Font.BOLD, 24)); // Increased font size
        add(title, BorderLayout.NORTH);

        // Toolbar
        JToolBar toolBar = new JToolBar();
        searchField = new JTextField(20);
        searchField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JButton searchBtn = new JButton("查询");
        JButton addBtn = new JButton("新增");
        JButton editBtn = new JButton("编辑");
        JButton deleteBtn = new JButton("删除");

        Font buttonFont = new Font("宋体", Font.BOLD, 18); // Button font size
        searchBtn.setFont(buttonFont);
        addBtn.setFont(buttonFont);
        editBtn.setFont(buttonFont);
        deleteBtn.setFont(buttonFont);

        toolBar.add(new JLabel("搜索:"));
        toolBar.add(searchField);
        toolBar.add(searchBtn);
        toolBar.addSeparator();
        toolBar.add(addBtn);
        toolBar.add(editBtn);
        toolBar.add(deleteBtn);

        add(toolBar, BorderLayout.NORTH);

        // Customer list panel
        customerListPanel = new JPanel();
        customerListPanel.setLayout(new BoxLayout(customerListPanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(customerListPanel), BorderLayout.CENTER);

        // Button actions
        searchBtn.addActionListener(e -> loadData(searchField.getText().trim()));
        addBtn.addActionListener(e -> showAddDialog());
        editBtn.addActionListener(e -> showEditDialog());
        deleteBtn.addActionListener(e -> deleteCustomer());

        // Load data
        loadData("");
    }

    private void loadData(String keyword) {
        customerListPanel.removeAll();
        List<Customer> list = new CustomerDAO().getAllCustomers();
        for (Customer c : list) {
            if (keyword.isEmpty() || c.getName().contains(keyword) || c.getAccount().contains(keyword) || c.getTel().contains(keyword)) {
                customerListPanel.add(createCustomerPanel(c));
            }
        }
        customerListPanel.revalidate();
        customerListPanel.repaint();
    }

    private JPanel createCustomerPanel(Customer customer) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        panel.setPreferredSize(new Dimension(600, 150));

        // Avatar
        JLabel avatarLabel = new JLabel();
        avatarLabel.setIcon(new ImageIcon("src/images/" + (customer.getId() == 1 ? "09.jpg" : customer.getId() == 2 ? "08.jpg" : "10.jpg")));
        avatarLabel.setPreferredSize(new Dimension(150, 150));
        panel.add(avatarLabel, BorderLayout.WEST);

        // Details
        JPanel detailsPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        detailsPanel.add(new JLabel("ID: " + customer.getId()));
        detailsPanel.add(new JLabel("账号: " + customer.getAccount()));
        detailsPanel.add(new JLabel("姓名: " + customer.getName()));
        detailsPanel.add(new JLabel("性别: " + (customer.getSex() == 1 ? "男" : "女")));
        detailsPanel.add(new JLabel("电话: " + customer.getTel()));
        panel.add(detailsPanel, BorderLayout.CENTER);

        return panel;
    }

    private void showAddDialog() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        JTextField accountField = new JTextField();
        accountField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JTextField passwordField = new JTextField();
        passwordField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JTextField nameField = new JTextField();
        nameField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JComboBox<String> sexCombo = new JComboBox<>(new String[]{"男", "女"});
        sexCombo.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JTextField telField = new JTextField();
        telField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size

        panel.add(new JLabel("账号:"));
        panel.add(accountField);
        panel.add(new JLabel("密码:"));
        panel.add(passwordField);
        panel.add(new JLabel("姓名:"));
        panel.add(nameField);
        panel.add(new JLabel("性别:"));
        panel.add(sexCombo);
        panel.add(new JLabel("电话:"));
        panel.add(telField);

        int result = JOptionPane.showConfirmDialog(this, panel, "新增客户信息", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Customer customer = new Customer();
                customer.setAccount(accountField.getText());
                customer.setPassword(passwordField.getText());
                customer.setName(nameField.getText());
                customer.setSex(sexCombo.getSelectedIndex() == 0 ? 1 : 0);
                customer.setTel(telField.getText());

                new CustomerDAO().addCustomer(customer);
                loadData(searchField.getText().trim());
                JOptionPane.showMessageDialog(this, "新增成功！");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "新增失败: " + e.getMessage());
            }
        }
    }

    private void showEditDialog() {
        String input = JOptionPane.showInputDialog(this, "请输入要编辑的客户ID:");
        if (input == null || input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入有效的客户ID！");
            return;
        }

        try {
            int id = Integer.parseInt(input);
            Customer customer = new CustomerDAO().getCustomerById(id);
            if (customer == null) {
                JOptionPane.showMessageDialog(this, "客户不存在！");
                return;
            }

            JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
            JTextField accountField = new JTextField(customer.getAccount());
            accountField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
            JTextField passwordField = new JTextField(customer.getPassword());
            passwordField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
            JTextField nameField = new JTextField(customer.getName());
            nameField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
            JComboBox<String> sexCombo = new JComboBox<>(new String[]{"男", "女"});
            sexCombo.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
            sexCombo.setSelectedIndex(customer.getSex() == 1 ? 0 : 1);
            JTextField telField = new JTextField(customer.getTel());
            telField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size

            panel.add(new JLabel("账号:"));
            panel.add(accountField);
            panel.add(new JLabel("密码:"));
            panel.add(passwordField);
            panel.add(new JLabel("姓名:"));
            panel.add(nameField);
            panel.add(new JLabel("性别:"));
            panel.add(sexCombo);
            panel.add(new JLabel("电话:"));
            panel.add(telField);

            int result = JOptionPane.showConfirmDialog(this, panel, "编辑客户信息", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                customer.setAccount(accountField.getText());
                customer.setPassword(passwordField.getText());
                customer.setName(nameField.getText());
                customer.setSex(sexCombo.getSelectedIndex() == 0 ? 1 : 0);
                customer.setTel(telField.getText());

                new CustomerDAO().updateCustomer(customer);
                loadData(searchField.getText().trim());
                JOptionPane.showMessageDialog(this, "编辑成功！");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "编辑失败: " + e.getMessage());
        }
    }

    private void deleteCustomer() {
        String input = JOptionPane.showInputDialog(this, "请输入要删除的客户ID:");
        if (input == null || input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入有效的客户ID！");
            return;
        }

        try {
            int id = Integer.parseInt(input);
            new CustomerDAO().deleteCustomer(id);
            loadData(searchField.getText().trim());
            JOptionPane.showMessageDialog(this, "删除成功！");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "删除失败: " + e.getMessage());
        }
    }
}