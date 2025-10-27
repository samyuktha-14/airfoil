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


String resultBorder = "╔══════════════════════════════════════════════════════════════════════════════════════════════════════╗";
String resultHeader = "║                                        TOP RECOMMENDED AIRFOILS                                      ║";
String resultFooter = "╚══════════════════════════════════════════════════════════════════════════════════════════════════════╝";
String separator =    "╠══════════════════════════════════════════════════════════════════════════════════════════════════════╣";

System.out.println("\n" + resultBorder);
System.out.println(resultHeader);
System.out.println(separator);

for (int i = 0; i < 5 && i < results.size(); i++) {
    RecommendationResult result = results.get(i);
    Airfoil airfoil = result.getAirfoil();
    
    // Airfoil name and score in one line
    System.out.printf("║ %d. %-85s║\n", (i + 1), airfoil.getName());
    System.out.printf("║     %-85s║\n", String.format("Fitness Score: %.2f", result.getScore()));
    
    // Technical specifications in a structured format
    System.out.printf("║    Type: %-80s║\n", airfoil.getApplicationType());
    System.out.printf("║    %-85s      ║\n", String.format("Max Cl: %.3f | Min Cd: %.4f | L/D: %.2f | Thickness: %.1f%%",
        airfoil.getMaxCl(), airfoil.getMinCd(), 
        airfoil.getMaxCl()/airfoil.getMinCd(), 
        airfoil.getThickness()));
    System.out.printf("║    %-85s ║\n", String.format("Re Range: %.1e - %.1e",
        airfoil.getReynoldsMin(), airfoil.getReynoldsMax()));
    
    // Reason with proper indentation
    System.out.printf("║    Reason: %-78s║\n", result.getReason());
    
    if (i < 4 && i < results.size() - 1) {
        System.out.println(separator);
    }
}
System.out.println(resultFooter);
    }

    public static void browseAirfoils(AirfoilService airfoilService) {
    List<Airfoil> airfoils = airfoilService.getAllAirfoils();
    if (airfoils.isEmpty()) {
        System.out.println("No airfoils available.");
        return;
    }

    String tableBorder = "╔═══════════════╦════════╦════════╦══════════════╦══════════════╦══════════════════════╦═══════════╗";
    String headerBorder = "╠═══════════════╬════════╬════════╬══════════════╬══════════════╬══════════════════════╬═══════════╣";
    String footerBorder = "╚═══════════════╩════════╩════════╩══════════════╩══════════════╩══════════════════════╩═══════════╝";

    System.out.println("\n" + tableBorder);
    System.out.printf("║ %-13s ║ %-6s ║ %-6s ║ %-12s ║ %-12s ║ %-20s ║ %-9s ║\n",
            "Airfoil", "Max Cl", "Min Cd", "Reynolds Min", "Reynolds Max", "Application Type", "Thickness");
    System.out.println(headerBorder);
    
    for (Airfoil a : airfoils) {
        System.out.printf("║ %-13s ║ %6.2f ║ %6.3f ║ %12.1f ║ %12.1f ║ %-20s ║ %9.2f ║\n",
                a.getName(), a.getMaxCl(), a.getMinCd(), a.getReynoldsMin(), a.getReynoldsMax(), a.getApplicationType(), a.getThickness());
    }
    System.out.println(footerBorder);
}

    
  public static void compareAirfoils(Scanner scanner, AirfoilService airfoilService) {
    List<Airfoil> airfoils = airfoilService.getAllAirfoils();
    if (airfoils.size() < 2) {
        System.out.println("Not enough airfoils to compare.");
        return;
    }

    System.out.println("\nAvailable Airfoils:");
    for (int i = 0; i < airfoils.size(); i++) {
        Airfoil a = airfoils.get(i);
        System.out.printf("%2d. %-15s (Thickness: %.1f%%, Application: %s)\n", 
            i + 1, a.getName(), a.getThickness(), a.getApplicationType());
    }

    System.out.print("\nSelect first airfoil (number): ");
    int idx1 = InputHelper.getIntInput(scanner) - 1;
    System.out.print("Select second airfoil (number): ");
    int idx2 = InputHelper.getIntInput(scanner) - 1;

    if (idx1 < 0 || idx1 >= airfoils.size() || idx2 < 0 || idx2 >= airfoils.size() || idx1 == idx2) {
        System.out.println("Invalid selection.");
        return;
    }

    Airfoil a1 = airfoils.get(idx1);
    Airfoil a2 = airfoils.get(idx2);

    // Enhanced comparison display
    displayEnhancedComparison(a1, a2);
    displayPerformanceAnalysis(a1, a2);
    displayRecommendations(a1, a2);
}

private static void displayEnhancedComparison(Airfoil a1, Airfoil a2) {
    String border = "╠══════════════════════════════════════════════════════════════════════════════════════════════════════╣";
    String headerBorder = "╔══════════════════════════════════════════════════════════════════════════════════════════════════════╗";
    String footerBorder = "╚══════════════════════════════════════════════════════════════════════════════════════════════════════╝";
    
    System.out.println("\n" + headerBorder);
    System.out.println("║                                  AIRFOIL COMPARISON - DETAILED ANALYSIS                              ║");
    System.out.println(border);
    
    // Parameter comparison table
    int rowWidth = headerBorder.length();
    String headerRow = String.format("║ %-22s ║ %-18s ║ %-18s ║ %-24s ║", "PARAMETER", a1.getName(), a2.getName(), "DIFFERENCE");
    // pad to match border width
    if (headerRow.length() < rowWidth) {
        int pad = rowWidth - headerRow.length();
        headerRow = headerRow.substring(0, headerRow.length() - 2) + "".repeat(pad) + " ║";
    }
    System.out.println(headerRow);
    System.out.println(border);

    compareParameter("Max Lift Coefficient", a1.getMaxCl(), a2.getMaxCl(), "Cl");
    compareParameter("Min Drag Coefficient", a1.getMinCd(), a2.getMinCd(), "Cd");
    compareParameter("Thickness (%)", a1.getThickness(), a2.getThickness(), "%");
    compareParameter("Reynolds Min", a1.getReynoldsMin(), a2.getReynoldsMin(), "Re");
    compareParameter("Reynolds Max", a1.getReynoldsMax(), a2.getReynoldsMax(), "Re");

    System.out.println(border);
    String appRow = String.format("║ %-22s ║ %-18s ║ %-18s ║ %-24s ║", "APPLICATION TYPE", a1.getApplicationType(), a2.getApplicationType(), "");
    if (appRow.length() < rowWidth) {
        int pad = rowWidth - appRow.length();
        appRow = appRow.substring(0, appRow.length() - 2) + "".repeat(pad) + " ║";
    }
    System.out.println(appRow);
    System.out.println(footerBorder);
}

private static void compareParameter(String paramName, double value1, double value2, String unit) {
    double diff = value1 - value2;
    double percentDiff = value2 != 0 ? (diff / value2) * 100 : 0;

    // Format values depending on the unit/type
    String v1Str;
    String v2Str;
    String diffDescription;
    
    if ("Re".equals(unit)) {
        v1Str = String.format("%.1fM", value1 / 1_000_000.0);
        v2Str = String.format("%.1fM", value2 / 1_000_000.0);
        diffDescription = String.format("%s by %.1fM Re (%+.1f%%)", 
            diff > 0 ? "Higher" : "Lower",
            Math.abs(diff / 1_000_000.0),
            percentDiff);
    } else if ("%".equals(unit)) {
        v1Str = String.format("%.2f", value1);
        v2Str = String.format("%.2f", value2);
        diffDescription = String.format("%s by %.2f%% absolute", 
            diff > 0 ? "Thicker" : "Thinner",
            Math.abs(diff));
    } else if ("Cl".equals(unit)) {
        v1Str = String.format("%.4f", value1);
        v2Str = String.format("%.4f", value2);
        diffDescription = String.format("%s lift by %.4f (%+.1f%%)", 
            diff > 0 ? "Better" : "Lower",
            Math.abs(diff),
            percentDiff);
    } else if ("Cd".equals(unit)) {
        v1Str = String.format("%.4f", value1);
        v2Str = String.format("%.4f", value2);
        diffDescription = String.format("%s drag by %.4f (%+.1f%%)", 
            diff > 0 ? "Higher" : "Lower",
            Math.abs(diff),
            percentDiff);
    } else {
        v1Str = String.format("%.3f", value1);
        v2Str = String.format("%.3f", value2);
        diffDescription = String.format("%+.3f (%+.1f%%)", diff, percentDiff);
    }

    int target = 94;
    String row = String.format("║ %-22s ║ %18s ║ %18s ║ %-24s ║", 
        paramName, v1Str, v2Str, diffDescription);
    
    if (row.length() < target) {
        int pad = target - row.length();
        row = row.substring(0, row.length() - 2) + " ".repeat(pad) + "║";
    }
    System.out.println(row);
}

private static void displayPerformanceAnalysis(Airfoil a1, Airfoil a2) {
    System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════════════════════════════╗");
    System.out.println("║                                  PERFORMANCE ANALYSIS                                                ║");
    System.out.println("╚══════════════════════════════════════════════════════════════════════════════════════════════════════╝");
    
    // Lift-to-drag ratio comparison (simplified)
    double ldr1 = a1.getMaxCl() / (a1.getMinCd() == 0 ? 0.001 : a1.getMinCd());
    double ldr2 = a2.getMaxCl() / (a2.getMinCd() == 0 ? 0.001 : a2.getMinCd());
    
    System.out.printf("Lift-to-Drag Ratio (Max Cl/Min Cd):\n");
    System.out.printf("  %s: %.2f\n", a1.getName(), ldr1);
    System.out.printf("  %s: %.2f\n", a2.getName(), ldr2);
    
    if (ldr1 > ldr2) {
        System.out.printf("  → %s has %.1f%% better lift-to-drag ratio\n", 
            a1.getName(), ((ldr1 - ldr2) / ldr2) * 100);
    } else if (ldr2 > ldr1) {
        System.out.printf("  → %s has %.1f%% better lift-to-drag ratio\n", 
            a2.getName(), ((ldr2 - ldr1) / ldr1) * 100);
    }
    
    // Reynolds number range comparison
    double range1 = a1.getReynoldsMax() - a1.getReynoldsMin();
    double range2 = a2.getReynoldsMax() - a2.getReynoldsMin();
    
    System.out.printf("\nOperational Range (Reynolds numbers):\n");
    System.out.printf("  %s: %.1f - %.1f million (Range: %.1fM)\n", 
        a1.getName(), a1.getReynoldsMin(), a1.getReynoldsMax(), range1);
    System.out.printf("  %s: %.1f - %.1f million (Range: %.1fM)\n", 
        a2.getName(), a2.getReynoldsMin(), a2.getReynoldsMax(), range2);
}

private static void displayRecommendations(Airfoil a1, Airfoil a2) {
    System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════════════════════════════╗");
    System.out.println("║                                  RECOMMENDATIONS                                                     ║");
    System.out.println("╚══════════════════════════════════════════════════════════════════════════════════════════════════════╝");
    
    // High lift recommendation
    if (a1.getMaxCl() > a2.getMaxCl() * 1.05) {
        System.out.printf("✓ %s is better for HIGH LIFT applications (%.1f%% higher max Cl)\n", 
            a1.getName(), ((a1.getMaxCl() - a2.getMaxCl()) / a2.getMaxCl()) * 100);
    } else if (a2.getMaxCl() > a1.getMaxCl() * 1.05) {
        System.out.printf("✓ %s is better for HIGH LIFT applications (%.1f%% higher max Cl)\n", 
            a2.getName(), ((a2.getMaxCl() - a1.getMaxCl()) / a1.getMaxCl()) * 100);
    }
    
    // Low drag recommendation
    if (a1.getMinCd() < a2.getMinCd() * 0.95) {
        System.out.printf("✓ %s is better for LOW DRAG applications (%.1f%% lower min Cd)\n", 
            a1.getName(), ((a2.getMinCd() - a1.getMinCd()) / a2.getMinCd()) * 100);
    } else if (a2.getMinCd() < a1.getMinCd() * 0.95) {
        System.out.printf("✓ %s is better for LOW DRAG applications (%.1f%% lower min Cd)\n", 
            a2.getName(), ((a1.getMinCd() - a2.getMinCd()) / a1.getMinCd()) * 100);
    }
    
    // Thickness-based recommendations
    if (a1.getThickness() > a2.getThickness() + 2) {
        System.out.printf("✓ %s is thicker (%.1f%%) - better for structural strength\n", 
            a1.getName(), a1.getThickness());
    } else if (a2.getThickness() > a1.getThickness() + 2) {
        System.out.printf("✓ %s is thicker (%.1f%%) - better for structural strength\n", 
            a2.getName(), a2.getThickness());
    }
    
    // Application-specific recommendations
    System.out.println("\nApplication Suitability:");
    System.out.printf("  %s: %s\n", a1.getName(), a1.getApplicationType());
    System.out.printf("  %s: %s\n", a2.getName(), a2.getApplicationType());
}

// Enhanced search history with filtering options
public static void viewSearchHistory(User currentUser, Scanner scanner) {
    System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════════════════════════════╗");
    System.out.println("║                                  SEARCH HISTORY                                                   ║");
    System.out.println("╚══════════════════════════════════════════════════════════════════════════════════════════════════════╝");
    
    Stack<FlightParameters> history = currentUser.getSearchHistory();

    if (history.isEmpty()) {
        System.out.println("No search history yet.");
        return;
    }

    System.out.println("Total searches: " + history.size());
    System.out.println("\nOptions:");
    System.out.println("1. View all searches (chronological)");
    System.out.println("2. View recent searches (last 10)");
    System.out.println("3. Search by aircraft type");
    System.out.print("Choose option: ");
    
    int option = InputHelper.getIntInput(scanner);
    
    List<FlightParameters> tempList = new ArrayList<>(history);
    Collections.reverse(tempList); // Most recent first
    
    switch (option) {
        case 1:
            displayAllSearches(tempList);
            break;
        case 2:
            displayRecentSearches(tempList);
            break;
        default:
            displayRecentSearches(tempList);
    }
}

private static void displayAllSearches(List<FlightParameters> searches) {
    System.out.println("\nAll Searches (most recent first):");
    String border = "──────────────────────────────────────────────────────────────────────────────────────────────────────";
    System.out.println(border);
    
    for (int i = 0; i < searches.size(); i++) {
        FlightParameters params = searches.get(i);
        System.out.printf("%2d. %s\n", i + 1, params.toDetailedString());
        if (i < searches.size() - 1) System.out.println("─".repeat(90));
    }
}

private static void displayRecentSearches(List<FlightParameters> searches) {
    int limit = Math.min(10, searches.size());
    System.out.printf("\nLast %d Searches:\n", limit);
    
    for (int i = 0; i < limit; i++) {
        System.out.printf("%2d. %s\n", i + 1, searches.get(i).toDetailedString());
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
        System.out.println(" Airfoil added successfully!");
    } else {
        System.out.println(" Airfoil with this name already exists in the database!");
    }
}

    public static void showEducationalInfo() {
        String eduBorder = "╔═══════════════════════════════════════════════════════════╗";
        String eduHeader = "║              AIRFOIL BASICS - EDUCATIONAL GUIDE           ║";
        String eduFooter = "╚═══════════════════════════════════════════════════════════╝";
        String eduSeparator = "╟───────────────────────────────────────────────────────────╢";
        
        System.out.println("\n" + eduBorder);
        System.out.println(eduHeader);
        System.out.println(eduSeparator);
        
        // Section 1: Lift Coefficient
        System.out.println("║ 1. LIFT COEFFICIENT (Cl)                                  ║");
        System.out.println("║    • Measures how much lift an airfoil generates          ║");
        System.out.println("║    • Higher Cl = more lift at same speed                  ║");
        System.out.println("║    • Important for takeoff and low-speed flight           ║");
        System.out.println(eduSeparator);
        
        // Section 2: Drag Coefficient
        System.out.println("║ 2. DRAG COEFFICIENT (Cd)                                  ║");
        System.out.println("║    • Measures resistance to motion                        ║");
        System.out.println("║    • Lower Cd = less drag = more efficient               ║");
        System.out.println("║    • Critical for cruise efficiency                       ║");
        System.out.println(eduSeparator);
        
        // Section 3: Lift-to-Drag Ratio
        System.out.println("║ 3. LIFT-TO-DRAG RATIO (L/D)                              ║");
        System.out.println("║    • Cl/Cd ratio indicates overall efficiency            ║");
        System.out.println("║    • Higher L/D = better performance                      ║");
        System.out.println("║    • Gliders need high L/D (>30), fighters need high Cl  ║");
        System.out.println(eduSeparator);
        
        // Section 4: Reynolds Number
        System.out.println("║ 4. REYNOLDS NUMBER (Re)                                   ║");
        System.out.println("║    • Indicates flow characteristics                       ║");
        System.out.println("║    • Re = (velocity × chord) / kinematic viscosity       ║");
        System.out.println("║    • Different airfoils work best at different Re ranges  ║");
        System.out.println(eduSeparator);
        
        // Section 5: Application Types
        System.out.println("║ 5. APPLICATION TYPES                                      ║");
        System.out.println("║    • Low Speed: High Cl, used in gliders, trainers       ║");
        System.out.println("║    • High Speed: Low drag, used in jets, racers          ║");
        System.out.println("║    • General Purpose: Balanced Cl/Cd for diverse use      ║");
        System.out.println(eduSeparator);
        
        // Section 6: Required Lift
        System.out.println("║ 6. REQUIRED LIFT                                          ║");
        System.out.println("║    • Force needed to counteract aircraft weight           ║");
        System.out.println("║    • Required Lift = Mass × 9.8 m/s²                     ║");
        System.out.println("║    • Example: 5 kg drone needs 49 N of lift              ║");
        System.out.println(eduFooter);
    }
    
}