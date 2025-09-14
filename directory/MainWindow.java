// Java
package ui;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private JPanel contentPanel;

    public MainWindow(JFrame loginWindow) {
        setTitle("花店管理系统");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Set background image
        JLabel background = new JLabel(new ImageIcon("src/images/02.jpg"));
        background.setLayout(new BorderLayout());
        add(background);

        // Title with artistic font and styling
        JLabel titleLabel = new JLabel("FlowersShop Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("宋体", Font.BOLD, 36));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 20, 0));
        background.add(titleLabel, BorderLayout.NORTH);

        // Menu bar with styled buttons
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);

        // Content panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        JLabel contentLabel = new JLabel("请选择菜单项以开始操作", SwingConstants.CENTER);
        contentLabel.setFont(new Font("宋体", Font.BOLD, 28));
        contentLabel.setForeground(new Color(50, 50, 50));
        contentPanel.add(contentLabel, BorderLayout.CENTER);

        background.add(contentPanel, BorderLayout.CENTER);

        // Dispose the login window after MainWindow is displayed
        loginWindow.dispose();
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setOpaque(false);

        JMenu plantMenu = createStyledMenu("植物管理", Color.BLACK);
        plantMenu.add(createMenuItem("植物类别管理", new FlowerClassPanel()));
        plantMenu.add(createMenuItem("植物信息管理", new FlowerInfoPanel()));
        plantMenu.add(createMenuItem("植物库存管理", new FlowerStockPanel()));

        JMenu inboundMenu = createStyledMenu("入库验收", Color.BLACK);
        inboundMenu.add(createMenuItem("植物入库记录", new InboundRecordPanel()));
        inboundMenu.add(createMenuItem("入库验收处理", new AcceptInboundPanel()));

        JMenu orderMenu = createStyledMenu("销售订单管理", Color.BLACK);
        orderMenu.add(createMenuItem("订单信息查看", new OrderPanel()));

        JMenu customerMenu = createStyledMenu("客户管理", Color.BLACK);
        customerMenu.add(createMenuItem("客户信息", new CustomerPanel()));

        JMenu deliveryMenu = createStyledMenu("配送管理", Color.BLACK);
        deliveryMenu.add(createMenuItem("配送记录", new DeliveryPanel()));

        menuBar.add(plantMenu);
        menuBar.add(inboundMenu);
        menuBar.add(orderMenu);
        menuBar.add(customerMenu);
        menuBar.add(deliveryMenu);

        return menuBar;
    }

    private JMenu createStyledMenu(String text, Color color) {
        JMenu menu = new JMenu(text);
        menu.setFont(new Font("宋体", Font.BOLD, 20));
        menu.setForeground(color);
        menu.setBorder(BorderFactory.createRaisedBevelBorder());
        menu.setBorderPainted(true);
        return menu;
    }

    private JMenuItem createMenuItem(String text, JPanel panel) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setFont(new Font("宋体", Font.BOLD, 18));
        menuItem.setOpaque(true);
        menuItem.setBackground(Color.BLACK);
        menuItem.setForeground(Color.WHITE);
        menuItem.setBorderPainted(true);

        menuItem.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                menuItem.setBackground(Color.DARK_GRAY);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                menuItem.setBackground(Color.BLACK);
            }
        });

        menuItem.addActionListener(e -> {
            // Ensure the panel is switched correctly
            JPanel wrappedPanel = wrapPanelWithHomeButton(panel);
            switchPanel(wrappedPanel);
        });

        return menuItem;
    }

    private JPanel wrapPanelWithHomeButton(JPanel panel) {
        JPanel wrappedPanel = new JPanel(new BorderLayout());
        wrappedPanel.add(panel, BorderLayout.CENTER);

        JButton homeButton = new JButton("返回主页");
        homeButton.setFont(new Font("宋体", Font.BOLD, 20));
        homeButton.addActionListener(e -> {
            contentPanel.removeAll();
            JLabel contentLabel = new JLabel("请选择菜单项以开始操作", SwingConstants.CENTER);
            contentLabel.setFont(new Font("宋体", Font.BOLD, 28));
            contentLabel.setForeground(new Color(50, 50, 50));
            contentPanel.add(contentLabel, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
        });
        wrappedPanel.add(homeButton, BorderLayout.SOUTH);

        return wrappedPanel;
    }

    private void switchPanel(JPanel newPanel) {
        contentPanel.removeAll();
        contentPanel.add(newPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}