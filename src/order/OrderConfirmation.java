package menu.dialogs;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import order.Order;
import order.Order.OrderItem;

public class OrderConfirmation extends JDialog {
    
    private JFrame menuGUIFrame;
    
    public OrderConfirmation(JFrame parent, Order order) {
        super(parent, "Pesanan Berhasil!", true);
        this.menuGUIFrame = parent;
        setSize(900, 650);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 248, 245));
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(new Color(245, 248, 245));
        outerPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel toastWrapper = new JPanel(new GridBagLayout());
        toastWrapper.setOpaque(false);
        toastWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        JLabel toastLabel = new JLabel("\u2714 Pesanan berhasil dibuat!");
        toastLabel.setOpaque(true);
        toastLabel.setBackground(Color.WHITE);
        toastLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(10, 18, 10, 18)
        ));
        toastLabel.setForeground(new Color(30, 30, 30));
        toastLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        toastWrapper.add(toastLabel);
        outerPanel.add(toastWrapper, BorderLayout.NORTH);

        ShadowRoundedPanel contentPanel = new ShadowRoundedPanel(new Color(76, 175, 80), 16);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setPadding(40);
        contentPanel.setPreferredSize(new Dimension(780, 530));

        JPanel iconPanel = new JPanel(new GridBagLayout());
        iconPanel.setOpaque(false);
        iconPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        JPanel circlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int size = Math.min(getWidth(), getHeight());
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;

                g2d.setColor(new Color(200, 245, 210));
                g2d.fillOval(x, y, size, size);
            }
        };

        circlePanel.setPreferredSize(new Dimension(120, 120));
        circlePanel.setOpaque(false);
        circlePanel.setLayout(new GridBagLayout());
        
        JLabel iconLabel = new JLabel("\u2713");
        iconLabel.setFont(new Font("SansSerif", Font.BOLD, 56));
        iconLabel.setForeground(new Color(34, 154, 42));
        circlePanel.add(iconLabel);

        iconPanel.add(circlePanel);
        contentPanel.add(iconPanel);
        
        contentPanel.add(Box.createVerticalStrut(16));

        JLabel titleLabel = new JLabel("Pesanan Berhasil!");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        titleLabel.setForeground(new Color(34, 154, 42));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);

        contentPanel.add(Box.createVerticalStrut(10));

        JLabel descLabel = new JLabel("Pesanan Anda sedang diproses oleh kitchen");
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descLabel.setForeground(new Color(100, 100, 100));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(descLabel);

        contentPanel.add(Box.createVerticalStrut(28));

        JPanel infoBox = new JPanel(new BorderLayout());
        infoBox.setBackground(new Color(230, 245, 230));
        infoBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 230, 200), 1),
            new EmptyBorder(14, 14, 14, 14)
        ));

        JLabel infoLabel = new JLabel("Anda dapat melihat status pesanan di halaman \"Pesanan Saya\"");
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        infoLabel.setForeground(new Color(34, 154, 42));
        infoBox.add(infoLabel, BorderLayout.CENTER);

        infoBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        infoBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(infoBox);

        contentPanel.add(Box.createVerticalStrut(28));

        if (order != null && order.getItems() != null && !order.getItems().isEmpty()) {
            JPanel itemsBox = new JPanel();
            itemsBox.setLayout(new BoxLayout(itemsBox, BoxLayout.Y_AXIS));
            itemsBox.setOpaque(false);
            for (OrderItem oi : order.getItems()) {
                JLabel li = new JLabel(
                    oi.getQuantity() + " x " + oi.getName() + " — Rp " 
                        + String.format("%,d", (long)oi.getTotalPrice()).replace(",", ".")
                );
                li.setFont(new Font("SansSerif", Font.PLAIN, 13));
                li.setAlignmentX(Component.CENTER_ALIGNMENT);
                itemsBox.add(li);
            }
            itemsBox.add(Box.createVerticalStrut(12));
            contentPanel.add(itemsBox);
        }

        double totalAmount = order != null ? order.getTotalAmount() : 0;
        JLabel amountLabel = new JLabel(
            String.format("Total: Rp %,d", (long)totalAmount).replace(",", ".")
        );
        amountLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        amountLabel.setForeground(new Color(255, 100, 0));
        amountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(amountLabel);

        contentPanel.add(Box.createVerticalStrut(32));

        JPanel buttonWrapper = new JPanel(new GridBagLayout());
        buttonWrapper.setOpaque(false);
        buttonWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JButton backButton = new JButton("Kembali ke Menu");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        backButton.setBackground(new Color(33, 150, 243));
        backButton.setOpaque(true);
        backButton.setForeground(Color.BLACK);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(12, 40, 12, 40));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setPreferredSize(new Dimension(280, 50));
        backButton.addActionListener(e -> {
            dispose();
            if (menuGUIFrame != null) {
                menuGUIFrame.setVisible(true);
                menuGUIFrame.toFront();
            }
        });

        buttonWrapper.add(backButton);
        contentPanel.add(buttonWrapper);

        JPanel centerWrap = new JPanel(new GridBagLayout());
        centerWrap.setOpaque(false);
        centerWrap.add(contentPanel);
        outerPanel.add(centerWrap, BorderLayout.CENTER);

        add(outerPanel, BorderLayout.CENTER);

        Timer t = new Timer(2200, e -> toastWrapper.setVisible(false));
        t.setRepeats(false);
        t.start();

        setVisible(true);
    }

    private static class ShadowRoundedPanel extends JPanel {
        private final Color borderColor;
        private final int radius;

        ShadowRoundedPanel(Color borderColor, int radius) {
            this.borderColor = borderColor;
            this.radius = radius;
            setOpaque(false);
        }

        public void setPadding(int p) {
            setBorder(new EmptyBorder(p, p, p, p));
        }

        @Override
        protected void paintComponent(Graphics g) {
            int w = getWidth();
            int h = getHeight();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(0, 0, 0, 18));
            g2.fillRoundRect(6, 6, Math.max(0, w - 12), Math.max(0, h - 12), radius + 6, radius + 6);

            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, Math.max(0, w - 12), Math.max(0, h - 12), radius, radius);

            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(3f));
            g2.drawRoundRect(0, 0, Math.max(0, w - 12), Math.max(0, h - 12), radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}