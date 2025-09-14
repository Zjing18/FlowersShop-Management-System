package ui;

import ui.user.UserWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginWindow extends JFrame {
    public LoginWindow() {
        setTitle("花店管理系统");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Set background image
        JLabel background = new JLabel(new ImageIcon("src/images/01.jpg"));
        background.setLayout(new BorderLayout());
        add(background);

        // Title
        JLabel titleLabel = new JLabel("花店管理系统", SwingConstants.CENTER);
        titleLabel.setFont(new Font("宋体", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE); // Change font color to white
        titleLabel.setBorder(BorderFactory.createEmptyBorder(100, 0, 0, 0)); // Move title downward
        background.add(titleLabel, BorderLayout.NORTH);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        JButton loginButton = new JButton("登录");
        JButton registerButton = new JButton("注册");
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        background.add(buttonPanel, BorderLayout.SOUTH);

        // Login button action
        loginButton.addActionListener((ActionEvent e) -> showLoginDialog());

        // Register button action (mocked for now)
        registerButton.addActionListener((ActionEvent e) -> JOptionPane.showMessageDialog(this, "注册功能暂未开放！"));
    }

    private void showLoginDialog() {
        JDialog loginDialog = new JDialog(this, "登录", true);
        loginDialog.setSize(400, 300);
        loginDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add spacing between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("用户名:", SwingConstants.CENTER);
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("密码:", SwingConstants.CENTER);
        JPasswordField passField = new JPasswordField();
        JLabel roleLabel = new JLabel("角色:", SwingConstants.CENTER);
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"用户", "管理员"});
        JButton confirmButton = new JButton("确认");

        // Add components to panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(userLabel, gbc);
        gbc.gridx = 1;
        panel.add(userField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passLabel, gbc);
        gbc.gridx = 1;
        panel.add(passField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(roleLabel, gbc);
        gbc.gridx = 1;
        panel.add(roleComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(confirmButton, gbc);

        loginDialog.add(panel);

        confirmButton.addActionListener((ActionEvent e) -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            String role = (String) roleComboBox.getSelectedItem();

            // Perform login validation (mocked for now)
            if (("admin".equals(username) || "user".equals(username)) && "1234".equals(password)) {
                JOptionPane.showMessageDialog(loginDialog, "登录成功！");
                loginDialog.dispose();
                if ("管理员".equals(role)) {
                    new MainWindow(this).setVisible(true); // Navigate to MainWindow for administrators
                } else {
                    new UserWindow().setVisible(true); // Navigate to UserWindow for users
                }
            } else {
                JOptionPane.showMessageDialog(loginDialog, "登录失败，请检查用户名或密码！");
            }
        });

        loginDialog.setVisible(true);
    }
}