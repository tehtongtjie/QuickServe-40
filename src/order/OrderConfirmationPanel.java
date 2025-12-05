package order;

import app.Main;
import java.awt.*;
import javax.swing.*;

public class OrderConfirmationPanel extends JPanel {

    private Main mainFrame;

    public OrderConfirmationPanel(Main mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout()); 
        setBackground(new Color(245, 245, 245));

        JPanel successBox = new JPanel();
        successBox.setLayout(new BoxLayout(successBox, BoxLayout.Y_AXIS));
        successBox.setBackground(Color.WHITE);
        successBox.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        successBox.setPreferredSize(new Dimension(500, 400));

        JLabel iconLabel = new JLabel("✔"); 
        iconLabel.setFont(new Font("Arial", Font.BOLD, 60));
        iconLabel.setForeground(new Color(46, 204, 113)); // Hijau Cerah
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Pesanan Berhasil!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel messageLabel = new JLabel("Pesanan Anda sedang diproses oleh kitchen");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel statusInfo = new JLabel("Anda dapat melihat status pesanan di halaman \"Pesanan Saya\"");
        statusInfo.setFont(new Font("Arial", Font.ITALIC, 14));
        statusInfo.setForeground(new Color(46, 204, 113));
        statusInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusInfo.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        statusInfo.setBackground(new Color(230, 255, 230)); // Latar hijau sangat muda
        statusInfo.setOpaque(true);

        JButton backToMenuBtn = new JButton("Kembali ke Menu");
        backToMenuBtn.setBackground(new Color(34, 47, 62)); 
        backToMenuBtn.setForeground(Color.WHITE);
        backToMenuBtn.setFont(new Font("Arial", Font.BOLD, 16));
        backToMenuBtn.setMaximumSize(new Dimension(300, 45));
        backToMenuBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        backToMenuBtn.addActionListener(e -> mainFrame.showPanel(Main.HOME_VIEW));

        successBox.add(iconLabel);
        successBox.add(Box.createRigidArea(new Dimension(0, 10)));
        successBox.add(titleLabel);
        successBox.add(Box.createRigidArea(new Dimension(0, 10)));
        successBox.add(messageLabel);
        successBox.add(Box.createRigidArea(new Dimension(0, 25)));
        successBox.add(statusInfo);
        successBox.add(Box.createRigidArea(new Dimension(0, 30)));
        successBox.add(backToMenuBtn);

        add(successBox);
    }
}