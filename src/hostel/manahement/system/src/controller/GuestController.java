package hostel.manahement.system.src.controller;

import hostel.manahement.system.src.dao.GuestDAO;
import hostel.manahement.system.src.model.Guest;

import java.util.List;

public class GuestController {
    private final GuestDAO guestDAO = new GuestDAO();

    public boolean addGuest(Guest guest) {
        return guestDAO.addGuest(guest);
    }

    public boolean updateGuest(Guest guest) {
        return guestDAO.updateGuest(guest);
    }

    public boolean deleteGuest(int guestId) {
        return guestDAO.deleteGuest(guestId);
    }

    public List<Guest> getAllGuests() {
        return guestDAO.getAllGuests();
    }

    public List<Guest> searchGuests(String keyword) {
        return guestDAO.searchGuests(keyword);
    }
}
