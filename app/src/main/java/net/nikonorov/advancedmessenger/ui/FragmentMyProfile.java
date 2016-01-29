package net.nikonorov.advancedmessenger.ui;

import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
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
public class FragmentMyProfile extends CallableFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public FragmentMyProfile(){}

    private static int MSG_REFRESH = 1;

    private final String LOG_TAG = "MyProfile";

    private static final int URL_LOADER = 1;

    private final int ONE_MINUTE_MILLIS = 6000;

    private AmazingPicture avaAP     = null;
    private TextView       nickTV    = null;
    private TextView       emailTV   = null;
    private TextView       phoneTV   = null;
    private TextView       statutTV  = null;
    private Button         editBtn   = null;
    private Button         logoutBtn = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_profile, null);

        avaAP = (AmazingPicture) view.findViewById(R.id.my_profile_ava);
        nickTV = (TextView) view.findViewById(R.id.my_profile_nick);
        emailTV = (TextView) view.findViewById(R.id.my_profile_email);
        phoneTV = (TextView) view.findViewById(R.id.my_profile_phone);
        statutTV = (TextView) view.findViewById(R.id.my_profile_status);
        editBtn = (Button) view.findViewById(R.id.my_profile_edit_btn);
        logoutBtn = (Button) view.findViewById(R.id.my_profile_logout);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.setLogin(null);
                User.setCid(null);
                User.setSid(null);
                User.setPass(null);

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putString("sid", null);
                editor.putString("cid", null);

                editor.putString("login", null);
                editor.putString("pass", null);

                editor.commit();

                startActivity(new Intent(getActivity(), ActivitySign.class));
                getActivity().finish();

            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActivityMain)getActivity()).changeFragment(FragmentSet.MAINEDITPROFILE);
            }
        });

        getLoaderManager().initLoader(URL_LOADER, null, this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(URL_LOADER, null, this);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void correctCodeHandle(int taskType, String data) {
        //TODO
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case URL_LOADER:
                // Returns a new CursorLoader
                return new CursorLoader(
                        getActivity(),   // Parent activity context
                        Uri.parse("content://net.nikonorov.advancedmessenger.providers.db/users"),        // Table to query
                        null,     // Projection to return
                        "login = \'" + User.getLogin() + "\'",            // selection clause
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
                User.setPicture(jsonObject.getString("picture"));

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
        BufferClass.setAskedUser(User.getLogin());

        StringBuilder sb = new StringBuilder();

        sb.append("{\"action\":\"userinfo\", \"data\":{\"cid\":\"");
        sb.append(User.getCid()).append("\", ");
        sb.append("\"user\": \"").append(User.getLogin()).append("\", ");
        sb.append("\"sid\": \"").append(User.getSid()).append("\"}} ");

        String reqObject = sb.toString();
        Log.d(LOG_TAG, reqObject.toString());
        serviceHelper.executeCommand(TaskType.USERINFO, reqObject, getActivity());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
