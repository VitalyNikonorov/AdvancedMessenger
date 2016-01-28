package net.nikonorov.advancedmessenger.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import net.nikonorov.advancedmessenger.R;

/**
 * Created by vitaly on 25.01.16.
 */
public class ActivityMain extends AppCompatActivity {

    FragmentViewPager fragmentViewPager;
    FragmentProfile fragmentProfile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentProfile = new FragmentProfile();
        fragmentViewPager = new FragmentViewPager();

        FragmentTransaction transaction =  getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_main_place, fragmentViewPager);
        transaction.commit();
    }
}
