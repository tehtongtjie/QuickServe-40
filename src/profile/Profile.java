package profile;

import app.Main;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import models.User;

public class Profile extends JDialog {

    private final JFrame parentFrame;
    private String username;
    private String email;

    private Runnable onLogout = null;

    public Profile(JFrame parent, String username) {
        super(parent, "Profil Pengguna", true);
        this.parentFrame = parent;
        this.username = username != null ? username : "User";
        this.email = "user@example.com";
        initializeUI();
    }

    public Profile(JFrame parent, String username, Runnable onLogout) {
        this(parent, username);
        this.onLogout = onLogout;
    }

    public Profile(JFrame parent, User user) {
        super(parent, "Profil Pengguna", true);
        this.parentFrame = parent;

        if (user != null) {
            this.username = user.getUsername() != null ? user.getUsername() : "User";
            this.email = user.getEmail() != null ? user.getEmail() : "user@example.com";
        } else {
            this.username = "User";
            this.email = "user@example.com";
        }

        initializeUI();
    }

    public Profile(JFrame parent, User user, Runnable onLogout) {
        this(parent, user);
        this.onLogout = onLogout;
    }

    private void initializeUI() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(getParent());

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(new Color(245, 245, 245));
        main.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel header = new JLabel("Profil Pengguna");
        header.setFont(new Font("Arial", Font.BOLD, 24));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        main.add(header);
        main.add(Box.createVerticalStrut(20));
        main.add(createProfileCard());
        main.add(Box.createVerticalStrut(20));

        // ================= LOGOUT BUTTON ========================
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton logout = new JButton("Logout");
        logout.setBackground(new Color(244, 67, 54));
        logout.setForeground(Color.WHITE);
        logout.setBorder(new EmptyBorder(8, 15, 8, 15));

        logout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Apakah Anda yakin ingin logout?",
                    "Konfirmasi Logout",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                dispose();

                // ==== FIX: panggil logout dari Main ====
                if (parentFrame instanceof Main mainApp) {
                    mainApp.logout();   // 🔥 SESSION BENAR-BENAR DIHAPUS
                }
            }
        });

        buttonPanel.add(logout);
        main.add(buttonPanel);
        add(main);
    }

    private JPanel createProfileCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // ==================== AVATAR ========================
        JLabel avatar;
        final int maxDim = 120;

        try {
            ImageIcon raw = new ImageIcon("Assets/profile.png");
            Image img = raw.getImage();

            int w = img.getWidth(null);
            int h = img.getHeight(null);

            double scale = Math.min((double) maxDim / w, (double) maxDim / h);
            int newW = (int) (w * scale);
            int newH = (int) (h * scale);

            Image scaled = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);

            avatar = new JLabel(new ImageIcon(scaled));
            avatar.setPreferredSize(new Dimension(maxDim, maxDim));
            avatar.setHorizontalAlignment(SwingConstants.CENTER);

        } catch (Exception e) {
            avatar = new JLabel("👤", SwingConstants.CENTER);
            avatar.setFont(new Font("Arial", Font.PLAIN, 60));
            avatar.setPreferredSize(new Dimension(maxDim, maxDim));
        }

        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(avatar);
        card.add(Box.createVerticalStrut(15));

        JLabel uname = new JLabel(username);
        uname.setFont(new Font("Arial", Font.BOLD, 18));
        uname.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(uname);

        card.add(Box.createVerticalStrut(20));
        card.add(new JSeparator());
        card.add(Box.createVerticalStrut(15));

        card.add(createDetailRow("Email:", email));
        card.add(Box.createVerticalStrut(10));

        return card;
    }

    private JPanel createDetailRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JLabel l = new JLabel(label);
        l.setFont(new Font("Arial", Font.BOLD, 12));
        l.setForeground(Color.GRAY);

        JLabel v = new JLabel(value);
        v.setFont(new Font("Arial", Font.PLAIN, 13));

        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.CENTER);

        return row;
    }

    public void setOnLogout(Runnable onLogout) {
        this.onLogout = onLogout;
    }
}
