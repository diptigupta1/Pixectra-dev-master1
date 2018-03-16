package com.pixectra.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pixectra.app.Models.Address;
import com.pixectra.app.Utils.SessionHelper;

/**
 * Created by  prashu on 2/6/2018.
 * detail of the shipping address
 */

public class ShippingAddressForm extends AppCompatActivity {
    String name,mobile,ward,street,pincode,state,city,optional;
    EditText Tname,Tmobile,Tward,Tstreet,Tpincode,Tstate,Tcity,Toptional;
    Button save,cancle;
    FirebaseDatabase db;
    DatabaseReference ref;
    Address adress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shipping_address_activity);
        db=FirebaseDatabase.getInstance();
        ref=db.getReference("Users/"+new SessionHelper(this).getUid()+"/ShippingAddress");
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Delivery Address");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Tname=(EditText)findViewById(R.id.shipi_name);
        Tmobile=(EditText)findViewById(R.id.shipi_mobile);
        Tward=(EditText)findViewById(R.id.shipi_ward);
        Tstreet=(EditText)findViewById(R.id.shipi_street);
        Tpincode=(EditText)findViewById(R.id.shipi_pin);
        Tstate=(EditText)findViewById(R.id.shipi_state);
        Tcity=(EditText)findViewById(R.id.shipi_city);
        Toptional=(EditText)findViewById(R.id.shipi_optional);

        final Intent intent = this.getIntent();
        int comefrom=intent.getIntExtra("status",1);
        if(comefrom==0)
        {
            Bundle bundle = intent.getExtras();
        adress=(Address)bundle.getSerializable("add");

            Tname.setText(adress.getName());
            Tmobile.setText(adress.getMobile());
            Tpincode.setText(adress.getPincode());
            Tstreet.setText(adress.getStreet());
            Tstate.setText(adress.getState());
            Tcity.setText(adress.getCity());
            Tward.setText(adress.getAddNo());
            Toptional.setText(adress.getOptional());
        }
        Tmobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b)
                {
                    if(Tmobile.getText().toString().length()<10)
                    {
                        Tmobile.setError("Enter a valid input");
                    }
                }
            }
        });
        Tpincode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b)
                {
                    if(Tpincode.getText().toString().length()<6)
                    {
                        Tpincode.setError("Enter a valid input");
                    }
                }
            }
        });
// save button
        findViewById(R.id.shipi_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name=Tname.getText().toString();
                mobile=Tmobile.getText().toString();
                pincode=Tpincode.getText().toString();
                ward=Tward.getText().toString();
                street=Tstate.getText().toString();
                state=Tstate.getText().toString();
                optional=Toptional.getText().toString();
                city=Tcity.getText().toString();
                if(name.length()==0) {Alert("Name"); return;}
                if(mobile.length()==0){Alert("Mobile no."); return;}
                if(ward.length()==0){Alert("Apartment,Floor,Lane etc"); return;}
                if(street.length()==0){Alert("Street Address"); return;}
                if(city.length()==0){Alert("City/District"); return;}
                if(state.length()==0){Alert("State");return ;}
                if(pincode.length()==0){Alert("Pincode"); return;}
                Address add=new Address(name,ward,street,pincode,city,state,mobile,optional);//pojo class,have to save it on firebase
                if (getIntent().getIntExtra("status",-1) == 0){
                    ref.child(adress.getKey()).setValue(add);
                    finish();
                }else {
                    String key=ref.push().getKey();
                    ref.child(key).setValue(add);
                    Intent intent1=new Intent();
                    intent1.putExtra("key",key);
                    setResult(RESULT_OK);
                    finish();
                }

            }
        });
        //cancel button
        findViewById(R.id.shipi_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
// function for alerts
    void Alert(String Message)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(ShippingAddressForm.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Please enter your "+Message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
