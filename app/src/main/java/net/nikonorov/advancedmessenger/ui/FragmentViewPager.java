package net.nikonorov.advancedmessenger.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import net.nikonorov.advancedmessenger.R;

/**
 * Created by vitaly on 28.01.16.
 */
public class FragmentViewPager extends CallableFragment {

    public FragmentViewPager(){}
    private final int PAGE_COUNT = 3;

    private Fragment[] fragments = new Fragment[PAGE_COUNT];
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private String[] titles = new String[PAGE_COUNT];


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_pager, null);

        fragments[FragmentSet.CHATLIST] = new FragmentChatList();
        fragments[FragmentSet.CONTACTS] = new FragmentContacts();
        fragments[FragmentSet.PROFILE] = new FragmentProfile();

        viewPager = (ViewPager) view.findViewById(R.id.pager);
        pagerAdapter = new MyFragmentPagerAdapter(getFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Log.i("TAG", "Scrolled");
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                View view = getActivity().getCurrentFocus();
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        titles[FragmentSet.CHATLIST] = getString(R.string.main_chats);
        titles[FragmentSet.CONTACTS] = getString(R.string.main_contacts);
        titles[FragmentSet.PROFILE] = getString(R.string.main_profile);

        return view;
    }

    @Override
    public void correctCodeHandle(int taskType, String data) {

    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

}
