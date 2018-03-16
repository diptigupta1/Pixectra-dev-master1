package com.pixectra.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pixectra.app.Adapter.ShippingAddressAdapter;
import com.pixectra.app.Models.Address;
import com.pixectra.app.Utils.SessionHelper;

import java.util.ArrayList;

/**
 * Created by prashu on 2/7/2018.
 * for multiple address recycler view
 */

public class SelectAddressActivity extends AppCompatActivity {

    RecyclerView recyclerview;
    DatabaseReference dataref;
    ArrayList<Address> list;
    FirebaseDatabase db;
    DatabaseReference ref;
    ShippingAddressAdapter adap;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiple_shipping);
        db=FirebaseDatabase.getInstance();
        ref=db.getReference("Users/"+new SessionHelper(this).getUid()+"/ShippingAddress");
        Toolbar toolbar = findViewById(R.id.toolbar3);
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
        list = new ArrayList<>();
        adap = new ShippingAddressAdapter(this,list);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot data:dataSnapshot.getChildren()){
                    Address address=data.getValue(Address.class);
                    assert address != null;
                    address.setKey(data.getKey());
                    list.add(address);
                }
                findViewById(R.id.progress_shipping).setVisibility(View.GONE);
                adap.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                findViewById(R.id.progress_shipping).setVisibility(View.GONE);
            }
        });
  //extract list from firebase
        // below is sudo list

        RecyclerView recycler = (RecyclerView)findViewById(R.id.multi_ship_recycler);

                recycler.setAdapter(adap);
                LinearLayoutManager layout = new LinearLayoutManager(getApplicationContext());
                recycler.setLayoutManager(layout);

         /*   }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
        findViewById(R.id.another_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SelectAddressActivity.this,ShippingAddressForm.class);
                intent.putExtra("status",1);//1 indicates ShippingAddressForm calls from Add new Address
                startActivityForResult(intent,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode ==RESULT_OK && requestCode == 1){
            ref.child(data.getStringExtra("key")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Address address = data.getValue(Address.class);
                        assert address != null;
                        address.setKey(data.getKey());
                        list.add(address);
                    }
                    //findViewById(R.id.progress_shipping).setVisibility(View.GONE);
                    adap.notifyDataSetChanged();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                   // findViewById(R.id.progress_shipping).setVisibility(View.GONE);
                }
            });
        }
    }
}
