package com.pixectra.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pixectra.app.Adapter.PhotobookRecyclerViewAdapter;
import com.pixectra.app.Models.Product;

import java.util.ArrayList;

public class PosterActivity extends AppCompatActivity {

    String[] activityTitles = {"FlipBook", "PostCard", "Polaroids", "Photos", "Posters"};





    TextView title_page;
    RecyclerView mrecyclerview;
    PhotobookRecyclerViewAdapter mposterRecyclerViewAdapter;
    int type;




ArrayList<Product> data;
    DatabaseReference dataref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poster);
        data=new ArrayList<>();
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference ref=database.getReference("CommonData");
        title_page = findViewById(R.id.title_poster_activity);
        mrecyclerview = findViewById(R.id.poster_activity_recyclerview);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(PosterActivity.this));
        setuprecyclerview();
        //Type Of Activity
        type = getIntent().getIntExtra("type", 1);
        title_page.setText(activityTitles[type-1]);

        dataref = ref.child(activityTitles[type - 1]);

        dataref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data.clear();
                for (DataSnapshot temp:dataSnapshot.getChildren()){
                    Product product = temp.getValue(Product.class);
                    data.add(product);
                }
                findViewById(R.id.poster_acivity_progress).setVisibility(View.GONE);
                mposterRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                findViewById(R.id.poster_acivity_progress).setVisibility(View.GONE);
            }
        });
        dataref.keepSynced(true);
    }


    /**
     * Method To Setup Recycler View
     */
    private void setuprecyclerview() {

        mposterRecyclerViewAdapter = new PhotobookRecyclerViewAdapter(PosterActivity.this, R.layout.photobook_recycler_view_card, data);
        mrecyclerview.setAdapter(mposterRecyclerViewAdapter);
    }
}

