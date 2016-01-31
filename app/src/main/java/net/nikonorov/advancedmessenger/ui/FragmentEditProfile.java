package net.nikonorov.advancedmessenger.ui;

import android.Manifest;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import net.nikonorov.advancedmessenger.R;
import net.nikonorov.advancedmessenger.User;
import net.nikonorov.advancedmessenger.utils.AmazingPicture;
import net.nikonorov.advancedmessenger.utils.BufferClass;
import net.nikonorov.advancedmessenger.utils.TaskType;
import net.nikonorov.advancedmessenger.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

/**
 * Created by vitaly on 25.01.16.
 */
public class FragmentEditProfile extends CallableFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public FragmentEditProfile(){}

    private final String LOG_TAG = "Edit Profile";

    private static final int URL_LOADER = 1;

    private final int ONE_MINUTE_MILLIS = 6000;

    private String preparedProto = null;

    private AmazingPicture avaAP    = null;
    private EditText       nickET   = null;
    private EditText       emailET  = null;
    private EditText       phoneET  = null;
    private EditText       statusET = null;
    private Button         saveBtn  = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, null);

        avaAP = (AmazingPicture) view.findViewById(R.id.profile_edit_ava);
        nickET = (EditText) view.findViewById(R.id.profile_edit_nick_et);
        emailET = (EditText) view.findViewById(R.id.profile_edit_email_et);
        phoneET = (EditText) view.findViewById(R.id.profile_edit_phone_et);
        statusET = (EditText) view.findViewById(R.id.profile_edit_status_et);
        saveBtn = (Button) view.findViewById(R.id.profile_edit_save_btn);


        avaAP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hasPhotoPermission = ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA);

                if (hasPhotoPermission != PackageManager.PERMISSION_GRANTED) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.CAMERA)) {
                        showMessageOKCancel("You need to allow access to CAMERA",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(getActivity(),
                                                new String[]{Manifest.permission.CAMERA},
                                                ActivityMain.REQUEST_CODE_PHOTO);
                                    }
                                });

                        if(ContextCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                            takePhoto();
                        }

                        return ;
                    }
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA},
                            ActivityMain.REQUEST_CODE_PHOTO);
                    return ;
                }

                takePhoto();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder sb = new StringBuilder();

                sb.append("{\"action\":\"setuserinfo\", \"data\":{\"cid\":\"");
                sb.append(User.getCid()).append("\", \"sid\":\"");
                sb.append(User.getSid()).append("\", ");

                sb.append("\"user_status\":\"");
                sb.append(statusET.getText().toString()).append("\", ");
                sb.append("\"phone\":\"");
                sb.append(phoneET.getText().toString()).append("\", ");
                sb.append("\"email\":\"");
                sb.append(emailET.getText().toString()).append("\", ");

                sb.append("\"picture\": \"").append(preparedProto).append("\"}} "); //preparedProto

                String dataObject = sb.toString();
                //Log.d(LOG_TAG, dataObject);
                serviceHelper.executeCommand(TaskType.SETUSERINFO, dataObject, getActivity());

                getUserFromNet();
            }
        });

        getLoaderManager().initLoader(URL_LOADER, null, this);

        return view;
    }

    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, ActivityMain.REQUEST_CODE_PHOTO);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ActivityMain.REQUEST_CODE_PHOTO) {
            if (resultCode == getActivity().RESULT_OK) {
                //Log.d(LOG_TAG, "Photo uri: " + data.getData());
                Bitmap photo = (Bitmap) data.getExtras().get("data");

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                byte[] compressedPhoto = baos.toByteArray();

                String encodedPhoto = Base64.encodeToString(compressedPhoto, Base64.NO_WRAP);

                Utils.setPhoto(encodedPhoto, avaAP);

                preparedProto = encodedPhoto;
            }

            Log.i("PHOTO", "photo");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        View view = getActivity().getCurrentFocus();
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void correctCodeHandle(int taskType, String data) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case URL_LOADER:
                return new CursorLoader(
                        getActivity(),   // Parent activity context
                        Uri.parse("content://net.nikonorov.advancedmessenger.providers.db/users"),        // Table to query
                        null,     // Projection to return
                        "login = \'" + User.getLogin() + "\'",            // selection clause
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
        String userData = "";
        long time = 0;

        if(cursor == null){
            getUserFromNet();
        }else {
            if (cursor.moveToFirst()){
                while(!cursor.isAfterLast()){
                    userData = cursor.getString(cursor.getColumnIndex("data"));
                    time = Long.valueOf(cursor.getString(cursor.getColumnIndex("time")));
                    cursor.moveToNext();
                }
            }

            try {
                JSONObject jsonObject = new JSONObject(userData);

                Utils.setPhoto(jsonObject.getString("picture"), avaAP);
                preparedProto = jsonObject.getString("picture");

                nickET.setText(jsonObject.getString("nick"));
                emailET.setText(jsonObject.getString("email"));
                phoneET.setText(jsonObject.getString("phone"));
                statusET.setText(jsonObject.getString("user_status"));

                //Log.i(LOG_TAG, "upated");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(time < (System.currentTimeMillis() - ONE_MINUTE_MILLIS)){
            getUserFromNet();
        }

    }

    private void getUserFromNet(){
        BufferClass.setAskedUser(User.getLogin());

        StringBuilder sb = new StringBuilder();

        sb.append("{\"action\":\"userinfo\", \"data\":{\"cid\":\"");
        sb.append(User.getCid()).append("\", ");
        sb.append("\"user\": \"").append(User.getLogin()).append("\", ");
        sb.append("\"sid\": \"").append(User.getSid()).append("\"}} ");

        String reqObject = sb.toString();
        //Log.d(LOG_TAG, reqObject.toString());
        serviceHelper.executeCommand(TaskType.USERINFO, reqObject, getActivity());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}