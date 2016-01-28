package net.nikonorov.advancedmessenger.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import net.nikonorov.advancedmessenger.App;
import net.nikonorov.advancedmessenger.R;
import net.nikonorov.advancedmessenger.ServiceHelperListener;
import net.nikonorov.advancedmessenger.User;
import net.nikonorov.advancedmessenger.logic.MasterService;
import net.nikonorov.advancedmessenger.utils.Code;
import net.nikonorov.advancedmessenger.utils.TaskType;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vitaly on 24.01.16.
 */
public class ActivitySplash extends Activity implements ServiceHelperListener {

    private int PAUSE = 2000;
    private boolean isActive = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActive = true;
        String data = "";
        startService(new Intent(this, MasterService.class).putExtra("type", TaskType.CONNECT).putExtra("data", data));
        ((App)getApplication()).getServiceHelper().addListener(this);
        new Thread(new Launcher()).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((App)getApplication()).getServiceHelper().removeListener(this);
    }

    @Override
    protected void onStop() {
        isActive = false;
        super.onStop();
    }

    @Override
    public void onServiceHelperCallback(int taskType, String data, int code) {

        if(code == Code.OK) {
            if (taskType == TaskType.AUTH) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ActivitySplash.this, "Welcome " + User.getLogin(), Toast.LENGTH_SHORT).show();
                    }
                });

                try {
                    JSONObject jsonObject = new JSONObject(data);
                    User.setSid(jsonObject.getJSONObject("data").getString("sid"));
                    User.setCid(jsonObject.getJSONObject("data").getString("cid"));

                    SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    editor.putString("sid", User.getSid());
                    editor.putString("cid", User.getCid());

                    editor.putString("login", User.getLogin());
                    editor.putString("pass", User.getPass());

                    editor.commit();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(this, ActivityMain.class);
                startActivity(intent);
            }
        }


    }

    private class Launcher implements Runnable{

        @Override
        public void run() {
            try {
                Thread.sleep(PAUSE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(isActive){

                //SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String sid = sharedPref.getString("sid", null);
                String cid = sharedPref.getString("cid", null);
                String login = sharedPref.getString("login", null);
                String pass = sharedPref.getString("pass", null);

                if (sid != null && cid != null && login != null){
                    User.setSid(sid);
                    User.setCid(cid);
                    User.setLogin(login);

                    StringBuilder sb = new StringBuilder();

                    sb.append("{\"action\":\"auth\", \"data\":{\"login\":\"");
                    sb.append(login).append("\", ");
                    sb.append("\"pass\": \"").append(pass).append("\"}} ");

                    String reqObject = sb.toString();

                    ((App)getApplication()).getServiceHelper().executeCommand(TaskType.AUTH, reqObject, ActivitySplash.this);
                }else {
                    startActivity(new Intent(ActivitySplash.this, ActivitySign.class));
                    ActivitySplash.this.finish();
                }
            }

        }
    }


}
