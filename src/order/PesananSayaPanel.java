package order;

import app.Main;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class PesananSayaPanel extends JPanel {

    private final Main mainFrame;
    private final JPanel contentPanel;
    private final List<Timer> activeTimers = new ArrayList<>();
    private final Map<String, JLabel> countdownMap = new HashMap<>();
    private final Map<String, JProgressBar> progressMap = new HashMap<>();
    private final Map<String, JLabel> statusMap = new HashMap<>();

    private final OrdersManager ordersManager = OrdersManager.getInstance();
    private final PropertyChangeListener ordersListener = this::onOrdersChanged;
    
    private String loggedInUserName = "User";
    private JLabel welcomeLabel;
    
    static class Theme {
        static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
        static final Color BORDER_COLOR = new Color(220, 220, 220);
        static final Color TEXT_DARK = new Color(50, 50, 50);
    }
    
    static class RoundedBorder implements Border {
        private final Color color;
        private final int radius;
        public RoundedBorder(Color color, int radius) { this.color = color; this.radius = radius; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) { /* ... */ }
        @Override public Insets getBorderInsets(Component c) { return new Insets(1, 1, 1, 1); }
        @Override public boolean isBorderOpaque() { return true; }
    }
    
    public PesananSayaPanel(Main mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND_COLOR);
        
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(Theme.BACKGROUND_COLOR);
        wrapperPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Theme.BACKGROUND_COLOR);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        wrapperPanel.add(scrollPane, BorderLayout.CENTER);
        add(wrapperPanel, BorderLayout.CENTER);
        
        loadAndDisplayOrderHistory();
        ordersManager.addPropertyChangeListener(ordersListener);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(20, 0)); 
        headerPanel.setBackground(Color.WHITE); 
    
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_COLOR),
            new EmptyBorder(15, 30, 15, 30)
        ));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);

        JLabel backIconLabel = new JLabel("❮ Kembali");
        backIconLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        backIconLabel.setForeground(new Color(34, 134, 34)); 
        backIconLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        backIconLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (mainFrame != null) {
                    mainFrame.showPanel(Main.HOME_VIEW); 
                }
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                 backIconLabel.setForeground(new Color(50, 150, 50)); 
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                 backIconLabel.setForeground(new Color(34, 134, 34)); 
            }
        });
        
        leftPanel.add(backIconLabel);
        headerPanel.add(leftPanel, BorderLayout.WEST); 
        
        JPanel emptyCenterPanel = new JPanel();
        emptyCenterPanel.setOpaque(false);
        headerPanel.add(emptyCenterPanel, BorderLayout.CENTER);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setOpaque(false);

        JLabel appTitle = new JLabel("Rumah Makan Sasak");
        appTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        appTitle.setForeground(Theme.TEXT_DARK);

        userInfoPanel.add(appTitle);

        rightPanel.add(userInfoPanel);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        return headerPanel;
    }
    
    public void setLoggedInUserName(String name) {
        this.loggedInUserName = name;
        if (welcomeLabel != null) {
            welcomeLabel.setText("Hallo selamat datang, " + loggedInUserName);
        }
    }

    private void loadAndDisplayOrderHistory() {
        for (Timer t : activeTimers) t.stop();
        activeTimers.clear();
        countdownMap.clear();
        progressMap.clear();
        statusMap.clear();

        contentPanel.removeAll();

        List<Order> orders = ordersManager.getOrders();
        for (Order o : orders) {
            contentPanel.add(createOrderCard(o));
            contentPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createOrderCard(Order order) {
        String orderId = order.getId();
        String orderTime = java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm").format(
                java.time.Instant.ofEpochMilli(order.getCreatedAt()).atZone(java.time.ZoneId.systemDefault()));
        String status = order.getStatus().name();
        int estimatedSeconds = order.getEstimatedSeconds();
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 380));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(14, 14, 14, 14)
        ));

        // Header row
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setBackground(Color.WHITE);

        JLabel orderLabel = new JLabel("Pesanan #" + orderId);
        orderLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel statusLabel = new JLabel(status);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setOpaque(true);
        statusLabel.setBorder(new EmptyBorder(6, 10, 6, 10));
        if (status.equalsIgnoreCase("Selesai") || estimatedSeconds <= 0) {
            statusLabel.setBackground(new Color(200, 230, 200));
            statusLabel.setForeground(new Color(34, 134, 34));
        } else {
            statusLabel.setBackground(new Color(255, 244, 229));
            statusLabel.setForeground(new Color(255, 140, 0));
        }

        headerRow.add(orderLabel, BorderLayout.WEST);
        headerRow.add(statusLabel, BorderLayout.EAST);
        card.add(headerRow);

        JLabel dateLabel = new JLabel(orderTime);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(120, 120, 120));
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(dateLabel);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel timelinePanel = new JPanel();
        timelinePanel.setLayout(new BoxLayout(timelinePanel, BoxLayout.Y_AXIS));
        timelinePanel.setBackground(Color.WHITE);

        JLabel timelineLabel = new JLabel("Waktu persiapan");
        timelineLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        timelinePanel.add(timelineLabel);

        JProgressBar progressBar = new JProgressBar();
        if (estimatedSeconds > 0) {
            progressBar.setMinimum(0);
            progressBar.setMaximum(estimatedSeconds);
            progressBar.setValue(order.getRemainingSeconds());
        } else {
            progressBar.setMinimum(0);
            progressBar.setMaximum(1);
            progressBar.setValue(0);
        }
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
        timelinePanel.add(Box.createRigidArea(new Dimension(0, 8)));
        timelinePanel.add(progressBar);

        JLabel countdownLabel = new JLabel();
        countdownLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countdownLabel.setForeground(new Color(100, 100, 100));
        timelinePanel.add(Box.createRigidArea(new Dimension(0, 8)));
        timelinePanel.add(countdownLabel);

        card.add(timelinePanel);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            JPanel itemsPanel = new JPanel();
            itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
            itemsPanel.setBackground(Color.WHITE);
            for (Order.OrderItem oi : order.getItems()) {
                JPanel row = new JPanel(new BorderLayout());
                row.setOpaque(false);
                JLabel left = new JLabel(oi.getQuantity() + " x " + oi.getName());
                left.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                JLabel right = new JLabel(String.format("Rp %,d", (long)oi.getTotalPrice()).replace(",", "."));
                right.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                row.add(left, BorderLayout.WEST);
                row.add(right, BorderLayout.EAST);
                itemsPanel.add(row);
            }
            itemsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            JLabel totalLabel = new JLabel(String.format("Total: Rp %,d", (long)order.getTotalAmount()).replace(",", "."));
            totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            totalLabel.setForeground(new Color(34, 134, 34));
            totalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            itemsPanel.add(totalLabel);
            card.add(itemsPanel);
            card.add(Box.createRigidArea(new Dimension(0, 12)));
        }

        countdownMap.put(orderId, countdownLabel);
        progressMap.put(orderId, progressBar);
        statusMap.put(orderId, statusLabel);

        Timer t = new Timer(1000, e -> {
            int rem = order.getRemainingSeconds();
            int est = order.getEstimatedSeconds();
            Order.Status st = order.getStatus();

            if (est > 0) {
                progressBar.setMaximum(est);
                progressBar.setValue(Math.max(0, rem));
            }

            if (rem > 0) {
                updateCountdownLabel(countdownLabel, rem);
            } else {
                if (st == Order.Status.FINISHED) {
                    countdownLabel.setText("✅ Pesanan selesai diproses");
                    progressBar.setValue(progressBar.getMaximum());
                    statusLabel.setText("Selesai");
                    statusLabel.setBackground(new Color(200, 230, 200));
                    statusLabel.setForeground(new Color(34, 134, 34));
                    ((Timer) e.getSource()).stop();
                } else if (st == Order.Status.READY) {
                    countdownLabel.setText("✅ Pesanan siap diambil di kasir");
                    progressBar.setValue(progressBar.getMaximum());
                    statusLabel.setText("Siap Diambil");
                    statusLabel.setBackground(new Color(173, 216, 230));
                    statusLabel.setForeground(new Color(0, 102, 204));
                } else if (st == Order.Status.PROCESSING) {
                    countdownLabel.setText("⏳ Sedang dimasak...");
                } else {
                    countdownLabel.setText("⏳ Menunggu diproses...");
                }
            }
        });
        t.start();
        activeTimers.add(t);

        return card;
    }

    private void updateCountdownLabel(JLabel label, int remainingSeconds) {
        long hrs = remainingSeconds / 3600;
        long mins = (remainingSeconds % 3600) / 60;
        long secs = remainingSeconds % 60;
        if (hrs > 0) {
            label.setText(String.format("⏱ Estimasi %d jam %02d:%02d", hrs, mins, secs));
        } else {
            label.setText(String.format("⏱ Estimasi %02d:%02d menit", mins, secs));
        }
    }

    @Override
    public void removeNotify() {
        for (Timer t : activeTimers) t.stop();
        activeTimers.clear();
        super.removeNotify();
        ordersManager.removePropertyChangeListener(ordersListener);
    }

    private void onOrdersChanged(PropertyChangeEvent ev) {
        String name = ev.getPropertyName();
        if ("orderAdded".equals(name)) {
            Order o = (Order) ev.getNewValue();
            SwingUtilities.invokeLater(() -> {
                contentPanel.add(createOrderCard(o), 0);
                contentPanel.add(Box.createRigidArea(new Dimension(0, 16)), 1);
                contentPanel.revalidate();
                contentPanel.repaint();
            });
        } else if ("orderUpdated".equals(name)) {
            Order o = (Order) ev.getNewValue();
            SwingUtilities.invokeLater(() -> {
                JLabel countdown = countdownMap.get(o.getId());
                JProgressBar bar = progressMap.get(o.getId());
                JLabel statusLabel = statusMap.get(o.getId());
                if (countdown != null) {
                    int rem = o.getRemainingSeconds();
                    if (rem > 0) updateCountdownLabel(countdown, rem);
                }
                if (bar != null) {
                    int est = o.getEstimatedSeconds();
                    int rem = o.getRemainingSeconds();
                    if (est > 0) {
                        bar.setMaximum(est);
                        bar.setValue(Math.max(0, rem));
                    }
                }
                if (statusLabel != null) {
                    statusLabel.setText(o.getStatus().name());
                    if (o.getStatus() == Order.Status.FINISHED || o.getStatus() == Order.Status.READY) {
                        statusLabel.setBackground(new Color(200, 230, 200));
                        statusLabel.setForeground(new Color(34, 134, 34));
                    }
                }
            });
        }
    }
}