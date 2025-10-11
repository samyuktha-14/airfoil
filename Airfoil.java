
public class Airfoil {
    private String name;
    private double maxCl;           // Maximum lift coefficient
    private double minCd;           // Minimum drag coefficient
    private double reynoldsMin;     // Minimum Reynolds number
    private double reynoldsMax;     // Maximum Reynolds number
    private String applicationType; // Application type (e.g., "Low Speed", "High Speed")
    private double thickness;       // Thickness ratio

    
    public Airfoil(String name, double maxCl, double minCd, double reynoldsMin, 
                   double reynoldsMax, String applicationType, double thickness) {
        this.name = name;
        this.maxCl = maxCl;
        this.minCd = minCd;
        this.reynoldsMin = reynoldsMin;
        this.reynoldsMax = reynoldsMax;
        this.applicationType = applicationType;
        this.thickness = thickness;
    }

    // Getters
    public String getName() { 
        return name; 
    }
    
    public double getMaxCl() { 
        return maxCl; 
    }
    
    public double getMinCd() { 
        return minCd; 
    }
    
    public double getReynoldsMin() { 
        return reynoldsMin; 
    }
    
    public double getReynoldsMax() { 
        return reynoldsMax; 
    }
    
    public String getApplicationType() { 
        return applicationType; 
    }
    
    public double getThickness() { 
        return thickness; 
    }

    /**
     * Calculate Lift-to-Drag ratio
     */
    public double getLiftToDragRatio() {
        return maxCl / minCd;
    }

    /**
     * Get detailed information about the airfoil
     */
    public String getDetails() {
        return String.format(
            "Type: %s | Max Cl: %.3f | Min Cd: %.4f | L/D: %.2f | Thickness: %.1f%% | Re Range: %.0e-%.0e",
            applicationType, maxCl, minCd, getLiftToDragRatio(), thickness*100, reynoldsMin, reynoldsMax
        );
    }

    @Override
    public String toString() {
        return name + " (" + applicationType + ")";
    }
}