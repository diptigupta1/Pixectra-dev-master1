package com.pixectra.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.pixectra.app.Instagram.ApplicationData;
import com.pixectra.app.Instagram.InstagramApp;

public class LinkedAccounts extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1;
    CardView facebook;
CardView google;
CardView insta;
    TextView instaText;
    TextView fbText;
    TextView googleText;
    InstagramApp instagramApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linked_accounts);
        facebook = findViewById(R.id.facebook_card);
        google = findViewById(R.id.google_card);
        insta = findViewById(R.id.instagram_card);
        instaText = findViewById(R.id.insta_text);
        fbText = findViewById(R.id.facebook_text);
        googleText = findViewById(R.id.google_text);
        instagramApp = new InstagramApp(this, ApplicationData.CLIENT_ID,
                ApplicationData.CLIENT_SECRET, ApplicationData.CALLBACK_URL);

        googleHandler();
        facebookHandler();
        instaHandler();

    }

    void googleHandler() {
        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            googleText.setText(R.string.disconnect);
            google.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Scope SCOPE_PICASA = new Scope("https://picasaweb.google.com/data/");

                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestScopes(SCOPE_PICASA)
                            .requestIdToken(getString(R.string.google_token))
                            .requestEmail()
                            .build();

                    GoogleSignIn.getClient(LinkedAccounts.this, gso).signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            googleHandler();
                        }
                    });
                }
            });
        } else {
            googleText.setText(R.string.connect);
            google.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Scope SCOPE_PICASA = new Scope("https://picasaweb.google.com/data/");
                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestScopes(SCOPE_PICASA)
                            .requestIdToken(getString(R.string.google_token))
                            .requestEmail()
                            .build();

                    googleSignIn(GoogleSignIn.getClient(LinkedAccounts.this, gso));
                }
            });
        }
    }

    void facebookHandler() {
        if (AccessToken.getCurrentAccessToken() != null) {
            fbText.setText(R.string.disconnect);
            facebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginManager.getInstance().logOut();
                    facebookHandler();
                }
            });
        } else {
            fbText.setText(R.string.connect);
            facebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(LinkedAccounts.this, FacebookActivity.class);
                    intent.putExtra("auth", false);
                    startActivityForResult(intent, 3);
                }
            });
        }


    }

    void instaHandler() {
        if (instagramApp.hasAccessToken()) {
            instaText.setText(R.string.disconnect);
            insta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    instagramApp.resetAccessToken();
                    instaHandler();
                }
            });
        } else {
            instaText.setText(R.string.connect);
            insta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    instagramApp.authorize();
                    instagramApp.setListener(new InstagramApp.OAuthAuthenticationListener() {

                        @Override
                        public void onSuccess() {
                            // tvSummary.setText("Connected as " + mApp.getUserName());
                            instaHandler();
                        }

                        @Override
                        public void onFail(String error) {
                            Toast.makeText(LinkedAccounts.this, "Failed To Login", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    void googleSignIn(GoogleSignInClient mGoogleSignInClient) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener..
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.v("Google Sign In", "Google sign in failed", e);
                // ...
            }
            googleHandler();
        } else if (resultCode == RESULT_OK && requestCode == 3) {
            facebookHandler();
        }
    }
}
