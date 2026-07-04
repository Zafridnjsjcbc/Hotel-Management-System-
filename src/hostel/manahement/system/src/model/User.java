package hostel.manahement.system.src.model;

public class User {
    private int userId;
    private String username;
    private String password;
    private String fullName;
    private String role;
    private boolean isActive;

    public User() {}

    public User(int userId, String username, String fullName, String role) {
        this.userId   = userId;
        this.username = username;
        this.fullName = fullName;
        this.role     = role;
    }

    public int getUserId()            { return userId; }
    public void setUserId(int id)     { this.userId = id; }

    public String getUsername()             { return username; }
    public void setUsername(String u)       { this.username = u; }

    public String getPassword()             { return password; }
    public void setPassword(String p)       { this.password = p; }

    public String getFullName()             { return fullName; }
    public void setFullName(String n)       { this.fullName = n; }

    public String getRole()                 { return role; }
    public void setRole(String r)           { this.role = r; }

    public boolean isActive()               { return isActive; }
    public void setActive(boolean a)        { this.isActive = a; }
}
