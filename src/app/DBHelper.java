package app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import models.User;

public class DBHelper {

    // 1. KONEKSI DATABASE
    /**
     * @return
     * @throws Exception
     */

    private Connection getConnection() throws Exception {
        return DBConnection.getConnection();
    }

    // 2. REGISTER USER
    public boolean register(String username, String email, String password) {
        return DBConnection.registerUser(username, email, password);
    }

    // 3. LOGIN USER
    public User login(String identifier, String password) {
        return DBConnection.loginUser(identifier, password);
    }

    // 4. TAMBAH MENU KE KERANJANG
    public boolean tambahKeKeranjang(int userId, int idMenu, int jumlah) {
        return DBConnection.tambahKeKeranjang(userId, idMenu, jumlah);
    }

    // 5. AMBIL ID MENU BERDASARKAN NAMA
    public int getIdMenuByName(String nama) {
        return DBConnection.getIdMenuByName(nama);
    }

    // 6. INSERT PESANAN (HEADER)
    public int insertPesanan(int userId, double total) {
        try (Connection conn = getConnection()) {

            String sql = "INSERT INTO pesanan (user_id, total_harga, status) VALUES (?, ?, 'menunggu')";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1, userId);
            ps.setDouble(2, total);
            ps.executeUpdate();

            // Ambil primary key yang baru dibuat
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // id pesanan
            }

        } catch (Exception e) {
            System.out.println("❌ insertPesanan gagal: " + e.getMessage());
        }
        return -1; // return -1 jika gagal
    }

    // 7. INSERT PESANAN DETAIL (ITEM PER MENU)
    public void insertPesananDetail(int pesananId, int menuId, int qty, double harga) {
        try (Connection conn = getConnection()) {

            String sql = "INSERT INTO pesanan_detail (pesanan_id, menu_id, quantity, harga) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, pesananId);
            ps.setInt(2, menuId);
            ps.setInt(3, qty);
            ps.setDouble(4, harga);

            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("❌ insertPesananDetail gagal: " + e.getMessage());
        }
    }
}
