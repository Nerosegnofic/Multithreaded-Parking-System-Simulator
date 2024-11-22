import java.io.*;
import java.util.*;

public class ParkingLotSimulation {
    public static void main(String[] args) {
        int totalSlots = 4;
        ParkingLot parkingLot = new ParkingLot(totalSlots);

        List<Car> cars = readCarsFromFile("input.txt", parkingLot);


        for (Car car : cars) {
            car.start();
        }
        for (Car car : cars) {
            try {
                car.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        parkingLot.reportStatistics();
    }

    private static List<Car> readCarsFromFile(String filename, ParkingLot parkingLot) {
        List<Car> cars = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();

                String[] parts = line.split(", ");
                if (parts.length != 4) {
                    System.err.println("Skipping invalid line: " + line);
                    continue;
                }

                try {

                    int gateId = Integer.parseInt(parts[0].split(" ")[1]);
                    int carId = Integer.parseInt(parts[1].split(" ")[1]);
                    int arriveTime = Integer.parseInt(parts[2].split(" ")[1]);
                    int parkingDuration = Integer.parseInt(parts[3].split(" ")[1]);


                    cars.add(new Car(carId, gateId, arriveTime, parkingDuration, parkingLot));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format in line: " + line + ". Error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cars;
    }
}
