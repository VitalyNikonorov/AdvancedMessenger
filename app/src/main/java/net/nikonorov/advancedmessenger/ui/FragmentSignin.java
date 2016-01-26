package net.nikonorov.advancedmessenger.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import net.nikonorov.advancedmessenger.R;
import net.nikonorov.advancedmessenger.User;
import net.nikonorov.advancedmessenger.utils.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by vitaly on 25.01.16.
 */
public class FragmentSignin extends CallableFragment {

    public FragmentSignin(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_signin, null);

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

                EditText loginET = (EditText) view.findViewById(R.id.signin_login);
                EditText passET = (EditText) view.findViewById(R.id.signin_pass);

                StringBuilder sb = new StringBuilder();

                User.setLogin(loginET.getText().toString());
                User.setPass(Utils.getHashMD5(passET.getText().toString()));

                sb.append("{\"action\":\"auth\", \"data\":{\"login\":\"");
                sb.append(loginET.getText()).append("\", ");
                sb.append("\"pass\": \"").append(Utils.getHashMD5(passET.getText().toString())).append("\"}} ");

                String reqObject = sb.toString();


            }
        });

        return view;
    }

    @Override
    public void correctCodeHandle(int action, String data) {
        Intent intent = new Intent(getActivity(), ActivityMain.class);
        startActivity(intent);
    }
}
