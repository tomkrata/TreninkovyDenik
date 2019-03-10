package com.example.krata.treninky_denik;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class UserLocalStore {

    public static final String SP_NAME = "userDetails";
    SharedPreferences userLocalDatabase;

    public UserLocalStore(Context context) {
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeUserData(User user)
    {


        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putString("nick", user.nick);
        spEditor.putString("mail", user.mail);
        spEditor.putString("pass", user.pass);
        spEditor.putBoolean("trainer", user.trainer);

        //encode profile picture and save
        if (user.profilePic != null)
        {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            user.profilePic.compress(Bitmap.CompressFormat.JPEG, 100, byteStream);
            String encodedImage = Base64.encodeToString(byteStream.toByteArray(), Base64.DEFAULT);
            spEditor.putString("profile_pic", encodedImage);
        }
        else
            spEditor.putString("profile_pic", null);

        spEditor.commit();
    }

    public User getLoggedUser()
    {
        String nick = userLocalDatabase.getString("nick", "");
        String mail = userLocalDatabase.getString("mail", "");
        String pass = userLocalDatabase.getString("pass", "");
        boolean trainer = userLocalDatabase.getBoolean("trainer", true);
        String profilePic = userLocalDatabase.getString("profile_pic", "");

        byte[] data = Base64.decode(profilePic, Base64.DEFAULT);
        Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
        User storedUser = new User(nick, mail, pass, trainer, image);
        return storedUser;
    }

    public boolean isTrainer()
    {
        return isUserLoggedIn() && getLoggedUser().trainer;
    }

    public void setUserLoggedIn(boolean loggedIn)
    {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putBoolean("LoggedIn", loggedIn);
        spEditor.commit();
    }

    public boolean isUserLoggedIn()
    {
        if (userLocalDatabase.getBoolean("LoggedIn", false) == true)
            return true;
        return false;
    }

    public void clearUserData()
    {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.clear();
        spEditor.commit();
    }
}
