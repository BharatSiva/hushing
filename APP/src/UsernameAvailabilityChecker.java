import java.util.*;

public class UsernameAvailabilityChecker {

    // Stores username -> userId
    private HashMap<String, Integer> userDatabase;

    // Stores username -> number of attempts
    private HashMap<String, Integer> attemptFrequency;

    public UsernameAvailabilityChecker() {
        userDatabase = new HashMap<>();
        attemptFrequency = new HashMap<>();

        // Sample existing users
        userDatabase.put("john_doe", 1);
        userDatabase.put("admin", 2);
        userDatabase.put("user123", 3);
    }

    // Check if username is available
    public boolean checkAvailability(String username) {

        // Track attempts
        attemptFrequency.put(username,
                attemptFrequency.getOrDefault(username, 0) + 1);

        // O(1) lookup
        return !userDatabase.containsKey(username);
    }

    // Suggest alternative usernames
    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            String suggestion = username + i;

            if (!userDatabase.containsKey(suggestion)) {
                suggestions.add(suggestion);
            }
        }

        // replace underscore with dot
        String alt = username.replace("_", ".");
        if (!userDatabase.containsKey(alt)) {
            suggestions.add(alt);
        }

        return suggestions;
    }

    // Register a new user
    public void registerUser(String username, int userId) {
        if (checkAvailability(username)) {
            userDatabase.put(username, userId);
            System.out.println(username + " registered successfully!");
        } else {
            System.out.println("Username already taken!");
        }
    }

    // Get most attempted username
    public String getMostAttempted() {

        String mostAttempted = null;
        int maxAttempts = 0;

        for (Map.Entry<String, Integer> entry : attemptFrequency.entrySet()) {
            if (entry.getValue() > maxAttempts) {
                maxAttempts = entry.getValue();
                mostAttempted = entry.getKey();
            }
        }

        return mostAttempted + " (" + maxAttempts + " attempts)";
    }

    public static void main(String[] args) {

        UsernameAvailabilityChecker system = new UsernameAvailabilityChecker();

        System.out.println("Check john_doe: " + system.checkAvailability("john_doe"));
        System.out.println("Check jane_smith: " + system.checkAvailability("jane_smith"));

        System.out.println("\nSuggestions for john_doe:");
        System.out.println(system.suggestAlternatives("john_doe"));

        system.checkAvailability("admin");
        system.checkAvailability("admin");
        system.checkAvailability("admin");

        System.out.println("\nMost Attempted Username: " + system.getMostAttempted());
    }
}