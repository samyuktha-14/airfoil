import java.io.*;
import java.util.*;

public class AirfoilService {
    private List<Airfoil> airfoilDatabase = new ArrayList<>();
    private Map<String, Airfoil> airfoilMap = new HashMap<>(); // For fast duplicate check
    private static final String DB_FILE = "airfoils.csv";

    public AirfoilService() {
        loadFromFile();
    }

    public boolean addAirfoil(Airfoil airfoil) {
        String nameKey = airfoil.getName().toLowerCase(); 
        if (airfoilMap.containsKey(nameKey)) {
            return false; 

        airfoilDatabase.add(airfoil);
        airfoilMap.put(nameKey, airfoil); 
        saveToFile();
        return true;
    }

    public List<Airfoil> getAllAirfoils() {
        airfoilDatabase.sort(Comparator.comparing(Airfoil::getName));
        return airfoilDatabase;
    }

    private void loadFromFile() {
        airfoilDatabase.clear();
        airfoilMap.clear(); 
        File file = new File(DB_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 7) {
                    String name = parts[0];
                    double maxCl = Double.parseDouble(parts[1]);
                    double minCd = Double.parseDouble(parts[2]);
                    double reynoldsMin = Double.parseDouble(parts[3]);
                    double reynoldsMax = Double.parseDouble(parts[4]);
                    String applicationType = parts[5];
                    double thickness = Double.parseDouble(parts[6]);

                    Airfoil airfoil = new Airfoil(name, maxCl, minCd, reynoldsMin, reynoldsMax, applicationType, thickness);
                    airfoilDatabase.add(airfoil);
                    airfoilMap.put(name.toLowerCase(), airfoil); 
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading airfoil database: " + e.getMessage());
        }
    }

    private void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DB_FILE))) {
            for (Airfoil a : airfoilDatabase) {
                pw.println(String.join(",",
                    a.getName(),
                    String.valueOf(a.getMaxCl()),
                    String.valueOf(a.getMinCd()),
                    String.valueOf(a.getReynoldsMin()),
                    String.valueOf(a.getReynoldsMax()),
                    a.getApplicationType(),
                    String.valueOf(a.getThickness())
                ));
            }
        } catch (IOException e) {
            System.out.println("Error saving airfoil database: " + e.getMessage());
        }
    }

    public List<RecommendationResult> recommendAirfoils(FlightParameters params) {
        List<RecommendationResult> results = new ArrayList<>();
        for (Airfoil airfoil : airfoilDatabase) {
            double score = airfoil.getLiftToDragRatio();
            String reason = "High L/D ratio";
            results.add(new RecommendationResult(airfoil, score, reason));
        }
        results.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        return results;
    }
}
