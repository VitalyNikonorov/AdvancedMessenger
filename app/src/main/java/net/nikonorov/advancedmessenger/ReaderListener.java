package net.nikonorov.advancedmessenger;

/**
 * Created by vitaly on 26.01.16.
 */
public interface ReaderListener {
    void onReadEvent(int taskType, String response, int code);
}
