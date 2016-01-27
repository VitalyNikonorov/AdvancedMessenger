package net.nikonorov.advancedmessenger.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
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
import android.widget.SimpleCursorAdapter;

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

    SimpleCursorAdapter cursorAdapter;

    private static final int URL_LOADER = 0;

    private final int ONE_MINUTE_MILLIS = 6000;

    private final String LOG_TAG = "ChatList Fragment";

    private Cursor cursor = null;

    public FragmentChatList(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, null);

        recyclerView = (RecyclerView) view.findViewById(R.id.chats_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new ContactListAdapter(new ArrayList<JSONObject>(data));
        recyclerView.setAdapter(adapter);

        getLoaderManager().initLoader(URL_LOADER, null, this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        cursor = getActivity().getContentResolver().query(Uri.parse("content://net.nikonorov.advancedmessenger.providers.db/contacts"), null, "user = \'"+ User.getLogin()+"\'",
                null, null);

        String dataList = "";
        long time = 0;

        if(cursor == null){
            getContactListFromNet();
        }else {
            if (cursor.moveToFirst()){
                while(!cursor.isAfterLast()){
                    dataList = cursor.getString(cursor.getColumnIndex("data"));
                    time = Long.valueOf(cursor.getString(cursor.getColumnIndex("time")));
                    cursor.moveToNext();
                }
            }
            cursor.close();

            try {
                JSONArray list = (new JSONObject(dataList)).getJSONArray("list");

                data.clear();

                for(int i = 0; i < list.length(); i++){
                    data.add(list.getJSONObject(i));
                }

                adapter = new ContactListAdapter(new ArrayList<JSONObject>(data));
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
                        "user = \'"+ User.getLogin()+"\'",            // No selection clause
                        null,            // No selection arguments
                        null             // Default sort order
                );
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
