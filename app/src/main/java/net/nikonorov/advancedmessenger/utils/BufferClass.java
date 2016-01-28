package net.nikonorov.advancedmessenger.utils;

/**
 * Created by vitaly on 28.01.16.
 */
public class BufferClass {
    private static String askedUser;

    public static String getAskedUser() {
        return askedUser;
    }

    public static void setAskedUser(String askedUser) {
        BufferClass.askedUser = askedUser;
    }
}
