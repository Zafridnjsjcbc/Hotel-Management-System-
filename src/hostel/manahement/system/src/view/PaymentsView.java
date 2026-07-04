package hostel.manahement.system.src.view;

import hostel.manahement.system.src.dao.PaymentDAO;
import hostel.manahement.system.src.model.Payment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PaymentsView extends JFrame {

    private JTable tblPayments;
    private DefaultTableModel tableModel;
    private JLabel lblTotalRevenue;
    private JButton btnRefresh;

    private PaymentDAO paymentDAO;

    public PaymentsView() {
        paymentDAO = new PaymentDAO();
        initUI();
        loadPayments();
    }

    private void initUI() {
        setTitle("Payment Records");
        setSize(850, 550);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0x1F618D));
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel title = new JLabel("💳  PAYMENT RECORDS");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        btnRefresh = new JButton("↻ Refresh");
        btnRefresh.setBackground(new Color(0x154360));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);

        header.add(title, BorderLayout.WEST);
        header.add(btnRefresh, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Table
        String[] cols = {"Pay#", "Booking#", "Guest Name", "Room", "Amount Paid (LKR)", "Method", "Payment Date"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblPayments = new JTable(tableModel);
        tblPayments.setRowHeight(26);
        tblPayments.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblPayments.getTableHeader().setBackground(new Color(0x2C3E50));
        tblPayments.getTableHeader().setForeground(Color.WHITE);
        tblPayments.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblPayments.setSelectionBackground(new Color(0xD6EAF8));

        // Column widths
        tblPayments.getColumnModel().getColumn(0).setPreferredWidth(45);
        tblPayments.getColumnModel().getColumn(1).setPreferredWidth(65);
        tblPayments.getColumnModel().getColumn(2).setPreferredWidth(180);
        tblPayments.getColumnModel().getColumn(3).setPreferredWidth(70);
        tblPayments.getColumnModel().getColumn(4).setPreferredWidth(150);
        tblPayments.getColumnModel().getColumn(5).setPreferredWidth(80);
        tblPayments.getColumnModel().getColumn(6).setPreferredWidth(180);

        add(new JScrollPane(tblPayments), BorderLayout.CENTER);

        // Footer — total revenue
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        footer.setBackground(new Color(0xEAF2FF));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xAED6F1)));

        lblTotalRevenue = new JLabel("Total Revenue: LKR 0.00");
        lblTotalRevenue.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTotalRevenue.setForeground(new Color(0x1A5276));
        footer.add(lblTotalRevenue);
        add(footer, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> loadPayments());

        setVisible(true);
    }

    private void loadPayments() {
        tableModel.setRowCount(0);
        List<Payment> payments = paymentDAO.getAllPayments();

        for (Payment p : payments) {
            tableModel.addRow(new Object[]{
                p.getPaymentId(),
                p.getBookingId(),
                p.getGuestName()   != null ? p.getGuestName()   : "—",
                p.getRoomNumber()  != null ? p.getRoomNumber()  : "—",
                String.format("%.2f", p.getAmountPaid()),
                p.getPaymentMethod(),
                p.getPaymentDate() != null ? p.getPaymentDate().toString().substring(0, 19) : "—"
            });
        }

        double total = paymentDAO.getTotalRevenue();
        lblTotalRevenue.setText("Total Revenue: LKR " + String.format("%,.2f", total));
    }
}
