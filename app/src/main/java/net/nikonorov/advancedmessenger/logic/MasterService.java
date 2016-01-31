package net.nikonorov.advancedmessenger.logic;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;

import net.nikonorov.advancedmessenger.App;
import net.nikonorov.advancedmessenger.MasterServiceListener;
import net.nikonorov.advancedmessenger.R;
import net.nikonorov.advancedmessenger.ReaderListener;
import net.nikonorov.advancedmessenger.ui.ActivityMain;
import net.nikonorov.advancedmessenger.utils.Code;
import net.nikonorov.advancedmessenger.utils.TaskType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by vitaly on 26.01.16.
 */
public class MasterService extends Service implements ReaderListener {

    private final String LOG_TAG = "MasterService Log: ";

    private String HOST    = "188.166.49.215";
    private int PORT    = 7788;

    private boolean isWork = false;

    private MasterServiceListener listener;

    private Socket socket;

    private InputStream is;
    private BufferedOutputStream bout;

    @Override
    public void onCreate() {
        super.onCreate();
        isWork = true;
        listener = ((App)getApplication()).getServiceHelper();
        new Connector().start();
    }

    @Override
    public void onReadEvent(int taskType, String response, int code) {

        if(taskType == TaskType.EV_MESSAGE && ((App)getApplication()).isAppHidden()){
            sendNotification(response);
        }

        listener.onRecieveMasterResponse(taskType, response, code);
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
                new SocketReader(is, isWork, MasterService.this).start();
            }

        }
    }

    public void sendNotification(String data){

        Notification.Builder builder = new Notification.Builder(MasterService.this);

        JSONObject object = null;

        try {
            object = (new JSONObject(data)).getJSONObject("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            Intent intent = new Intent(MasterService.this, ActivityMain.class);
            intent.putExtra("uid", object.getString("from"));
            PendingIntent pIntent = PendingIntent.getActivity(MasterService.this, (int) System.currentTimeMillis(), intent, 0);

            builder
                    .setContentIntent(pIntent)
                    .setSmallIcon(R.drawable.icon)
                    .setTicker("new message from " + object.getString("nick"))
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setContentTitle("Advanced messenger")
                    .setContentText(Html.fromHtml("<b>"+object.getString("nick") +"</b>: "+object.getString("body")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getIntExtra("type", -1) != TaskType.CONNECT){
            sendMessage(intent);
        }

        return START_REDELIVER_INTENT;
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
            listener.onRecieveMasterResponse(TaskType.CONNECT, null, Code.TIMEOUT);
            onCreate();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
