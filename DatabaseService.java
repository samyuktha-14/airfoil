import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.HashMap;
import java.util.Map;

public class DatabaseService {
    private static final Logger LOGGER = Logger.getLogger(DatabaseService.class.getName());
    private final Connection connection;
    
    // Cache for storing authenticated users (username -> User)
    private final Map<String, User> userCache = new HashMap<>();

    public DatabaseService() {
        Connection conn = null;
        try {
            // Load the SQLite JDBC driver (requires the driver jar on the classpath)
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException e) {
                String error = "\n✗ FATAL ERROR: SQLite JDBC driver not found!\n" +
                    "This program requires the SQLite JDBC driver to run.\n\n" +
                    "To fix this:\n" +
                    "1. Make sure you're in the project folder (c:\\project\\airfoil1)\n" +
                    "2. Verify sqlite-jdbc-3.42.0.0.jar exists in the folder\n" +
                    "3. Run the program with the jar on the classpath:\n" +
                    "   java -cp \".;sqlite-jdbc-3.42.0.0.jar\" Main";
                LOGGER.severe(error);
                throw new RuntimeException(error, e);
            }

            // Connect to the database
            try {
                conn = DriverManager.getConnection("jdbc:sqlite:airfoils.db");
                initializeDatabaseTables(conn);
                this.connection = conn;
                LOGGER.info("✓ Database connected successfully.");
            } catch (SQLException e) {
                String error = "\n✗ FATAL ERROR: Could not connect to database!\n" +
                    "Error: " + e.getMessage() + "\n\n" +
                    "To fix this:\n" +
                    "1. Make sure airfoils.db exists in " + System.getProperty("user.dir") + "\n" +
                    "2. Make sure you have write permissions in the folder\n" +
                    "3. Try deleting airfoils.db and let the program recreate it";
                LOGGER.severe(error);
                throw new RuntimeException(error, e);
            }
        } catch (Exception e) {
            String error = "\n✗ FATAL ERROR: Unexpected database error!\n" +
                "Error: " + e.getMessage();
            LOGGER.log(Level.SEVERE, error, e);
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException se) {
                    LOGGER.log(Level.WARNING, "Failed to close connection", se);
                }
            }
            throw new RuntimeException(error, e);
        }
    }

    // ----- User Authentication Methods -----
    public User authenticateUser(String username, String password) {
        // Check cache first
        if (username != null) {
            User cachedUser = userCache.get(username.toLowerCase());
            if (cachedUser != null) {
                System.out.println("Debug: Found user in cache: " + username);
                return cachedUser;
            }
        }
        
        // Input validation and normalization
        String inputUser = (username == null) ? "" : username.trim().toLowerCase();
        String inputPassword = (password == null) ? "" : password;

        // SQL query using parameterized query for security
        String sql = "SELECT id, username, email, password FROM users WHERE lower(username) = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, inputUser);
            
            // Debug log before executing query
            System.out.println("Debug: Checking credentials for user (normalized): '" + inputUser + "'");
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    
                    // Debug log for password length (without revealing actual passwords)
                    System.out.println("Debug: storedPassword.length=" + 
                        (storedPassword == null ? 0 : storedPassword.length()) + 
                        ", inputPassword.length=" + inputPassword.length());
                    
                    // Compare passwords
                    if (inputPassword.equals(storedPassword)) {
                        // Authentication successful
                        int id = rs.getInt("id");
                        String email = rs.getString("email");
                        String storedUsername = rs.getString("username");
                        
                        // Create user and add to cache
                        User authenticatedUser = new User(id, storedUsername, email);
                        userCache.put(storedUsername.toLowerCase(), authenticatedUser);
                        
                        System.out.println("Debug: Authentication successful for user: " + storedUsername);
                        return authenticatedUser;
                    } else {
                        System.out.println("Debug: Password mismatch for user: " + inputUser);
                    }
                } else {
                    System.out.println("Debug: User not found: " + inputUser);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error during authentication", e);
        }
        
        return null;
    }

    // Clear user from cache (call this when user data changes)
    public void clearUserFromCache(String username) {
        if (username != null) {
            userCache.remove(username.toLowerCase());
        }
    }
    
    // ----- User Management Methods -----
    public boolean addUser(String username, String password, String email) {
        if (connection == null) {
            return false;
        }
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding user: " + username, e);
            return false;
        }
    }

    public boolean userExists(String username) {
        if (connection == null) {
            return false;
        }
        String inputUser = (username == null) ? "" : username.trim();
        String sql = "SELECT 1 FROM users WHERE lower(username) = lower(?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, inputUser);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if user exists: " + inputUser, e);
            return false;
        }
    }

    public boolean emailExists(String email) {
        if (connection == null) {
            return false;
        }
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if email exists: " + email, e);
            return false;
        }
    }

    // ----- Airfoil Methods -----
    public boolean addAirfoil(String name, double maxCl, double minCd, double reynoldsMin, double reynoldsMax, String applicationType, double thickness) {
        if (connection == null) {
            return false;
        }
        String sql = "INSERT INTO airfoils (name, maxCl, minCd, reynoldsMin, reynoldsMax, applicationType, thickness) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setDouble(2, maxCl);
            stmt.setDouble(3, minCd);
            stmt.setDouble(4, reynoldsMin);
            stmt.setDouble(5, reynoldsMax);
            stmt.setString(6, applicationType);
            stmt.setDouble(7, thickness);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding airfoil: " + name, e);
            return false;
        }
    }

    public List<Airfoil> getAllAirfoils() {
        List<Airfoil> airfoils = new ArrayList<>();
        if (connection == null) {
            return airfoils;
        }
        String sql = "SELECT * FROM airfoils";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Airfoil a = new Airfoil(
                    rs.getString("name"),
                    rs.getDouble("maxCl"),
                    rs.getDouble("minCd"),
                    rs.getDouble("reynoldsMin"),
                    rs.getDouble("reynoldsMax"),
                    rs.getString("applicationType"),
                    rs.getDouble("thickness")
                );
                airfoils.add(a);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all airfoils", e);
        }
        return airfoils;
    }

    public boolean airfoilExists(String name) {
        if (connection == null) {
            return false;
        }
        String sql = "SELECT 1 FROM airfoils WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if airfoil exists: " + name, e);
            return false;
        }
    }

    private static void initializeDatabaseTables(Connection conn) throws SQLException {
        // Create users table if it doesn't exist
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    email TEXT UNIQUE
                )
            """);

            // Create airfoils table if it doesn't exist
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS airfoils (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE,
                    maxCl REAL,
                    minCd REAL,
                    reynoldsMin REAL,
                    reynoldsMax REAL,
                    applicationType TEXT,
                    thickness REAL
                )
            """);
        }
    }
}