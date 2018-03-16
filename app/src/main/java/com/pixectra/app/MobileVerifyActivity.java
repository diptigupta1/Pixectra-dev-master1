package com.pixectra.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

/**
 * Created by prashu on 2/11/2018.
 */

public class MobileVerifyActivity extends AppCompatActivity {
    View view;
    EditText e1, e2, e3, e4, mobile;
    String entered_otp;
    String code;
    PhoneAuthProvider.ForceResendingToken tokenCode;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mobile_verification);
        final Button b = (Button) findViewById(R.id.verify_next);
        mobile = (EditText) findViewById(R.id.verify_mobile);
        mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mobile.length() == 10) {
                    b.setEnabled(true);
                    b.setBackgroundColor(getResources().getColor(R.color.userProfileCardViewBlue));

                } else {
                    b.setEnabled(false);
                    b.setBackgroundColor(getResources().getColor(R.color.dot_dark_screen2));

                }
            }
            });
                b.findViewById(R.id.verify_next).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String mobile_no = mobile.getText().toString().trim();
//                final int PERMISSION_REQUEST_CODE = 1;
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                    if (checkSelfPermission(android.Manifest.permission.SEND_SMS)
//                            == PackageManager.PERMISSION_DENIED) {
//
//                        String[] permissions = {android.Manifest.permission.SEND_SMS};
//
//                        requestPermissions(permissions, PERMISSION_REQUEST_CODE);
//
//                    }
//                }
                        LayoutInflater factory = LayoutInflater.from(MobileVerifyActivity.this);
                        final View deleteDialogView = factory.inflate(R.layout.dialog_otp, null);
                        final AlertDialog deleteDialog = new AlertDialog.Builder(MobileVerifyActivity.this).create();

                        mCallbacks= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                                FirebaseDatabase db;
                                DatabaseReference ref;
                                db = FirebaseDatabase.getInstance();
                                String uid=getIntent().getStringExtra("uid");
                                if (uid == null){
                                    uid="testid";
                                }
                                ref = db.getReference("Users").child(uid).child("Info").child("phoneNo");
                                ref.setValue(mobile_no);
                                deleteDialog.dismiss();

                                Intent intent = new Intent(MobileVerifyActivity.this, MainActivity.class);
                                startActivity(intent);
                                MobileVerifyActivity.this.finish();
                            }

                            @Override
                            public void onVerificationFailed(FirebaseException e) {
                                deleteDialog.dismiss();
                            }
                            @Override
                            public void onCodeSent(String verificationId,
                                                   PhoneAuthProvider.ForceResendingToken token) {
                                // The SMS verification code has been sent to the provided phone number, we
                                // now need to ask the user to enter the code and then construct a credential
                                // by combining the code with a verification ID.
                                code=verificationId;
                                tokenCode=token;
                                Log.d("verification id", "onCodeSent:" + verificationId);
                                Log.d("token", "onCodeSent:" + token);

                                // Save verification ID and resending token so we can use them later


                                // ...
                            }
                        };
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                mobile_no,        // Phone number to verify
                                60,                 // Timeout duration
                                TimeUnit.SECONDS,   // Unit of timeout
                                MobileVerifyActivity.this,
                                mCallbacks// Activity (for callback binding)
                                );
                        deleteDialog.setView(deleteDialogView);
                        e1 = deleteDialogView.findViewById(R.id.char1);
                        e2 = deleteDialogView.findViewById(R.id.char2);
                        e3 = deleteDialogView.findViewById(R.id.char3);
                        e4 = deleteDialogView.findViewById(R.id.char4);
                        deleteDialog.show();
                        e1.addTextChangedListener(new GenericTextWatcher(e1));
                        e2.addTextChangedListener(new GenericTextWatcher(e2));
                        e3.addTextChangedListener(new GenericTextWatcher(e3));
                        e4.addTextChangedListener(new GenericTextWatcher(e4));
                        deleteDialog.setCancelable(false);
                        deleteDialog.findViewById(R.id.verify_otp).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(code, entered_otp);
                                Toast.makeText(MobileVerifyActivity.this, credential.getSmsCode(), Toast.LENGTH_SHORT).show();
                                signInWithPhoneAuthCredential(mobile_no,credential);




                            }
                        });
                        deleteDialogView.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                deleteDialog.dismiss();
                            }
                        });
                        deleteDialogView.findViewById(R.id.dialog_resend).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                               resendVerificationCode(mobile_no,tokenCode); //code for resend
                            }
                        });
                        deleteDialogView.findViewById(R.id.dialog_another).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                deleteDialog.dismiss();
                                mobile.setText("");
                                //code
                            }
                        });

                    }
                });
            }

            class GenericTextWatcher implements TextWatcher {
                private View view;

                private GenericTextWatcher(View view) {
                    this.view = view;
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    // TODO Auto-generated method stub
                    String text = editable.toString();
                    switch (view.getId()) {

                        case R.id.char1:
                            entered_otp = e1.getText().toString() + e2.getText().toString() + e3.getText().toString() + e4.getText().toString();
                            if (text.length() == 1)
                                e2.requestFocus();
                            break;
                        case R.id.char2:
                            entered_otp = e1.getText().toString() + e2.getText().toString() + e3.getText().toString() + e4.getText().toString();
                            if (text.length() == 1)
                                e3.requestFocus();
                            break;
                        case R.id.char3:
                            entered_otp = e1.getText().toString() + e2.getText().toString() + e3.getText().toString() + e4.getText().toString();
                            if (text.length() == 1)
                                e4.requestFocus();
                            break;
                        case R.id.char4:
                            entered_otp = e1.getText().toString() + e2.getText().toString() + e3.getText().toString() + e4.getText().toString();
                            break;
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                }


            };
 /*   void send_msg(String mobile_no)
    {
        SecureRandom test = new SecureRandom();
        int result = test.nextInt(1000000);
        String resultStr = result + "";
        if (resultStr.length() != 6)
            for (int x = resultStr.length(); x < 6; x++) resultStr = "0" + resultStr;
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(mobile_no, null, resultStr, null, null);
            Toast.makeText(getApplicationContext(), "SMS Sent!",
                    Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS faild, please try again later!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }*/
 private void signInWithPhoneAuthCredential(final String mob, PhoneAuthCredential phoneAuthCredential) {
     final FirebaseAuth mAuth=FirebaseAuth.getInstance();
     mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
         @Override
         public void onComplete(@NonNull Task<AuthResult> task) {
             if (task.isSuccessful()){
                 FirebaseDatabase db;
                 DatabaseReference ref;
                 db = FirebaseDatabase.getInstance();
                 String uid=getIntent().getStringExtra("uid");
                 if (uid == null){
                     uid="testid";
                 }
                 ref = db.getReference("Users").child(uid).child("Info").child("phoneNo");
                 ref.setValue(mob);
                 Intent intent = new Intent(MobileVerifyActivity.this, MainActivity.class);
                 startActivity(intent);
                 MobileVerifyActivity.this.finish();
             }else {
                 if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                     resendVerificationCode(mob,tokenCode);// The verification code entered was invalid
                 }
             }
         }
     });
 }
    }
