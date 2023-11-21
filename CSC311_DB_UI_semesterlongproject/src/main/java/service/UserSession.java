package service;

import java.util.prefs.Preferences;

public class UserSession {

    // volatile keyword ensures changes to this variable are visible to all threads
    private static volatile UserSession instance;

    private String userName;
    private String password; // Consider using a secure token instead
    private String privileges;

    private UserSession(String userName, String password, String privileges) {
        this.userName = userName;
        this.password = password;
        this.privileges = privileges;
        storeSessionData();
    }

    private void storeSessionData() {
        Preferences userPreferences = Preferences.userRoot();
        userPreferences.put("USERNAME", userName);
        userPreferences.put("PASSWORD", password); // Consider security implications
        userPreferences.put("PRIVILEGES", privileges);
    }

    // Double-checked locking for thread safety in singleton
    public static UserSession getInstance(String userName, String password, String privileges) {
        if (instance == null) {
            // Synchronized block to remove multi-threading issues
            synchronized (UserSession.class) {
                // Double-checking singleton instance
                if (instance == null) {
                    instance = new UserSession(userName, password, privileges);
                }
            }
        }
        return instance;
    }

    public static UserSession getInstance(String userName, String password) {
        return getInstance(userName, password, "NONE");
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getPrivileges() {
        return privileges;
    }

    public void cleanUserSession() {
        Preferences userPreferences = Preferences.userRoot();
        userPreferences.remove("USERNAME");
        userPreferences.remove("PASSWORD");
        userPreferences.remove("PRIVILEGES");
        // Synchronized block to ensure thread safety when resetting instance
        synchronized (UserSession.class) {
            instance = null;
        }
    }

    @Override
    public String toString() {
        return "UserSession{" +
                "userName='" + userName + '\'' +
                ", privileges=" + privileges +
                '}';
    }
}