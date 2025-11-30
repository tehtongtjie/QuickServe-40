package auth;

import app.Main;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import models.User;


public class LoginPanel extends JPanel {
    private JFrame mainFrame;
    private CardLayout formCardLayout = new CardLayout();
    private JPanel formContainer = new JPanel(formCardLayout); 

    // Komponen UI
    private JTextField loginUsernameField; 
    private JPasswordField loginPasswordField;
    private JButton loginTabButton;

    private JTextField regNamaField;
    private JTextField regEmailField;
    private JPasswordField regPasswordField;
    private JButton registerTabButton;
    private JButton daftarButton;
    private JButton loginSubmitButton; 

    // Konfigurasi Konstanta
    private static final int CONTENT_WIDTH = 400; // Lebar Konten utama (putih)
    private static final int CONTENT_HEIGHT = 550; 
    private static final int FIELD_WIDTH = 300; // Lebar input field
    private static final Color ORANGE_COLOR = new Color(255, 100, 0); // Warna utama
    private static final Color DARK_ORANGE_COLOR = new Color(255, 50, 0);

    public LoginPanel(JFrame parentFrame) {
        this.mainFrame = parentFrame;
        setLayout(new GridBagLayout()); 
        
        // --- Panel Konten Utama (Untuk Sudut Bulat) ---
        JPanel contentArea = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                // Make corners subtler (less rounded)
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 12, 12)); 
                g2.dispose();
            }
            @Override
            public Dimension getPreferredSize() { 
                return new Dimension(CONTENT_WIDTH, CONTENT_HEIGHT);
            }
            @Override
            public Dimension getMaximumSize() { 
                return getPreferredSize();
            }
        };
        contentArea.setLayout(new BoxLayout(contentArea, BoxLayout.Y_AXIS));
        contentArea.setBackground(Color.WHITE);
        // Border: Disesuaikan agar form tidak terlalu ke tepi
        contentArea.setBorder(new EmptyBorder(30, 40, 30, 40)); 

        // ===== LOGO IMAGE (SAFE & RESPONSIVE) =====
        JLabel logo;

        try {
            // Pastikan path gambar benar di komputer Anda
            ImageIcon sourceIcon = new ImageIcon(
                "Assets/download.jpg"
            );

            // Resize gambar agar pas (maks 120px)
            if (sourceIcon.getIconWidth() > 0) {
                Image scaled = sourceIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                ImageIcon finalIcon = new ImageIcon(scaled);
                logo = new JLabel(finalIcon);
            } else {
                throw new Exception("Image not found");
            }

        } catch (Exception e) {
            // fallback jika gambar gagal dimuat
            logo = new JLabel("LOGO");
            logo.setFont(new Font("Arial", Font.BOLD, 20));
            logo.setForeground(Color.DARK_GRAY);
        }

        // Tata letak logo
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));  // jarak bawah

        JLabel title = new JLabel("FoodOrder", SwingConstants.CENTER);
        title.setFont(new Font("Montserrat", Font.BOLD, 24)); 
        title.setForeground(ORANGE_COLOR); 
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Pesan makanan favoritmu dengan mudah", SwingConstants.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0)); 

        // --- Tabs (Login / Register Button Group) ---
        JPanel tabPanel = createTabPanel(); 
        tabPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        tabPanel.setOpaque(false); 

        // --- Form Container ---
        formContainer.setOpaque(false); 
        formContainer.add(createLoginForm(), "LOGIN");
        formContainer.add(createRegisterForm(), "REGISTER"); 
        // Mengatur lebar maksimum sesuai lebar konten dikurangi padding
        formContainer.setMaximumSize(new Dimension(CONTENT_WIDTH - 80, 450)); 
        
        // default to Register to match screenshot
        formCardLayout.show(formContainer, "REGISTER");

        // --- Tambahkan Komponen ke Main Content ---
        contentArea.add(logo);
        contentArea.add(Box.createVerticalStrut(5));
        contentArea.add(title);
        contentArea.add(subtitle);
        contentArea.add(Box.createVerticalStrut(10));
        contentArea.add(tabPanel);
        contentArea.add(Box.createVerticalStrut(15)); 
        contentArea.add(formContainer);
        
        add(contentArea); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create(); 
        
        // Gradient background Orange Merah
        GradientPaint gp = new GradientPaint(0, 0, new Color(255, 100, 0), 
                                             getWidth(), getHeight(), new Color(255, 50, 0)); 
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
    }

    // --- Tab Methods ---
    private JPanel createTabPanel() {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60)); 
        
        JPanel panel = new JPanel(new GridLayout(1, 2, 0, 0));
        panel.setPreferredSize(new Dimension(FIELD_WIDTH, 45)); 
        panel.setOpaque(false);
        
        loginTabButton = new JButton("Login");
        registerTabButton = new JButton("Register");
        
        // default state: Register active like screenshot
        styleTabButton(loginTabButton, false, true); // Login inactive, left
        styleTabButton(registerTabButton, true, false); // Register active, right

        loginTabButton.addActionListener(e -> {
            formCardLayout.show(formContainer, "LOGIN");
            styleTabButton(loginTabButton, true, true);
            styleTabButton(registerTabButton, false, false);
            clearRegisterFields();
        });

        registerTabButton.addActionListener(e -> {
            formCardLayout.show(formContainer, "REGISTER");
            styleTabButton(loginTabButton, false, true);
            styleTabButton(registerTabButton, true, false);
            clearLoginFields();
        });

        // Make buttons full width (each half of FIELD_WIDTH)
        int tabBtnW = FIELD_WIDTH / 2;
        loginTabButton.setPreferredSize(new Dimension(tabBtnW, 45));
        registerTabButton.setPreferredSize(new Dimension(tabBtnW, 45));
        panel.add(loginTabButton);
        panel.add(registerTabButton);
        wrapper.add(panel);
        return wrapper;
    }

    /**
     * @param isLeftButton true jika tombol kiri (Login), false jika kanan (Register)
     */
    private void styleTabButton(JButton button, boolean isActive, boolean isLeftButton) {
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 14)); // Font standar
        
        int radius = 8; // Radius sudut (reduced, subtler)
        
        if (isActive) {
            // Active tab: light blue fill, orange outline
            button.setBackground(new Color(225, 240, 255));
            button.setForeground(Color.BLACK);
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ORANGE_COLOR, 2, true),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)
            ));
        } else {
            // Inactive: subtle gray
            button.setBackground(new Color(245, 245, 245));
            button.setForeground(Color.GRAY.darker());
            button.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        }

        // Outer rounded border to create pill shape
        button.setBorder(new TabRoundedBorder(isActive ? ORANGE_COLOR : new Color(220, 220, 220), 1, radius, isLeftButton, isActive));
    }

    // --- Bagian Membuat Form Login ---
    private JPanel createLoginForm() {
        JPanel form = new JPanel();
        form.setMaximumSize(new Dimension(FIELD_WIDTH, 450));
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false); 
        form.setBorder(new EmptyBorder(10, 0, 0, 0)); 

        loginUsernameField = new JTextField();
        loginPasswordField = new JPasswordField();
        
        styleTextField(loginUsernameField, "nama@email.com"); 
        styleTextField(loginPasswordField, "Password");
        
        loginSubmitButton = new CustomGradientButton("LOGIN SEKARANG", ORANGE_COLOR, DARK_ORANGE_COLOR);
        styleSubmitButton(loginSubmitButton); 

        // Label Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        form.add(emailLabel);
        form.add(Box.createVerticalStrut(5));
        
        loginUsernameField.setMaximumSize(new Dimension(FIELD_WIDTH, 45));
        loginUsernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(loginUsernameField);
        form.add(Box.createVerticalStrut(15));
        
        // Label Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        form.add(passwordLabel);
        form.add(Box.createVerticalStrut(5));
        
        loginPasswordField.setMaximumSize(new Dimension(FIELD_WIDTH, 45));
        loginPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(loginPasswordField);
        form.add(Box.createVerticalStrut(30));
        
        loginSubmitButton.setMaximumSize(new Dimension(FIELD_WIDTH, 50));
        loginSubmitButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(loginSubmitButton);
        
        loginSubmitButton.addActionListener(e -> attemptLogin());
        return form;
    }
    
    // --- Bagian Membuat Form Register ---
    private JPanel createRegisterForm() {
        JPanel form = new JPanel();
        form.setMaximumSize(new Dimension(FIELD_WIDTH, 450));
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false); 
        form.setBorder(new EmptyBorder(10, 0, 0, 0)); 

        regNamaField = new JTextField();
        regEmailField = new JTextField();
        regPasswordField = new JPasswordField();

        styleTextField(regNamaField, "Nama Lengkap");
        styleTextField(regEmailField, "nama@email.com");
        styleTextField(regPasswordField, "Password");
        
        daftarButton = new CustomGradientButton("DAFTAR SEKARANG", ORANGE_COLOR, DARK_ORANGE_COLOR);
        styleSubmitButton(daftarButton); 
        
        // Label Nama Lengkap
        JLabel namaLabel = new JLabel("Nama Lengkap:");
        namaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        namaLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        form.add(namaLabel);
        form.add(Box.createVerticalStrut(5));
        
        regNamaField.setMaximumSize(new Dimension(FIELD_WIDTH, 45));
        regNamaField.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(regNamaField);
        form.add(Box.createVerticalStrut(15));
        
        // Label Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        form.add(emailLabel);
        form.add(Box.createVerticalStrut(5));
        
        regEmailField.setMaximumSize(new Dimension(FIELD_WIDTH, 45));
        regEmailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(regEmailField);
        form.add(Box.createVerticalStrut(15));
        
        // Label Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        form.add(passwordLabel);
        form.add(Box.createVerticalStrut(5));
        
        regPasswordField.setMaximumSize(new Dimension(FIELD_WIDTH, 45));
        regPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(regPasswordField);
        form.add(Box.createVerticalStrut(30)); 
        
        daftarButton.setMaximumSize(new Dimension(FIELD_WIDTH, 50));
        daftarButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(daftarButton);
        
        daftarButton.addActionListener(e -> attemptRegister());
        return form;
    }

    // --- Utility Methods untuk Styling ---
    private void styleTextField(JTextField field, String placeholder) {

        // --- Layout & Size ---
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setHorizontalAlignment(SwingConstants.LEFT);
        field.setMaximumSize(new Dimension(FIELD_WIDTH, 48));

        // --- Base Style ---
        field.setBackground(Color.WHITE);
        field.setForeground(new Color(60, 60, 60));
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(new RoundedBorder(new Color(210, 210, 210), 1, 12, true));

        // === Placeholder Style ===
        field.setForeground(new Color(160, 160, 160));      
        field.setFont(new Font("SansSerif", Font.ITALIC, 14));
        field.setText(placeholder);

        // --- Password placeholder specific ---
        if (field instanceof JPasswordField) {
            ((JPasswordField) field).setEchoChar((char) 0);
        }

        // --- Focus Listener ---
        field.addFocusListener(new java.awt.event.FocusAdapter() {

            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                boolean isPlaceholder = field.getText().equals(placeholder) ||
                                        field.getForeground().equals(new Color(160,160,160));

                if (isPlaceholder) {
                    field.setText("");
                    field.setForeground(new Color(40, 40, 40));
                    field.setFont(new Font("SansSerif", Font.PLAIN, 14));
                }

                // Password start masking
                if (field instanceof JPasswordField && ((JPasswordField) field).getEchoChar() == 0) {
                    ((JPasswordField) field).setEchoChar('•');
                }

                // Smooth highlight border saat fokus
                field.setBorder(new RoundedBorder(new Color(255, 140, 0), 2, 12, true));
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                boolean empty;
                if (field instanceof JPasswordField) {
                    char[] pw = ((JPasswordField) field).getPassword();
                    empty = pw == null || new String(pw).isEmpty();
                } else {
                    empty = field.getText().trim().isEmpty();
                }

                if (empty) {
                    field.setText(placeholder);
                    field.setForeground(new Color(160, 160, 160));
                    field.setFont(new Font("SansSerif", Font.ITALIC, 14));

                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar((char) 0);
                    }
                }

                // Kembalikan border normal
                field.setBorder(new RoundedBorder(new Color(210, 210, 210), 1, 12, true));
            }
        });
    }

    private void styleSubmitButton(JButton button) {
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Montserrat", Font.BOLD, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); 
    }
    
    // --- Logika Bisnis & Clear Fields ---
    private void attemptLogin() {
        String username = loginUsernameField.getText();
        String password = new String(loginPasswordField.getPassword());
        
        if (username.equals("nama@email.com") || username.isEmpty() || password.isEmpty() || password.equals("Password")) {
             JOptionPane.showMessageDialog(this, "Semua kolom harus diisi dengan benar.", "Error", JOptionPane.ERROR_MESSAGE);
             return;
        }

        // Catatan: Pastikan class Main memiliki method getDbHelper() dan konstanta HOME_VIEW
        User loggedInUser = ((Main) mainFrame).getDbHelper().login(username, password);
        if (loggedInUser != null) {
            JOptionPane.showMessageDialog(this, "Login Berhasil! Selamat datang, " + loggedInUser.getUsername(), "Sukses", JOptionPane.INFORMATION_MESSAGE);
            // pass the user object to the main/home panel so greeting and profile show the right details
            ((Main) mainFrame).setLoggedInUser(loggedInUser);
            ((Main) mainFrame).showPanel(Main.HOME_VIEW);
        } else {
            JOptionPane.showMessageDialog(this, "Username atau Password salah.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void attemptRegister() {
        String nama = regNamaField.getText();
        String email = regEmailField.getText();
        String password = new String(regPasswordField.getPassword());

        if (nama.equals("Nama Lengkap")) nama = "";
        if (email.equals("nama@email.com")) email = "";
        if (password.equals("Password")) password = ""; 

        if (nama.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua kolom harus diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Catatan: Pastikan class Main memiliki method getDbHelper()
        if (((Main) mainFrame).getDbHelper().register(nama, email, password)) {
            JOptionPane.showMessageDialog(this, "Registrasi Berhasil! Silakan Login.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loginTabButton.doClick(); 
        } else {
             JOptionPane.showMessageDialog(this, "Registrasi Gagal (Email mungkin sudah terdaftar).", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearLoginFields() {
        loginUsernameField.setText("nama@email.com"); 
        loginUsernameField.setForeground(new Color(160, 160, 160));
        loginUsernameField.setFont(new Font("SansSerif", Font.ITALIC, 14));
        
        loginPasswordField.setText("Password");
        loginPasswordField.setForeground(new Color(160, 160, 160));
        loginPasswordField.setFont(new Font("SansSerif", Font.ITALIC, 14));
        loginPasswordField.setEchoChar((char) 0);
    }

    private void clearRegisterFields() {
        regNamaField.setText("Nama Lengkap");
        regNamaField.setForeground(new Color(160, 160, 160));
        regNamaField.setFont(new Font("SansSerif", Font.ITALIC, 14));
        
        regEmailField.setText("nama@email.com");
        regEmailField.setForeground(new Color(160, 160, 160));
        regEmailField.setFont(new Font("SansSerif", Font.ITALIC, 14));
        
        regPasswordField.setText("Password");
        regPasswordField.setForeground(new Color(160, 160, 160));
        regPasswordField.setFont(new Font("SansSerif", Font.ITALIC, 14));
        regPasswordField.setEchoChar((char) 0);
    }
} 
// END OF LoginPanel CLASS

// --- Helper Classes (Non-Public, placed in same file) ---

class RoundedBorder implements Border {
    private int radius;
    private Color color;
    private int thickness;
    private boolean isTextField; 

    public RoundedBorder(Color color, int thickness, int radius, boolean isTextField) {
        this.radius = radius;
        this.color = color;
        this.thickness = thickness;
        this.isTextField = isTextField;
    }

    public Insets getBorderInsets(Component c) {
        if (isTextField) {
            return new Insets(8, 12, 8, 12); 
        }
        return new Insets(this.radius/2 + 2, this.radius/2 + 2, this.radius/2 + 2, this.radius/2 + 2);
    }

    public boolean isBorderOpaque() {
        return true;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(thickness));
        
        g2.draw(new RoundRectangle2D.Double(x, y, width - 1, height - 1, radius, radius));
        g2.dispose();
    }
}

class TabRoundedBorder implements Border {
    private int radius;
    private Color color;
    private int thickness;
    private boolean isLeftButton; 
    private boolean isActive;

    public TabRoundedBorder(Color color, int thickness, int radius, boolean isLeftButton, boolean isActive) {
        this.radius = radius;
        this.color = color;
        this.thickness = thickness;
        this.isLeftButton = isLeftButton;
        this.isActive = isActive;
    }

    public Insets getBorderInsets(Component c) {
         return new Insets(8, 12, 8, 12);
    }

    public boolean isBorderOpaque() {
        return true;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(color);
        g2.setStroke(new BasicStroke(thickness));
        
        int arc = radius * 2;
        
        // Menggambar garis luar rounded
        g2.draw(new RoundRectangle2D.Double(x, y, width - thickness, height - thickness, arc, arc));
        
        g2.dispose();
    }
}

class CustomGradientButton extends JButton {
    private Color startColor;
    private Color endColor;
    private int radius = 8; 

    public CustomGradientButton(String text, Color startColor, Color endColor) {
        super(text);
        this.startColor = startColor;
        this.endColor = endColor;
        setContentAreaFilled(false); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        GradientPaint gp = new GradientPaint(0, 0, startColor, 0, getHeight(), endColor);
        g2.setPaint(gp);
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
        
        super.paintComponent(g); 
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        // Hapus border default
    }
}