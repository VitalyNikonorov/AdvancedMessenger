package net.nikonorov.advancedmessenger.logic;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by vitaly on 26.01.16.
 */
public class MasterService extends Service {

    private final String LOG_TAG = "MasterService Log: ";

    private String HOST    = "188.166.49.215";
    private int PORT    = 7788;

    private boolean isWork = false;

    private Socket socket;

    private InputStream is;
    private BufferedOutputStream bout;

    @Override
    public void onCreate() {
        super.onCreate();
        new Connector().start();
        isWork = true;
    }

    private class Connector extends Thread{

        @Override
        public void run() {
            super.run();

            try {
                socket = new Socket(HOST, PORT);

                is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();
                bout = new BufferedOutputStream(os);
            } catch (IOException e) {
                e.printStackTrace();
            }


            if (socket != null) {
                new Reader().start();
            }

        }
    }

    public class Reader extends Thread{

        Reader(){}

        @Override
        public void run() {
            int readBytes = 0;
            byte[] buffer = new byte[1024];

            String temp = null;
            int bracketCount = 0;

            StringBuilder sb = new StringBuilder();

            while (isWork) {
                try {
                    readBytes = is.read(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (readBytes != -1) {

                    byte[] readBuffer = new byte[readBytes];

                    System.arraycopy(buffer, 0, readBuffer, 0, readBytes);

                    temp = new String(readBuffer);

                    for( int i = 0; i < temp.length(); i++ ) {
                        if( temp.charAt(i) == '{' ) {
                            bracketCount++;
                        }else if( temp.charAt(i) == '}' ) {
                            bracketCount--;
                        }
                    }

                    if( bracketCount < 1 ){
                        sb.append(temp);
                        Log.i(LOG_TAG, "I RECEIVE IT: " + sb.toString());
                        //Intent intent = new Intent(ActivityBase.BROADCAST_EVENT);
                        //intent.putExtra("data", sb.toString());
                        //sendBroadcast(intent);

                        sb.setLength(0);
                        bracketCount = 0;
                    }else{
                        sb.append(temp);
                    }
                }
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "doSomeCommand");
        new Connector().start();
        sendMessage(intent);

        return super.onStartCommand(intent, flags, startId);
    }

    private void sendMessage(Intent intent){
        try {
            Log.d(LOG_TAG, "Try to send: ");
            Log.d(LOG_TAG, intent.getStringExtra("data"));

            String sdata = intent.getStringExtra("data");

            bout.write(sdata.getBytes());
            bout.flush();
            Log.d(LOG_TAG, "gone");

        } catch (IOException e) {
            e.printStackTrace();
            onCreate();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
