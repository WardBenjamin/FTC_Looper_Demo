package com.testpak;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Looper {
    ScheduledExecutorService scheduler;
    private final Object taskRunningLock_ = new Object();
    private final long period = 10; // 100 hz = 10 milliseconds

    boolean running = false;

    private double timestamp = 0, dt = 0;

    List<Loop> loops = new ArrayList<>();

    private final CrashTrackingRunnable runnable = new CrashTrackingRunnable() {
        @Override
        public void runCrashTracked() {
            synchronized (taskRunningLock_) {
                if (running) {
                    double now = System.nanoTime() * Constants.NANO_TO_MILLI;
                    loops.forEach(Loop::onLoop);
                    dt = now - timestamp;
                    timestamp = now;
                    System.out.println(Math.round(dt / 1e11) / 10f);
                }
            }
        }
    };

    public Looper() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        running = false;
        loops = new ArrayList<>();
    }

    public synchronized void register(Loop loop) {
        synchronized (taskRunningLock_) {
            loops.add(loop);
        }
    }

    public synchronized void start() {
        if (!running) {
            System.out.println("Starting loops");
            synchronized (taskRunningLock_) {
                timestamp = System.nanoTime() * Constants.NANO_TO_MILLI;
                loops.forEach(Loop::onStart);
                running = true;
            }
            scheduler.scheduleAtFixedRate(runnable, 0, period, TimeUnit.MILLISECONDS);
        }
    }

    public synchronized void stop() {
        if (running) {
            Log.w(Constants.LogTag, "Stopping loops");

            scheduler.shutdown();

            try {
                if (!scheduler.awaitTermination(100, TimeUnit.MILLISECONDS)) {
                    scheduler.shutdownNow();
                    if (!scheduler.awaitTermination(100, TimeUnit.MILLISECONDS)) {
                        throw new InterruptedException("Scheduler did not terminate");
                    }
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                CrashTracker.logThrowableCrash(e);
            }

            synchronized (taskRunningLock_) {
                running = false;
                loops.forEach(Loop::onStop);
            }
        }
    }
}