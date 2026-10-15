import java.util.Scanner;

/** Coordinates the six implemented TravelSync modules. */
public class TravelSyncEngine {
    private final BookingManager bookings = new BookingManager();
    public void showDestinations() {
        System.out.println("--------------- DESTINATIONS ---------------");
        for (int i = 0; i < TravelData.DESTINATIONS.length; i++) {
            System.out.println((i + 1) + ". " + TravelData.DESTINATIONS[i]);
            System.out.println("   " + TravelData.DESCRIPTIONS[i]);
            System.out.println("   Daily budget: Rs. " + TravelData.DAILY_COST[i] + " | Seats: " + TravelData.AVAILABLE_SEATS[i]);
        }
    }
    public void searchDestinations(Scanner scanner) {
        String query = InputHelper.readText(scanner, "Enter a destination or keyword: ").toLowerCase();
        boolean found = false;
        System.out.println("--------------- SEARCH RESULTS ---------------");
        for (int i = 0; i < TravelData.DESTINATIONS.length; i++) if (TravelData.DESTINATIONS[i].toLowerCase().contains(query) || TravelData.DESCRIPTIONS[i].toLowerCase().contains(query)) { printDestination(i); found = true; }
        if (!found) System.out.println("No exact match found. Did you mean " + TravelData.DESTINATIONS[closestDestination(query)] + "?");
    }
    public void planTrip(Scanner scanner) {
        showDestinations(); int d = chooseDestination(scanner); int days = InputHelper.readNumber(scanner, "How many days (1-14)? ", 1, 14);
        System.out.println("--------------- TRIP PLAN ---------------");
        System.out.println("Destination : " + TravelData.DESTINATIONS[d]);
        System.out.println("Duration    : " + days + " day(s)");
        System.out.println("Travel      : Rs. 1500");
        System.out.println("Stay + food : Rs. " + (TravelData.DAILY_COST[d] * days));
        System.out.println("Estimate    : Rs. " + (1500 + TravelData.DAILY_COST[d] * days));
    }
    public void allocateTravelService(Scanner scanner) {
        showDestinations(); int d = chooseDestination(scanner); int travellers = InputHelper.readNumber(scanner, "Number of travellers: ", 1, 10);
        if (travellers <= TravelData.AVAILABLE_SEATS[d]) { TravelData.AVAILABLE_SEATS[d] -= travellers; System.out.println("Allocation confirmed for " + travellers + " traveller(s). Remaining seats: " + TravelData.AVAILABLE_SEATS[d]); }
        else System.out.println("Allocation unavailable. Only " + TravelData.AVAILABLE_SEATS[d] + " seat(s) remain.");
    }
    public void optimiseBudget(Scanner scanner) {
        int budget = InputHelper.readNumber(scanner, "Activity budget (Rs. 500-10000): ", 500, 10000), spent = 0;
        System.out.println("--------------- BUDGET-OPTIMISED ACTIVITIES ---------------");
        for (int i = 0; i < TravelData.ACTIVITIES.length; i++) if (spent + TravelData.ACTIVITY_COSTS[i] <= budget) { spent += TravelData.ACTIVITY_COSTS[i]; System.out.println("+ " + TravelData.ACTIVITIES[i] + " - Rs. " + TravelData.ACTIVITY_COSTS[i]); }
        System.out.println("Selected cost: Rs. " + spent + " | Remaining: Rs. " + (budget - spent));
    }
    public void showTravelInsights() {
        int total = 0, cheapest = 0;
        for (int i = 0; i < TravelData.DAILY_COST.length; i++) { total += TravelData.DAILY_COST[i]; if (TravelData.DAILY_COST[i] < TravelData.DAILY_COST[cheapest]) cheapest = i; }
        int recommended = bookings.getCount() % TravelData.DESTINATIONS.length;
        System.out.println("--------------- TRAVEL INSIGHTS ---------------");
        System.out.println("Average daily budget: Rs. " + total / TravelData.DAILY_COST.length);
        System.out.println("Lowest-cost destination: " + TravelData.DESTINATIONS[cheapest]);
        System.out.println("Recommendation: " + TravelData.DESTINATIONS[recommended] + " - " + TravelData.DESCRIPTIONS[recommended]);
    }
    public void createBooking(Scanner scanner) { String name = InputHelper.readText(scanner, "Traveller name: "); showDestinations(); System.out.println("Booking created. Reservation code: " + bookings.create(name, chooseDestination(scanner))); }
    public void showBookings() { bookings.show(); }
    private int chooseDestination(Scanner scanner) { return InputHelper.readNumber(scanner, "Choose destination number: ", 1, TravelData.DESTINATIONS.length) - 1; }
    private void printDestination(int i) { System.out.println(TravelData.DESTINATIONS[i] + " - " + TravelData.DESCRIPTIONS[i] + " (Rs. " + TravelData.DAILY_COST[i] + "/day)"); }
    private int closestDestination(String query) { int best = 0, score = distance(query, TravelData.DESTINATIONS[0].toLowerCase()); for (int i = 1; i < TravelData.DESTINATIONS.length; i++) { int candidate = distance(query, TravelData.DESTINATIONS[i].toLowerCase()); if (candidate < score) { score = candidate; best = i; } } return best; }
    private int distance(String a, String b) { int[] previous = new int[b.length() + 1]; for (int j = 0; j <= b.length(); j++) previous[j] = j; for (int i = 1; i <= a.length(); i++) { int[] current = new int[b.length() + 1]; current[0] = i; for (int j = 1; j <= b.length(); j++) { int change = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1; current[j] = Math.min(Math.min(current[j - 1] + 1, previous[j] + 1), previous[j - 1] + change); } previous = current; } return previous[b.length()]; }
}
