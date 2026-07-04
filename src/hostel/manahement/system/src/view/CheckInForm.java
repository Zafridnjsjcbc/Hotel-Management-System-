/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hostel.manahement.system.src.view;


import hostel.manahement.system.src.controller.BookingController;
import hostel.manahement.system.src.dao.GuestDAO;
import hostel.manahement.system.src.dao.RoomDAO;
import exception.InvalidDateException;
import hostel.manahement.system.src.exception.RoomNotAvailableException;
import hostel.manahement.system.src.model.Guest;
import hostel.manahement.system.src.model.Room;
import hostel.manahement.system.src.util.Validator;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class CheckInForm extends JFrame {

    private JTextField txtName, txtNIC, txtPhone, txtEmail;
    private JComboBox<String> cmbRooms;
    private JSpinner spinCheckIn, spinCheckOut;
    private JButton btnCheckIn, btnClear;
    private JLabel lblTotal;

    private BookingController bookingController;
    private RoomDAO roomDAO;
    private GuestDAO guestDAO;

    public CheckInForm() {
        bookingController = new BookingController();
        roomDAO = new RoomDAO();
        guestDAO = new GuestDAO();
        initUI();
    }

    private void initUI() {
        setTitle("Hotel Check-In");
        setSize(600, 500);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(0x2C3E50));
        JLabel title = new JLabel("GUEST CHECK-IN");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridLayout(9, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        form.setBackground(Color.WHITE);

        txtName = new JTextField(); txtNIC = new JTextField();
        txtPhone = new JTextField(); txtEmail = new JTextField();
        cmbRooms = new JComboBox<>();
        spinCheckIn = new JSpinner(new SpinnerDateModel());
        spinCheckOut = new JSpinner(new SpinnerDateModel());
        lblTotal = new JLabel("LKR 0.00");
        btnCheckIn = new JButton("✔ Check In");
        btnClear = new JButton("✖ Clear");

        btnCheckIn.setBackground(new Color(0x27AE60));
        btnCheckIn.setForeground(Color.WHITE);
        btnClear.setBackground(new Color(0xE74C3C));
        btnClear.setForeground(Color.WHITE);

        loadAvailableRooms();

        form.add(new JLabel("Guest Name:")); form.add(txtName);
        form.add(new JLabel("NIC:")); form.add(txtNIC);
        form.add(new JLabel("Phone:")); form.add(txtPhone);
        form.add(new JLabel("Email:")); form.add(txtEmail);
        form.add(new JLabel("Room:")); form.add(cmbRooms);
        form.add(new JLabel("Check-In Date:")); form.add(spinCheckIn);
        form.add(new JLabel("Check-Out Date:")); form.add(spinCheckOut);
        form.add(new JLabel("Estimated Total:")); form.add(lblTotal);
        form.add(btnClear); form.add(btnCheckIn);

        add(form, BorderLayout.CENTER);

        btnCheckIn.addActionListener(e -> handleCheckIn());
        btnClear.addActionListener(e -> clearFields());

        setVisible(true);
    }

    private void loadAvailableRooms() {
        cmbRooms.removeAllItems();
        List<Room> rooms = roomDAO.getAvailableRooms();
        for (Room r : rooms) {
            cmbRooms.addItem(r.getRoomNumber() + " - " + r.getRoomType() + " (LKR " + r.getPricePerNight() + ")");
        }
    }

    private void handleCheckIn() {
        try {
            // Validation
            if (txtName.getText().trim().isEmpty()) throw new Exception("Guest name is required.");
            if (!Validator.isValidNIC(txtNIC.getText().trim())) throw new Exception("Invalid NIC format.");
            if (!Validator.isValidPhone(txtPhone.getText().trim())) throw new Exception("Invalid phone number.");

            // Date validation
            java.util.Date ciDate = (java.util.Date) spinCheckIn.getValue();
            java.util.Date coDate = (java.util.Date) spinCheckOut.getValue();
            LocalDate checkIn = ciDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            LocalDate checkOut = coDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            Validator.validateDates(checkIn, checkOut);

            // Room selection
            if (cmbRooms.getSelectedItem() == null) throw new RoomNotAvailableException("No available rooms.");

            // Create Guest
            Guest guest = new Guest();
            guest.setFullName(txtName.getText().trim());
            guest.setNic(txtNIC.getText().trim());
            guest.setPhone(txtPhone.getText().trim());
            guest.setEmail(txtEmail.getText().trim());

            // Process booking
            bookingController.processCheckIn(guest, cmbRooms.getSelectedIndex(), checkIn, checkOut);

            JOptionPane.showMessageDialog(this, "Check-In Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            loadAvailableRooms();

        } catch (RoomNotAvailableException ex) {
            JOptionPane.showMessageDialog(this, "Room Error: " + ex.getMessage(), "Room Error", JOptionPane.ERROR_MESSAGE);
        } catch (InvalidDateException ex) {
            JOptionPane.showMessageDialog(this, "Date Error: " + ex.getMessage(), "Date Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearFields() {
        txtName.setText(""); txtNIC.setText("");
        txtPhone.setText(""); txtEmail.setText("");
    }
}
