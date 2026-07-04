package hostel.manahement.system.src.model;

public class Guest {
    private int guestId;
    private String fullName;
    private String nic;
    private String email;
    private String phone;
    private String address;

    public Guest() {}

    public Guest(int guestId, String fullName, String nic, String email, String phone, String address) {
        this.guestId = guestId;
        this.fullName = fullName;
        this.nic = nic;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    public int getGuestId() { return guestId; }
    public void setGuestId(int guestId) { this.guestId = guestId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() {
        return fullName + " (" + nic + ")";
    }
}
