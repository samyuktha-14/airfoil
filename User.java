import java.util.Stack;

public class User {
    private int id;
    private String username;
    private String email;
    private Stack<FlightParameters> searchHistory;

    public User(int id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.searchHistory = new Stack<>(); // initialize the stack
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }

    // Add a new search to the history
    public void addSearchHistory(FlightParameters params) {
        searchHistory.push(params);
    }

    // Retrieve the search history
    public Stack<FlightParameters> getSearchHistory() {
        return searchHistory;
    }

    // Optional: clear all history
    public void clearSearchHistory() {
        searchHistory.clear();
    }
}
