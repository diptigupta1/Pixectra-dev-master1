package com.pixectra.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pixectra.app.Models.User;
import com.pixectra.app.Utils.SessionHelper;

import java.util.Arrays;

public class FacebookActivity extends AppCompatActivity {
    FirebaseDatabase db;
    DatabaseReference ref;
    CallbackManager mCallbackManager;
    String fFirstName, fLastName, fEmail;
    Uri fImageurl;
    boolean auth;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook2);
        auth = getIntent().getBooleanExtra("auth", false);
        userdetails();
        FacebookSdk.sdkInitialize(getApplicationContext());
        db = FirebaseDatabase.getInstance();
        ref = db.getReference("Users");
        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Login", "Login sucess");
                if (auth)
                    firebaseAuthWithFacebook(loginResult.getAccessToken());
                else {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Login cancelled", Toast.LENGTH_SHORT).show();
                if (!auth) {
                    Intent intent = new Intent();
                    setResult(RESULT_CANCELED, intent);
                    finish();
                } else {
                    finish();
                }
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "" + error.getMessage(), Toast.LENGTH_LONG).show();
                if (!auth) {
                    Intent intent = new Intent();
                    setResult(RESULT_CANCELED, intent);
                    finish();
                } else {
                    finish();
                }
            }
        });

    }

    private void firebaseAuthWithFacebook(AccessToken token) {
        Log.d("TAG", "handleFacebookAccessToken:" + token);

        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TAG", "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                           // Log.d("Facebook Sign In", "onComplete: " + (isNew ? "new user" : "old user"));
                            Toast.makeText(FacebookActivity.this, "onComplete: " + (isNew ? "new user" : "old user"), Toast.LENGTH_SHORT).show();
                            if (isNew) {

                                Profile profile = Profile.getCurrentProfile();
                                fFirstName = profile.getFirstName();
                                fLastName = profile.getLastName();
                                fEmail = mAuth.getCurrentUser().getEmail();
                                fImageurl = profile.getProfilePictureUri(400, 400);
                                ref.child(mAuth.getCurrentUser().getUid()).child("Info").setValue(new User(fFirstName + " " + fLastName, fEmail, fImageurl.toString(), ""));
                                new SessionHelper(FacebookActivity.this).setUserDetails(mAuth.getCurrentUser().getUid()
                                        , fFirstName + " " + fLastName
                                        , fEmail
                                        , fImageurl);
                                Intent intent = new Intent(FacebookActivity.this, MobileVerifyActivity.class);
                                intent.putExtra("uid", mAuth.getCurrentUser().getUid());
                                startActivity(intent);
                                finish();

                            }
                            else {
                                ref.child(mAuth.getCurrentUser().getUid()).child("Info").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        User user = dataSnapshot.getValue(User.class);
                                        new SessionHelper(FacebookActivity.this).setUserDetails(mAuth.getCurrentUser().getUid()
                                                , user.getName()
                                                , user.getEmail()
                                                , Uri.parse(user.getProfilePic()));
                                        FirebaseUser userfb = mAuth.getCurrentUser();
                                        updateUI(userfb);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.


                        } else {
                            Log.w("TAG", "signInWithCredential", task.getException());
                            Toast.makeText(FacebookActivity.this, "Already Signed In Using Google",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void userdetails() {
        LoginManager
                .getInstance()
                .logInWithReadPermissions(
                        this,
                        Arrays.asList("public_profile", "user_friends", "email", "user_photos")
                );

    }

    void updateUI(Object o) {
        if (o != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}

