import java.io.*;
import java.util.*;

public class AuthenticationService {
    private static final String USER_DB = "users.csv";
    private TreeMap<String, User> users = new TreeMap<>();

    public AuthenticationService() {
        loadUsers();
    }

    private void loadUsers() {
        users.clear();
        File file = new File(USER_DB);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String username = parts[0];
                    String password = parts[1];
                    String email = parts[2];
                    users.put(username, new User(username, password, email));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
    }

    private void saveUsers() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(USER_DB))) {
            for (User user : users.values()) {
                pw.println(user.getUsername() + "," + user.getPassword() + "," + user.getEmail());
            }
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    public boolean signUp(String username, String password, String email) {
        if (users.containsKey(username)) {
            return false; // Username already exists
        }
        User user = new User(username, password, email);
        users.put(username, user);
        saveUsers();
        return true;
    }

    public User login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    public String validatePassword(String password) {
        if (password == null || password.length() < 8) {
            return "Password must be at least 8 characters.";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter.";
        }
        if (!password.matches(".*[a-z].*")) {
            return "Password must contain at least one lowercase letter.";
        }
        if (!password.matches(".*\\d.*")) {
            return "Password must contain at least one digit.";
        }
        if (!password.matches(".*[!@#$%^&*()_+=\\[\\]{};':\"\\\\|,.<>/?-].*")) {
            return "Password must contain at least one special character.";
        }
        return null;
    }

    public String validateSignup(String username, String password, String email) {
        if (username == null || username.trim().isEmpty()) {
            return "Username cannot be empty.";
        }
        if (username.contains(" ")) {
            return "Username cannot contain spaces.";
        }
        if (!username.matches("^[a-zA-Z0-9_]{4,}$")) {
            return "Username must be at least 4 characters and contain only letters, digits, or underscores.";
        }
        if (users.containsKey(username)) {
            return "Username already exists.";
        }
        // Password validation
        String passwordError = validatePassword(password);
        if (passwordError != null) {
            return passwordError;
        }
        if (email == null || !email.contains("@") || !email.contains(".")) {
            return "Invalid email address.";
        }
        for (User user : users.values()) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return "Email already registered.";
            }
        }
        return null;
    }
}
