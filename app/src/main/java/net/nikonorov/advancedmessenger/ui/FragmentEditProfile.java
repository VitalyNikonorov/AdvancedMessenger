package net.nikonorov.advancedmessenger.ui;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.nikonorov.advancedmessenger.R;
import net.nikonorov.advancedmessenger.utils.AmazingPicture;

/**
 * Created by vitaly on 25.01.16.
 */
public class FragmentEditProfile extends CallableFragment {

    public FragmentEditProfile(){}

    private final String LOG_TAG = "Edit Profile";

    private static final int URL_LOADER = 1;

    private final int ONE_MINUTE_MILLIS = 6000;

    private AmazingPicture avaAP    = null;
    private EditText       nickET   = null;
    private EditText       emailET  = null;
    private EditText       phoneET  = null;
    private EditText       statutET = null;
    private Button         saveBtn  = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, null);

        avaAP = (AmazingPicture) view.findViewById(R.id.profile_edit_ava);
        nickET = (EditText) view.findViewById(R.id.profile_edit_nick_et);
        emailET = (EditText) view.findViewById(R.id.profile_edit_email_et);
        phoneET = (EditText) view.findViewById(R.id.profile_edit_phone_et);
        statutET = (EditText) view.findViewById(R.id.profile_edit_status_et);
        saveBtn = (Button) view.findViewById(R.id.profile_edit_save_btn);

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

    @Override
    public void correctCodeHandle(int taskType, String data) {

    }
}
