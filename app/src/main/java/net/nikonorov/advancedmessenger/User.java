package net.nikonorov.advancedmessenger;

/**
 * Created by vitaly on 26.01.16.
 */
public class User {
    private static String login;
    private static String pass;

    private static String sid;
    private static String cid;

    private static String picture;

    public static void setLogin(String login) {
        User.login = login;
    }

    public static void setPass(String pass) {
        User.pass = pass;
    }

    public static String getLogin() {
        return login;
    }

    public static String getPass() {
        return pass;
    }

    public static void setSid(String sid) {
        User.sid = sid;
    }

    public static String getSid() {
        return sid;
    }

    public static void setCid(String cid) {
        User.cid = cid;
    }

    public static String getCid() {
        return cid;
    }

    public static String getPicture() {
        return picture;
    }

    public static void setPicture(String picture) {
        User.picture = picture;
    }
}
