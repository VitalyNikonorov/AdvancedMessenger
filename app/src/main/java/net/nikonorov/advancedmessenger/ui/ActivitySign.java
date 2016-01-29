package net.nikonorov.advancedmessenger.ui;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import net.nikonorov.advancedmessenger.R;

/**
 * Created by vitaly on 24.01.16.
 */
public class ActivitySign extends Activity {

    private Fragment[] fragments = new Fragment[2];

    final static int REQUEST_CONTACTS = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        fragments[FragmentSet.SIGNIN] = new FragmentSignin();
        fragments[FragmentSet.SIGNUP] = new FragmentSignup();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_sign_place, fragments[FragmentSet.SIGNIN]);
        transaction.commit();
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int hasReadContactsPermission = ContextCompat.checkSelfPermission(ActivitySign.this,
                Manifest.permission.READ_CONTACTS);

        if (hasReadContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(ActivitySign.this,
                    Manifest.permission.READ_CONTACTS)) {
                showMessageOKCancel("You need to allow access to Contacts",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(ActivitySign.this,
                                        new String[] {Manifest.permission.READ_CONTACTS},
                                        REQUEST_CONTACTS);
                            }
                        });
                return;
            }
            ActivityCompat.requestPermissions(ActivitySign.this,
                    new String[] {Manifest.permission.READ_CONTACTS},
                    REQUEST_CONTACTS);
            return;
        }
    }

    public void changeFragment(int oldFragment, int newFragment){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_sign_place, fragments[newFragment]);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
