package net.nikonorov.advancedmessenger.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.nikonorov.advancedmessenger.R;

/**
 * Created by vitaly on 25.01.16.
 */
public class ActivityMain extends AppCompatActivity {

    public Fragment[] fragments = new Fragment[3];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragments[FragmentSet.MAINVIEWPAGER] = new FragmentViewPager();
        fragments[FragmentSet.MAINEDITPROFILE] = new FragmentEditProfile();
        fragments[FragmentSet.MAINPROFILE] = new FragmentProfile();

        FragmentTransaction transaction =  getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_main_place, fragments[FragmentSet.MAINVIEWPAGER]);
        transaction.commit();
    }

    public void changeFragment(int newFragment){
        FragmentTransaction transaction =  getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_main_place, fragments[newFragment]);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
