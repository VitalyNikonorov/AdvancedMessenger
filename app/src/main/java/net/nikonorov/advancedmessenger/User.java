package net.nikonorov.advancedmessenger;

/**
 * Created by vitaly on 26.01.16.
 */
public class User {
    private static String login;
    private static String pass;

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
}
