package net.nikonorov.advancedmessenger.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.widget.Toast;

import net.nikonorov.advancedmessenger.App;
import net.nikonorov.advancedmessenger.ServiceHelperListener;
import net.nikonorov.advancedmessenger.logic.ServiceHelper;
import net.nikonorov.advancedmessenger.utils.Code;

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
    public void onServiceHelperCallback(int action, int code, String data) {
        switch (code){
            case Code.OK:
                correctCodeHandle(action, data);
                break;
            case Code.TIMEOUT:
                timeoutCodeHandle();
        }
    }

    public abstract void correctCodeHandle(int action, String data);

    private void timeoutCodeHandle(){
        Toast.makeText(getActivity(), "Connection time out. Check network.", Toast.LENGTH_SHORT).show();
    }
}
