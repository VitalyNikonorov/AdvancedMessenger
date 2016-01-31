package net.nikonorov.advancedmessenger.ui;

import android.Manifest;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import net.nikonorov.advancedmessenger.R;
import net.nikonorov.advancedmessenger.User;
import net.nikonorov.advancedmessenger.ui.adapters.ContactListAdapter;
import net.nikonorov.advancedmessenger.utils.TaskType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vitaly on 25.01.16.
 */
public class FragmentImport extends CallableFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public FragmentImport(){}

    private ContactListAdapter adapter = null;
    private ArrayList<JSONObject> data = new ArrayList<>();
    private RecyclerView recyclerView  = null;

    private ImageButton findUserBtn = null;

    private boolean isDialogFinding = false;

    private static final int URL_LOADER = 2;

    private final int ONE_MINUTE_MILLIS = 6000;

    final static int REQUEST_CONTACTS = 2;

    private final String LOG_TAG = "Contacts Fragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_contacts, null);

        recyclerView = (RecyclerView) view.findViewById(R.id.contacts_list);

        findUserBtn = (ImageButton) view.findViewById(R.id.find_users_btn);

        findUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.popup_find_user);
                dialog.setTitle("Find user");

                final EditText email = (EditText) dialog.findViewById(R.id.find_user_email);
                final EditText phone = (EditText) dialog.findViewById(R.id.find_user_phone);

                Button findDialogButton = (Button) dialog.findViewById(R.id.find_user_find);
                findDialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isDialogFinding = true;
                        String[] contacts = {"{\"myid\":\""+User.getCid()+"\", \"name\": \"name\", \"phone\":\""+phone.getText()+"\", \"email\":\""+email.getText()+"\"}"};
                        getContactFromNet(contacts);
                        dialog.dismiss();
                    }
                });

                Button closeDialogDtn = (Button) dialog.findViewById(R.id.find_user_close);
                closeDialogDtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new ContactListAdapter(data, (ActivityMain)getActivity());
        recyclerView.setAdapter(adapter);

        getLoaderManager().initLoader(URL_LOADER, null, this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        int hasReadContactsPermission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_CONTACTS);

        if (hasReadContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CONTACTS)) {
                showMessageOKCancel("You need to allow access to Contacts",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[] {Manifest.permission.READ_CONTACTS},
                                        REQUEST_CONTACTS);
                            }
                        });
                return;
            }
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] {Manifest.permission.READ_CONTACTS},
                    REQUEST_CONTACTS);
            return;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public String[] fetchContacts() {

        String phoneNumber = null;
        String email = null;

        ArrayList<String> result = new ArrayList<>();

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        Uri EmailCONTENT_URI =  ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String DATA = ContactsContract.CommonDataKinds.Email.DATA;

        StringBuffer temp = new StringBuffer();

        ContentResolver contentResolver = getActivity().getContentResolver();

        Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null);

        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
                String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));

                if (hasPhoneNumber > 0) {


                    temp.append("{\"myid\":\"").append(User.getCid()).append("\", \"name\":\"").append(name).append("\",");

                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);

                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER)) != null ? phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER)) :  "";
                    }
                    temp.append("\"phone\":\"").append(phoneNumber).append("\", ");
                    phoneNumber = null;
                    phoneCursor.close();

                    // Query and loop for every email of the contact
                    Cursor emailCursor = contentResolver.query(EmailCONTENT_URI,	null, EmailCONTACT_ID+ " = ?", new String[] { contact_id }, null);

                    while (emailCursor.moveToNext()) {
                        email = emailCursor.getString(emailCursor.getColumnIndex(DATA)) != null ? emailCursor.getString(emailCursor.getColumnIndex(DATA)) : "";
                    }
                    temp.append("\"email\":\"").append(email).append("\"}");
                    email = null;

                    result.add(temp.toString());
                    temp.setLength(0);
                    emailCursor.close();
                }
            }

        }
        return result.toArray(new String[result.size()]);
    }

    @Override
    public void correctCodeHandle(int taskType, String data) {
        //Log.i(LOG_TAG, "Correct code");
        if(isDialogFinding){
            isDialogFinding = false;
            try {
                JSONObject responseData = new JSONObject(data).getJSONObject("data");
                JSONArray contacts = responseData.getJSONArray("list");

                if(contacts.length() == 0){

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "User not found", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    String uid = contacts.getJSONObject(0).getString("uid");
                    ((FragmentProfile)((ActivityMain)getActivity()).fragments[FragmentSet.MAINPROFILE]).setUser(uid);
                    ((ActivityMain)getActivity()).changeFragment(FragmentSet.MAINPROFILE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case URL_LOADER:
                // Returns a new CursorLoader
                return new CursorLoader(
                        getActivity(),   // Parent activity context
                        Uri.parse("content://net.nikonorov.advancedmessenger.providers.db/import"),        // Table to query
                        null,     // Projection to return
                        "user = \'"+ User.getLogin()+"\'",            // selection clause
                        null,            // No selection arguments
                        null             // Default sort order
                );
            default:
                // An invalid id was passed in
                return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        String dataList = "";
        long time = 0;

        if((cursor == null) || (cursor.getCount() == 0)){
            getContactFromNet(null);
        }else {
            if (cursor.moveToFirst()){
                while(!cursor.isAfterLast()){
                    dataList = cursor.getString(cursor.getColumnIndex("data"));
                    time = Long.valueOf(cursor.getString(cursor.getColumnIndex("time")));
                    cursor.moveToNext();
                }
            }

            try {
                JSONArray list = (new JSONObject(dataList)).getJSONArray("list");

                data.clear();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

                for(int i = 0; i < list.length(); i++){
                    data.add(list.getJSONObject(i));
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(time < (System.currentTimeMillis() - ONE_MINUTE_MILLIS)){
            getContactFromNet(null);
        }

    }

    private void getContactFromNet(String[] contacts) {

        //String[] contacts = {"{\"myid\":\""+User.getCid()+"\", \"name\": \"bob\", \"phone\":\"555555\", \"email\":\"bob@bob\"}",
        //        "{\"myid\":\""+User.getCid()+"\", \"name\": \"bobby\", \"phone\":\"55555\", \"email\":\"bobby@bobby\"}"};

        if (contacts == null) {
            contacts = fetchContacts();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{\"action\":\"import\",\"data\": {")
                .append("\"contacts\":[");

        for (int i = 0; i < contacts.length; i++) {
            sb.append(contacts[i]);
            if (i < contacts.length - 1) {
                sb.append(", ");
            }
        }

        sb.append("]}}");

        String importObject = sb.toString();

        //Log.d(LOG_TAG, importObject);
        //Log.i(LOG_TAG, "test contacts");

        serviceHelper.executeCommand(TaskType.IMPORT, importObject, getActivity());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
