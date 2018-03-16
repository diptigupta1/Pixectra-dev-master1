package com.pixectra.app;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.pixectra.app.Fragments.OnetimeFragment;
import com.pixectra.app.Fragments.SubscribeFragment;

public class PhotobookActivity extends AppCompatActivity implements ActionBar.TabListener {


    public String tabTitles[] = {"Subscribe", "One time"};
    ViewPager viewPager = null;
    TextView title;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_photobook);


        title = findViewById(R.id.title_poster);
        viewPager = findViewById(R.id.poster_pager);
        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new MyAdpater(fragmentManager));
        //---------setup dummy title----
        title.setText("Photo Book");



        /*
        setting up TAB LAYOUT
        <--
         */
        TabLayout tabLayout = findViewById(R.id.tablayout_poster);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#ffffff"));
        tabLayout.setTabTextColors(ContextCompat.getColorStateList(this, R.color.colorwhite));


    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }



    public class MyAdpater extends FragmentPagerAdapter {
        public MyAdpater(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new SubscribeFragment();
                case 1:
                    return new OnetimeFragment();

                default:
                    return null;


            }

        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}