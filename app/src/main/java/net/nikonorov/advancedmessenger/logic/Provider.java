package net.nikonorov.advancedmessenger.logic;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by vitaly on 27.01.16.
 */
public class Provider extends ContentProvider {

    final static String DB_NAME = "advmessenger";
    final static int DB_VERSION = 1;

    final static String _ID = "_id";

    final static String USER_TABLE = "users";
    final static String USER_ID = "login";
    final static String USER_DATA = "data";
    final static String USER_TIME = "time";

    static final String DB_CREATE_USERS = (new StringBuilder())
            .append("CREATE TABLE IF NOT EXISTS ")
            .append(USER_TABLE)
            .append("(")
            .append(_ID)
            .append(" integer primary key autoincrement, ")
            .append(USER_ID)
            .append(" text, ")
            .append(USER_TIME)
            .append(" integer, ")
            .append(USER_DATA)
            .append(" text);").toString();


    final static String DIALOGS_TABLE = "dialogs";
    final static String TO_USER = "to_user";
    final static String FROM_USER = "from_user";
    final static String DIALOGS_DATA = "data";
    final static String DIALOGS_TIME = "time";

    static final String DB_CREATE_DIALOGS = (new StringBuilder())
            .append("CREATE TABLE IF NOT EXISTS ")
            .append(DIALOGS_TABLE)
            .append("(")
            .append(_ID)
            .append(" integer primary key autoincrement, ")
            .append(TO_USER)
            .append(" text, ")
            .append(FROM_USER)
            .append(" text, ")
            .append(DIALOGS_DATA)
            .append(" text, ")
            .append(DIALOGS_TIME)
            .append(" integer);").toString();


    final static String IMPORT_TABLE = "import";
    final static String IMPORT_USER = "user";
    final static String IMPORT_DATA = "data";
    final static String IMPORT_TIME = "time";


    static final String DB_CREATE_IMPORT = (new StringBuilder())
            .append("CREATE TABLE IF NOT EXISTS ")
            .append(IMPORT_TABLE)
            .append("(")
            .append(_ID)
            .append(" integer primary key autoincrement, ")
            .append(IMPORT_USER)
            .append(" text, ")
            .append(IMPORT_DATA)
            .append(" text, ")
            .append(IMPORT_TIME)
            .append(" integer);").toString();

    final static String CONTACTS_TABLE = "contacts";
    final static String CONTACTS_USER = "user";
    final static String CONTACTS_DATA = "data";
    final static String CONTACTS_TIME = "time";


    static final String DB_CREATE_CONTACTS = (new StringBuilder())
            .append("CREATE TABLE IF NOT EXISTS ")
            .append(CONTACTS_TABLE)
            .append("(")
            .append(_ID)
            .append(" integer primary key autoincrement, ")
            .append(CONTACTS_USER)
            .append(" text, ")
            .append(CONTACTS_DATA)
            .append(" text, ")
            .append(CONTACTS_TIME)
            .append(" integer);").toString();

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
