import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.lang.InterruptedException;

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

    public synchronized void carArrives(Car car) { // 20220027
        System.out.println("Car " + car.getCarId() + " from Gate " + car.getGateId() + " arrived at time " + car.getArrivalTime());
        
        if (parkingSlots.tryAcquire()) {
            car.park();
            totalServedCars++;
            servedCarsPerGate.put(car.getGateId(), servedCarsPerGate.getOrDefault(car.getGateId(), 0) + 1);
        } else {
            waitingQueue.add(car);
            System.out.println("Car " + car.getCarId() + " from Gate " + car.getGateId() + " waiting for a spot.");
        }
    }

    public synchronized void carLeaves(Car car) { // 20220027
        parkingSlots.release();
        System.out.println("Car " + car.getCarId() + " from Gate " + car.getGateId() + " left after " + car.getParkingTime() + " units of time. (Parking Status: " + (totalSlots - parkingSlots.availablePermits()) + " spots occupied)");
        
        if (!waitingQueue.isEmpty()) {
            Car nextCar = waitingQueue.poll();
            nextCar.parkAfterWaiting();
            totalServedCars++;
            servedCarsPerGate.put(nextCar.getGateId(), servedCarsPerGate.getOrDefault(nextCar.getGateId(), 0) + 1);
        }
    }

    public synchronized void reportStatistics() {
        System.out.println("Total Cars Served: " + totalServedCars);
        System.out.println("Current Cars in Parking: " + (totalSlots - parkingSlots.availablePermits()));
        System.out.println("Details:");
        for (Map.Entry<Integer, Integer> entry : servedCarsPerGate.entrySet()) {
            System.out.println("- Gate " + entry.getKey() + " served " + entry.getValue() + " cars.");
        }
    }
}

class Car extends Thread {
    private final int carId;
    private final int gateId;
    private final int arrivalTime;
    private final int parkingDuration;
    private int waitingTime;
    private ParkingLot parkingLot;

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

    public int getParkingTime() {
        return parkingDuration;
    }

    public int getWaitingTime() {
        return waitingTime;
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
