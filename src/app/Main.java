package app;

import auth.LoginPanel;
import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import menu.MenuGUI;
import models.User;
import order.PesananSayaPanel;

public class Main extends JFrame {

    // KONSTANTA NAMA PANEL (CardLayout)
    public static final String LOGIN_VIEW = "LOGIN";
    public static final String HOME_VIEW  = "MENU";
    public static final String ORDER_VIEW = "ORDERS";

    // CardLayout untuk navigasi antar panel
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);

    // Helper database
    private final DBHelper dbHelper = new DBHelper();

    // Data user yang sedang login
    private User loggedInUser = null;

    // Panel utama aplikasi
    private MenuGUI homePanel;
    private PesananSayaPanel orderPanel;

    // KONSTRUKTOR UTAMA APLIKASI
    public Main() {

        // --- Setup basic window ---
        setTitle("FoodOrder");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // --- Inisialisasi Panel ---
        LoginPanel loginPanel = new LoginPanel(this);
        homePanel = new MenuGUI(this);
        orderPanel = new PesananSayaPanel(this);

        // --- Masukkan panel ke CardLayout ---
        mainPanel.add(loginPanel, LOGIN_VIEW);
        mainPanel.add(homePanel, HOME_VIEW);
        mainPanel.add(orderPanel, ORDER_VIEW);

        add(mainPanel);

        // Tampilkan panel login pertama kali
        showPanel(LOGIN_VIEW);

        setVisible(true);
    }

    // MENGELOLA INFORMASI USER YANG SEDANG LOGIN
    public void setLoggedInUser(User user) {
        this.loggedInUser = user;

        // Kirim informasi ke panel menu
        if (homePanel != null) 
            homePanel.setLoggedInUser(user);

        // Kirim username ke panel pesanan
        if (orderPanel != null) 
            orderPanel.setLoggedInUserName(user.getUsername());
    }

    /**
     * Mengambil user yang sedang login.
     */
    public User getLoggedInUser() {
        return this.loggedInUser;
    }

    // AKSESOR UNTUK DBHelper
    public DBHelper getDbHelper() {
        return dbHelper;
    }

    // FUNGSI NAVIGASI ANTAR PANEL
    public void showMenu() {
        showPanel(HOME_VIEW);
    }
    public void showOrders() {
        showPanel(ORDER_VIEW);
    }
    public void showPanel(String name) {
        cardLayout.show(mainPanel, name);
    }

    // MAIN METHOD (Start Program)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
