package hostel.manahement.system.src.dao;
import hostel.manahement.system.src.model.Booking;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {
    private Connection conn = DBConnection.getInstance().getConnection();

    public boolean addBooking(Booking booking) {
        String sql = "INSERT INTO bookings (guest_id, room_id, check_in_date, check_out_date, total_amount, status) " +
                     "VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, booking.getGuestId());
            ps.setInt(2, booking.getRoomId());
            ps.setDate(3, Date.valueOf(booking.getCheckInDate()));
            ps.setDate(4, Date.valueOf(booking.getCheckOutDate()));
            ps.setDouble(5, booking.getTotalAmount());
            ps.setString(6, booking.getStatus());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    booking.setBookingId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateBookingStatus(int bookingId, String status) {
        String sql = "UPDATE bookings SET status=? WHERE booking_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Booking getBookingById(int bookingId) {
        String sql = "SELECT b.*, g.full_name AS guest_name, r.room_number, r.room_type " +
                     "FROM bookings b " +
                     "JOIN guests g ON b.guest_id = g.guest_id " +
                     "JOIN rooms r ON b.room_id = r.room_id " +
                     "WHERE b.booking_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Booking> getAllActiveBookings() {
        return getBookingsByStatus("Active");
    }

    public List<Booking> getAllBookings() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, g.full_name AS guest_name, r.room_number, r.room_type " +
                     "FROM bookings b " +
                     "JOIN guests g ON b.guest_id = g.guest_id " +
                     "JOIN rooms r ON b.room_id = r.room_id " +
                     "ORDER BY b.booking_date DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private List<Booking> getBookingsByStatus(String status) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, g.full_name AS guest_name, r.room_number, r.room_type " +
                     "FROM bookings b " +
                     "JOIN guests g ON b.guest_id = g.guest_id " +
                     "JOIN rooms r ON b.room_id = r.room_id " +
                     "WHERE b.status = ? " +
                     "ORDER BY b.check_in_date";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Booking mapRow(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setBookingId(rs.getInt("booking_id"));
        b.setGuestId(rs.getInt("guest_id"));
        b.setRoomId(rs.getInt("room_id"));
        b.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
        b.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());
        b.setTotalAmount(rs.getDouble("total_amount"));
        b.setStatus(rs.getString("status"));
        b.setBookingDate(rs.getString("booking_date"));
        // Joined display fields (may be null if queried without join)
        try { b.setGuestName(rs.getString("guest_name")); } catch (SQLException ignored) {}
        try { b.setRoomNumber(rs.getString("room_number")); } catch (SQLException ignored) {}
        try { b.setRoomType(rs.getString("room_type")); } catch (SQLException ignored) {}
        return b;
    }
}
