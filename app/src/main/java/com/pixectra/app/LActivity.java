package com.pixectra.app;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pixectra.app.Models.User;
import com.pixectra.app.Utils.SessionHelper;

public class LActivity extends AppCompatActivity {
    private static final String TAG = "HANDLESIGNINRESULT";
    String fFirstName,fLastName, fEmail;
    Uri fImageurl;
    String gpersonName, gpersonEmail;
    Uri gImageUrl;
    int RC_SIGN_IN = 1;
    Dialog errorDialog;
    GoogleSignInClient mGoogleSignInClient;
    ProgressBar progressBar;
FirebaseDatabase db;
DatabaseReference ref;
    private ImageView imageView, facebookimageview1;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_l);
        db = FirebaseDatabase.getInstance();
        ref = db.getReference("Users");
        mAuth = FirebaseAuth.getInstance();
        imageView = findViewById(R.id.google_login_button);
        facebookimageview1 = findViewById(R.id.facebook_login_button);
        progressBar = findViewById(R.id.simpleProgressBar);
        Scope SCOPE_PICASA = new Scope("https://picasaweb.google.com/data/");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(SCOPE_PICASA)
                .requestIdToken(getString(R.string.google_token))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.google_login_button:
                        if (checkPlayServices())
                            signIn();
                        break;
                    // ...
                }
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    updateUI(user);
                    Log.d("Tag", "user already Exists");
                }
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @SuppressLint("LongLogTag")
    public void onFacebookLogInClicked(View view) {

        Intent intent = new Intent(LActivity.this, FacebookActivity.class);
        intent.putExtra("auth", true);
        startActivity(intent);

    }


    //showing error dialog of user accounts
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct, final GoogleSignInResult result) {
        Log.d("Google Sign In", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Google Sign In", "signInWithCredential:success");
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            Log.d("Google Sign In", "onComplete: " + (isNew ? "new user" : "old user"));
                            Toast.makeText(LActivity.this, "onComplete: " + (isNew ? "new user" : "old user"), Toast.LENGTH_SHORT).show();
                            if (isNew) {
                                // mAuth.getCurrentUser().getUid();
                                Log.d("TAG", "handleSignInResult:" + result.isSuccess());
                                if (result.isSuccess()) {
                                    // Signed in successfully, show authenticated UI.
                                    GoogleSignInAccount acct = result.getSignInAccount();

                                    gpersonName = acct.getDisplayName();
                                    gImageUrl = acct.getPhotoUrl();
                                    gpersonEmail = acct.getEmail();
                                    ref.child(mAuth.getCurrentUser().getUid()).child("Info").setValue(new User(gpersonName,gpersonEmail,gImageUrl.toString(),""));
                                    new SessionHelper(LActivity.this).setUserDetails(mAuth.getCurrentUser().getUid()
                                            , gpersonName
                                            , gpersonEmail
                                            , gImageUrl);
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Intent intent = new Intent(LActivity.this, MobileVerifyActivity.class);
                                    intent.putExtra("uid", mAuth.getCurrentUser().getUid());
                                    startActivity(intent);
                                    finish();
                                    if (gImageUrl != null) {
                                        Toast.makeText(LActivity.this, "found image url", Toast.LENGTH_SHORT).show();
                                    }
                                    Toast.makeText(LActivity.this, gpersonName + "\n" + gpersonEmail, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                ref.child(mAuth.getCurrentUser().getUid()).child("Info").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        User user = dataSnapshot.getValue(User.class);
                                        new SessionHelper(LActivity.this).setUserDetails(mAuth.getCurrentUser().getUid()
                                                , user.getName()
                                                , user.getEmail()
                                                , Uri.parse(user.getProfilePic()));
                                        FirebaseUser userfire = mAuth.getCurrentUser();
                                        updateUI(userfire);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential", task.getException());
                            Toast.makeText(LActivity.this, "Try Logging In Using Facebook\nAlready Registered Using Facebook",
                                    Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener..
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account, result);
            } catch (ApiException e) {
                Toast.makeText(this, "Failed SignIn Exception Key: Google Sign In ", Toast.LENGTH_SHORT).show();
                // Google Sign In failed, update UI appropriately
                Log.v("Google Sign In", "Google sign in failed", e);
                // ...
            }

        } else {
            Toast.makeText(this, "" + requestCode + "NOT OK", Toast.LENGTH_SHORT).show();
        }

    }

    void updateUI(Object o) {
        if (o != null) {
            progressBar.setVisibility(View.GONE);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean checkPlayServices() {

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {

                if (errorDialog == null) {
                    errorDialog = googleApiAvailability.getErrorDialog(this, resultCode, 2404);
                    errorDialog.setCancelable(false);
                }

                if (!errorDialog.isShowing())
                    errorDialog.show();

            }
        }

        return resultCode == ConnectionResult.SUCCESS;
    }

}

