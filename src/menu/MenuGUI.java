package menu;

import app.DBConnection;
import app.DBHelper;
import app.Main;
import app.Theme;
import cart.KeranjangPanel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import menu.border.RoundedCornerBorder;
import menu.components.ImageRounderPanel;
import menu.components.QuantityControlPanel;
import menu.dialogs.MenuDetailDialog;
import menu.dialogs.OrderAction;
import menu.dialogs.OrderConfirmation;
import models.User;
import order.Order;
import order.OrderProcessor;
import order.OrdersManager;
import order.PesananSayaPanel;
import order.SimpleOrderProcessor;
import profile.Profile;

public class MenuGUI extends JPanel implements OrderAction {

    private static final Color BANNER_BG_COLOR = new Color(255, 100, 0);

    private final List<MenuItem> allMenuItems = new ArrayList<>(); // menyimpan semua menu item
    private final JPanel menuPanel = new JPanel();

    private JTextField searchField;
    private JLabel welcomeLabel;

    private JDialog cartDialog;
    private KeranjangPanel cartPanel;

    private final java.util.Map<String, Integer> itemQuantities = new java.util.HashMap<>();
    private String loggedInUserName = "User";
    private User loggedInUser = null;

    private Main parentFrameRef = null;
    private DBHelper dbHelper = new DBHelper();

    private final OrdersManager ordersManager = OrdersManager.getInstance(); // melacak semua pesanan yang telah dibuat

    
    // Constructors
    public MenuGUI() {
        setBackground(Theme.BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        initializeMenuData();
        add(createHeaderPanel(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Theme.BACKGROUND_COLOR);

        center.add(createBannerPanel(), BorderLayout.NORTH);
        center.add(createMenuPanel(), BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
        filterMenu("");
    }

    public MenuGUI(String name) {
        this();
        setLoggedInUserName(name);
    }

    public MenuGUI(Main frame) {
        this();
        this.parentFrameRef = frame;
    }

    // ============================
    // Implementasi OrderAction Interface
    // ============================
    @Override
    public void onOrderPlaced(MenuItem item) {
        placeSingleItemOrderFromDetail(item);
    }

    // ============================
    // Menu Item Card
    // ============================
    private JPanel createMenuItemCard(MenuItem item) {
        int w = 250, h = 300, radius = 20;

        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(w, h));
        card.setBackground(Theme.CARD_BACKGROUND);
        card.setBorder(new RoundedCornerBorder(radius));

        int imgHeight = 180;
        ImageRounderPanel imgPanel = new ImageRounderPanel(radius, true, true, false, false);
        imgPanel.setPreferredSize(new Dimension(w, imgHeight));
        imgPanel.setBackground(Theme.CARD_BACKGROUND);
        imgPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        imgPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(MenuGUI.this);
                // MENGGUNAKAN KONSTRUKTOR DENGAN IMPLEMENTASI INTERFACE
                new MenuDetailDialog(parent, item, MenuGUI.this)
                    .setVisible(true);
            }
        });

        try {
            ImageIcon ori = new ImageIcon(item.getImageUrl());
            if (ori.getIconWidth() > 0) {
                Image scaled = ori.getImage().getScaledInstance(w, imgHeight, Image.SCALE_SMOOTH);
                imgPanel.setImage(new ImageIcon(scaled));
                card.add(imgPanel, BorderLayout.NORTH);
            } else throw new Exception();
        } catch (Exception e) {
            JLabel ph = new JLabel("Gambar\n" + item.getName(), SwingConstants.CENTER);
            ph.setOpaque(true);
            ph.setBackground(Theme.BACKGROUND_COLOR);
            ph.setPreferredSize(new Dimension(w, imgHeight));
            card.add(ph, BorderLayout.NORTH);
        }

        JPanel detail = new JPanel(new BorderLayout());
        detail.setOpaque(false);
        detail.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel name = new JLabel(item.getName());
        name.setFont(new Font("SansSerif", Font.BOLD, 16));
        name.setForeground(Theme.TEXT_DARK);

        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        namePanel.setOpaque(false);
        namePanel.add(name);

        detail.add(namePanel, BorderLayout.NORTH);

        JPanel priceAndControl = new JPanel(new BorderLayout());
        priceAndControl.setOpaque(false);

        JLabel price = new JLabel(String.format("Rp %,d", (int) item.getPrice()).replace(",", "."));
        price.setFont(new Font("SansSerif", Font.BOLD, 18));
        price.setForeground(Theme.TEXT_DARK);

        priceAndControl.add(price, BorderLayout.WEST);

        QuantityControlPanel qty = new QuantityControlPanel(
        item.getName(),
        itemQuantities,
        () -> syncCartPanelFromQuantities(),
        (menuName, delta) -> insertToDB(menuName, delta)
        );

        qty.setOpaque(false);
        priceAndControl.add(qty, BorderLayout.EAST);

        detail.add(priceAndControl, BorderLayout.SOUTH);

        card.add(detail, BorderLayout.CENTER);
        return card;
    }

    // ============================
    // Banner
    // ============================
    private JPanel createBannerPanel() {
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(BANNER_BG_COLOR);
        banner.setPreferredSize(new Dimension(1200, 230));
        banner.setBorder(new EmptyBorder(30, 40, 25, 40));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel title = new JLabel("<html><b style='color:white;font-size:26px'>Pedasnya Nendang,<br>Cita Rasa <span style='color:#FFD700'>Asli Lombok</span></b></html>");
        JLabel sub = new JLabel("<html><div style='width:450px;color:#E0E0E0;font-size:13px;margin-top:10px;'>Nikmati kelezatan kuliner Lombok otentik.</div></html>");

        left.add(title);
        left.add(sub);

        banner.add(left, BorderLayout.WEST);
        banner.add(new JLabel(new ImageIcon("Assets/Ayam.jpg")), BorderLayout.EAST);

        return banner;
    }

    // ============================
    // Header
    // ============================
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(12, 30, 12, 30));

        // Left
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        left.setOpaque(false);

        JLabel icon = new JLabel("🍽");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel title = new JLabel("Rumah Makan Sasak");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        welcomeLabel = new JLabel("Hallo selamat datang, " + loggedInUserName);
        welcomeLabel.setForeground(Color.GRAY);

        info.add(title);
        info.add(welcomeLabel);

        left.add(icon);
        left.add(info);

        header.add(left, BorderLayout.WEST);

        // Center (search bar)
        JPanel searchContainer = new JPanel(new BorderLayout());
        searchContainer.setBackground(Theme.SEARCH_BG_COLOR);
        searchContainer.setBorder(new EmptyBorder(0, 15, 0, 15));

        searchField = new JTextField("Cari makanan");
        searchField.setBorder(null);
        searchField.setOpaque(false);

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String t = searchField.getText().trim();
                filterMenu(t.equals("cari makanan") ? "" : t);
            }
        });

        searchContainer.add(new JLabel("🔍"), BorderLayout.WEST);
        searchContainer.add(searchField, BorderLayout.CENTER);

        JPanel centerWrap = new JPanel(new GridBagLayout());
        centerWrap.setOpaque(false);
        centerWrap.add(searchContainer);

        header.add(centerWrap, BorderLayout.CENTER);

        // Right menu
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        right.setOpaque(false);

        right.add(createMenuHeaderItem("📋", "Menu"));
        right.add(createMenuHeaderItem("📝", "Pesanan Saya"));
        right.add(createMenuHeaderItem("🛒", "Keranjang"));
        right.add(createMenuHeaderItem("👤", "Profil"));

        header.add(right, BorderLayout.EAST);

        return header;
    }

    private JPanel createMenuHeaderItem(String icon, String type) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        p.add(new JLabel(icon));
        p.add(new JLabel(type));

        p.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switch (type) {
                    case "Menu" -> {}
                    case "Keranjang" -> { ensureCartFrame(); cartDialog.setVisible(true); }
                    case "Pesanan Saya" -> {
                        JFrame f = new JFrame("Pesanan Saya");
                        f.add(new PesananSayaPanel(null));
                        f.setSize(900, 700);
                        f.setLocationRelativeTo(MenuGUI.this);
                        f.setVisible(true);
                    }
                    case "Profil" -> {
                        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(MenuGUI.this);
                        if (loggedInUser != null)
                            new Profile(parent, loggedInUser, MenuGUI.this::logout).setVisible(true);
                        else
                            new Profile(parent, loggedInUserName, MenuGUI.this::logout).setVisible(true);
                    }
                }
            }
        });

        return p;
    }

    // ============================
    // Menu Panel
    // ============================
    private JScrollPane createMenuPanel() {
        menuPanel.setLayout(new GridLayout(0, 4, 20, 20));
        menuPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        menuPanel.setBackground(Theme.BACKGROUND_COLOR);

        JScrollPane scroll = new JScrollPane(menuPanel);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);

        scroll.getVerticalScrollBar().setUnitIncrement(30);

        return scroll;
    }

    // ============================
    // Menu Data
    // ============================
    private void initializeMenuData() {
        allMenuItems.clear(); 
        
        allMenuItems.add(new MenuItem(
                "Ayam Taliwang", 35000, 4.8, "Assets/Ayam.jpg",
                "Ayam kampung muda bakar dengan bumbu Taliwang khas Lombok yang pedas manis. Disajikan dengan plecing kangkung dan taburan kacang."
        ));
        allMenuItems.add(new MenuItem(
                "Bebalung", 45000, 4.9, "Assets/Bebalung.jpg",
                "Sup iga sapi khas Sasak dengan bumbu rempah yang kaya, berkuah bening dan menghangatkan. Sempurna untuk pecinta daging dan kuah kaldu."
        ));
        allMenuItems.add(new MenuItem(
                "Sate Rembiga", 25000, 4.9, "Assets/Sate.jpg",
                "Sate daging sapi yang dibakar dengan bumbu pedas manis rahasia, teksturnya empuk dan kaya rasa. Wajib dicoba bagi penikmat sate sejati."
        ));
        allMenuItems.add(new MenuItem(
                "Nasi Balap Puyung", 15000, 4.7, "Assets/Puyung.jpg",
                "Nasi campur dengan suwiran ayam pedas, kacang kedelai goreng, dan sambal khas Puyung. Porsinya pas, pedasnya nendang!"
        ));
        allMenuItems.add(new MenuItem(
                "Beberuk Terong", 12000, 4.4, "Assets/beberuk.jpg",
                "Terong ungu mentah yang diiris dan disiram dengan sambal tomat segar. Rasanya asam, pedas, dan menyegarkan sebagai pendamping lauk utama."
        ));
        allMenuItems.add(new MenuItem(
                "Sayur Ares", 13000, 4.3, "Assets/ares.jpg",
                "Sayur kuah santan dengan bahan utama pelepah pisang muda. Makanan tradisional yang gurih dan otentik dari Lombok."
        ));
        allMenuItems.add(new MenuItem(
                "Nasi Campur", 10000, 4.0, "Assets/Campur.jpg",
                "Nasi dengan berbagai macam lauk pauk sederhana seperti tempe, tahu, dan sayuran. Pilihan cepat dan mengenyangkan."
        ));
        allMenuItems.add(new MenuItem(
                "Sate Pusut", 15000, 4.6, "Assets/SateP.jpg",
                "Sate lilit yang terbuat dari campuran daging sapi/ikan dan parutan kelapa. Aromanya harum dan rasanya gurih pedas."
        ));
        allMenuItems.add(new MenuItem(
                "Sate Bulayak", 35000, 4.9, "Assets/Bulayak.jpg",
                "Sate daging sapi yang disajikan dengan lontong kecil ('bulayak') dan kuah santan pedas. Tekstur lontongnya unik dan kuahnya adiktif."
        ));
        allMenuItems.add(new MenuItem(
                "Kelak Lebui", 12000, 4.6, "Assets/Lebui.jpg",
                "Sup kacang kedelai hitam yang dimasak dengan bumbu rempah. Merupakan makanan sehat yang sering disajikan pada acara adat."
        ));
        allMenuItems.add(new MenuItem(
                "Pelecing Kangkung", 10000, 4.5, "Assets/PelecingK.jpg",
                "Kangkung rebus yang disiram dengan sambal tomat pedas khas Lombok, ditaburi kacang tanah goreng. Pedas dan segar!"
        ));
        allMenuItems.add(new MenuItem(
                "Ayam Rarang", 30000, 4.5, "Assets/Rarang.jpg",
                "Ayam goreng yang dimasak dengan bumbu cabai merah pedas yang dominan. Rasa pedasnya lebih menonjol daripada Ayam Taliwang."
        ));
    }

    // ============================
    // Filter Menu
    // ============================
    private void filterMenu(String q) {
        menuPanel.removeAll();
        String query = q == null ? "" : q.toLowerCase();

        for (MenuItem item : allMenuItems) {
            if (item.getName().toLowerCase().contains(query)) {
                menuPanel.add(createMenuItemCard(item));
            }
        }

        menuPanel.revalidate();
        menuPanel.repaint();
    }
    
    // ============================================
    // Aksi Pemesanan dari MenuDetailDialog
    // ============================================
    private void placeSingleItemOrderFromDetail(MenuItem item) {
        // Verifikasi Status Login
        if (loggedInUser == null) {
            JOptionPane.showMessageDialog(this, "Anda harus login untuk memesan!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Memperbarui Keranjang Memori
        if (!itemQuantities.containsKey(item.getName())) {
            itemQuantities.put(item.getName(), 1);
        } else {
            itemQuantities.put(item.getName(), itemQuantities.get(item.getName()) + 1);
        }

        // Sinkronisasi Data ke Database dan U
        insertToDB(item.getName(), 1); 
        syncCartPanelFromQuantities(); // Perbarui tampilan keranjang jika ada

        filterMenu(searchField.getText().trim()); 
        
        JOptionPane.showMessageDialog(this, 
                item.getName() + " telah ditambahkan ke keranjang!\nSilakan lanjutkan ke halaman Keranjang untuk checkout.", 
                "Item Ditambahkan", 
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ============================
    // Cart Sync
    // ============================
    private void ensureCartFrame() {
        if (cartPanel == null) {
            cartPanel = new KeranjangPanel(
                    e -> checkoutCart()
            );
        }

        if (cartDialog == null) {
            Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
            cartDialog = new JDialog(owner, "Keranjang", false);
            cartDialog.setUndecorated(true);
            cartDialog.add(cartPanel);
            cartDialog.setSize(350, 600);
            cartDialog.setLocationRelativeTo(this);
        }

        cartPanel.setCloseListener(e -> cartDialog.setVisible(false));

        cartPanel.syncItems(buildCartItems());
    }

    private List<KeranjangPanel.CartItem> buildCartItems() {
        List<KeranjangPanel.CartItem> list = new ArrayList<>();

        for (String name : itemQuantities.keySet()) {
            int qty = itemQuantities.get(name);
            if (qty > 0) {
                for (MenuItem mi : allMenuItems) {
                    if (mi.getName().equals(name)) {
                        for (int i = 0; i < qty; i++) {
                            list.add(new KeranjangPanel.CartItem(
                                    mi.getName(),
                                    String.format("Rp %,d", (int) mi.getPrice()).replace(",", "."),
                                    mi.getImageUrl(),
                                    mi.getPrice()
                            ));
                        }
                    }
                }
            }
        }

        return list;
    }

    private void syncCartPanelFromQuantities() {
        if (cartPanel != null) {
            cartPanel.syncItems(buildCartItems());
        }
    }

    // ============================
    // DB + Checkout
    // ============================
    public void insertToDB(String menuName, int delta) {
        try {
            if (parentFrameRef == null) return;
            User user = parentFrameRef.getLoggedInUser();
            if (user == null) return;

            int userId = user.getId();
            int idMenu = dbHelper.getIdMenuByName(menuName);

            if (idMenu > 0) {
                dbHelper.tambahKeKeranjang(userId, idMenu, delta);
            }

        } catch (Exception ignored) {}
    }

    private void checkoutCart() {

        if (loggedInUser == null) {
            JOptionPane.showMessageDialog(this, "Anda harus login untuk memesan!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int userId = loggedInUser.getId();
        double total = calculateCartTotal();

        List<Order.OrderItem> orderItems = new ArrayList<>();

        for (String name : itemQuantities.keySet()) {
            int qty = itemQuantities.get(name);
            if (qty > 0) {
                for (MenuItem mi : allMenuItems) {
                    if (mi.getName().equals(name)) {
                        orderItems.add(new Order.OrderItem(name, qty, mi.getPrice()));
                    }
                }
            }
        }

        if (orderItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keranjang kosong!");
            return;
        }

        int pesananId = dbHelper.insertPesanan(userId, total);

        if (pesananId <= 0) {
            JOptionPane.showMessageDialog(this, "Gagal membuat pesanan di database!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (Order.OrderItem oi : orderItems) {
            int menuId = dbHelper.getIdMenuByName(oi.getName());
            if (menuId > 0) {
                dbHelper.insertPesananDetail(
                    pesananId,
                    menuId,
                    oi.getQuantity(),
                    oi.getPricePerUnit()
                );
            }
        }

        clearKeranjangDB(userId);

        Order order = new Order(String.valueOf(pesananId), orderItems, total, 0);

        ordersManager.addOrder(order);

        // Proses order secara asynchronous
        OrderProcessor processor = new SimpleOrderProcessor(pesananId);

        processor.submitOrder(order, o -> SwingUtilities.invokeLater(() -> {
            dbHelper.updateStatusPesanan(pesananId, "selesai");
            resetAfterOrder();
        }));

        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        new OrderConfirmation(parent, order);

        if (cartDialog != null)
            cartDialog.setVisible(false);

        resetAfterOrder();
    }

    private void clearKeranjangDB(int userId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM keranjang WHERE user_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("❌ clearKeranjangDB gagal: " + e.getMessage());
        }
    }

    private void resetAfterOrder() {
        itemQuantities.clear();
        if (searchField != null) searchField.setText("");
        filterMenu("");

        if (cartPanel != null) cartPanel.syncItems(new ArrayList<>());
    }

    private double calculateCartTotal() {
        double subtotal = 0;

        for (String name : itemQuantities.keySet()) {
            for (MenuItem mi : allMenuItems) {
                if (mi.getName().equals(name)) {
                    subtotal += mi.getPrice() * itemQuantities.get(name);
                }
            }
        }

        return subtotal + (subtotal * 0.10);
    }

    // ============================
    // User Session
    // ============================
    public void setLoggedInUserName(String name) {
        this.loggedInUserName = name;
        if (welcomeLabel != null) welcomeLabel.setText("Hallo selamat datang, " + name);
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
        setLoggedInUserName(user.getUsername());
    }

    public void logout() {
        loggedInUser = null;
        loggedInUserName = "User";
        if (welcomeLabel != null) welcomeLabel.setText("Hallo selamat datang, User");

        itemQuantities.clear();

        if (parentFrameRef != null) {
            parentFrameRef.setLoggedInUser(null);
            parentFrameRef.showPanel(Main.LOGIN_VIEW);
        }
    }
}