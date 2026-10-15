/** Shared in-memory data used by the TravelSync modules. */
public final class TravelData {
    static final String[] DESTINATIONS = {"Goa", "Jaipur", "Manali", "Kochi"};
    static final String[] DESCRIPTIONS = {
        "Beaches, water sports, cafes and relaxed coastal stays.",
        "Forts, palaces, local markets and Rajasthani food.",
        "Mountain views, short hikes, cafes and cool weather.",
        "Backwaters, heritage streets, seafood and culture."
    };
    static final int[] DAILY_COST = {3500, 2800, 3200, 3000};
    static final int[] AVAILABLE_SEATS = {12, 8, 6, 10};
    static final String[] ACTIVITIES = {"Sightseeing", "Food tour", "Adventure activity", "Local shopping"};
    static final int[] ACTIVITY_COSTS = {900, 1200, 1800, 1000};
    private TravelData() { }
}
