package cart;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.PanelUI;
import javax.swing.plaf.basic.BasicButtonUI;

public class KeranjangPanel extends JPanel {

    // --- Data dan Model ---
    
    // Kelas data untuk Item dalam Keranjang
    // Diambil dari MenuItem record di MenuKatalogPanel
    public record CartItem(String name, String priceString, String imageUrl, double pricePerItem) {}

    // Objek yang menyimpan item keranjang saat ini
    private final List<CartEntry> cartItems = new ArrayList<>(); 
    // Format mata uang Rupiah
    // Use Locale.forLanguageTag to avoid deprecated Locale(String,String) constructor
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

    // Komponen UI
    private JPanel cartListPanel; // Container untuk daftar item
    private JLabel subtotalLabel;
    private JLabel taxLabel;
    private JLabel totalLabel;
    private JLabel itemCountLabel;
    private JButton checkoutButton;
    // Exposed close button so container (frame) can attach behaviour
    private JButton closeButton;
    
    // Konstanta Warna dan Gaya (Mengambil dari MenuKatalogPanel)
    private static final Color PRIMARY_ORANGE = new Color(255, 100, 0); 
    private static final Color DARK_ORANGE = new Color(240, 50, 0); 
    private static final Color BACKGROUND_GRAY = new Color(245, 245, 245); 
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 20); 
    private static final int CORNER_RADIUS = 20;

    /**
     * Entry Cart: Menyimpan item dan kuantitasnya.
     */
    public class CartEntry {
        public CartItem item;
        public int quantity;

        public CartEntry(CartItem item, int quantity) {
            this.item = item;
            this.quantity = quantity;
        }
        
        // Menghitung total harga untuk entry ini
        public double getTotalPrice() {
            return item.pricePerItem * quantity;
        }
    }

    /**
     * Konstruktor untuk KeranjangPanel.
     * @param checkoutListener ActionListener yang akan dipanggil saat tombol Checkout ditekan.
     */
    public KeranjangPanel(ActionListener checkoutListener) {
        // Mengatur properti tampilan sidebar (mirip JDialog atau JLayeredPane)
        // Do not enforce a rigid fixed size here — parent frame will decide size.
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        // Header Panel (Keranjang Belanja, Jumlah Item, Tombol Tutup)
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Konten Utama: Daftar Item yang bisa di-scroll
        cartListPanel = new JPanel();
        cartListPanel.setLayout(new BoxLayout(cartListPanel, BoxLayout.Y_AXIS));
        cartListPanel.setOpaque(false);
        cartListPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JScrollPane scrollPane = new JScrollPane(cartListPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); 
        add(scrollPane, BorderLayout.CENTER);

        // Footer Panel: Ringkasan Harga dan Tombol Checkout
        JPanel footerPanel = createFooterPanel(checkoutListener);
        add(footerPanel, BorderLayout.SOUTH);
    
        // Inisialisasi tampilan keranjang
        updateCartDisplay();
    }
    
    // --- UI Helpers ---

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(5, 0));
        headerPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        headerPanel.setBackground(Color.WHITE);

        // Ikon dan Judul
        JLabel titleLabel = new JLabel("\uD83D\uDCCB Keranjang Belanja");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 18));
        
        // Jumlah Item
        itemCountLabel = new JLabel("0 item dalam keranjang");
        itemCountLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        itemCountLabel.setForeground(Color.GRAY.darker());

        JPanel titleContainer = new JPanel(new BorderLayout(0, 5));
        titleContainer.setOpaque(false);
        titleContainer.add(titleLabel, BorderLayout.NORTH);
        titleContainer.add(itemCountLabel, BorderLayout.CENTER);

        headerPanel.add(titleContainer, BorderLayout.WEST);
        
        // Tombol Tutup (X) - disimpan ke field agar container (frame) bisa menghubungkan aksi tutup
        closeButton = new JButton("×");
        closeButton.setFont(new Font("Inter", Font.PLAIN, 24));
        closeButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.setFocusPainted(false);
        closeButton.setContentAreaFilled(false);
        headerPanel.add(closeButton, BorderLayout.EAST);
        
        // Tambahkan separator di bawah header
        headerPanel.add(new JSeparator(SwingConstants.HORIZONTAL), BorderLayout.SOUTH);

        return headerPanel;
    }
    
    private JPanel createFooterPanel(ActionListener checkoutListener) {
        JPanel footerPanel = new JPanel(new BorderLayout(10, 10));
        footerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        footerPanel.setBackground(Color.WHITE);

        // 1. Ringkasan Harga
        JPanel summaryPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        summaryPanel.setOpaque(false);
        
        // Baris Subtotal
        summaryPanel.add(new JLabel("Subtotal"));
        subtotalLabel = new JLabel("Rp 0", SwingConstants.RIGHT);
        subtotalLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        summaryPanel.add(subtotalLabel);
        
        // Baris Pajak (Dummy 10%)
        summaryPanel.add(new JLabel("Pajak (10%)"));
        taxLabel = new JLabel("Rp 0", SwingConstants.RIGHT);
        taxLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        summaryPanel.add(taxLabel);

        // Baris Total
        summaryPanel.add(new JLabel("Total"));
        totalLabel = new JLabel("Rp 0", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Inter", Font.BOLD, 18));
        totalLabel.setForeground(DARK_ORANGE.darker());
        summaryPanel.add(totalLabel);
        
        // 2. Tombol Checkout
        // Use gradient rounded button to match example
        checkoutButton = new JButton("Checkout");
        checkoutButton.setFont(new Font("Inter", Font.BOLD, 16));
        checkoutButton.setBackground(PRIMARY_ORANGE);
        checkoutButton.setForeground(Color.WHITE);
        checkoutButton.setFocusPainted(false);
        checkoutButton.setBorder(new EmptyBorder(15, 20, 15, 20));
        checkoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        checkoutButton.setUI(new RoundedButtonUI(20)); // Sudut melengkung
        checkoutButton.addActionListener(checkoutListener); // Tambahkan listener dari konstruktor

        footerPanel.add(summaryPanel, BorderLayout.NORTH);
        footerPanel.add(Box.createRigidArea(new Dimension(0, 10)), BorderLayout.CENTER); // Spasi
        // Make checkout full-width
        JPanel checkoutWrapper = new JPanel(new BorderLayout());
        checkoutWrapper.setOpaque(false);
        checkoutWrapper.add(checkoutButton, BorderLayout.CENTER);
        // Set preferred width to match panel width minus horizontal padding (20+20)
        checkoutButton.setPreferredSize(new Dimension(340, 50));
        footerPanel.add(checkoutWrapper, BorderLayout.SOUTH);

        return footerPanel;
    }
    
    /**
     * Membuat tampilan baris untuk satu item di keranjang.
     */
    private JPanel createCartItemRow(CartEntry entry) {
        // Card-like panel with subtle border to match design
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                // white background
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, w, h);
                // light bottom border (separator line)
                g2.setColor(new Color(230, 230, 230));
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(0, h - 1, w, h - 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setLayout(new BorderLayout(15, 0));
        card.setOpaque(true);
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(12, 12, 12, 12));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90 + 24));
        
        // 1. Gambar (thumbnail)
        JLabel imageLabel = new JLabel();
        final int thumbW = 90;
        final int thumbH = 90;
        imageLabel.setPreferredSize(new Dimension(thumbW, thumbH));
        imageLabel.setMinimumSize(new Dimension(thumbW, thumbH));
        imageLabel.setMaximumSize(new Dimension(thumbW, thumbH));
        imageLabel.setBackground(BACKGROUND_GRAY);
        imageLabel.setOpaque(true);
        imageLabel.setUI(new RoundedLabelUI(8)); 

        // Load image with fallback attempts
        Image img = null;
        String path = entry.item.imageUrl();
        try {
            URL imageUrl = null;
            if (path != null) imageUrl = getClass().getResource(path);
            if (imageUrl == null && path != null && !path.startsWith("/")) imageUrl = getClass().getResource('/' + path);
            if (imageUrl == null && path != null) imageUrl = getClass().getResource("/Assets/" + path);
            if (imageUrl == null && path != null) imageUrl = getClass().getResource("/assets/" + path);
            if (imageUrl != null) {
                img = new ImageIcon(imageUrl).getImage();
            } else {
                java.io.File f = path == null ? null : new java.io.File(path);
                if ((f == null || !f.exists()) && path != null) {
                    f = new java.io.File("Assets" + System.getProperty("file.separator") + path);
                }
                if (f != null && f.exists()) img = new ImageIcon(f.getAbsolutePath()).getImage();
            }
        } catch (Exception ex) {
            img = null;
        }

        if (img != null) {
            Image scaledImage = img.getScaledInstance(thumbW, thumbH, Image.SCALE_AREA_AVERAGING);
            imageLabel.setIcon(new ImageIcon(scaledImage));
            imageLabel.setText("");
        } else {
            imageLabel.setText("Foto");
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }
        card.add(imageLabel, BorderLayout.WEST);

        // 2. Center panel: nama, harga, dan quantity controls
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        
        // Row 1: Nama item (left aligned)
        JLabel nameLabel = new JLabel(entry.item.name());
        nameLabel.setFont(new Font("Inter", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(nameLabel);
        centerPanel.add(Box.createVerticalStrut(3));
        
        // Row 2: Harga (left aligned)
        JLabel priceLabel = new JLabel(entry.item.priceString());
        priceLabel.setFont(new Font("Inter", Font.BOLD, 14));
        priceLabel.setForeground(PRIMARY_ORANGE);
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(priceLabel);
        centerPanel.add(Box.createVerticalStrut(5));
        
        // Row 3: Quantity controls (- qty +) di bawah harga
        JButton minusButton = new JButton("-");
        minusButton.setFont(new Font("Inter", Font.BOLD, 12));
        minusButton.setForeground(Color.BLACK);
        minusButton.setBackground(Color.WHITE);
        minusButton.setFocusPainted(false);
        minusButton.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        minusButton.setPreferredSize(new Dimension(28, 28));
        minusButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel quantityLabel = new JLabel(String.valueOf(entry.quantity), SwingConstants.CENTER);
        quantityLabel.setFont(new Font("Inter", Font.BOLD, 13));
        quantityLabel.setPreferredSize(new Dimension(30, 28));
        
        JButton plusButton = new JButton("+");
        plusButton.setFont(new Font("Inter", Font.BOLD, 13));
        plusButton.setForeground(Color.BLACK);
        plusButton.setBackground(Color.WHITE);
        plusButton.setFocusPainted(false);
        plusButton.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        plusButton.setPreferredSize(new Dimension(28, 28));
        plusButton.setMargin(new Insets(0, 0, 0, 0));
        plusButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        minusButton.addActionListener(e -> updateQuantity(entry, entry.quantity - 1));
        plusButton.addActionListener(e -> updateQuantity(entry, entry.quantity + 1));
        
        JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        qtyPanel.setOpaque(false);
        qtyPanel.add(minusButton);
        qtyPanel.add(quantityLabel);
        qtyPanel.add(plusButton);
        qtyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(qtyPanel);
        
        card.add(centerPanel, BorderLayout.CENTER);

        // 3. Right panel: Only trash icon
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        rightPanel.setOpaque(false);
        
        JButton deleteButton = new JButton("\uD83D\uDDD1"); // Trash icon
        deleteButton.setFont(new Font("Inter", Font.BOLD, 16));
        deleteButton.setForeground(new Color(220, 50, 50));
        deleteButton.setBackground(new Color(255, 240, 240));
        deleteButton.setFocusPainted(false);
        deleteButton.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteButton.addActionListener(e -> removeItem(entry));
        rightPanel.add(deleteButton);
        
        card.add(rightPanel, BorderLayout.EAST);

        // Wrap in outer panel with spacing
        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.setBorder(new EmptyBorder(8, 0, 8, 0));
        outer.add(card, BorderLayout.CENTER);
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90 + 32));

        return outer;
    }
    
    // --- Logika Keranjang ---

    /**
     * Menambahkan item ke keranjang. Jika sudah ada, kuantitasnya ditambah.
     */
    public void addItem(CartItem item) {
        for (CartEntry entry : cartItems) {
            if (entry.item.name().equals(item.name())) {
                entry.quantity++;
                updateCartDisplay();
                return;
            }
        }
        // Item baru
        cartItems.add(new CartEntry(item, 1));
        updateCartDisplay();
    }
    
    /**
     * Clear cart and rebuild from scratch with new items.
     */
    public void syncItems(List<CartItem> newItems) {
        cartItems.clear();
        
        // Group by item name dan hitung total quantity
        java.util.Map<String, Integer> quantityMap = new java.util.HashMap<>();
        java.util.Map<String, CartItem> itemMap = new java.util.HashMap<>();
        
        for (CartItem ci : newItems) {
            String itemName = ci.name();
            quantityMap.put(itemName, quantityMap.getOrDefault(itemName, 0) + 1);
            itemMap.put(itemName, ci);
        }
        
        // Buat SATU CartEntry per item dengan quantity yang benar
        for (java.util.Map.Entry<String, Integer> entry : quantityMap.entrySet()) {
            String itemName = entry.getKey();
            int quantity = entry.getValue();
            CartItem ci = itemMap.get(itemName);
            cartItems.add(new CartEntry(ci, quantity));
        }
        
        updateCartDisplay();
    }
    
    /**
     * Memperbarui kuantitas item.
     */
    private void updateQuantity(CartEntry entry, int newQuantity) {
        if (newQuantity <= 0) {
            removeItem(entry);
        } else {
            entry.quantity = newQuantity;
            updateCartDisplay();
        }
    }
    
    /**
     * Menghapus item dari keranjang.
     */
    private void removeItem(CartEntry entry) {
        cartItems.remove(entry);
        updateCartDisplay();
    }
    
    /**
     * Menghitung total harga keranjang.
     */
    private double calculateTotal() {
        double subtotal = cartItems.stream()
                                   .mapToDouble(CartEntry::getTotalPrice)
                                   .sum();
        double tax = subtotal * 0.10; // Pajak 10%
        
        // Update Ringkasan Harga
        subtotalLabel.setText(currencyFormat.format(subtotal));
        taxLabel.setText(currencyFormat.format(tax));
        totalLabel.setText(currencyFormat.format(subtotal + tax));
        
        return subtotal + tax;
    }
    
    /**
     * Memperbarui tampilan KeranjangPanel berdasarkan data cartItems.
     */
    public void updateCartDisplay() {
        cartListPanel.removeAll();
        
        if (cartItems.isEmpty()) {
            // Tampilan keranjang kosong
            JPanel emptyPanel = new JPanel(new GridBagLayout());
            emptyPanel.setOpaque(false);
            JLabel emptyLabel = new JLabel("Keranjang Anda kosong. Ayo pesan makanan!");
            emptyLabel.setFont(new Font("Inter", Font.ITALIC, 14));
            emptyLabel.setForeground(Color.GRAY);
            emptyPanel.add(emptyLabel);
            emptyPanel.setPreferredSize(new Dimension(300, 300));
            
            cartListPanel.add(emptyPanel);
            
            // Non-aktifkan tombol checkout
            checkoutButton.setEnabled(false);
        } else {
            // Tampilan daftar item
            cartItems.forEach(entry -> cartListPanel.add(createCartItemRow(entry)));
            checkoutButton.setEnabled(true);
        }

        int totalItems = cartItems.stream().mapToInt(e -> e.quantity).sum();
        itemCountLabel.setText(totalItems + " item dalam keranjang");
        
        calculateTotal(); // Hitung dan perbarui ringkasan harga
        
        cartListPanel.revalidate();
        cartListPanel.repaint();
    }

    /**
     * Allow external callers (frames/dialogs) to attach a close action to the header close button.
     */
    public void setCloseListener(ActionListener al) {
        if (closeButton != null) closeButton.addActionListener(al);
    }

    
    // UI kustom untuk JLabel (Digunakan untuk Gambar Item agar Rounded)
    private static class RoundedLabelUI extends javax.swing.plaf.basic.BasicLabelUI {
        private final int radius;
        RoundedLabelUI(int radius) { this.radius = radius; }
        
        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(c.getBackground());
            g2d.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), radius, radius);
            
            // Kliping untuk memastikan icon/text juga rounded
            RoundRectangle2D rect = new RoundRectangle2D.Double(0, 0, c.getWidth(), c.getHeight(), radius, radius);
            g2d.clip(rect);

            super.paint(g2d, c);
            g2d.dispose();
        }
    }
    
    // UI kustom untuk JButton 
    private static class RoundedButtonUI extends BasicButtonUI {
        private final int radius;
        RoundedButtonUI(int radius) { this.radius = radius; }

        @Override
        public void installUI(JComponent c) {
            super.installUI(c);
            c.setOpaque(false); 
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            AbstractButton button = (AbstractButton) c;
            paintBackground(g, button, button.getModel().isPressed() ? 1 : 0);
            super.paint(g, c);
        }

        private void paintBackground(Graphics g, JComponent c, int yOffset) {
            Dimension size = c.getSize();
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(c.getBackground());
            g2d.fillRoundRect(0, yOffset, size.width, size.height - yOffset, radius, radius);
            g2d.dispose();
        }
    }
    
    // UI kustom untuk JPanel 
    private static class RoundedPanelUI extends PanelUI {
        private final int radius;
        RoundedPanelUI(int radius) { this.radius = radius; }

        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = c.getWidth();
            int h = c.getHeight();
            
            // Menggambar Background Utama
            g2d.setColor(c.getBackground());
            g2d.fillRoundRect(0, 0, w, h, radius, radius);

            g2d.dispose();
            super.paint(g, c);
        }
    }
}
