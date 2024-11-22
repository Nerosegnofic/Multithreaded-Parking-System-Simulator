import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class ParkingLot {
    final Semaphore parkingSlots;
    final Queue<Car> waitingQueue;
    public final int totalSlots;
    private final Map<Integer, Integer> servedCarsPerGate;
    private PrintStream output; //20220241
    private int totalServedCars;
    public ParkingLot(int totalSlots) {
        this.parkingSlots = new Semaphore(totalSlots, true);
        this.waitingQueue = new LinkedList<>();
        this.totalSlots = totalSlots;
        this.servedCarsPerGate = new HashMap<>();
        this.totalServedCars = 0;
        try { //20220241
            this.output = new PrintStream(new FileOutputStream("output.txt"));
            System.setOut(output); // Redirect System.out to output.txt
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int getOccupiedSpots() {
        return totalSlots - parkingSlots.availablePermits();
    }

    public void carArrives(Car car) { // 20220027
        System.out.printf("Car %d from Gate %d arrived at time %d%n", car.getCarId(), car.getGateId(), car.getArrivalTime());
        
        if (parkingSlots.tryAcquire()) {
            totalServedCars++;
            servedCarsPerGate.put(car.getGateId(), servedCarsPerGate.getOrDefault(car.getGateId(), 0) + 1);
            System.out.printf("Car %d from Gate %d parked. (Parking Status: %d spots occupied)%n", car.getCarId(), car.getGateId(), getOccupiedSpots());
            car.park();
        } else {
            waitingQueue.add(car);
            System.out.printf("Car %d from Gate %d waiting for a spot.%n", car.getCarId(), car.getGateId());
        }
    }

    public void carLeaves(Car car) { // 20220027
        parkingSlots.release();
        System.out.printf("Car %d from Gate %d left after %d units of time. (Parking Status: %d spots occupied)%n", car.getCarId(), car.getGateId(), car.getParkingDuration(), getOccupiedSpots());

        if (!waitingQueue.isEmpty()) {
            Car nextCar = waitingQueue.poll();
            try {
                parkingSlots.acquire();
                nextCar.setWaitingTime(car.getArrivalTime() + car.getWaitingTime() + car.getParkingDuration() - nextCar.getArrivalTime() + 1);
                System.out.printf("Car %d from Gate %d parked after waiting for %d units of time. (Parking Status: %d spots occupied)%n", nextCar.getCarId(), nextCar.getGateId(), nextCar.getWaitingTime(), getOccupiedSpots());
                nextCar.park();
                totalServedCars++;
                servedCarsPerGate.put(nextCar.getGateId(), servedCarsPerGate.getOrDefault(nextCar.getGateId(), 0) + 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
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