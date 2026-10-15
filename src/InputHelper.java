import java.util.Scanner;

/**
 * Keeps console input validation separate from the main application flow.
 */
public class InputHelper {
    private InputHelper() {
        // This class only contains helper methods.
    }

    public static int readNumber(Scanner scanner, String prompt, int minimum, int maximum) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                int number = Integer.parseInt(input);
                if (number >= minimum && number <= maximum) {
                    return number;
                }
                System.out.println("Enter a number from " + minimum + " to " + maximum + ".");
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    public static String readText(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = scanner.nextLine().trim();
            if (!text.isEmpty()) {
                return text;
            }
            System.out.println("This field cannot be empty.");
        }
    }
}
