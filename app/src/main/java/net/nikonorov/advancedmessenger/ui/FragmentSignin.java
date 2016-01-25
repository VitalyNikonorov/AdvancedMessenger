package net.nikonorov.advancedmessenger.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.nikonorov.advancedmessenger.R;

/**
 * Created by vitaly on 25.01.16.
 */
public class FragmentSignin extends Fragment {

    public FragmentSignin(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signin, null);

        View signupButton = view.findViewById(R.id.sign_bar_button);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActivitySign)getActivity()).changeFragment(FragmentSet.SIGNIN, FragmentSet.SIGNUP);
            }
        });

        Button signinButton = (Button)  view.findViewById(R.id.signin_auth_action);

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivityMain.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
