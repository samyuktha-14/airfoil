import java.util.*;

public class User {
    private String username;
    private String password;
    private String email;
    private Date registrationDate;
    private Stack<FlightParameters> searchHistory; 

    
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.registrationDate = new Date();
        this.searchHistory = new Stack<>();
    }

    
    public String getUsername() { 
        return username; 
    }
    
    public String getPassword() { 
        return password; 
    }
    
    public String getEmail() { 
        return email; 
    }
    
    public Date getRegistrationDate() { 
        return registrationDate; 
    }

    public void addSearchHistory(FlightParameters params) {
        searchHistory.push(params);
    }
    
    
    public Stack<FlightParameters> getSearchHistory() {
        return searchHistory;
    }

    @Override
    public String toString() {
        return "User: " + username + " (Email: " + email + ")";
    }
}