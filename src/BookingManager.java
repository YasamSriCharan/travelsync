/** Stores bookings for the lifetime of one console session. */
public class BookingManager {
    private final String[] records = new String[20];
    private int count;
    public String create(String name, int destination) {
        if (count == records.length) return "BOOKING-LIMIT-REACHED";
        String code = "TS" + (1001 + count);
        records[count++] = code + " | " + name + " | " + TravelData.DESTINATIONS[destination] + " | Confirmed";
        return code;
    }
    public int getCount() { return count; }
    public void show() {
        System.out.println("--------------- BOOKINGS ---------------");
        if (count == 0) { System.out.println("No bookings have been created in this session."); return; }
        for (int i = 0; i < count; i++) System.out.println((i + 1) + ". " + records[i]);
    }
}
