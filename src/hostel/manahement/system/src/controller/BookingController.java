package hostel.manahement.system.src.controller;

import hostel.manahement.system.src.dao.BookingDAO;
import hostel.manahement.system.src.dao.GuestDAO;
import hostel.manahement.system.src.dao.PaymentDAO;
import hostel.manahement.system.src.dao.RoomDAO;
import hostel.manahement.system.src.exception.RoomNotAvailableException;
import hostel.manahement.system.src.model.Booking;
import hostel.manahement.system.src.model.Guest;
import hostel.manahement.system.src.model.Payment;
import hostel.manahement.system.src.model.Room;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * BookingController handles all business logic for the check-in / check-out
 * workflow and delegates persistence to the DAO layer.
 */
public class BookingController {

    private final BookingDAO bookingDAO = new BookingDAO();
    private final GuestDAO guestDAO = new GuestDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();

    /**
     * Full check-in flow:
     *  1. Save (or reuse) the guest record.
     *  2. Calculate the total amount based on room price × nights.
     *  3. Create the booking.
     *  4. Mark the room as Occupied.
     *
     * @param guest       Guest details (may be new or returning)
     * @param roomIndex   Index into the list returned by RoomDAO.getAvailableRooms()
     * @param checkIn     Check-in date
     * @param checkOut    Check-out date
     * @throws RoomNotAvailableException if no available rooms exist at roomIndex
     */
    public void processCheckIn(Guest guest, int roomIndex,
                               LocalDate checkIn, LocalDate checkOut)
            throws RoomNotAvailableException {

        List<Room> available = roomDAO.getAvailableRooms();
        if (available.isEmpty() || roomIndex < 0 || roomIndex >= available.size()) {
            throw new RoomNotAvailableException("Selected room is no longer available.");
        }
        Room room = available.get(roomIndex);

        // Save guest (or retrieve existing by NIC)
        Guest existing = guestDAO.getGuestByNIC(guest.getNic());
        if (existing != null) {
            guest.setGuestId(existing.getGuestId());
        } else {
            guestDAO.addGuest(guest);  // sets guest.guestId via generated key
        }

        // Calculate cost
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        double total = nights * room.getPricePerNight();

        // Create booking
        Booking booking = new Booking();
        booking.setGuestId(guest.getGuestId());
        booking.setRoomId(room.getRoomId());
        booking.setCheckInDate(checkIn);
        booking.setCheckOutDate(checkOut);
        booking.setTotalAmount(total);
        booking.setStatus("Active");
        bookingDAO.addBooking(booking);

        // Mark room occupied
        roomDAO.updateRoomStatus(room.getRoomId(), "Occupied");
    }

    /**
     * Full check-out flow:
     *  1. Record payment.
     *  2. Mark the booking as Completed.
     *  3. Mark the room as Available again.
     *
     * @param bookingId     ID of the active booking to close.
     * @param amountPaid    Amount paid by the guest.
     * @param paymentMethod "Cash", "Card", or "Online".
     */
    public void processCheckOut(int bookingId, double amountPaid, String paymentMethod) {
        // Record payment
        Payment payment = new Payment();
        payment.setBookingId(bookingId);
        payment.setAmountPaid(amountPaid);
        payment.setPaymentMethod(paymentMethod);
        paymentDAO.addPayment(payment);

        // Close booking
        bookingDAO.updateBookingStatus(bookingId, "Completed");

        // Free room — look up which room this booking held
        Booking b = bookingDAO.getBookingById(bookingId);
        if (b != null) {
            roomDAO.updateRoomStatus(b.getRoomId(), "Available");
        }
    }

    public List<Booking> getActiveBookings() {
        return bookingDAO.getAllActiveBookings();
    }

    /**
     * Computes the estimated total for display before check-in is confirmed.
     */
    public double calculateTotal(int roomIndex, LocalDate checkIn, LocalDate checkOut) {
        List<Room> available = roomDAO.getAvailableRooms();
        if (available.isEmpty() || roomIndex < 0 || roomIndex >= available.size()) return 0;
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        return nights * available.get(roomIndex).getPricePerNight();
    }
}
