package hostel.manahement.system.src.dao;
import hostel.manahement.system.src.model.Payment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {
    private Connection conn = DBConnection.getInstance().getConnection();

    public boolean addPayment(Payment payment) {
        String sql = "INSERT INTO payments (booking_id, amount_paid, payment_method) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, payment.getBookingId());
            ps.setDouble(2, payment.getAmountPaid());
            ps.setString(3, payment.getPaymentMethod());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Payment> getAllPayments() {
        List<Payment> list = new ArrayList<>();
        // Join with bookings and guests for display
        String sql = "SELECT p.*, g.full_name AS guest_name, r.room_number " +
                     "FROM payments p " +
                     "JOIN bookings b ON p.booking_id = b.booking_id " +
                     "JOIN guests g ON b.guest_id = g.guest_id " +
                     "JOIN rooms r ON b.room_id = r.room_id " +
                     "ORDER BY p.payment_date DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Payment p = new Payment(
                    rs.getInt("payment_id"),
                    rs.getInt("booking_id"),
                    rs.getDouble("amount_paid"),
                    rs.getString("payment_method"),
                    rs.getString("payment_date")
                );
                p.setGuestName(rs.getString("guest_name"));
                p.setRoomNumber(rs.getString("room_number"));
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public double getTotalRevenue() {
        String sql = "SELECT SUM(amount_paid) AS total FROM payments";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble("total");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public Payment getPaymentByBookingId(int bookingId) {
        String sql = "SELECT * FROM payments WHERE booking_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Payment(
                    rs.getInt("payment_id"),
                    rs.getInt("booking_id"),
                    rs.getDouble("amount_paid"),
                    rs.getString("payment_method"),
                    rs.getString("payment_date")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
