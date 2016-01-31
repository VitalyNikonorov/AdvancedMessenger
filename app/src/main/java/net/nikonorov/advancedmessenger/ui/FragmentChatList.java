package net.nikonorov.advancedmessenger.ui;

import android.app.Dialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import net.nikonorov.advancedmessenger.R;
import net.nikonorov.advancedmessenger.User;
import net.nikonorov.advancedmessenger.ui.adapters.ContactListAdapter;
import net.nikonorov.advancedmessenger.utils.TaskType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vitaly on 25.01.16.
 */
public class FragmentChatList extends CallableFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ContactListAdapter adapter = null;
    private ArrayList<JSONObject> data = new ArrayList<>();
    private RecyclerView recyclerView  = null;
    private ImageButton addUserBtn = null;

    private static final int URL_LOADER = 0;

    private final int ONE_MINUTE_MILLIS = 6000;

    private final String LOG_TAG = "ChatList Fragment";

    public FragmentChatList(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, null);

        recyclerView = (RecyclerView) view.findViewById(R.id.chats_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new ContactListAdapter(data, (ActivityMain)getActivity());
        recyclerView.setAdapter(adapter);

        getLoaderManager().initLoader(URL_LOADER, null, this);

        addUserBtn = (ImageButton) view.findViewById(R.id.add_users_btn);

        addUserBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // custom dialog
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.popup_add_user);
                dialog.setTitle("Title...");

                Button btnDismiss = (Button) dialog.findViewById(R.id.add_user_close);
                btnDismiss.setOnClickListener(new Button.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                Button addBtn = (Button) dialog.findViewById(R.id.add_user_byuid_btn);
                final EditText addUserUidET = (EditText) dialog.findViewById(R.id.add_user_uid);
                addBtn.setOnClickListener(new Button.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        StringBuilder sb = new StringBuilder();

                        sb.append("{\"action\":\"addcontact\", \"data\":{\"cid\":\"");
                        sb.append(User.getCid()).append("\", \"sid\":\"");
                        sb.append(User.getSid()).append("\", ");
                        sb.append("\"uid\": \"").append(addUserUidET.getText().toString()).append("\"}} ");

                        String reqObject = sb.toString();

                        serviceHelper.executeCommand(TaskType.DELCONTACT, reqObject, getActivity());

                        dialog.dismiss();
                    }
                });


                Button dialogButton = (Button) dialog.findViewById(R.id.add_user_close);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void getContactListFromNet(){

        StringBuilder sb = new StringBuilder();

        sb.append("{\"action\":\"contactlist\", \"data\":{\"cid\":\"");
        sb.append(User.getCid()).append("\", ");
        sb.append("\"sid\": \"").append(User.getSid()).append("\"}} ");

        String reqObject = sb.toString();
        Log.d(LOG_TAG, reqObject.toString());
        serviceHelper.executeCommand(TaskType.CONTACTLIST, reqObject, getActivity());
    }

    @Override
    public void correctCodeHandle(int taskType, String data) {
        Log.i("chat list ", "Correct Code");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case URL_LOADER:
                // Returns a new CursorLoader
                return new CursorLoader(
                        getActivity(),   // Parent activity context
                        Uri.parse("content://net.nikonorov.advancedmessenger.providers.db/contacts"),        // Table to query
                        null,     // Projection to return
                        "user = \'"+ User.getLogin()+"\'",            // selection clause
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

        String dataList = "";
        long time = 0;

        if((cursor == null) || (cursor.getCount() == 0)){
            getContactListFromNet();
        }else {
            if (cursor.moveToFirst()){
                while(!cursor.isAfterLast()){
                    dataList = cursor.getString(cursor.getColumnIndex("data"));
                    time = Long.valueOf(cursor.getString(cursor.getColumnIndex("time")));
                    cursor.moveToNext();
                }
            }

            try {
                JSONArray list = (new JSONObject(dataList)).getJSONArray("list");

                data.clear();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

                for(int i = 0; i < list.length(); i++){
                    data.add(list.getJSONObject(i));
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(time < (System.currentTimeMillis() - ONE_MINUTE_MILLIS)){
            getContactListFromNet();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
