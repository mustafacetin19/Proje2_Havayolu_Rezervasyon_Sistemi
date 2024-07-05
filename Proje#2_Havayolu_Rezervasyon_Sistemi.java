import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

class ReservationSystem {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private int seatsAvailable;

    public ReservationSystem(int seats) {
        this.seatsAvailable = seats;
    }

    private String getCurrentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
    }

    // Writer method to make a reservation
    public void makeReservation() {
        lock.writeLock().lock();
        try {
            if (seatsAvailable > 0) {
                seatsAvailable--;
                System.out.println("Time: " + getCurrentTime() + " " + Thread.currentThread().getName() + " tries to book the seat");
                System.out.println("Time: " + getCurrentTime() + " " + Thread.currentThread().getName() + " booked seat number 1 successfully.");
            } else {
                System.out.println("Time: " + getCurrentTime() + " " + Thread.currentThread().getName() + " tried to book the seat, but no seats are available.");
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    // Writer method to cancel a reservation
    public void cancelReservation() {
        lock.writeLock().lock();
        try {
            seatsAvailable++;
            System.out.println("Time: " + getCurrentTime() + " " + Thread.currentThread().getName() + " cancelled a reservation. Seats left: " + seatsAvailable);
        } finally {
            lock.writeLock().unlock();
        }
    }

    // Reader method to query available seats
    public void queryReservation() {
        lock.readLock().lock();
        try {
            System.out.println("Time: " + getCurrentTime() + " " + Thread.currentThread().getName() + " looks for available seats. Seats available: " + seatsAvailable);
        } finally {
            lock.readLock().unlock();
        }
    }
}

// Writer thread class
class WriterThread implements Runnable {
    private final ReservationSystem reservationSystem;
    private final String operation;

    public WriterThread(ReservationSystem reservationSystem, String operation) {
        this.reservationSystem = reservationSystem;
        this.operation = operation;
    }

    @Override
    public void run() {
        if (operation.equals("reserve")) {
            reservationSystem.makeReservation();
        } else if (operation.equals("cancel")) {
            reservationSystem.cancelReservation();
        }
    }
}

// Reader thread class
class ReaderThread implements Runnable {
    private final ReservationSystem reservationSystem;

    public ReaderThread(ReservationSystem reservationSystem) {
        this.reservationSystem = reservationSystem;
    }

    @Override
    public void run() {
        reservationSystem.queryReservation();
    }
}

public class Main {
    public static void main(String[] args) {
        ReservationSystem reservationSystem = new ReservationSystem(10);

        Thread writer1 = new Thread(new WriterThread(reservationSystem, "reserve"), "Writer1");
        Thread writer2 = new Thread(new WriterThread(reservationSystem, "reserve"), "Writer2");
        Thread writer3 = new Thread(new WriterThread(reservationSystem, "reserve"), "Writer3");

        Thread reader1 = new Thread(new ReaderThread(reservationSystem), "Reader1");
        Thread reader2 = new Thread(new ReaderThread(reservationSystem), "Reader2");
        Thread reader3 = new Thread(new ReaderThread(reservationSystem), "Reader3");

        writer1.start();
        writer2.start();
        writer3.start();
        
        reader1.start();
        reader2.start();
        reader3.start();

        try {
            writer1.join();
            writer2.join();
            writer3.join();
            reader1.join();
            reader2.join();
            reader3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
