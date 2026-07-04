package hostel.manahement.system.src.dao;

import hostel.manahement.system.src.model.Guest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GuestDAO {
    private Connection conn = DBConnection.getInstance().getConnection();

    public boolean addGuest(Guest guest) {
        String sql = "INSERT INTO guests (full_name, nic, email, phone, address) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, guest.getFullName());
            ps.setString(2, guest.getNic());
            ps.setString(3, guest.getEmail());
            ps.setString(4, guest.getPhone());
            ps.setString(5, guest.getAddress());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    guest.setGuestId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateGuest(Guest guest) {
        String sql = "UPDATE guests SET full_name=?, nic=?, email=?, phone=?, address=? WHERE guest_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, guest.getFullName());
            ps.setString(2, guest.getNic());
            ps.setString(3, guest.getEmail());
            ps.setString(4, guest.getPhone());
            ps.setString(5, guest.getAddress());
            ps.setInt(6, guest.getGuestId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteGuest(int guestId) {
        String sql = "DELETE FROM guests WHERE guest_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, guestId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Guest> getAllGuests() {
        List<Guest> list = new ArrayList<>();
        String sql = "SELECT * FROM guests ORDER BY full_name";
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

    public Guest getGuestById(int id) {
        String sql = "SELECT * FROM guests WHERE guest_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Guest getGuestByNIC(String nic) {
        String sql = "SELECT * FROM guests WHERE nic = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nic);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Guest> searchGuests(String keyword) {
        List<Guest> list = new ArrayList<>();
        String sql = "SELECT * FROM guests WHERE full_name LIKE ? OR nic LIKE ? OR phone LIKE ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k);
            ps.setString(2, k);
            ps.setString(3, k);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Guest mapRow(ResultSet rs) throws SQLException {
        return new Guest(
            rs.getInt("guest_id"),
            rs.getString("full_name"),
            rs.getString("nic"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("address")
        );
    }
}
