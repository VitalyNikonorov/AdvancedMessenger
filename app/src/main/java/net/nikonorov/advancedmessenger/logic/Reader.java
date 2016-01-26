package net.nikonorov.advancedmessenger.logic;

import android.util.Log;

import net.nikonorov.advancedmessenger.ReaderListener;
import net.nikonorov.advancedmessenger.utils.TaskType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by vitaly on 26.01.16.
 */
public class Reader extends Thread{

    private boolean isWork;
    private InputStream is;

    private ReaderListener readerListener;

    private final String LOG_TAG = "MasterService Log: ";

    public Reader(InputStream is, boolean isWork, ReaderListener readerListener){
        this.isWork = isWork;
        this.is = is;
        this.readerListener = readerListener;
    }

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

                    sendIncomingData(sb.toString());

                    sb.setLength(0);
                    bracketCount = 0;
                }else{
                    sb.append(temp);
                }
            }
        }
    }

    public void sendIncomingData(String data){
        int taskType = -1;
        int code = -1;

        try {
            JSONObject jsonObject = new JSONObject(data);

            switch (jsonObject.getString("action")){
                case "register":
                    taskType = TaskType.REGISTRATE;
                    break;

                case "auth":
                    taskType = TaskType.AUTH;
                    break;

                case "userinfo":
                    taskType = TaskType.USERINFO;
                    break;

                case "contactlist":
                    taskType = TaskType.CONTACTLIST;
                    break;

                case "addcontact":
                    taskType = TaskType.ADDCONTACT;
                    break;

                case "delcontact":
                    taskType = TaskType.DELCONTACT;
                    break;

                case "import":
                    taskType = TaskType.IMPORT;
                    break;

                case "setuserinfo":
                    taskType = TaskType.SETUSERINFO;
                    break;

                case "message":
                    taskType = TaskType.MESSAGE;
                    break;

                case "ev_message":
                    taskType = TaskType.EV_MESSAGE;
                    break;
            }

            if (taskType != TaskType.EV_MESSAGE) {
                code = jsonObject.getJSONObject("data").getInt("status");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        readerListener.onReadEvent(taskType, data, code);  //TODO
    }

}
