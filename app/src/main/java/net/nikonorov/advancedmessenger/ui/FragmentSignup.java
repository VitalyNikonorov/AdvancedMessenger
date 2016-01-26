package net.nikonorov.advancedmessenger.ui;

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
import net.nikonorov.advancedmessenger.utils.TaskType;
import net.nikonorov.advancedmessenger.utils.Utils;

/**
 * Created by vitaly on 25.01.16.
 */
public class FragmentSignup extends CallableFragment {

    public FragmentSignup(){};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, null);

        Button regBtn = (Button) view.findViewById(R.id.signup_reg_action);

        final EditText loginET = (EditText) view.findViewById(R.id.signup_login);
        final EditText passET = (EditText) view.findViewById(R.id.signup_pass);
        final EditText nickET = (EditText) view.findViewById(R.id.signup_nickname);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringBuilder sb = new StringBuilder();

                User.setLogin(loginET.getText().toString());
                User.setPass(Utils.getHashMD5((passET.getText().toString())));

                sb.append("{\"action\":\"register\", \"data\":{\"login\":\"");
                sb.append(User.getLogin()).append("\", ");
                sb.append("\"pass\": \"").append(User.getPass()).append("\", ");
                sb.append("\"nick\": \"").append(nickET.getText()).append("\"}} ");

                final String data = sb.toString();

                serviceHelper.executeCommand(TaskType.REGISTRATE, data, getActivity());
            }
        });

        return view;
    }

    @Override
    public void correctCodeHandle(int action, String data) {
        //TODO
    }
}
