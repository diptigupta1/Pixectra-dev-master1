package com.pixectra.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pixectra.app.Models.User;
import com.pixectra.app.Utils.GlideHelper;
import com.pixectra.app.Utils.SessionHelper;

/**
 * Created by prashu on 2/16/2018.
 */

public class MyProfileActivity extends AppCompatActivity {

    TextView imagename, name, email, mobile, logout, help, delete;
    ImageView imageView;
    ProgressBar progress;
    FirebaseDatabase db;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        db = FirebaseDatabase.getInstance();
        ref = db.getReference("Users");
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        ref.child(uid).child("Info").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                if (user.getName() != null)
                    name.setText(user.getName());
                else
                    name.setText("Not Found");
                if (user.getEmail() != null)
                    email.setText(user.getEmail());
                else
                    email.setText("Not Found");
                if (user.getEmail() != null)
                    mobile.setText(user.getPhoneNo());
                else
                    mobile.setText("Not Found");
                if (user.getProfilePic() != null)
                    GlideHelper.load(MyProfileActivity.this, user.getProfilePic(), imageView, progress);
                else
                    progress.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Toolbar toolbar = findViewById(R.id.profile_tool);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // imagename=(TextView)findViewById(R.id.image_name);
        name = findViewById(R.id.profile_name);
        progress = findViewById(R.id.progress_profile);
        email = findViewById(R.id.profile_email);
        mobile = findViewById(R.id.profile_mobile);
        imageView = findViewById(R.id.image_profile);
        logout = findViewById(R.id.profile_logout);
        help = findViewById(R.id.profile_help);
        delete = findViewById(R.id.profile_delete);
        //imagename.setText("Salman");
        name.setText("Loading...");
        email.setText("Loading...");
        mobile.setText("Loading...");
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //help page
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(MyProfileActivity.this).create();
                alertDialog.setMessage("Are ou Sure You Want To Logout?");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Log Out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new SessionHelper(MyProfileActivity.this).logOutUser();
                        Intent intent = new Intent(MyProfileActivity.this, LActivity.class);
                        startActivity(intent);
                        finishAffinity();


                    }
                });
                alertDialog.show();

            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(MyProfileActivity.this).create();
                alertDialog.setMessage("Delete Account ?");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Deactivate", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(MyProfileActivity.this, "Deleted SuccessFully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MyProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                alertDialog.show();


            }
        });


    }
}
