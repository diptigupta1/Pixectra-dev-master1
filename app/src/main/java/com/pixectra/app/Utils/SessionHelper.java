package com.pixectra.app.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.pixectra.app.Instagram.ApplicationData;
import com.pixectra.app.Instagram.InstagramApp;
import com.pixectra.app.MainActivity;
import com.pixectra.app.R;

import java.util.HashMap;

/**
 * Created by Suhail on 2/6/2018.
 */

public class SessionHelper extends MainActivity {

    // Email address (make variable public to access from outside)
    public static final String User_Email = "USER_EMAIL";
    public static final String User_Image = "USER_Image";
    public static final String User_Name = "USER_NAME";
    public static final String User_Uid = "USER_UID";
    public static final String User_Phone = "USER_UID";

    // All Shared Preferences Keys
    // Password (make variable public to access from outside)
    public static final String User_Password = "USER_PASSWORD";
    private static final String PREF_NAME = "LoginSharedPreferences";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String API_USERNAME = "username";
    private static final String API_ID = "id";
    private static final String API_NAME = "name";
    private static final String API_ACCESS_TOKEN = "access_token";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    SharedPreferences sharedPreferences;
    //Editor for shared preferences..
    SharedPreferences.Editor editor;
    Context context;
    int PRIVATE_MODE = 0;


    /**
     * Constructor to initialise
     *
     * @param context
     */

    public SessionHelper(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }

    public boolean isFirstTimeLaunch() {
        return sharedPreferences.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    /**
     * @param email
     * @param password
     */

    public void createLoginSession(String email, String password) {
        editor.putString(User_Email, email);
        editor.putString(User_Password, password);
        editor.apply();

    }

    void setUser_Phone(String phone){
        editor.putString(User_Phone,phone);
        editor.apply();
    }

    /**
     * HashMap to get the user currently logged in
     *
     * @return
     */

    public HashMap<String, String> getUserDetails() {

        HashMap<String, String> user = new HashMap<String, String>();

        // user Email
        user.put(User_Email, sharedPreferences.getString(User_Email, null));

        // user password
        user.put(User_Image, sharedPreferences.getString(User_Image, null));
        user.put(User_Name, sharedPreferences.getString(User_Name, null));
        user.put(User_Uid, sharedPreferences.getString(User_Uid, null));
        user.put(User_Phone, sharedPreferences.getString(User_Phone, null));

        // return user
        return user;

    }

    /**
     * To return login status
     *
     * @return
     */

    public boolean isLoggedIn() {

        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(IS_LOGIN, false);
        }
        return false;

    }


    /* to access password
  *@param null
  */
    public String getUser_Password() {

        if (sharedPreferences != null) {
            return sharedPreferences.getString(User_Password, null);

        }

        return null;
    }


    /* to access email
     *@param null
     */
    public String getUser_Email() {

        if (sharedPreferences != null) {
            return sharedPreferences.getString(User_Email, null);

        }

        return null;
    }

    /**
     * Check if no user logged in after starting main activity and if not logged in send to the login activity
     */

    public void checkLoginStatus() {
        if(!isLoggedIn()) {


            Intent i = new Intent(context,

                    MainActivity.class   //go to login activity if user has not logged in

                    // replace MainActivity with login activity
            );

            //Clear all the previous activities if user not logged in
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(i);
        }
    }



    /**
     * While Signing out user
     */
    public void logOutUser() {

        //Clearing all data from the shared preferences
        editor.clear();
        editor.commit();
        FirebaseAuth.getInstance().signOut();
        if (GoogleSignIn.getLastSignedInAccount(context) != null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.google_token))
                    .requestEmail()
                    .build();

            GoogleSignIn.getClient(context, gso).signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
        }
        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
        }
        new InstagramApp(context, ApplicationData.CLIENT_ID,
                ApplicationData.CLIENT_SECRET, ApplicationData.CALLBACK_URL).resetAccessToken();

    }
    public void setUserDetails(String uid,String name,String email,Uri url){
        editor.putString(User_Image, url.toString());
        editor.putString(User_Name, name);
        editor.putString(User_Uid, uid);
        editor.putString(User_Email, email);
        editor.apply();
    }

    public String getUid(){
        return sharedPreferences.getString(User_Uid,null);
    }
    public void storeAccessToken(String accessToken, String id, String username, String name) {
        editor.putString(API_ID, id);
        editor.putString(API_NAME, name);
        editor.putString(API_ACCESS_TOKEN, accessToken);
        editor.putString(API_USERNAME, username);
        editor.commit();
    }

    public void storeAccessToken(String accessToken) {
        editor.putString(API_ACCESS_TOKEN, accessToken);
        editor.commit();
    }

    public void resetAccessToken() {
        editor.putString(API_ID, null);
        editor.putString(API_NAME, null);
        editor.putString(API_ACCESS_TOKEN, null);
        editor.putString(API_USERNAME, null);
        editor.commit();
    }

    /**
     * Get user name
     *
     * @return User name
     */
    public String getUsername() {
        return sharedPreferences.getString(API_USERNAME, null);
    }

    /**
     *
     * @return
     */
    public String getId() {
        return sharedPreferences.getString(API_ID, null);
    }

    /**
     *
     * @return
     */
    public String getName() {
        return sharedPreferences.getString(API_NAME, null);
    }

    public String getAccessToken() {
        return sharedPreferences.getString(API_ACCESS_TOKEN, null);
    }
}
