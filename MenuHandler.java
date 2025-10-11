import java.util.*;
public class MenuHandler {
    public static void findBestAirfoil(Scanner scanner, AirfoilService airfoilService, User currentUser) {
        System.out.println("\n=== AIRFOIL RECOMMENDATION ===");
        
        System.out.print("Enter flight speed (m/s): ");
        double speed = InputHelper.getValidSpeed(scanner);
        
        System.out.print("Enter altitude (meters): ");
        double altitude = InputHelper.getValidAltitude(scanner);
        
        System.out.print("Enter wing area (m²): ");
        double wingArea = InputHelper.getValidWingArea(scanner);
        
        System.out.print("Enter required lift (N): ");
        double requiredLift = InputHelper.getValidLift(scanner);

        FlightParameters params = new FlightParameters(speed, altitude, wingArea, requiredLift);
        currentUser.addSearchHistory(params);

    
List<RecommendationResult> results = airfoilService.recommendAirfoils(params);


System.out.println("\n╔════════════════════════════════════════════════════════════╗");
System.out.println("║              TOP RECOMMENDED AIRFOILS                      ║");
System.out.println("╚════════════════════════════════════════════════════════════╝");

for (int i = 0; i < 5; i++) {
    RecommendationResult result = results.get(i);
    System.out.println("\n" + (i + 1) + ". " + result.getAirfoil().getName());
    System.out.println("   Fitness Score: " + String.format("%.2f", result.getScore()));
    System.out.println("   " + result.getAirfoil().getDetails());
    System.out.println("   Reason: " + result.getReason());
}
    }

    public static void browseAirfoils(AirfoilService airfoilService) {
    List<Airfoil> airfoils = airfoilService.getAllAirfoils();
    if (airfoils.isEmpty()) {
        System.out.println("No airfoils available.");
        return;
    }

    String border = "+-------------+--------+--------+--------------+--------------+--------------------+-----------+";
    System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════════════════════════╗");
    System.out.println("║                                  AVAILABLE AIRFOILS                                              ║");
    System.out.println("╚══════════════════════════════════════════════════════════════════════════════════════════════════╝");
    System.out.println(border);
    System.out.printf("| %-11s | %-6s | %-6s | %-12s | %-12s | %-18s | %-9s |\n",
            "Airfoil", "Max Cl", "Min Cd", "Reynolds Min", "Reynolds Max", "Application Type", "Thickness");
    System.out.println(border);
    for (Airfoil a : airfoils) {
        System.out.printf("| %-11s | %6.2f | %6.3f | %12.1f | %12.1f | %-18s | %9.2f |\n",
                a.getName(), a.getMaxCl(), a.getMinCd(), a.getReynoldsMin(), a.getReynoldsMax(), a.getApplicationType(), a.getThickness());
    }
    System.out.println(border);
}

    
    public static void compareAirfoils(Scanner scanner, AirfoilService airfoilService) {
    List<Airfoil> airfoils = airfoilService.getAllAirfoils();
    if (airfoils.size() < 2) {
        System.out.println("Not enough airfoils to compare.");
        return;
    }

    
    System.out.println("\nAvailable Airfoils:");
    for (int i = 0; i < airfoils.size(); i++) {
        System.out.println(String.format("%2d. %s", i + 1, airfoils.get(i).getName()));
    }

    System.out.print("Select first airfoil (number): ");
    int idx1 = InputHelper.getIntInput(scanner) - 1;
    System.out.print("Select second airfoil (number): ");
    int idx2 = InputHelper.getIntInput(scanner) - 1;

    if (idx1 < 0 || idx1 >= airfoils.size() || idx2 < 0 || idx2 >= airfoils.size() || idx1 == idx2) {
        System.out.println("Invalid selection.");
        return;
    }

    Airfoil a1 = airfoils.get(idx1);
    Airfoil a2 = airfoils.get(idx2);

    String border = "+-------------+--------+--------+--------------+--------------+--------------------+-----------+";
    System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════════════════════════╗");
    System.out.println("║                                  AIRFOIL COMPARISON                                            ║");
    System.out.println("╚══════════════════════════════════════════════════════════════════════════════════════════════════╝");
    System.out.println(border);
    System.out.printf("| %-11s | %-6s | %-6s | %-12s | %-12s | %-18s | %-9s |\n",
            "Airfoil", "Max Cl", "Min Cd", "Reynolds Min", "Reynolds Max", "Application Type", "Thickness");
    System.out.println(border);
    for (Airfoil a : new Airfoil[]{a1, a2}) {
        System.out.printf("| %-11s | %6.2f | %6.3f | %12.1f | %12.1f | %-18s | %9.2f |\n",
                a.getName(), a.getMaxCl(), a.getMinCd(), a.getReynoldsMin(), a.getReynoldsMax(), a.getApplicationType(), a.getThickness());
    }
    System.out.println(border);
}
    
    public static void viewSearchHistory(User currentUser) {
        System.out.println("\n=== SEARCH HISTORY ===");
        Stack<FlightParameters> history = currentUser.getSearchHistory();
        
        if (history.isEmpty()) {
            System.out.println("No search history yet.");
            return;
        }

        
        Stack<FlightParameters> temp = new Stack<>();
        int count = 1;
        
        System.out.println("Total searches: " + history.size() + "\n");
        
        while (!history.isEmpty()) {
            FlightParameters params = history.pop();
            temp.push(params);
            System.out.println(count++ + ". " + params);
        }
        
        
        while (!temp.isEmpty()) {
            history.push(temp.pop());
        }
    }
    
public static void addNewAirfoil(Scanner scanner, AirfoilService airfoilService) {
    System.out.println("\n=== ADD NEW AIRFOIL ===");
    System.out.print("Enter airfoil name: ");
    String name = scanner.nextLine().trim();

    System.out.print("Enter maximum lift coefficient (Cl): ");
    double maxCl = InputHelper.getDoubleInput(scanner);

    System.out.print("Enter minimum drag coefficient (Cd): ");
    double minCd = InputHelper.getDoubleInput(scanner);

    System.out.print("Enter minimum Reynolds number: ");
    double reynoldsMin = InputHelper.getDoubleInput(scanner);

    System.out.print("Enter maximum Reynolds number: ");
    double reynoldsMax = InputHelper.getDoubleInput(scanner);

    System.out.print("Enter application type (e.g., Low Speed, High Speed): ");
    String applicationType = scanner.nextLine().trim();

    System.out.print("Enter thickness ratio (e.g., 0.12 for 12%): ");
    double thickness = InputHelper.getDoubleInput(scanner);

    Airfoil newAirfoil = new Airfoil(
        name, maxCl, minCd, reynoldsMin, reynoldsMax, applicationType, thickness
    );
    boolean added = airfoilService.addAirfoil(newAirfoil);

    if (added) {
        System.out.println("✓ Airfoil added successfully!");
    } else {
        System.out.println("✗ Airfoil with this name already exists in the database!");
    }
}

    public static void showEducationalInfo() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              AIRFOIL BASICS - EDUCATIONAL GUIDE            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        
        System.out.println("\n1. LIFT COEFFICIENT (Cl):");
        System.out.println("   - Measures how much lift an airfoil generates");
        System.out.println("   - Higher Cl = more lift at same speed");
        System.out.println("   - Important for takeoff and low-speed flight");
        
        System.out.println("\n2. DRAG COEFFICIENT (Cd):");
        System.out.println("   - Measures resistance to motion");
        System.out.println("   - Lower Cd = less drag = more efficient");
        System.out.println("   - Critical for cruise efficiency");
        
        System.out.println("\n3. LIFT-TO-DRAG RATIO (L/D):");
        System.out.println("   - Cl/Cd ratio indicates overall efficiency");
        System.out.println("   - Higher L/D = better performance");
        System.out.println("   - Gliders need high L/D (>30), fighters need high Cl");
        
        System.out.println("\n4. REYNOLDS NUMBER (Re):");
        System.out.println("   - Indicates flow characteristics");
        System.out.println("   - Re = (velocity × chord) / kinematic viscosity");
        System.out.println("   - Different airfoils work best at different Re ranges");
        
        System.out.println("\n5. APPLICATION TYPES:");
        System.out.println("   - Low Speed: High Cl, used in gliders, trainers");
        System.out.println("   - High Speed: Low drag, used in jets, racers");
        System.out.println("   - General Purpose: Balanced Cl/Cd for diverse use");
        
        System.out.println("\n6. REQUIRED LIFT:");
        System.out.println("   - Force needed to counteract aircraft weight");
        System.out.println("   - Required Lift = Mass × 9.8 m/s²");
        System.out.println("   - Example: 5 kg drone needs 49 N of lift");
    }
    
}