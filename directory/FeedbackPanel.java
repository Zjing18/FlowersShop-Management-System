package ui.user;

import javax.swing.*;
import java.awt.*;

public class FeedbackPanel extends JPanel {
    public FeedbackPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 248, 255)); // Light blue background

        // Title
        JLabel title = new JLabel("留言评价", SwingConstants.CENTER);
        title.setFont(new Font("宋体", Font.BOLD, 28));
        title.setForeground(new Color(70, 130, 180)); // Steel blue text
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Add padding
        add(title, BorderLayout.NORTH);

        // Feedback form
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding
        formPanel.setBackground(new Color(240, 248, 255)); // Match background color

        JTextArea feedbackArea = new JTextArea();
        feedbackArea.setFont(new Font("宋体", Font.PLAIN, 18));
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        feedbackArea.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 2)); // Add border

        JButton submitButton = createStyledButton("提交评价");

        formPanel.add(new JScrollPane(feedbackArea), BorderLayout.CENTER);
        formPanel.add(submitButton, BorderLayout.SOUTH);

        add(formPanel, BorderLayout.CENTER);

        // Submit button action
        submitButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "评价已提交！");
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
}