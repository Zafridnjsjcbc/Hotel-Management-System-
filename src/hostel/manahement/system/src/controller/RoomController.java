package hostel.manahement.system.src.controller;

import hostel.manahement.system.src.dao.RoomDAO;
import hostel.manahement.system.src.model.Room;

import java.util.List;

public class RoomController {
    private final RoomDAO roomDAO = new RoomDAO();

    public boolean addRoom(String number, String type, double price) {
        Room room = new Room(0, number, type, price, "Available");
        return roomDAO.addRoom(room);
    }

    public boolean updateRoom(Room room) {
        return roomDAO.updateRoom(room);
    }

    public boolean deleteRoom(int roomId) {
        return roomDAO.deleteRoom(roomId);
    }

    public List<Room> getAllRooms() {
        return roomDAO.getAllRooms();
    }

    public List<Room> getAvailableRooms() {
        return roomDAO.getAvailableRooms();
    }
}
