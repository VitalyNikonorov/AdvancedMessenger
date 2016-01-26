package net.nikonorov.advancedmessenger;

/**
 * Created by vitaly on 26.01.16.
 */
public interface MasterServiceListener {
    void onRecieveMasterResponse(int taskType, String response, int code);
}