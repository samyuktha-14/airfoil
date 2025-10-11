public class RecommendationResult {
    private Airfoil airfoil;
    private double score;
    private String reason;

    public RecommendationResult(Airfoil airfoil, double score, String reason) {
        this.airfoil = airfoil;
        this.score = score;
        this.reason = reason;
    }

    public Airfoil getAirfoil() { return airfoil; }
    public double getScore() { return score; }
    public String getReason() { return reason; }
}