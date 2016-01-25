package net.nikonorov.advancedmessenger.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.nikonorov.advancedmessenger.R;

/**
 * Created by vitaly on 25.01.16.
 */
public class FragmentChatList extends Fragment {

    public FragmentChatList(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, null);

        return view;
    }
}
