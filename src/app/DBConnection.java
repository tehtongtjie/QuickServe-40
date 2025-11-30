package app;

import java.sql.*;
import models.User;

public class DBConnection {

    // KONFIGURASI DATABASE
    private static final String URL  = "jdbc:mysql://localhost:3306/restoran?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "";

    // 1. FUNGSI KONEKSI DATABASE
    /**
     * Membuat koneksi ke database MySQL.
     * @return Connection object jika berhasil, null jika gagal.
     */

    public static Connection getConnection() {
        try {
            // Load driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Membuka koneksi
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Database connection SUCCESS");
            return conn;

        } catch (ClassNotFoundException cnfe) {
            System.err.println("MySQL JDBC Driver NOT FOUND!");
            return null;

        } catch (SQLException sqle) {
            System.err.println("Database connection FAILED: " + sqle.getMessage());
            return null;
        }
    }

    // 2. FUNGSI REGISTER USER BARU
    /**
     * Registrasi user baru (validasi username dan email sebelum insert).
     * @return true jika berhasil, false jika gagal.
     */

    public static boolean registerUser(String username, String email, String password) {

        // Query cek apakah username/email sudah ada
        String cekUser  = "SELECT username FROM users WHERE username = ? OR email = ?";

        // Query insert user baru
        String insertUser = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";

        try (Connection conn = getConnection()) {

            if (conn == null) return false;

            // --- CEK USER ---
            PreparedStatement ps1 = conn.prepareStatement(cekUser);
            ps1.setString(1, username);
            ps1.setString(2, email);

            ResultSet rs = ps1.executeQuery();

            // Jika baris ditemukan -> username/email sudah digunakan
            if (rs.next()) {
                System.err.println("❌ Username/email already exists");
                return false;
            }

            // --- INSERT USER BARU ---
            PreparedStatement ps2 = conn.prepareStatement(insertUser);
            ps2.setString(1, username);
            ps2.setString(2, email);
            ps2.setString(3, password);

            int result = ps2.executeUpdate();

            System.out.println(" User registered: " + username);
            return result > 0;

        } catch (SQLException e) {
            System.err.println(" Registration failed: " + e.getMessage());
            return false;
        }
    }

    // 3. FUNGSI LOGIN USER
    /**
     * Validasi login menggunakan username ATAU email.
     * @return User object jika berhasil login, null jika gagal.
     */

    public static User loginUser(String identifier, String password) {

        System.out.println("🔐 Attempting login for: " + identifier);

        // Query ambil data user berdasarkan username/email
        String query = "SELECT user_id, username, email, password FROM users WHERE username = ? OR email = ?";

        try (Connection conn = getConnection()) {

            if (conn == null) return null;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, identifier);
            ps.setString(2, identifier);

            ResultSet rs = ps.executeQuery();

            // Jika user ditemukan
            if (rs.next()) {

                String storedPassword = rs.getString("password");

                // Cocokkan password (plain compare)
                if (storedPassword.equals(password)) {

                    int id = rs.getInt("user_id");
                    String username = rs.getString("username");
                    String email = rs.getString("email");

                    System.out.println("Login SUCCESS: " + username);
                    return new User(id, username, email);

                } else {
                    System.err.println("Password mismatch");
                }

            } else {
                System.err.println("User not found: " + identifier);
            }

            return null;

        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
            return null;
        }
    }

    // 4. FUNGSI AMBIL ID MENU BERDASARKAN NAMA
    /**
     * Mengambil id_menu dari tabel menu berdasarkan nama menu.
     * @return id_menu jika ditemukan, -1 jika tidak ada.
     */

    public static int getIdMenuByName(String namaMenu) {

        String sql = "SELECT id_menu FROM menu WHERE nama = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, namaMenu);

            ResultSet rs = ps.executeQuery();

            // Jika data menu ditemukan
            if (rs.next()) {
                return rs.getInt("id_menu");
            }

        } catch (SQLException e) {
            System.err.println("Error getIdMenuByName: " + e.getMessage());
        }

        return -1; // Jika menu tidak ditemukan
    }

    // 5. FUNGSI TAMBAH MENU KE KERANJANG
    /**
     * Menambahkan satu item menu ke keranjang user.
     * @return true jika berhasil insert, false jika gagal.
     */

    public static boolean tambahKeKeranjang(int userId, int idMenu, int jumlah) {

        String sql = "INSERT INTO keranjang (user_id, id_menu, jumlah) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, idMenu);
            ps.setInt(3, jumlah);

            int result = ps.executeUpdate();

            System.out.println("🛒 Insert cart item success! idMenu=" + idMenu + ", qty=" + jumlah);
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Error tambahKeKeranjang: " + e.getMessage());
            return false;
        }
    }
}
