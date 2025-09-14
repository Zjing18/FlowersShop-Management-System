package ui.user;

import javax.swing.*;
import java.awt.*;

public class OrderCreationPanel extends JPanel {
    public OrderCreationPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 248, 255)); // Light blue background

        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180)); // Steel blue background
        titlePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.WHITE)); // Decorative border
        JLabel title = new JLabel("订单创建", SwingConstants.CENTER);
        title.setFont(new Font("宋体", Font.BOLD, 28));
        title.setForeground(Color.WHITE); // White text
        titlePanel.add(title);
        add(titlePanel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(new Color(240, 248, 255)); // Match background color

        JTextField flowerNameField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField phoneField = new JTextField();
        JButton submitButton = new JButton("提交订单");

        // Style the submit button
        submitButton.setFont(new Font("宋体", Font.BOLD, 18));
        submitButton.setForeground(Color.WHITE);
        submitButton.setBackground(new Color(70, 130, 180)); // Steel blue background
        submitButton.setFocusPainted(false);
        submitButton.setBorder(BorderFactory.createRaisedBevelBorder());

        formPanel.add(createStyledLabel("花卉名称:"));
        formPanel.add(flowerNameField);
        formPanel.add(createStyledLabel("数量:"));
        formPanel.add(quantityField);
        formPanel.add(createStyledLabel("配送地址:"));
        formPanel.add(addressField);
        formPanel.add(createStyledLabel("联系电话:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel()); // Empty space
        formPanel.add(submitButton);

        add(formPanel, BorderLayout.CENTER);

        // Submit button action
        submitButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "订单已提交！");
        });
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.RIGHT);
        label.setFont(new Font("宋体", Font.BOLD, 18));
        label.setForeground(new Color(70, 130, 180)); // Steel blue text
        return label;
    }
}