package hostel.manahement.system.src.view;

import hostel.manahement.system.src.dao.UserDAO;
import hostel.manahement.system.src.model.User;
import hostel.manahement.system.src.util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class LoginForm extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnClear;
    private JLabel lblError;
    private JCheckBox chkShow;

    private UserDAO userDAO = new UserDAO();

    public LoginForm() {
        initUI();
    }

    private void initUI() {
        setTitle("Hotel Management System — Login");
        setSize(420, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // ── Top banner ──────────────────────────────────────────
        JPanel banner = new JPanel(new GridLayout(3, 1));
        banner.setBackground(new Color(0x1A5276));
        banner.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblHotel = new JLabel("🏨 GRAND VISTA HOTEL", SwingConstants.CENTER);
        lblHotel.setForeground(Color.WHITE);
        lblHotel.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JLabel lblSystem = new JLabel("Management System", SwingConstants.CENTER);
        lblSystem.setForeground(new Color(0xAED6F1));
        lblSystem.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel lblWelcome = new JLabel("Please sign in to continue", SwingConstants.CENTER);
        lblWelcome.setForeground(new Color(0x85C1E9));
        lblWelcome.setFont(new Font("Segoe UI", Font.ITALIC, 11));

        banner.add(lblHotel);
        banner.add(lblSystem);
        banner.add(lblWelcome);
        add(banner, BorderLayout.NORTH);

        // ── Login form ──────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(30, 40, 20, 40));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(8, 0, 8, 0);
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;

        // Username
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        form.add(lblUser, gc);

        gc.gridy++;
        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtUsername.setPreferredSize(new Dimension(300, 38));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xAEB6BF), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        form.add(txtUsername, gc);

        // Password
        gc.gridy++;
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        form.add(lblPass, gc);

        gc.gridy++;
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.setPreferredSize(new Dimension(300, 38));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xAEB6BF), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        form.add(txtPassword, gc);

        // Show password checkbox
        gc.gridy++; gc.gridwidth = 2;
        chkShow = new JCheckBox("Show password");
        chkShow.setBackground(Color.WHITE);
        chkShow.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        chkShow.addActionListener(e ->
            txtPassword.setEchoChar(chkShow.isSelected() ? (char) 0 : '•')
        );
        form.add(chkShow, gc);

        // Error label
        gc.gridy++;
        lblError = new JLabel(" ");
        lblError.setForeground(new Color(0xC0392B));
        lblError.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblError.setHorizontalAlignment(SwingConstants.CENTER);
        form.add(lblError, gc);

        // Buttons
        gc.gridy++; gc.gridwidth = 1; gc.insets = new Insets(4, 0, 4, 5);
        btnLogin = new JButton("🔐  LOGIN");
        btnLogin.setBackground(new Color(0x1A5276));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(140, 40));
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        form.add(btnLogin, gc);

        gc.gridx = 1; gc.insets = new Insets(4, 5, 4, 0);
        btnClear = new JButton("✖  CLEAR");
        btnClear.setBackground(new Color(0x95A5A6));
        btnClear.setForeground(Color.WHITE);
        btnClear.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClear.setFocusPainted(false);
        btnClear.setPreferredSize(new Dimension(140, 40));
        btnClear.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        form.add(btnClear, gc);

        add(form, BorderLayout.CENTER);

        // ── Footer ──────────────────────────────────────────────
        JPanel footer = new JPanel();
        footer.setBackground(new Color(0xEAEAEA));
        JLabel lblFooter = new JLabel("Default: admin / admin123  |  staff / staff123");
        lblFooter.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        lblFooter.setForeground(Color.GRAY);
        footer.add(lblFooter);
        add(footer, BorderLayout.SOUTH);

        // ── Event listeners ──────────────────────────────────────
        btnLogin.addActionListener(e -> handleLogin());
        btnClear.addActionListener(e -> clearForm());

        // Allow pressing Enter to login
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) handleLogin();
            }
        });
        txtUsername.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) txtPassword.requestFocus();
            }
        });

        setVisible(true);
        txtUsername.requestFocus();
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Checking...");

        // Authenticate
        User user = userDAO.login(username, password);

        if (user != null) {
            // Save to session
            Session.getInstance().setCurrentUser(user);

            lblError.setForeground(new Color(0x27AE60));
            lblError.setText("Welcome, " + user.getFullName() + "! Loading...");

            // Small delay for UX then open dashboard
            Timer timer = new Timer(600, e -> {
                dispose();
                new MainDashboard();
            });
            timer.setRepeats(false);
            timer.start();

        } else {
            showError("Invalid username or password. Please try again.");
            txtPassword.setText("");
            btnLogin.setEnabled(true);
            btnLogin.setText("🔐  LOGIN");
            txtPassword.requestFocus();
        }
    }

    private void showError(String msg) {
        lblError.setForeground(new Color(0xC0392B));
        lblError.setText(msg);
    }

    private void clearForm() {
        txtUsername.setText("");
        txtPassword.setText("");
        lblError.setText(" ");
        chkShow.setSelected(false);
        txtPassword.setEchoChar('•');
        txtUsername.requestFocus();
    }
}
