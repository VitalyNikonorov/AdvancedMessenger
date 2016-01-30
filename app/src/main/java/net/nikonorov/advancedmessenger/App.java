package net.nikonorov.advancedmessenger;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import net.nikonorov.advancedmessenger.logic.ServiceHelper;

/**
 * Created by vitaly on 26.01.16.
 */
public class App extends Application{

    private ServiceHelper serviceHelper;
    private static int activeActivity = 0;

    public ServiceHelper getServiceHelper(){
        return serviceHelper;
    }

    public boolean isAppHidden(){
        return (activeActivity == 0);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        serviceHelper = new ServiceHelper(this);
    }

    public static void onActivityStart(){
        ++activeActivity;
    }

    public static void onActivityStop(){
        --activeActivity;
    }

}
