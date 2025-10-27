public class AuthenticationService {
    private DatabaseService dbService;

    public AuthenticationService() {
        dbService = new DatabaseService(); // Ensure connection is initialized in DatabaseService
    }

    // ----- Sign up -----
    public boolean signUp(String username, String password, String email) {
        // Check if username already exists
        if (dbService.userExists(username)) {
            System.out.println("Username already exists!");
            return false;
        }

        // Check if email already exists
        if (dbService.emailExists(email)) {
            System.out.println("Email already registered!");
            return false;
        }

        // Add user to database
        return dbService.addUser(username, password, email);
    }

    // ----- Login -----
    public User login(String username, String password) {
        // Authenticate user and get user information
        return dbService.authenticateUser(username, password);
    }

    // Check if username exists
    public boolean userExists(String username) {
        return dbService.userExists(username);
    }

    // ----- Password validation -----
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

    // ----- Full signup validation -----
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
        if (dbService.userExists(username)) {
            return "Username already exists.";
        }

        // Password validation
        String passwordError = validatePassword(password);
        if (passwordError != null) {
            return passwordError;
        }

        // Email validation
        if (email == null || !email.contains("@") || !email.contains(".")) {
            return "Invalid email address.";
        }
        if (dbService.emailExists(email)) {
            return "Email already registered.";
        }

        return null; // All validations passed
    }
}

