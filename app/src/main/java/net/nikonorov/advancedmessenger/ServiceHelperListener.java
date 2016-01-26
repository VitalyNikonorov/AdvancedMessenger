package net.nikonorov.advancedmessenger;

/**
 * Created by vitaly on 26.01.16.
 */
public interface ServiceHelperListener {
    void onServiceHelperCallback(int taskType, String data, int code);
}
