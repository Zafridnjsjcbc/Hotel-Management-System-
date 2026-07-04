package hostel.manahement.system.src.util;

import hostel.manahement.system.src.model.User;

/**
 * Singleton session holder.
 * After successful login, store the user here so any class can access it.
 *
 * Usage:
 *   Session.getInstance().setCurrentUser(user);   // on login
 *   Session.getInstance().getCurrentUser();        // anywhere in the app
 *   Session.getInstance().clear();                 // on logout
 */
public class Session {

    private static Session instance;
    private User currentUser;

    private Session() {}

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public User getCurrentUser()          { return currentUser; }
    public void setCurrentUser(User u)    { this.currentUser = u; }

    public boolean isAdmin() {
        return currentUser != null && "Admin".equals(currentUser.getRole());
    }

    public void clear() {
        currentUser = null;
    }
}
