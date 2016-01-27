package net.nikonorov.advancedmessenger.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.nikonorov.advancedmessenger.R;
import net.nikonorov.advancedmessenger.ui.adapters.ContactListAdapter;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vitaly on 25.01.16.
 */
public class FragmentChatList extends CallableFragment {

    private ContactListAdapter adapter = null;
    private ArrayList<JSONObject> data = new ArrayList<>();
    private RecyclerView recyclerView  = null;

    public FragmentChatList(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, null);

        recyclerView = (RecyclerView) view.findViewById(R.id.chats_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new ContactListAdapter(data);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void correctCodeHandle(int taskType, String data) {
        Log.i("chat list ", "Correct Code");
    }
}
