package net.nikonorov.advancedmessenger.logic;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;

import net.nikonorov.advancedmessenger.MasterServiceListener;
import net.nikonorov.advancedmessenger.ServiceHelperListener;
import net.nikonorov.advancedmessenger.utils.Code;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by vitaly on 25.01.16.
 */
public class ServiceHelper implements MasterServiceListener {

    private final String LOG_TAG = "ServiceHelper";

    Application application;

    public ServiceHelper(Application application){
        this.application = application;
    }

    private ArrayList<ServiceHelperListener> listeners = new ArrayList<ServiceHelperListener>();

    private AtomicInteger idCounter = new AtomicInteger();

    private SparseArray<Intent> pendingActivities = new SparseArray<Intent>();

    public void addListener(ServiceHelperListener listener){
        listeners.add(listener);
    }

    public void removeListener(ServiceHelperListener listener){
        listeners.remove(listener);
    }

    public void executeCommand (int taskType, String data, Activity activity) {
        activity.startService(new Intent(activity, MasterService.class).putExtra("type", taskType).putExtra("data", data));
    }

    @Override
    public void onRecieveMasterResponse(int taskType, String response, int code) {
        Log.i(LOG_TAG, "Receive it: task" + (new Integer(taskType).toString()) + " error code: " + (new Integer(code).toString()) +" response: " +response);

        Iterator it = listeners.iterator();

        while (it.hasNext()) {

            ServiceHelperListener item = (ServiceHelperListener) it.next();
            item.onServiceHelperCallback(taskType, response, code);
        }

    }
}
