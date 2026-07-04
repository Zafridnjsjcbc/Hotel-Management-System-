/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hostel.manahement.system.src.util;

import exception.InvalidDateException;
import java.time.LocalDate;

public class Validator {

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^[0-9]{10}$");
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    public static boolean isValidNIC(String nic) {
        return nic != null && (nic.matches("^[0-9]{9}[VvXx]$") || nic.matches("^[0-9]{12}$"));
    }

    public static void validateDates(LocalDate checkIn, LocalDate checkOut) throws InvalidDateException {
        if (checkIn == null || checkOut == null) {
            throw new InvalidDateException("Dates cannot be null.");
        }
        if (!checkIn.isBefore(checkOut)) {
            throw new InvalidDateException("Check-out date must be after check-in date.");
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw new InvalidDateException("Check-in date cannot be in the past.");
        }
    }
}
