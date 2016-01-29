package net.nikonorov.advancedmessenger.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import net.nikonorov.advancedmessenger.R;
import net.nikonorov.advancedmessenger.User;
import net.nikonorov.advancedmessenger.utils.AmazingPicture;
import net.nikonorov.advancedmessenger.utils.BufferClass;
import net.nikonorov.advancedmessenger.utils.TaskType;
import net.nikonorov.advancedmessenger.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vitaly on 28.01.16.
 */
public class FragmentProfile extends CallableFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public FragmentProfile(){}

    private final String LOG_TAG = "Profile";

    private String user = null;

    private static final int URL_LOADER = 1;

    private final int ONE_MINUTE_MILLIS = 6000;

    private AmazingPicture avaAP     = null;
    private TextView       nickTV    = null;
    private TextView       emailTV   = null;
    private TextView       phoneTV   = null;
    private TextView       statutTV  = null;
    private Button         addFriend = null;
    private Button         delFriend = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, null);

        avaAP = (AmazingPicture) view.findViewById(R.id.profile_ava);
        nickTV = (TextView) view.findViewById(R.id.profile_nick);
        emailTV = (TextView) view.findViewById(R.id.profile_email);
        phoneTV = (TextView) view.findViewById(R.id.profile_phone);
        statutTV = (TextView) view.findViewById(R.id.profile_status);
        addFriend = (Button) view.findViewById(R.id.profile_add_btn);
        delFriend = (Button) view.findViewById(R.id.profile_del_btn);

        getLoaderManager().initLoader(URL_LOADER, null, this);

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder sb = new StringBuilder();

                sb.append("{\"action\":\"addcontact\", \"data\":{\"cid\":\"");
                sb.append(User.getCid()).append("\", \"sid\":\"");
                sb.append(User.getSid()).append("\", ");
                sb.append("\"uid\": \"").append(user).append("\"}} ");

                String reqObject = sb.toString();

                serviceHelper.executeCommand(TaskType.ADDCONTACT, reqObject, getActivity());
            }
        });

        delFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder sb = new StringBuilder();

                sb.append("{\"action\":\"delcontact\", \"data\":{\"cid\":\"");
                sb.append(User.getCid()).append("\", \"sid\":\"");
                sb.append(User.getSid()).append("\", ");
                sb.append("\"uid\": \"").append(user).append("\"}} ");

                String reqObject = sb.toString();

                serviceHelper.executeCommand(TaskType.DELCONTACT, reqObject, getActivity());
            }
        });

        return view;
    }

    public void setUser(String user){
        this.user = user;
    }

    @Override
    public void onPause() {
        super.onPause();
        user = User.getLogin();
    }

    @Override
    public void correctCodeHandle(int taskType, String data) {
        //TODO
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (user == null){
            user = User.getLogin();
        }
        switch (id) {
            case URL_LOADER:
                // Returns a new CursorLoader
                return new CursorLoader(
                        getActivity(),   // Parent activity context
                        Uri.parse("content://net.nikonorov.advancedmessenger.providers.db/users"),        // Table to query
                        null,     // Projection to return
                        "login = \'" + user + "\'",            // selection clause
                        null,            // No selection arguments
                        null             // Default sort order
                );
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        String userData = "";
        long time = 0;

        if(cursor == null){
            getUserFromNet();
        }else {
            if (cursor.moveToFirst()){
                while(!cursor.isAfterLast()){
                    userData = cursor.getString(cursor.getColumnIndex("data"));
                    time = Long.valueOf(cursor.getString(cursor.getColumnIndex("time")));
                    cursor.moveToNext();
                }
            }

            try {
                JSONObject jsonObject = new JSONObject(userData);

                Utils.setPhoto(jsonObject.getString("picture"), avaAP);
                nickTV.setText(jsonObject.getString("nick"));
                emailTV.setText(jsonObject.getString("email"));
                phoneTV.setText(jsonObject.getString("phone"));
                statutTV.setText(jsonObject.getString("user_status"));

                Log.i(LOG_TAG, "upated");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(time < (System.currentTimeMillis() - ONE_MINUTE_MILLIS)){
            getUserFromNet();
        }
    }

    private void getUserFromNet(){

        if(user == null){
            user = User.getLogin();
        }

        BufferClass.setAskedUser(user);

        StringBuilder sb = new StringBuilder();

        sb.append("{\"action\":\"userinfo\", \"data\":{\"cid\":\"");
        sb.append(User.getCid()).append("\", ");
        sb.append("\"user\": \"").append(user).append("\", ");
        sb.append("\"sid\": \"").append(User.getSid()).append("\"}} ");

        String reqObject = sb.toString();
        Log.d(LOG_TAG, reqObject.toString());
        serviceHelper.executeCommand(TaskType.USERINFO, reqObject, getActivity());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
