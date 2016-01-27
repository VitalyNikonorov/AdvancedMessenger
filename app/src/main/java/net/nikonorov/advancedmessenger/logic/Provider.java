package net.nikonorov.advancedmessenger.logic;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by vitaly on 27.01.16.
 */
public class Provider extends ContentProvider {

    final String LOG_TAG = "Provider ";

    SQLiteOpenHelper dbHelper;
    SQLiteDatabase db;

    static final String AUTHORITY = "net.nikonorov.advancedmessenger.providers.db";

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
            .append(" text UNIQUE ON CONFLICT REPLACE,")
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
            .append(" text UNIQUE ON CONFLICT REPLACE, ")
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
            .append(" text UNIQUE ON CONFLICT REPLACE, ")
            .append(CONTACTS_DATA)
            .append(" text, ")
            .append(CONTACTS_TIME)
            .append(" integer);").toString();

    @Override
    public boolean onCreate() {
        dbHelper = new SQLiteOpenHelper(getContext(), DB_NAME, null, DB_VERSION) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL(DB_CREATE_USERS);
                Log.i(LOG_TAG, "users table created");
                db.execSQL(DB_CREATE_CONTACTS);
                Log.i(LOG_TAG, "planned list table created");
                db.execSQL(DB_CREATE_DIALOGS);
                Log.i(LOG_TAG, "read list table created");
                db.execSQL(DB_CREATE_IMPORT);
                Log.i(LOG_TAG, "books table created");
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                onCreate(db);
            }
        };
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.i(LOG_TAG, "!query data " + uri.toString());

        db = dbHelper.getWritableDatabase();
        String table = uri.getLastPathSegment();

        Cursor cursor = db.query(table, projection, selection,
                selectionArgs, null, null, sortOrder);

        Uri CONTENT_URI = Uri.parse("content://"
                + AUTHORITY + "/" + table);

        cursor.setNotificationUri(getContext().getContentResolver(),
                CONTENT_URI);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(LOG_TAG, "insert, " + uri.toString());

        db = dbHelper.getWritableDatabase();

        String table = uri.getLastPathSegment();

        long rowID = db.insert(table, null, values);

        Uri CONTENT_URI = Uri.parse("content://"
                + AUTHORITY + "/" + table);

        Uri resultUri = ContentUris.withAppendedId(CONTENT_URI, rowID);

        return resultUri;
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
