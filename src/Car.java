public class Car extends Thread {
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

    @Override
    public void run() {
        try {
            sleep(arrivalTime * 500);
            parkingLot.carArrives(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void park() {
        try {
            sleep(parkingDuration * 500);
            parkingLot.carLeaves(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getParkingDuration() {
        return parkingDuration;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }
}