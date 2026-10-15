3package texthack;

import java.util.Scanner;

/**
 * Safe console input helper.
 * Provides resilient parsing for integers, doubles, and non-empty strings.
 * Note: Scanner is only used here at the console interaction boundary.
 */
public class InputHelper {

    private final Scanner scanner;

    public InputHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readString(String prompt, String defaultValue) {
        System.out.print(prompt + (defaultValue != null ? " [" + defaultValue + "]: " : ": "));
        String line = scanner.nextLine().trim();
        if (line.isEmpty() && defaultValue != null) {
            return defaultValue;
        }
        return line;
    }

    public int readInt(String prompt, int min, int max, int defaultValue) {
        while (true) {
            System.out.print(prompt + " [" + defaultValue + "] (" + min + "-" + max + "): ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                return defaultValue;
            }
            try {
                int val = Integer.parseInt(line);
                if (val >= min && val <= max) {
                    return val;
                }
                System.out.println("  [Error] Value must be between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("  [Error] Invalid integer. Please try again.");
            }
        }
    }

    public double readDouble(String prompt, double min, double max, double defaultValue) {
        while (true) {
            System.out.print(prompt + " [" + defaultValue + "]: ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                return defaultValue;
            }
            try {
                double val = Double.parseDouble(line);
                if (val >= min && val <= max) {
                    return val;
                }
                System.out.println("  [Error] Value must be between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("  [Error] Invalid number. Please try again.");
            }
        }
    }

    public void waitForEnter() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
}
