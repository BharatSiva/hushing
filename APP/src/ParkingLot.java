import java.util.*;

class ParkingSpot {
    String licensePlate;
    long entryTime;
    String status;

    ParkingSpot() {
        status = "EMPTY";
    }
}

public class ParkingLot {

    private ParkingSpot[] table;
    private int capacity = 500;
    private int occupied = 0;
    private int totalProbes = 0;
    private int totalParks = 0;

    private Map<String, Integer> vehicleSpot = new HashMap<>();

    public ParkingLot() {
        table = new ParkingSpot[capacity];
        for (int i = 0; i < capacity; i++) {
            table[i] = new ParkingSpot();
        }
    }

    private int hash(String plate) {
        return Math.abs(plate.hashCode()) % capacity;
    }

    public void parkVehicle(String plate) {

        int index = hash(plate);
        int probes = 0;

        while (probes < capacity) {

            int spot = (index + probes) % capacity;

            if (table[spot].status.equals("EMPTY") || table[spot].status.equals("DELETED")) {

                table[spot].licensePlate = plate;
                table[spot].entryTime = System.currentTimeMillis();
                table[spot].status = "OCCUPIED";

                vehicleSpot.put(plate, spot);

                occupied++;
                totalProbes += probes;
                totalParks++;

                System.out.println("parkVehicle(\"" + plate + "\") → Assigned spot #" + spot + " (" + probes + " probes)");
                return;
            }

            probes++;
        }

        System.out.println("Parking lot full");
    }

    public void exitVehicle(String plate) {

        if (!vehicleSpot.containsKey(plate)) {
            System.out.println("Vehicle not found");
            return;
        }

        int spot = vehicleSpot.get(plate);
        ParkingSpot ps = table[spot];

        long durationMillis = System.currentTimeMillis() - ps.entryTime;
        double hours = durationMillis / (1000.0 * 60 * 60);

        double fee = hours * 5;

        ps.status = "DELETED";
        ps.licensePlate = null;

        vehicleSpot.remove(plate);
        occupied--;

        System.out.println("exitVehicle(\"" + plate + "\") → Spot #" + spot +
                " freed, Duration: " + String.format("%.2f", hours) +
                "h, Fee: $" + String.format("%.2f", fee));
    }

    public void getStatistics() {

        double occupancy = (occupied * 100.0) / capacity;
        double avgProbes = totalParks == 0 ? 0 : (double) totalProbes / totalParks;

        System.out.println("getStatistics() → Occupancy: " +
                String.format("%.2f", occupancy) +
                "%, Avg Probes: " +
                String.format("%.2f", avgProbes));
    }

    public static void main(String[] args) {

        ParkingLot lot = new ParkingLot();

        lot.parkVehicle("ABC-1234");
        lot.parkVehicle("ABC-1235");
        lot.parkVehicle("XYZ-9999");

        lot.exitVehicle("ABC-1234");

        lot.getStatistics();
    }
}