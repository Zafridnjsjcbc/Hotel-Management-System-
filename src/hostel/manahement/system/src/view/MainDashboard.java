/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hostel.manahement.system.src.view;


import hostel.manahement.system.src.dao.BookingDAO;
import hostel.manahement.system.src.dao.RoomDAO;
import hostel.manahement.system.src.util.Session;
import javax.swing.*;
import java.awt.*;

public class MainDashboard extends JFrame {
 
    public MainDashboard() {
        setTitle("Hotel Management System - Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }
 
    private void initUI() {
        setLayout(new BorderLayout());
 
        // ── Top bar with user info ───────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(0x1A5276));
        topBar.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
 
        JLabel lblTitle = new JLabel("🏨 GRAND VISTA HOTEL — Management System");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
 
        // Show logged-in user name and role
        String userInfo = "";
        if (Session.getInstance().getCurrentUser() != null) {
            userInfo = "👤 " + Session.getInstance().getCurrentUser().getFullName()
                     + "  |  " + Session.getInstance().getCurrentUser().getRole();
        }
        JLabel lblUser = new JLabel(userInfo);
        lblUser.setForeground(new Color(0xAED6F1));
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 12));
 
        topBar.add(lblTitle, BorderLayout.WEST);
        topBar.add(lblUser,  BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);
 
        // ── Sidebar menu ─────────────────────────────────────────
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(0x2C3E50));
        sidebar.setPreferredSize(new Dimension(200, 600));
        sidebar.setLayout(new GridLayout(9, 1, 5, 5));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
 
        String[] menuItems = {
            "🏠 Dashboard",
            "✔ Check In",
            "✖ Check Out",
            "🛏 Rooms",
            "👤 Guests",
            "💳 Payments",
            "📊 Reports",
            "🔒 Logout",
            "🚪 Exit"
        };
 
        for (String item : menuItems) {
            JButton btn = new JButton(item);
            btn.setForeground(Color.WHITE);
            btn.setBackground(new Color(0x34495E));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            // Highlight logout red
            if (item.contains("Logout")) btn.setBackground(new Color(0x922B21));
            btn.addActionListener(e -> handleMenu(item));
            sidebar.add(btn);
        }
 
        // ── Stats cards ──────────────────────────────────────────
        JPanel stats = new JPanel(new GridLayout(2, 2, 15, 15));
        stats.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        stats.setBackground(new Color(0xECF0F1));
 
        RoomDAO roomDAO = new RoomDAO();
        BookingDAO bookingDAO = new BookingDAO();
 
        int available = (int) roomDAO.getAllRooms().stream()
                .filter(r -> r.getStatus().equals("Available")).count();
        int occupied = (int) roomDAO.getAllRooms().stream()
                .filter(r -> r.getStatus().equals("Occupied")).count();
        int activeBookings = bookingDAO.getAllActiveBookings().size();
        int totalRooms = roomDAO.getAllRooms().size();
 
        stats.add(createCard("Available Rooms",  String.valueOf(available),     new Color(0x27AE60)));
        stats.add(createCard("Occupied Rooms",   String.valueOf(occupied),      new Color(0xE74C3C)));
        stats.add(createCard("Active Bookings",  String.valueOf(activeBookings),new Color(0x2980B9)));
        stats.add(createCard("Total Rooms",      String.valueOf(totalRooms),    new Color(0x8E44AD)));
 
        add(sidebar, BorderLayout.WEST);
        add(stats,   BorderLayout.CENTER);
        setVisible(true);
    }
 
    private JPanel createCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
 
        JLabel lTitle = new JLabel(title);
        lTitle.setForeground(Color.WHITE);
        lTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
 
        JLabel lValue = new JLabel(value);
        lValue.setForeground(Color.WHITE);
        lValue.setFont(new Font("Segoe UI", Font.BOLD, 40));
        lValue.setHorizontalAlignment(SwingConstants.CENTER);
 
        card.add(lTitle,  BorderLayout.NORTH);
        card.add(lValue,  BorderLayout.CENTER);
        return card;
    }
 
    private void handleMenu(String item) {
        switch (item) {
            case "✔ Check In":   new CheckInForm();       break;
            case "✖ Check Out":  new CheckOutForm();      break;
            case "🛏 Rooms":     new RoomManagement();    break;
            case "👤 Guests":    new GuestManagement();   break;
            case "💳 Payments":   new PaymentsView();     break;
            case "📊 Reports":   new ReportViewer();      break;
            case "🔒 Logout":    handleLogout();          break;
            case "🚪 Exit":      System.exit(0);          break;
        }
    }
 
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            Session.getInstance().clear();   // wipe session
            dispose();                        // close dashboard
            new LoginForm();                  // back to login
        }
    }
 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginForm::new);   // start at login, not dashboard
    }
}
