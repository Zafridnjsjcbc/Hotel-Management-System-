package hostel.manahement.system.src.view;

import hostel.manahement.system.src.controller.RoomController;
import hostel.manahement.system.src.model.Room;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RoomManagement extends JFrame {

    private JTable tblRooms;
    private DefaultTableModel tableModel;
    private JTextField txtRoomNumber, txtPrice;
    private JComboBox<String> cmbType, cmbStatus;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    private JLabel lblStatus;

    private RoomController roomController;
    private List<Room> rooms;

    public RoomManagement() {
        roomController = new RoomController();
        initUI();
        loadRooms();
    }

    private void initUI() {
        setTitle("Room Management");
        setSize(850, 560);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(0x2980B9));
        JLabel title = new JLabel("ROOM MANAGEMENT");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Room No.", "Type", "Price/Night (LKR)", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblRooms = new JTable(tableModel);
        tblRooms.setRowHeight(26);
        tblRooms.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblRooms.getTableHeader().setBackground(new Color(0x2C3E50));
        tblRooms.getTableHeader().setForeground(Color.WHITE);
        tblRooms.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblRooms.getSelectionModel().addListSelectionListener(e -> onRowSelected());
        add(new JScrollPane(tblRooms), BorderLayout.CENTER);

        // Form panel
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Room Details"));
        form.setPreferredSize(new Dimension(260, 0));
        form.setBackground(Color.WHITE);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 8, 6, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridx = 0; gc.gridy = 0;

        txtRoomNumber = new JTextField(12);
        txtPrice = new JTextField(12);
        cmbType = new JComboBox<>(new String[]{"Single", "Double", "Suite", "Deluxe"});
        cmbStatus = new JComboBox<>(new String[]{"Available", "Occupied", "Maintenance"});

        addFormRow(form, gc, "Room Number:", txtRoomNumber);
        addFormRow(form, gc, "Type:", cmbType);
        addFormRow(form, gc, "Price/Night:", txtPrice);
        addFormRow(form, gc, "Status:", cmbStatus);

        gc.gridx = 0; gc.gridwidth = 2; gc.gridy++;
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 6, 6));
        btnPanel.setBackground(Color.WHITE);

        btnAdd    = createBtn("➕ Add",    new Color(0x27AE60));
        btnUpdate = createBtn("✏ Update",  new Color(0x2980B9));
        btnDelete = createBtn("🗑 Delete",  new Color(0xE74C3C));
        btnClear  = createBtn("✖ Clear",   new Color(0x95A5A6));

        btnPanel.add(btnAdd); btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete); btnPanel.add(btnClear);
        form.add(btnPanel, gc);

        gc.gridy++;
        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        form.add(lblStatus, gc);

        add(form, BorderLayout.EAST);

        btnAdd.addActionListener(e -> handleAdd());
        btnUpdate.addActionListener(e -> handleUpdate());
        btnDelete.addActionListener(e -> handleDelete());
        btnClear.addActionListener(e -> clearForm());

        setVisible(true);
    }

    private void addFormRow(JPanel p, GridBagConstraints gc, String label, JComponent field) {
        gc.gridx = 0; gc.gridwidth = 1;
        p.add(new JLabel(label), gc);
        gc.gridx = 1;
        p.add(field, gc);
        gc.gridy++;
    }

    private JButton createBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return b;
    }

    private void loadRooms() {
        tableModel.setRowCount(0);
        rooms = roomController.getAllRooms();
        for (Room r : rooms) {
            tableModel.addRow(new Object[]{
                r.getRoomId(), r.getRoomNumber(), r.getRoomType(),
                String.format("%.2f", r.getPricePerNight()), r.getStatus()
            });
        }
    }

    private void onRowSelected() {
        int row = tblRooms.getSelectedRow();
        if (row < 0 || row >= rooms.size()) return;
        Room r = rooms.get(row);
        txtRoomNumber.setText(r.getRoomNumber());
        txtPrice.setText(String.valueOf(r.getPricePerNight()));
        cmbType.setSelectedItem(r.getRoomType());
        cmbStatus.setSelectedItem(r.getStatus());
        lblStatus.setText("Selected: Room " + r.getRoomNumber());
    }

    private void handleAdd() {
        if (!validateForm()) return;
        boolean ok = roomController.addRoom(
            txtRoomNumber.getText().trim(),
            (String) cmbType.getSelectedItem(),
            Double.parseDouble(txtPrice.getText().trim())
        );
        showResult(ok, "Room added successfully.", "Failed to add room.");
        if (ok) { loadRooms(); clearForm(); }
    }

    private void handleUpdate() {
        int row = tblRooms.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a room to update."); return; }
        if (!validateForm()) return;
        Room r = rooms.get(row);
        r.setRoomNumber(txtRoomNumber.getText().trim());
        r.setRoomType((String) cmbType.getSelectedItem());
        r.setPricePerNight(Double.parseDouble(txtPrice.getText().trim()));
        r.setStatus((String) cmbStatus.getSelectedItem());
        boolean ok = roomController.updateRoom(r);
        showResult(ok, "Room updated.", "Failed to update room.");
        if (ok) { loadRooms(); clearForm(); }
    }

    private void handleDelete() {
        int row = tblRooms.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a room to delete."); return; }
        Room r = rooms.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete Room " + r.getRoomNumber() + "? This cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = roomController.deleteRoom(r.getRoomId());
            showResult(ok, "Room deleted.", "Cannot delete — room may have bookings.");
            if (ok) { loadRooms(); clearForm(); }
        }
    }

    private boolean validateForm() {
        if (txtRoomNumber.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Room number is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            double price = Double.parseDouble(txtPrice.getText().trim());
            if (price <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter a valid price.", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void clearForm() {
        txtRoomNumber.setText(""); txtPrice.setText("");
        cmbType.setSelectedIndex(0); cmbStatus.setSelectedIndex(0);
        tblRooms.clearSelection();
        lblStatus.setText(" ");
    }

    private void showResult(boolean ok, String success, String fail) {
        if (ok) {
            JOptionPane.showMessageDialog(this, success, "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, fail, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
