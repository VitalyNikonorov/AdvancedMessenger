package net.nikonorov.advancedmessenger;

import android.app.Application;

import net.nikonorov.advancedmessenger.logic.ServiceHelper;

/**
 * Created by vitaly on 26.01.16.
 */
public class App extends Application {

    private ServiceHelper serviceHelper;

    public ServiceHelper getServiceHelper(){
        return serviceHelper;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        serviceHelper = new ServiceHelper(this);
    }
}
