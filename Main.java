import java.util.*;


public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static AuthenticationService authService = new AuthenticationService();
    private static AirfoilService airfoilService = new AirfoilService();
    private static User currentUser = null;

    public static void main(String[] args) {
        displayWelcomeBanner();
        
        

        boolean running = true;
        while (running) {
            if (currentUser == null) {
                running = showLoginMenu();
            } else {
                showMainMenu();
            }
        }

        scanner.close();
        System.out.println("\nThank you for using Airfoil Selection System!");
    }

    private static void displayWelcomeBanner() {
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║   AIRFOIL SELECTION SYSTEM                 ║");
        System.out.println("║                                            ║");
        System.out.println("╚════════════════════════════════════════════╝\n");
    }

    private static boolean showLoginMenu() {
        System.out.println("\n=== AUTHENTICATION ===");
        System.out.println("1. Sign Up");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Choose option: ");

        int choice = InputHelper.getIntInput(scanner);

        switch (choice) {
            case 1:
                signUp();
                break;
            case 2:
                login();
                break;
            case 3:
                return false;
            default:
                System.out.println("Invalid option!");
        }
        return true;
    }

  public static void signUp() {
    System.out.println("\n=== SIGN UP ===");
    String username;
    while (true) {
        System.out.println("Username: At least 4 characters*, only underscores*, no spaces*, must be unique.\n");
        System.out.print("Enter username: ");
        username = scanner.nextLine().trim();
        // Validate username only
        String usernameError = null;
        if (username == null || username.trim().isEmpty()) {
            usernameError = "Username cannot be empty.";
        } else if (username.contains(" ")) {
            usernameError = "Username cannot contain spaces.";
        } else if (!username.matches("^[a-zA-Z0-9_]{4,}$")) {
            usernameError = "Username must be at least 4 characters and contain only letters, digits, or underscores.";
        } else if (authService.userExists(username)) { 
            usernameError = "Username already exists.";
        }
        if (usernameError == null) break;
        System.out.println("\nInvalid username: " + usernameError);
    }

    
    String password;
    while (true) {
        System.out.println("Password: At least 8 characters, must include uppercase, lowercase, digit, and special character.");
        System.out.print("Enter password: ");
        password = scanner.nextLine().trim();
        String passwordError = authService.validatePassword(password);
        if (passwordError == null) break;
        System.out.println("Invalid password: " + passwordError);
    }

    System.out.println("Email: Must be a valid email address (e.g., user@example.com), must be unique.");
    System.out.print("Enter email: ");
    String email = scanner.nextLine().trim();

    // Validate email and password together
    String validationError = authService.validateSignup(username, password, email);
    if (validationError != null) {
        System.out.println("Signup failed: " + validationError);
        return;
    }

    boolean success = authService.signUp(username, password, email);
    if (success) {
        System.out.println("Signup successful! Please login.");
    } else {
        System.out.println("Signup failed: Username already exists.");
    }
}
    private static void login() {
        System.out.println("\n=== LOGIN ===");
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        currentUser = authService.login(username, password);
        if (currentUser != null) {
            System.out.println("✓ Login successful! Welcome, " + currentUser.getUsername());
        } else {
            System.out.println("✗ Invalid credentials!");
        }
    }
    public static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            System.out.println("Unable to clear console.");
        }
    }

    public static void showMainMenu() {
       
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. Find Best Airfoil");
        System.out.println("2. Browse All Airfoils");
        System.out.println("3. Compare Airfoils");
        System.out.println("4. View Search History");
        System.out.println("5. Educational Info");
        System.out.println("6. Add New Airfoil"); 
        System.out.println("7. Logout");
        System.out.print("Choose option: ");

        int choice = InputHelper.getIntInput(scanner);

        switch (choice) {
            case 1:
                clearConsole();
                MenuHandler.findBestAirfoil(scanner, airfoilService, currentUser);
                break;
            case 2:
                clearConsole();
                MenuHandler.browseAirfoils(airfoilService);
                break;
            case 3:
                clearConsole();
                MenuHandler.compareAirfoils(scanner, airfoilService);
                break;
            case 4:
                clearConsole();
                MenuHandler.viewSearchHistory(currentUser);
                break;
            case 5:
                clearConsole();
                MenuHandler.showEducationalInfo();
                break;
            case 6:
                clearConsole();

                MenuHandler.addNewAirfoil(scanner, airfoilService); 
            break;
            case 7:
                   clearConsole();
                currentUser = null;
                System.out.println("Logged out successfully!");
                break;
            default:
                System.out.println("Invalid option!");
        }
    }
}