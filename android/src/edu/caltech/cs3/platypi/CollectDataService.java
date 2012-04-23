package edu.caltech.cs3.platypi;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Collects location and signal data every 10 minutes, writing it to a file
 * on the sdcard. Eventually should send the data to the server, but this is
 * not yet implemented.
 */
public class CollectDataService extends Service {
    private final String TAG = "CollectDataService";
    private final int INTERVAL = 600 * 1000; // ten minutes in milliseconds
    private boolean running;

    @Override public IBinder onBind(Intent arg0) { return null; }

    @Override
    public void onCreate() {
        Log.d(TAG,"onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStart");

        if (running) { return super.onStartCommand(intent, flags, startId); }
        running = true;

        final CollectDataApp app = (CollectDataApp) getApplication();

        // Regularly wake up and collect data. Run in new thread to avoid
        // locking up the UI with the sleep command.
        new Thread() {
            public void run() {
                while (running) {
                    app.submitToDB(app.getLatitude(), app.getLongitude(),
                            app.getSignalStrength());
                    try {
                        Thread.sleep(INTERVAL);
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
        Log.d(TAG,"onDestroy");
        super.onDestroy();
    }
}
