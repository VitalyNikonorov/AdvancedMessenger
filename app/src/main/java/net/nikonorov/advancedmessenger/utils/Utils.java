package net.nikonorov.advancedmessenger.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by vitaly on 26.01.16.
 */
public class Utils {
    public static String getHashMD5(String str) {

        MessageDigest md5 ;
        StringBuffer  hexString = new StringBuffer();

        try {

            md5 = MessageDigest.getInstance("md5");

            md5.reset();
            md5.update(str.getBytes());


            byte messageDigest[] = md5.digest();

            for (int i = 0; i < messageDigest.length; i++) {
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            }

        }
        catch (NoSuchAlgorithmException e) {
            return e.toString();
        }

        return hexString.toString();
    }

    public static void setPhoto(String encodedPhoto, ImageView imageView) {
        byte[] decodedPhoto = Base64.decode(encodedPhoto, Base64.NO_WRAP);

        Bitmap procPhoto = BitmapFactory.decodeByteArray(decodedPhoto, 0, decodedPhoto.length);
        imageView.setImageBitmap(procPhoto);
    }
}
