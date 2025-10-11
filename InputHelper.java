import java.util.Scanner;


public class InputHelper {

   
    public static int getIntInput(Scanner scanner) {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("✗ Invalid input. Please enter a valid number: ");
            }
        }
    }

    
    public static double getDoubleInput(Scanner scanner) {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.print("✗ Invalid input. Please enter a valid number: ");
            }
        }
    }

    
    public static double getValidSpeed(Scanner scanner) {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                double speed = Double.parseDouble(input);
                
                if (speed <= 0) {
                    System.out.print("✗ Invalid speed! Speed must be positive. Enter speed (m/s): ");
                    continue;
                }
                
                return speed;
            } catch (NumberFormatException e) {
                System.out.print("✗ Invalid input. Please enter a valid number: ");
            }
        }
    }

    
    public static double getValidAltitude(Scanner scanner) {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                double altitude = Double.parseDouble(input);
                
                if (altitude < 0) {
                    System.out.print("✗ Invalid altitude! Altitude cannot be negative. Enter altitude (meters): ");
                    continue;
                }
                
                return altitude;
            } catch (NumberFormatException e) {
                System.out.print("✗ Invalid input. Please enter a valid number: ");
            }
        }
    }

    
    public static double getValidWingArea(Scanner scanner) {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                double wingArea = Double.parseDouble(input);
                
                if (wingArea <= 0) {
                    System.out.print("✗ Invalid wing area! Wing area must be positive. Enter wing area (m²): ");
                    continue;
                }
                
                return wingArea;
            } catch (NumberFormatException e) {
                System.out.print("✗ Invalid input. Please enter a valid number: ");
            }
        }
    }

    
    public static double getValidLift(Scanner scanner) {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                double lift = Double.parseDouble(input);
                
                if (lift <= 0) {
                    System.out.print("✗ Invalid lift! Required lift must be positive. Enter required lift (N): ");
                    continue;
                }
                
                return lift;
            } catch (NumberFormatException e) {
                System.out.print("✗ Invalid input. Please enter a valid number: ");
            }
        }
    }

    
    public static int getIntInputInRange(Scanner scanner, int min, int max) {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                int value = Integer.parseInt(input);
                
                if (value < min || value > max) {
                    System.out.print("✗ Invalid input! Please enter a number between " + min + " and " + max + ": ");
                    continue;
                }
                
                return value;
            } catch (NumberFormatException e) {
                System.out.print("✗ Invalid input. Please enter a valid number: ");
            }
        }
    }
}