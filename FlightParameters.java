
public class FlightParameters {
    private double speed;        
    private double altitude;     
    private double wingArea;    
    private double requiredLift; 

   
    public FlightParameters(double speed, double altitude, double wingArea, double requiredLift) {
        this.speed = speed;
        this.altitude = altitude;
        this.wingArea = wingArea;
        this.requiredLift = requiredLift;
    }

   
    public double getSpeed() { 
        return speed; 
    }
    
    public double getAltitude() { 
        return altitude; 
    }
    
    public double getWingArea() { 
        return wingArea; 
    }
    
    public double getRequiredLift() { 
        return requiredLift; 
    }


    public double calculateReynoldsNumber() {
        double chord = Math.sqrt(wingArea); 
        double kinematicViscosity = 1.5e-5; 
        return (speed * chord) / kinematicViscosity;
    }

    public double calculateRequiredCl() {
        double airDensity = 1.225; 
        double dynamicPressure = 0.5 * airDensity * speed * speed;
        return requiredLift / (dynamicPressure * wingArea);
    }

    @Override
    public String toString() {
        return String.format("Speed: %.1f m/s, Alt: %.0f m, Wing Area: %.2f m², Lift: %.0f N, Re: %.2e",
                           speed, altitude, wingArea, requiredLift, calculateReynoldsNumber());
    }
}