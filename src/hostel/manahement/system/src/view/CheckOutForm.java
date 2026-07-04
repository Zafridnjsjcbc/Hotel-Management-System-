package hostel.manahement.system.src.view;

import hostel.manahement.system.src.controller.BookingController;
import hostel.manahement.system.src.model.Booking;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CheckOutForm extends JFrame {

    private JTable tblBookings;
    private DefaultTableModel tableModel;
    private JComboBox<String> cmbPaymentMethod;
    private JTextField txtAmountPaid;
    private JLabel lblTotal, lblGuest, lblRoom, lblNights;
    private JButton btnCheckOut, btnRefresh;

    private BookingController bookingController;
    private List<Booking> activeBookings;

    public CheckOutForm() {
        bookingController = new BookingController();
        initUI();
        loadActiveBookings();
    }

    private void initUI() {
        setTitle("Hotel Check-Out");
        setSize(800, 550);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(0xC0392B));
        JLabel title = new JLabel("GUEST CHECK-OUT");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Table of active bookings
        String[] cols = {"Booking ID", "Guest Name", "Room", "Type", "Check-In", "Check-Out", "Amount (LKR)", "Nights"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblBookings = new JTable(tableModel);
        tblBookings.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblBookings.setRowHeight(25);
        tblBookings.getTableHeader().setBackground(new Color(0x2C3E50));
        tblBookings.getTableHeader().setForeground(Color.WHITE);
        tblBookings.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblBookings.getSelectionModel().addListSelectionListener(e -> onRowSelected());

        JScrollPane scroll = new JScrollPane(tblBookings);
        scroll.setPreferredSize(new Dimension(780, 280));
        add(scroll, BorderLayout.CENTER);

        // Bottom checkout panel
        JPanel bottom = new JPanel(new GridLayout(2, 1));
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        bottom.setBackground(Color.WHITE);

        // Info row
        JPanel info = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        info.setBackground(new Color(0xF0F0F0));
        lblGuest = new JLabel("Guest: —");
        lblRoom  = new JLabel("Room: —");
        lblNights = new JLabel("Nights: —");
        lblTotal = new JLabel("Total: LKR 0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotal.setForeground(new Color(0xC0392B));
        info.add(lblGuest);
        info.add(lblRoom);
        info.add(lblNights);
        info.add(lblTotal);

        // Action row
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        actions.setBackground(Color.WHITE);
        txtAmountPaid = new JTextField(10);
        cmbPaymentMethod = new JComboBox<>(new String[]{"Cash", "Card", "Online"});
        btnCheckOut = new JButton("✖ Confirm Check-Out");
        btnRefresh  = new JButton("↻ Refresh");

        btnCheckOut.setBackground(new Color(0xC0392B));
        btnCheckOut.setForeground(Color.WHITE);
        btnCheckOut.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRefresh.setBackground(new Color(0x7F8C8D));
        btnRefresh.setForeground(Color.WHITE);

        actions.add(new JLabel("Amount Paid (LKR):"));
        actions.add(txtAmountPaid);
        actions.add(new JLabel("Payment Method:"));
        actions.add(cmbPaymentMethod);
        actions.add(btnCheckOut);
        actions.add(btnRefresh);

        bottom.add(info);
        bottom.add(actions);
        add(bottom, BorderLayout.SOUTH);

        btnCheckOut.addActionListener(e -> handleCheckOut());
        btnRefresh.addActionListener(e -> loadActiveBookings());

        setVisible(true);
    }

    private void loadActiveBookings() {
        tableModel.setRowCount(0);
        activeBookings = bookingController.getActiveBookings();
        for (Booking b : activeBookings) {
            tableModel.addRow(new Object[]{
                b.getBookingId(),
                b.getGuestName(),
                b.getRoomNumber(),
                b.getRoomType(),
                b.getCheckInDate().toString(),
                b.getCheckOutDate().toString(),
                String.format("%.2f", b.getTotalAmount()),
                b.getNights()
            });
        }
        clearInfo();
    }

    private void onRowSelected() {
        int row = tblBookings.getSelectedRow();
        if (row < 0 || row >= activeBookings.size()) return;
        Booking b = activeBookings.get(row);
        lblGuest.setText("Guest: " + b.getGuestName());
        lblRoom.setText("Room: " + b.getRoomNumber());
        lblNights.setText("Nights: " + b.getNights());
        lblTotal.setText("Total: LKR " + String.format("%.2f", b.getTotalAmount()));
        txtAmountPaid.setText(String.format("%.2f", b.getTotalAmount()));
    }

    private void handleCheckOut() {
        int row = tblBookings.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Booking b = activeBookings.get(row);

        double amount;
        try {
            amount = Double.parseDouble(txtAmountPaid.getText().trim());
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.", "Invalid Amount", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String method = (String) cmbPaymentMethod.getSelectedItem();

        int confirm = JOptionPane.showConfirmDialog(this,
            "Check out " + b.getGuestName() + " from Room " + b.getRoomNumber() + "?\n" +
            "Amount: LKR " + String.format("%.2f", amount) + "  |  Method: " + method,
            "Confirm Check-Out", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            bookingController.processCheckOut(b.getBookingId(), amount, method);
            JOptionPane.showMessageDialog(this, "Check-Out successful! Room " + b.getRoomNumber() + " is now available.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            loadActiveBookings();
        }
    }

    private void clearInfo() {
        lblGuest.setText("Guest: —");
        lblRoom.setText("Room: —");
        lblNights.setText("Nights: —");
        lblTotal.setText("Total: LKR 0.00");
        txtAmountPaid.setText("");
    }
}
