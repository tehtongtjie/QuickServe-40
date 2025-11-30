package auth;

import app.Main;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import models.User;

public class LoginPanel extends JPanel {

    // ============================================================
    // 1. FIELD UTAMA & KONSTANTA
    // ============================================================
    private JFrame mainFrame;
    private CardLayout formCardLayout = new CardLayout();
    private JPanel formContainer = new JPanel(formCardLayout);

    // Login
    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;
    private JButton loginTabButton;

    // Register
    private JTextField regNamaField;
    private JTextField regEmailField;
    private JPasswordField regPasswordField;
    private JButton registerTabButton;
    private JButton daftarButton;

    private JButton loginSubmitButton;

    private static final int CONTENT_WIDTH = 400;
    private static final int CONTENT_HEIGHT = 550;
    private static final int FIELD_WIDTH = 300;
    private static final Color ORANGE_COLOR = new Color(255, 100, 0);
    private static final Color DARK_ORANGE_COLOR = new Color(255, 50, 0);

    
    // ============================================================
    // 2. KONSTRUKTOR UTAMA
    // ============================================================
    public LoginPanel(JFrame parentFrame) {
        this.mainFrame = parentFrame;

        setLayout(new GridBagLayout());

        JPanel contentArea = createRoundedContentArea();
        contentArea.setLayout(new BoxLayout(contentArea, BoxLayout.Y_AXIS));
        contentArea.setBackground(Color.WHITE);
        contentArea.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Logo
        JLabel logo = loadLogo();
        contentArea.add(logo);

        // Title + Subtitle
        contentArea.add(makeTitle());
        contentArea.add(makeSubtitle());

        // Tab Login/Register
        JPanel tabPanel = createTabPanel();
        contentArea.add(tabPanel);

        // Form Container (CardLayout)
        formContainer.setOpaque(false);
        formContainer.add(createLoginForm(), "LOGIN");
        formContainer.add(createRegisterForm(), "REGISTER");
        formContainer.setMaximumSize(new Dimension(CONTENT_WIDTH - 80, 450));

        formCardLayout.show(formContainer, "REGISTER");

        contentArea.add(Box.createVerticalStrut(10));
        contentArea.add(formContainer);

        add(contentArea);
    }


    // ============================================================
    // 3. BACKGROUND GRADIENT PANEL
    // ============================================================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        GradientPaint gp = new GradientPaint(
            0, 0, ORANGE_COLOR,
            getWidth(), getHeight(), DARK_ORANGE_COLOR
        );

        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
    }


    // ============================================================
    // 4. BUAT AREA KONTEN DENGAN ROUND CORNER
    // ============================================================
    private JPanel createRoundedContentArea() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(CONTENT_WIDTH, CONTENT_HEIGHT);
            }
        };
    }


    // ============================================================
    // 5. LOAD LOGO
    // ============================================================
    private JLabel loadLogo() {
        JLabel logo;
        try {
            ImageIcon icon = new ImageIcon("Assets/download.jpg");

            if (icon.getIconWidth() > 0) {
                Image scaled = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                logo = new JLabel(new ImageIcon(scaled));
            } else throw new Exception();

        } catch (Exception e) {
            logo = new JLabel("LOGO");
            logo.setFont(new Font("Arial", Font.BOLD, 20));
            logo.setForeground(Color.DARK_GRAY);
        }
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        return logo;
    }


    // ============================================================
    // 6. TITLE & SUBTITLE
    // ============================================================
    private JLabel makeTitle() {
        JLabel title = new JLabel("FoodOrder", SwingConstants.CENTER);
        title.setFont(new Font("Montserrat", Font.BOLD, 24));
        title.setForeground(ORANGE_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        return title;
    }

    private JLabel makeSubtitle() {
        JLabel subtitle = new JLabel("Pesan makanan favoritmu dengan mudah", SwingConstants.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        return subtitle;
    }


    // ============================================================
    // 7. TAB LOGIN / REGISTER (BUTTON SWITCH)
    // ============================================================
    private JPanel createTabPanel() {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setOpaque(false);

        JPanel panel = new JPanel(new GridLayout(1, 2, 0, 0));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(FIELD_WIDTH, 45));

        loginTabButton = new JButton("Login");
        registerTabButton = new JButton("Register");

        styleTabButton(loginTabButton, false, true);
        styleTabButton(registerTabButton, true, false);

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

        panel.add(loginTabButton);
        panel.add(registerTabButton);
        wrapper.add(panel);

        return wrapper;
    }


    // ============================================================
    // 8. STYLE TAB BUTTON
    // ============================================================
    private void styleTabButton(JButton btn, boolean isActive, boolean isLeft) {
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.PLAIN, 14));

        Color textColor = isActive ? Color.BLACK : Color.GRAY.darker();
        btn.setForeground(textColor);

        Color bgColor = isActive ? new Color(225, 240, 255) : new Color(245, 245, 245);
        btn.setBackground(bgColor);

        btn.setBorder(new TabRoundedBorder(
            isActive ? ORANGE_COLOR : new Color(220, 220, 220),
            1, 8, isLeft, isActive
        ));
    }


    // ============================================================
    // 9. LOGIN FORM
    // ============================================================
    private JPanel createLoginForm() {
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        loginUsernameField = new JTextField();
        loginPasswordField = new JPasswordField();

        styleTextField(loginUsernameField, "nama@email.com");
        styleTextField(loginPasswordField, "Password");

        loginSubmitButton = new CustomGradientButton("LOGIN SEKARANG", ORANGE_COLOR, DARK_ORANGE_COLOR);
        styleSubmitButton(loginSubmitButton);

        loginSubmitButton.addActionListener(e -> attemptLogin());

        addLoginComponents(form);
        return form;
    }


    // ============================================================
    // 10. REGISTER FORM
    // ============================================================
    private JPanel createRegisterForm() {
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        regNamaField = new JTextField();
        regEmailField = new JTextField();
        regPasswordField = new JPasswordField();

        styleTextField(regNamaField, "Nama Lengkap");
        styleTextField(regEmailField, "nama@email.com");
        styleTextField(regPasswordField, "Password");

        daftarButton = new CustomGradientButton("DAFTAR SEKARANG", ORANGE_COLOR, DARK_ORANGE_COLOR);
        styleSubmitButton(daftarButton);

        daftarButton.addActionListener(e -> attemptRegister());

        addRegisterComponents(form);
        return form;
    }


    // ============================================================
    // 11. STYLE TEXTFIELD + PLACEHOLDER
    // ============================================================
    private void styleTextField(JTextField field, String placeholder) {
        field.setMaximumSize(new Dimension(FIELD_WIDTH, 48));
        field.setForeground(new Color(160, 160, 160));
        field.setFont(new Font("SansSerif", Font.ITALIC, 14));
        field.setText(placeholder);

        field.setBorder(new RoundedBorder(new Color(210, 210, 210), 1, 12, true));

        // Focus listener → placeholder logic
        field.addFocusListener(new java.awt.event.FocusAdapter() {

            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(new Color(40, 40, 40));
                    field.setFont(new Font("SansSerif", Font.PLAIN, 14));
                }

                if (field instanceof JPasswordField) {
                    ((JPasswordField) field).setEchoChar('•');
                }

                field.setBorder(new RoundedBorder(ORANGE_COLOR, 2, 12, true));
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                boolean empty = (field instanceof JPasswordField)
                    ? new String(((JPasswordField) field).getPassword()).isEmpty()
                    : field.getText().trim().isEmpty();

                if (empty) {
                    field.setText(placeholder);
                    field.setForeground(new Color(160, 160, 160));
                    field.setFont(new Font("SansSerif", Font.ITALIC, 14));

                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar((char) 0);
                    }
                }

                field.setBorder(new RoundedBorder(new Color(210, 210, 210), 1, 12, true));
            }
        });
    }


    // ============================================================
    // 12. STYLE BUTTON SUBMIT
    // ============================================================
    private void styleSubmitButton(JButton btn) {
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Montserrat", Font.BOLD, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }


    // ============================================================
    // 13. LOGIN LOGIC
    // ============================================================
    private void attemptLogin() {
        String user = loginUsernameField.getText();
        String pass = new String(loginPasswordField.getPassword());

        if (user.contains("@") == false || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Isi semua kolom dengan benar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User loggedIn = ((Main) mainFrame).getDbHelper().login(user, pass);

        if (loggedIn != null) {
            JOptionPane.showMessageDialog(this, "Login Berhasil!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            ((Main) mainFrame).setLoggedInUser(loggedIn);
            ((Main) mainFrame).showPanel(Main.HOME_VIEW);
        } else {
            JOptionPane.showMessageDialog(this, "Username atau Password salah.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    // ============================================================
    // 14. REGISTER LOGIC
    // ============================================================
    private void attemptRegister() {
        String nama = regNamaField.getText();
        String email = regEmailField.getText();
        String pass = new String(regPasswordField.getPassword());

        if (nama.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua kolom harus diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean ok = ((Main) mainFrame).getDbHelper().register(nama, email, pass);

        if (ok) {
            JOptionPane.showMessageDialog(this, "Registrasi Berhasil!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loginTabButton.doClick();
        } else {
            JOptionPane.showMessageDialog(this, "Email sudah terdaftar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    // ============================================================
    // 15. CLEAR FIELDS
    // ============================================================
    private void clearLoginFields() {
        loginUsernameField.setText("nama@email.com");
        loginPasswordField.setText("Password");
    }

    private void clearRegisterFields() {
        regNamaField.setText("Nama Lengkap");
        regEmailField.setText("nama@email.com");
        regPasswordField.setText("Password");
    }


    // ============================================================
    // 16. SUB KOMPONEN (UNTUK LOGIN/REGISTER FORM)
    // ============================================================
    private void addLoginComponents(JPanel form) {
        form.add(new JLabel("Email:"));
        form.add(loginUsernameField);
        form.add(Box.createVerticalStrut(10));

        form.add(new JLabel("Password:"));
        form.add(loginPasswordField);
        form.add(Box.createVerticalStrut(20));

        form.add(loginSubmitButton);
    }

    private void addRegisterComponents(JPanel form) {
        form.add(new JLabel("Nama Lengkap:"));
        form.add(regNamaField);
        form.add(Box.createVerticalStrut(10));

        form.add(new JLabel("Email:"));
        form.add(regEmailField);
        form.add(Box.createVerticalStrut(10));

        form.add(new JLabel("Password:"));
        form.add(regPasswordField);
        form.add(Box.createVerticalStrut(20));

        form.add(daftarButton);
    }

    // ============================================================
    // 17. Helper Class: RoundedBorder
    // ============================================================
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
            return new Insets(6, 6, 6, 6);
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

    // ============================================================
    // 18. Helper Class: TabRoundedBorder
    // ============================================================
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
            g2.draw(new RoundRectangle2D.Double(x, y, width - thickness, height - thickness, arc, arc));

            g2.dispose();
        }
    }

    // ============================================================
    // 20. Helper Class: CustomGradientButton
    // ============================================================
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
        protected void paintBorder(Graphics g) { }
    }
    }
