import java.util.*;

public class AirfoilService {
    private DatabaseService dbService;

    public AirfoilService() {
        dbService = new DatabaseService();
    }

    public boolean addAirfoil(Airfoil airfoil) {
        String nameKey = airfoil.getName().toLowerCase();

        // Duplicate check
        if (airfoilExists(airfoil.getName())) {
            return false;
        }

        // Add to database
        return dbService.addAirfoil(
            airfoil.getName(),
            airfoil.getThickness(),
            airfoil.getMinCd(),
            airfoil.getReynoldsMin(),
            airfoil.getReynoldsMax(),
            airfoil.getApplicationType(),
            airfoil.getMaxCl()
        );
    }

    public List<Airfoil> getAllAirfoils() {
        return dbService.getAllAirfoils();
    }

    public boolean airfoilExists(String name) {
        return dbService.airfoilExists(name);
    }

    public List<RecommendationResult> recommendAirfoils(FlightParameters params) {
        List<RecommendationResult> results = new ArrayList<>();

        for (Airfoil airfoil : dbService.getAllAirfoils()) {
            double score = airfoil.getLiftToDragRatio();
            String reason = "High L/D ratio";
            results.add(new RecommendationResult(airfoil, score, reason));
        }

        results.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        return results;
    }
}
