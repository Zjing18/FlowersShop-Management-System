package ui.user;

import javax.swing.*;
import java.awt.*;

public class UserWindow extends JFrame {
    private JPanel mainPanel; // Main panel to switch content
    private JPanel navPanel; // Navigation panel for main menu

    public UserWindow() {
        setTitle("花店管理系统");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Set background image
        JLabel background = new JLabel(new ImageIcon("src/images/14.jpg"));
        background.setLayout(new BorderLayout());
        setContentPane(background);

        // Title
        JLabel titleLabel = new JLabel("FlowersShop Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("宋体", Font.BOLD, 36));
        titleLabel.setForeground(new Color(60, 90, 120)); // Dark blue text
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Add padding
        background.add(titleLabel, BorderLayout.NORTH);

        // Main content panel
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false); // Make panel transparent
        background.add(mainPanel, BorderLayout.CENTER);

        // Navigation panel
        navPanel = new JPanel(new GridLayout(2, 3, 20, 20)); // Increased spacing
        navPanel.setOpaque(false); // Make panel transparent
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding

        JButton browseButton = createStyledButton("浏览商品");
        JButton createOrderButton = createStyledButton("订单创建");
        JButton manageOrderButton = createStyledButton("订单管理");
        JButton manageInfoButton = createStyledButton("个人信息管理");
        JButton feedbackButton = createStyledButton("留言评价");

        // Add action listeners to switch panels
        browseButton.addActionListener(e -> switchPanel(new FlowerBrowsingPanel()));
        createOrderButton.addActionListener(e -> switchPanel(new OrderCreationPanel()));
        manageOrderButton.addActionListener(e -> switchPanel(new OrderManagementPanel()));
        manageInfoButton.addActionListener(e -> switchPanel(new UserProfilePanel()));
        feedbackButton.addActionListener(e -> switchPanel(new FeedbackPanel()));

        navPanel.add(browseButton);
        navPanel.add(createOrderButton);
        navPanel.add(manageOrderButton);
        navPanel.add(manageInfoButton);
        navPanel.add(feedbackButton);

        // Add navigation panel to main panel
        mainPanel.add(navPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false); // Make panel transparent
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Add padding
        JButton exitButton = createStyledButton("退出");
        exitButton.addActionListener(e -> System.exit(0)); // Exit application
        footerPanel.add(exitButton);
        background.add(footerPanel, BorderLayout.SOUTH);
    }

    private void switchPanel(JPanel panel) {
        panel.setOpaque(false); // Make panel transparent
        panel.add(createReturnButton(), BorderLayout.SOUTH); // Add return button to feature panel
        mainPanel.removeAll();
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private JButton createReturnButton() {
        JButton returnButton = createStyledButton("返回主页");
        returnButton.addActionListener(e -> {
            mainPanel.removeAll();
            mainPanel.add(navPanel, BorderLayout.CENTER); // Switch back to main menu
            mainPanel.revalidate();
            mainPanel.repaint();
        });
        return returnButton;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("宋体", Font.BOLD, 18));
        button.setForeground(Color.WHITE); // White text
        button.setBackground(new Color(70, 130, 180)); // Steel blue background
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder()); // Add 3D effect
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserWindow().setVisible(true));
    }
}