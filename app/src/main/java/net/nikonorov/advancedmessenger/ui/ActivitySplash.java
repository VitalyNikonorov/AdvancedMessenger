package net.nikonorov.advancedmessenger.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import net.nikonorov.advancedmessenger.R;

/**
 * Created by vitaly on 24.01.16.
 */
public class ActivitySplash extends Activity {

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
        new Thread(new Launcher()).start();
    }

    @Override
    protected void onStop() {
        isActive = false;
        super.onStop();
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
                startActivity(new Intent(ActivitySplash.this, ActivitySign.class));
                ActivitySplash.this.finish();
            }

        }
    }


}
