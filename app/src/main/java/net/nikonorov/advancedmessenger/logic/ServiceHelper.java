package net.nikonorov.advancedmessenger.logic;

import android.app.Application;
import android.content.Intent;
import android.util.SparseArray;

import net.nikonorov.advancedmessenger.ServiceHelperListener;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by vitaly on 25.01.16.
 */
public class ServiceHelper {

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

}
