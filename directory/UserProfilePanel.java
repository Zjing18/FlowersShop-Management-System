package ui.user;

import javax.swing.*;
import java.awt.*;

public class UserProfilePanel extends JPanel {
    public UserProfilePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 248, 255)); // Light blue background

        // Title
        JLabel title = new JLabel("个人信息管理", SwingConstants.CENTER);
        title.setFont(new Font("宋体", Font.BOLD, 28));
        title.setForeground(new Color(70, 130, 180)); // Steel blue text
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Add padding
        add(title, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding
        formPanel.setBackground(new Color(240, 248, 255)); // Match background color

        JTextField nameField = new JTextField("张三");
        JTextField phoneField = new JTextField("123456789");
        JTextField addressField = new JTextField("北京市");
        JButton saveButton = createStyledButton("保存信息");

        formPanel.add(createStyledLabel("姓名:"));
        formPanel.add(nameField);
        formPanel.add(createStyledLabel("电话:"));
        formPanel.add(phoneField);
        formPanel.add(createStyledLabel("地址:"));
        formPanel.add(addressField);
        formPanel.add(new JLabel()); // Empty space
        formPanel.add(saveButton);

        add(formPanel, BorderLayout.CENTER);

        // Save button action
        saveButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "信息已保存！");
        });
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