import java.util.Scanner;

/** Entry point for the TravelSync six-module console application. */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TravelSyncEngine engine = new TravelSyncEngine();
        boolean running = true;
        printHeader();
        while (running) {
            printMenu();
            switch (InputHelper.readNumber(scanner, "Choose an option: ", 0, 8)) {
                case 1: engine.showDestinations(); break;
                case 2: engine.searchDestinations(scanner); break;
                case 3: engine.planTrip(scanner); break;
                case 4: engine.allocateTravelService(scanner); break;
                case 5: engine.optimiseBudget(scanner); break;
                case 6: engine.showTravelInsights(); break;
                case 7: engine.createBooking(scanner); break;
                case 8: engine.showBookings(); break;
                case 0: running = false; break;
                default: break;
            }
        }
        System.out.println("\nThank you for using TravelSync. Safe travels!");
        scanner.close();
    }

    private static void printHeader() {
        System.out.println("========================================================");
        System.out.println("                     TRAVELSYNC");
        System.out.println("   Intelligent Travel Planning & Information Platform");
        System.out.println("========================================================");
    }

    private static void printMenu() {
        System.out.println("\n--- MODULE MENU ---");
        System.out.println("1. Destination information              [Module 1]");
        System.out.println("2. Smart destination search             [Module 2]");
        System.out.println("3. Dynamic trip planner                  [Module 3]");
        System.out.println("4. Travel service allocation             [Module 4]");
        System.out.println("5. Budget and route optimisation         [Module 5]");
        System.out.println("6. Travel insights and recommendations   [Module 6]");
        System.out.println("7. Create a booking");
        System.out.println("8. View bookings");
        System.out.println("0. Exit");
    }
}
