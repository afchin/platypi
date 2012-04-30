package edu.caltech.cs3.platypi;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Collects location and signal data every minute, writing it to a local
 * database. When internet connection is available, sends data in local database
 * to proudplatypi.appspot.com by POST request.
 */
public class CollectDataService extends Service {
    private final String TAG = "CollectDataService";
    private final int INTERVAL_ms = 30 * 1000; // one minute in milliseconds
    private boolean running = false;
    private SignalFinderApp app;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        app = (SignalFinderApp) getApplication();
        super.onCreate();
    }

    /**
     * If data collection service isn't already running, starts new thread which
     * collects data every INTERVAL_ms milliseconds and attempts to send local
     * data to the server.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStart");

        if (running) {
            return super.onStartCommand(intent, flags, startId);
        }
        running = true;

        // Run in new thread to avoid locking up the UI with the sleep command.
        new Thread() {
            public void run() {
                while (running) {
                        app.insertCurrentData();
                        app.sendLocalData();
                    try {
                        Thread.sleep(INTERVAL_ms);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        running = false;
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }
}
