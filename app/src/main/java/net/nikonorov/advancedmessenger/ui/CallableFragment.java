package net.nikonorov.advancedmessenger.ui;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import net.nikonorov.advancedmessenger.App;
import net.nikonorov.advancedmessenger.R;
import net.nikonorov.advancedmessenger.ServiceHelperListener;
import net.nikonorov.advancedmessenger.logic.MasterService;
import net.nikonorov.advancedmessenger.logic.ServiceHelper;
import net.nikonorov.advancedmessenger.utils.Code;
import net.nikonorov.advancedmessenger.utils.TaskType;
import net.nikonorov.advancedmessenger.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vitaly on 26.01.16.
 */
public abstract class CallableFragment extends Fragment implements ServiceHelperListener {

    ServiceHelper serviceHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceHelper = ((App)getActivity().getApplication()).getServiceHelper();
    }

    @Override
    public void onResume() {
        super.onResume();
        serviceHelper.addListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        serviceHelper.removeListener(this);
    }

    @Override
    public void onServiceHelperCallback(int taskType, final String data, int code) {
        switch (code){
            case Code.OK:
                correctCodeHandle(taskType, data);
                break;
            case Code.TIMEOUT:
                timeoutCodeHandle();
                break;
            default:
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Toast.makeText(getActivity(), new JSONObject(data).getJSONObject("data").getString("error"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        }
    }

    public abstract void correctCodeHandle(int taskType, String data);

    private void timeoutCodeHandle(){

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.popup_reconnect);
                dialog.setTitle("Attached file");

                Button btnDismiss = (Button) dialog.findViewById(R.id.reconnect_close);
                btnDismiss.setOnClickListener(new Button.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                Button reconnect = (Button) dialog.findViewById(R.id.reconnect_reconnect);
                reconnect.setOnClickListener(new Button.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        getActivity().startService(new Intent(getActivity(), MasterService.class).putExtra("type", TaskType.CONNECT).putExtra("data", ""));
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }
}
