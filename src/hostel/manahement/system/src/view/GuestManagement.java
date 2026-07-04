package hostel.manahement.system.src.view;

import hostel.manahement.system.src.controller.GuestController;
import hostel.manahement.system.src.model.Guest;
import hostel.manahement.system.src.util.Validator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GuestManagement extends JFrame {

    private JTable tblGuests;
    private DefaultTableModel tableModel;
    private JTextField txtName, txtNIC, txtPhone, txtEmail, txtAddress, txtSearch;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch;

    private GuestController guestController;
    private List<Guest> guests;

    public GuestManagement() {
        guestController = new GuestController();
        initUI();
        loadGuests();
    }

    private void initUI() {
        setTitle("Guest Management");
        setSize(1000, 580);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0x8E44AD));
        JLabel title = new JLabel("  GUEST MANAGEMENT");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.add(title, BorderLayout.WEST);

        // Search bar in header
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        searchPanel.setBackground(new Color(0x8E44AD));
        txtSearch = new JTextField(18);
        btnSearch = new JButton("🔍 Search");
        btnSearch.setBackground(new Color(0x6C3483));
        btnSearch.setForeground(Color.WHITE);
        searchPanel.add(new JLabel("Search:") {{ setForeground(Color.WHITE); }});
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        header.add(searchPanel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Full Name", "NIC", "Phone", "Email", "Address"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblGuests = new JTable(tableModel);
        tblGuests.setRowHeight(26);
        tblGuests.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblGuests.getTableHeader().setBackground(new Color(0x2C3E50));
        tblGuests.getTableHeader().setForeground(Color.WHITE);
        tblGuests.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblGuests.getSelectionModel().addListSelectionListener(e -> onRowSelected());
        add(new JScrollPane(tblGuests), BorderLayout.CENTER);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Guest Details"));
        form.setPreferredSize(new Dimension(280, 0));
        form.setBackground(Color.WHITE);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 8, 6, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridx = 0; gc.gridy = 0;

        txtName    = new JTextField(14);
        txtNIC     = new JTextField(14);
        txtPhone   = new JTextField(14);
        txtEmail   = new JTextField(14);
        txtAddress = new JTextField(14);

        addRow(form, gc, "Full Name:", txtName);
        addRow(form, gc, "NIC:", txtNIC);
        addRow(form, gc, "Phone:", txtPhone);
        addRow(form, gc, "Email:", txtEmail);
        addRow(form, gc, "Address:", txtAddress);

        gc.gridx = 0; gc.gridwidth = 2; gc.gridy++;
        JPanel btns = new JPanel(new GridLayout(2, 2, 6, 6));
        btns.setBackground(Color.WHITE);
        btnAdd    = mkBtn("➕ Add",    new Color(0x27AE60));
        btnUpdate = mkBtn("✏ Update",  new Color(0x2980B9));
        btnDelete = mkBtn("🗑 Delete",  new Color(0xE74C3C));
        btnClear  = mkBtn("✖ Clear",   new Color(0x95A5A6));
        btns.add(btnAdd); btns.add(btnUpdate);
        btns.add(btnDelete); btns.add(btnClear);
        form.add(btns, gc);
        add(form, BorderLayout.EAST);

        // Wire events
        btnAdd.addActionListener(e -> handleAdd());
        btnUpdate.addActionListener(e -> handleUpdate());
        btnDelete.addActionListener(e -> handleDelete());
        btnClear.addActionListener(e -> clearForm());
        btnSearch.addActionListener(e -> handleSearch());
        txtSearch.addActionListener(e -> handleSearch());

        setVisible(true);
    }

    private void addRow(JPanel p, GridBagConstraints gc, String label, JComponent field) {
        gc.gridx = 0; gc.gridwidth = 1;
        p.add(new JLabel(label), gc);
        gc.gridx = 1;
        p.add(field, gc);
        gc.gridy++;
    }

    private JButton mkBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return b;
    }

    private void loadGuests() {
        guests = guestController.getAllGuests();
        refreshTable(guests);
    }

    private void refreshTable(List<Guest> list) {
        tableModel.setRowCount(0);
        for (Guest g : list) {
            tableModel.addRow(new Object[]{
                g.getGuestId(), g.getFullName(), g.getNic(),
                g.getPhone(), g.getEmail(), g.getAddress()
            });
        }
        guests = list;
    }

    private void onRowSelected() {
        int row = tblGuests.getSelectedRow();
        if (row < 0 || row >= guests.size()) return;
        Guest g = guests.get(row);
        txtName.setText(g.getFullName());
        txtNIC.setText(g.getNic());
        txtPhone.setText(g.getPhone());
        txtEmail.setText(g.getEmail());
        txtAddress.setText(g.getAddress());
    }

    private void handleAdd() {
        if (!validateForm()) return;
        Guest g = buildGuest();
        boolean ok = guestController.addGuest(g);
        if (ok) { JOptionPane.showMessageDialog(this, "Guest added.", "Success", JOptionPane.INFORMATION_MESSAGE); loadGuests(); clearForm(); }
        else    JOptionPane.showMessageDialog(this, "Failed — NIC may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void handleUpdate() {
        int row = tblGuests.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a guest first."); return; }
        if (!validateForm()) return;
        Guest g = buildGuest();
        g.setGuestId(guests.get(row).getGuestId());
        boolean ok = guestController.updateGuest(g);
        if (ok) { JOptionPane.showMessageDialog(this, "Guest updated.", "Success", JOptionPane.INFORMATION_MESSAGE); loadGuests(); clearForm(); }
        else    JOptionPane.showMessageDialog(this, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void handleDelete() {
        int row = tblGuests.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a guest first."); return; }
        Guest g = guests.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete guest \"" + g.getFullName() + "\"?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = guestController.deleteGuest(g.getGuestId());
            if (ok) { JOptionPane.showMessageDialog(this, "Guest deleted."); loadGuests(); clearForm(); }
            else    JOptionPane.showMessageDialog(this, "Cannot delete — guest may have bookings.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSearch() {
        String kw = txtSearch.getText().trim();
        if (kw.isEmpty()) { loadGuests(); return; }
        refreshTable(guestController.searchGuests(kw));
    }

    private boolean validateForm() {
        if (txtName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required.", "Validation", JOptionPane.WARNING_MESSAGE); return false;
        }
        if (!Validator.isValidNIC(txtNIC.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Invalid NIC format.\nAccepted: 9 digits + V/X  or  12 digits.", "Validation", JOptionPane.WARNING_MESSAGE); return false;
        }
        if (!Validator.isValidPhone(txtPhone.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Phone must be 10 digits.", "Validation", JOptionPane.WARNING_MESSAGE); return false;
        }
        if (!txtEmail.getText().trim().isEmpty() && !Validator.isValidEmail(txtEmail.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Invalid email format.", "Validation", JOptionPane.WARNING_MESSAGE); return false;
        }
        return true;
    }

    private Guest buildGuest() {
        Guest g = new Guest();
        g.setFullName(txtName.getText().trim());
        g.setNic(txtNIC.getText().trim());
        g.setPhone(txtPhone.getText().trim());
        g.setEmail(txtEmail.getText().trim());
        g.setAddress(txtAddress.getText().trim());
        return g;
    }

    private void clearForm() {
        txtName.setText(""); txtNIC.setText(""); txtPhone.setText("");
        txtEmail.setText(""); txtAddress.setText(""); txtSearch.setText("");
        tblGuests.clearSelection();
        loadGuests();
    }
}
