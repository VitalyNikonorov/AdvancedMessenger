package net.nikonorov.advancedmessenger.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import net.nikonorov.advancedmessenger.R;

/**
 * Created by vitaly on 24.01.16.
 */
public class ActivitySign extends Activity {

    private Fragment[] fragments = new Fragment[2];


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

    public void changeFragment(int oldFragment, int newFragment){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_sign_place, fragments[newFragment]);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
