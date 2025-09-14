package ui.user;

import javax.swing.*;
import java.awt.*;
import java.util.List;

class FlowerBrowsingPanel extends JPanel {
    public FlowerBrowsingPanel() {
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("花卉商品浏览", SwingConstants.CENTER);
        title.setFont(new Font("宋体", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        // Flower list panel
        JPanel flowerListPanel = new JPanel(new GridLayout(0, 3, 10, 10)); // 3 items per row
        JScrollPane scrollPane = new JScrollPane(flowerListPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Specific image names and flower names
        String[] imageNames = {"03.jpg", "04.jpg", "05.jpg", "06.jpg"};
        String[] flowerNames = {"玫瑰", "百合", "康乃馨", "向日葵"};

        // Load specific images
        for (int i = 0; i < imageNames.length; i++) {
            JPanel flowerPanel = new JPanel(new BorderLayout());
            flowerPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            flowerPanel.setPreferredSize(new Dimension(200, 300));

            // Scale image to fit the panel
            ImageIcon originalIcon = new ImageIcon("src/images/" + imageNames[i]);
            Image scaledImage = originalIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
            imageLabel.setPreferredSize(new Dimension(200, 200));
            flowerPanel.add(imageLabel, BorderLayout.NORTH);

            JLabel nameLabel = new JLabel("花卉名称: " + flowerNames[i], SwingConstants.CENTER);
            JLabel priceLabel = new JLabel("价格: ¥" + (i + 1) * 10, SwingConstants.CENTER);
            flowerPanel.add(nameLabel, BorderLayout.CENTER);
            flowerPanel.add(priceLabel, BorderLayout.SOUTH);

            flowerListPanel.add(flowerPanel);
        }
    }
}