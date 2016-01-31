package net.nikonorov.advancedmessenger.logic;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import net.nikonorov.advancedmessenger.ReaderListener;
import net.nikonorov.advancedmessenger.User;
import net.nikonorov.advancedmessenger.utils.BufferClass;
import net.nikonorov.advancedmessenger.utils.TaskType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by vitaly on 26.01.16.
 */
public class SocketReader extends Thread{

    final static String CONTACTS_TABLE = "contacts";
    final static String USER_TABLE = "users";
    final static String IMPORT_TABLE = "import";
    final static String DIALOGS_TABLE = "dialogs";

    private boolean isWork;
    private InputStream is;

    private ReaderListener readerListener;

    private final String LOG_TAG = "MasterService Log: ";

    public SocketReader(InputStream is, boolean isWork, ReaderListener readerListener){
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
                    //Log.i(LOG_TAG, "I RECEIVE IT: " + sb.toString());

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

                    HashMap<String, String> dataSetUser = new HashMap<>();
                    dataSetUser.put("login", BufferClass.getAskedUser());
                    dataSetUser.put("data", jsonObject.getJSONObject("data").toString());
                    dataSetUser.put("time", Long.valueOf(System.currentTimeMillis()).toString());

                    saveData(dataSetUser, USER_TABLE);
                    break;

                case "contactlist":
                    taskType = TaskType.CONTACTLIST;

                    HashMap<String, String> dataSetContacts = new HashMap<>();
                    dataSetContacts.put("user", User.getLogin());
                    dataSetContacts.put("data", jsonObject.getJSONObject("data").toString());
                    dataSetContacts.put("time", Long.valueOf(System.currentTimeMillis()).toString());

                    saveData(dataSetContacts, CONTACTS_TABLE);
                    break;

                case "addcontact":
                    taskType = TaskType.ADDCONTACT;
                    break;

                case "delcontact":
                    taskType = TaskType.DELCONTACT;
                    break;

                case "import":
                    taskType = TaskType.IMPORT;

                    HashMap<String, String> dataSetImport = new HashMap<>();
                    dataSetImport.put("user", User.getLogin());
                    dataSetImport.put("data", jsonObject.getJSONObject("data").toString());
                    dataSetImport.put("time", Long.valueOf(System.currentTimeMillis()).toString());

                    saveData(dataSetImport, IMPORT_TABLE);

                    break;

                case "setuserinfo":
                    taskType = TaskType.SETUSERINFO;
                    break;

                case "message":
                    taskType = TaskType.MESSAGE;
                    break;

                case "ev_message":
                    taskType = TaskType.EV_MESSAGE;


                    if(!jsonObject.getJSONObject("data").getString("from").equals(User.getLogin())) {

                        HashMap<String, String> dataSetDialogs = new HashMap<>();
                        dataSetDialogs.put("to_user", User.getLogin());
                        dataSetDialogs.put("from_user", jsonObject.getJSONObject("data").getString("from"));
                        dataSetDialogs.put("data", jsonObject.getJSONObject("data").toString());
                        dataSetDialogs.put("time", Long.valueOf(System.currentTimeMillis()).toString());

                        saveData(dataSetDialogs, DIALOGS_TABLE);

                    }
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

    private void saveData(HashMap<String, String> data, String table) {
        ContentValues cv = new ContentValues();
        Set<String> set = data.keySet();

        for (String key : set){
            cv.put(key, data.get(key));
        }

        final Uri URI = Uri
                .parse("content://net.nikonorov.advancedmessenger.providers.db/"+table);
        Uri newUri = ((Context)readerListener).getContentResolver().insert(URI, cv);

        //Log.i(LOG_TAG, "insert, result Uri : " + newUri.toString());
    }
}
