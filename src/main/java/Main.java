import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        // Create a ScheduledExecutorService with a single thread
        ScheduledExecutorService heartbeatScheduler = Executors.newScheduledThreadPool(1);

        // Schedule the heartbeat task to run every second
        heartbeatScheduler.scheduleAtFixedRate(Main::sendHeartbeat, 0, 1, TimeUnit.SECONDS);
    }

    // Static method to send heartbeats
    private static void sendHeartbeat() {
        Heartbeat heartbeat = new Heartbeat();
        try {
            heartbeat.sendHeartbeat();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// Heartbeat class
class Heartbeat {
    // Send heartbeat method
    void sendHeartbeat() throws Exception {
        // Implementation goes here
    }
}
