package menu.dialogs;

import app.Theme;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import menu.MenuItem;

public class MenuDetailDialog extends JDialog {

    public MenuDetailDialog(JFrame owner, MenuItem item, OrderAction orderAction) {
        super(owner, item.getName(), true);
        setSize(430, 540);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        JLabel imgLabel = new JLabel();
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imgLabel.setBorder(new EmptyBorder(15, 15, 15, 15));

        try {
            ImageIcon icon = new ImageIcon(item.getImageUrl());
            if (icon.getIconWidth() > 0) {
                Image scaled = icon.getImage().getScaledInstance(380, 260, Image.SCALE_SMOOTH);
                imgLabel.setIcon(new ImageIcon(scaled));
            } else {
                imgLabel.setText("Gambar tidak tersedia");
            }
        } catch (Exception e) {
            imgLabel.setText("Gambar tidak tersedia");
        }

        add(imgLabel, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameLabel.setForeground(Theme.TEXT_DARK);

        JLabel priceLabel = new JLabel(
                String.format("Rp %,d", (int) item.getPrice()).replace(",", ".")
        );
        priceLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        priceLabel.setBorder(new EmptyBorder(8, 0, 8, 0));
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        priceLabel.setForeground(Theme.TEXT_DARK);

        JTextArea desc = new JTextArea(item.getDescription());
        desc.setEditable(false);
        desc.setOpaque(false);
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setFont(new Font("SansSerif", Font.PLAIN, 14));
        desc.setForeground(Color.DARK_GRAY);
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(nameLabel);
        infoPanel.add(priceLabel);
        infoPanel.add(desc);

        add(infoPanel, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBorder(new EmptyBorder(10, 10, 10, 10));
        footer.setBackground(Color.WHITE);

        JButton orderBtn = new JButton("Pesan Sekarang");
        orderBtn.setBackground(Theme.PRIMARY_COLOR);
        orderBtn.setForeground(Color.WHITE);
        orderBtn.setFocusPainted(false);
        orderBtn.setFont(new Font("SansSerif", Font.BOLD, 14));

        orderBtn.addActionListener(e -> {
            if (orderAction != null) {
                orderAction.onOrderPlaced(item);
            }
            dispose();
        });

        footer.add(orderBtn);
        add(footer, BorderLayout.SOUTH);
    }
}