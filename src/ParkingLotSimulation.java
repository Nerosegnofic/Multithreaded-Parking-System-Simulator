import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.lang.InterruptedException;

class Car extends Thread {
    private final int carId;
    private final int gateId;
    private final int arrivalTime;
    private final int parkingDuration;
    private final ParkingLot parkingLot;
    private int waitingTime;

    public Car(int carId, int gateId, int arrivalTime, int parkingDuration, ParkingLot parkingLot) {
        this.carId = carId;
        this.gateId = gateId;
        this.arrivalTime = arrivalTime;
        this.parkingDuration = parkingDuration;
        this.parkingLot = parkingLot;
        this.waitingTime = 0;
    }

    public int getCarId() {
        return carId;
    }

    public int getGateId() {
        return gateId;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void run() {
        try {

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void park() {

    }

    public void parkAfterWaiting() {

    }

    public int getParkingDuration() {
        return parkingDuration;
    }

    public int getWaitingTime() {
        return waitingTime;
    }
}

class ParkingLot {
    final Semaphore parkingSlots;
    final Queue<Car> waitingQueue;
    public final int totalSlots;
    private final Map<Integer, Integer> servedCarsPerGate;
    private int totalServedCars;
    public ParkingLot(int totalSlots) {
        this.parkingSlots = new Semaphore(totalSlots, true);
        this.waitingQueue = new LinkedList<>();
        this.totalSlots = totalSlots;
        this.servedCarsPerGate = new HashMap<>();
        this.totalServedCars = 0;
    }

    public int getOccupiedSpots() {
        return totalSlots - parkingSlots.availablePermits();
    }

    public void carArrives(Car car) { // 20220027
        System.out.printf("Car %d from Gate %d arrived at time %d%n", car.getCarId(), car.getGateId(), car.getArrivalTime());

        synchronized (this) {
            if (parkingSlots.tryAcquire()) {
                totalServedCars++;
                servedCarsPerGate.put(car.getGateId(), servedCarsPerGate.getOrDefault(car.getGateId(), 0) + 1);
                car.park();
            } else {
                waitingQueue.add(car);
                System.out.printf("Car %d from Gate %d waiting for a spot.%n", car.getCarId(), car.getGateId());
            }
        }
    }

    public void carLeaves(Car car) { // 20220027
        parkingSlots.release();
        System.out.printf("Car %d from Gate %d left after %d units of time. (Parking Status: %d spots occupied)%n", car.getCarId(), car.getGateId(), car.getParkingDuration(), getOccupiedSpots());

        synchronized (this) {
            if (!waitingQueue.isEmpty()) {
                Car nextCar = waitingQueue.poll();
                try {
                    parkingSlots.acquire();
                    nextCar.parkAfterWaiting();
                    totalServedCars++;
                    servedCarsPerGate.put(nextCar.getGateId(), servedCarsPerGate.getOrDefault(nextCar.getGateId(), 0) + 1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void reportStatistics() {
        System.out.printf("Total Cars Served: %d%n", totalServedCars);
        System.out.printf("Current Cars in Parking: %d%n", getOccupiedSpots());
        System.out.println("Details:");
        for (Map.Entry<Integer, Integer> entry : servedCarsPerGate.entrySet()) {
            System.out.printf("- Gate %d served %d cars.%n", entry.getKey(), entry.getValue());
        }
    }
}

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
