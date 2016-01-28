package net.nikonorov.advancedmessenger.ui;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import net.nikonorov.advancedmessenger.R;

/**
 * Created by vitaly on 25.01.16.
 */
public class FragmentEditProfile extends Fragment {

    public FragmentEditProfile(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, null);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        View view = getActivity().getCurrentFocus();
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
