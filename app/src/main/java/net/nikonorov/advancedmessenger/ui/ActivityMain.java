package net.nikonorov.advancedmessenger.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.nikonorov.advancedmessenger.R;

/**
 * Created by vitaly on 25.01.16.
 */
public class ActivityMain extends AppCompatActivity {

    private Fragment[] fragments = new Fragment[3];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragments[FragmentSet.CHATLIST] = new FragmentChatList();
        fragments[FragmentSet.CONTACTS] = new FragmentContacts();
        fragments[FragmentSet.PROFILE] = new FragmentProfile();
    }
}
