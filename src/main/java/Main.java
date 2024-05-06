import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        // Create a ScheduledExecutorService with a single thread to manage the heartbeat scheduling
        ScheduledExecutorService heartbeatScheduler = Executors.newScheduledThreadPool(1);

        // Schedule the heartbeat task to run every second using the scheduleAtFixedRate method
        // The first argument is the Runnable task (Main::sendHeartbeat), the second argument is the initial delay (0 seconds),
        // the third argument is the period between consecutive executions (1 second), and the fourth argument is the TimeUnit (SECONDS)
        heartbeatScheduler.scheduleAtFixedRate(Main::sendHeartbeat, 0, 1, TimeUnit.SECONDS);
    }

    // Static method to send heartbeats
    private static void sendHeartbeat() {
        Heartbeat heartbeat = new Heartbeat();
        try {
            // Call the sendHeartbeat method on the Heartbeat object
            heartbeat.sendHeartbeat();
        } catch (Exception e) {
            // Print the stack trace if there's an exception during heartbeat sending
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
