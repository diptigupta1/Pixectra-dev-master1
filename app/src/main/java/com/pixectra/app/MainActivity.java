package com.pixectra.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.pixectra.app.Models.Product;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.SharingHelper;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ShareSheetStyle;


@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity implements UserProfileFragment.OnFragmentInteractionListener {
    BranchUniversalObject branchUniversalObject;

    DashboardFragment dashboardFragment;
    UserProfileFragment userProfileFragment;
    ReferAndEarnFragment referAndEarnFragment;

    FirebaseAuth auth, mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    ViewPager viewPager;

    BottomNavigationView bottomNavigationView;

    void add() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("CommonData");

        ref.child("PhotoBooks").setValue(null);
        ref.child("PhotoBooks").push().setValue(new Product("PhotoBooks", "Size1", "http://blog.platinastudio.com/wp-content/uploads/2013/01/saloon1.jpg", 5, 2, 100, "Book"));
        ref.child("FlipBook").setValue(null);

        ref.child("FlipBook").push().setValue(new Product("FlipBook", "Size1", "http://blog.platinastudio.com/wp-content/uploads/2013/01/saloon1.jpg", 5, 2, 100, "Book"));
        ref.child("Photos").setValue(null);

        ref.child("Photos").push().setValue(new Product("Photos", "Size1", "http://blog.platinastudio.com/wp-content/uploads/2013/01/saloon1.jpg", 5, 2, 100, "Book"));
        ref.child("Polaroids").setValue(null);

        ref.child("Polaroids").push().setValue(new Product("Polaroids", "Size1", "http://blog.platinastudio.com/wp-content/uploads/2013/01/saloon1.jpg", 5, 2, 100, "Book"));
        ref.child("PostCard").setValue(null);

        ref.child("PostCard").push().setValue(new Product("PostCard", "Size1", "http://blog.platinastudio.com/wp-content/uploads/2013/01/saloon1.jpg", 5, 2, 100, "Book"));
        ref.child("Posters").setValue(null);

        ref.child("Posters").push().setValue(new Product("Posters", "Size1", "http://blog.platinastudio.com/wp-content/uploads/2013/01/saloon1.jpg", 5, 2, 100, "Book"));

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Hook up your share button to initiate sharing
        findViewById(R.id.buttonShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                initiateSharing();
            }
        });
        contentLoaded();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, LActivity.class));
            finish();
            return;
        } else {
            Toast.makeText(getApplicationContext(), "Signed in", Toast.LENGTH_SHORT).show();
        }
        //rewards to refers
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            Toast.makeText(MainActivity.this, deepLink.toString(), Toast.LENGTH_SHORT).show();

                            String referrerUid = deepLink.getQueryParameter("user");
                            if (referrerUid.equals("universal")) {
                                String code = deepLink.getQueryParameter("coupon");
                                Toast.makeText(MainActivity.this, code, Toast.LENGTH_SHORT).show();
                            }
                            //rewarded gives to inviter
                            Toast.makeText(MainActivity.this, referrerUid, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        viewPager = findViewById(R.id.main_viewpager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(1, true);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.refer_earn);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.dashboard);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.settings);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.dashboard);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.refer_earn:
                        viewPager.setCurrentItem(0, true);
                        return true;
                    case R.id.dashboard:
                        viewPager.setCurrentItem(1, true);
                        return true;
                    case R.id.settings:
                        viewPager.setCurrentItem(2, true);
                        return true;
                }
                return false;
            }
        });


    }

    // This is the function to handle sharing when a user clicks the share button
    void initiateSharing() {
        // Create your link properties
        // More link properties available at https://dev.branch.io/getting-started/configuring-links/guide/#link-control-parameters
        LinkProperties linkProperties = new LinkProperties()
                .setFeature("sharing");

        // Customize the appearance of your share sheet
        ShareSheetStyle shareSheetStyle = new ShareSheetStyle(this, "Check this out!", "Hey friend - I know you'll love this: ")
                .setCopyUrlStyle(getResources().getDrawable(android.R.drawable.ic_menu_send), "Copy link", "Link added to clipboard!")
                .setMoreOptionStyle(getResources().getDrawable(android.R.drawable.ic_menu_search), "Show more")
                .addPreferredSharingOption( SharingHelper.SHARE_WITH.FACEBOOK)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.MESSAGE)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.TWITTER);

        // Show the share sheet for the content you want the user to share. A link will be automatically created and put in the message.
        branchUniversalObject.showShareSheet(this, linkProperties, shareSheetStyle, new Branch.BranchLinkShareListener() {
            @Override
            public void onShareLinkDialogLaunched() { }
            @Override
            public void onShareLinkDialogDismissed() { }
            @Override
            public void onChannelSelected(String channelName) { }
            @Override
            public void onLinkShareResponse(String sharedLink, String sharedChannel, BranchError error) {
                // The link will be available in sharedLink
            }
        });
    }

    // This would be your own method where you've loaded the content for this page
    void contentLoaded() {
        // Initialize a Branch Universal Object for the page the user is viewing
        branchUniversalObject = new BranchUniversalObject()
                .setCanonicalIdentifier("item_id_12345")
                .setTitle("My Content Title")
                .setContentDescription("Check out this awesome piece of content")
                .setContentImageUrl("https://example.com/mycontent-12345.png")
                .addContentMetadata("item_id", "12345")
                .addContentMetadata("user_id", "678910");

        // Trigger a view on the content for analytics tracking
        branchUniversalObject.registerView();

        // List on Google App Indexing
        branchUniversalObject.listOnGoogleSearch(this);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (referAndEarnFragment == null) {
                        referAndEarnFragment = new ReferAndEarnFragment();
                        return referAndEarnFragment;
                    }
                    return referAndEarnFragment;
                case 1:
                    if (dashboardFragment == null) {
                        dashboardFragment = new DashboardFragment();
                        return dashboardFragment;
                    }
                    return dashboardFragment;
                case 2:
                    if (userProfileFragment == null) {
                        userProfileFragment = new UserProfileFragment();
                        return userProfileFragment;
                    }
                    return userProfileFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        Branch branch = Branch.getInstance();
        branch.initSession(new Branch.BranchUniversalReferralInitListener() {
            @Override
            public void onInitFinished(BranchUniversalObject branchUniversalObject, LinkProperties linkProperties, BranchError error) {
                if (error == null && branchUniversalObject != null) {
                    // This code will execute when your app is opened from a Branch deep link, which
                    // means that you can route to a custom activity depending on what they clicked.
                    // In this example, we'll just print out the data from the link that was clicked.

                    Log.i("BranchTestBed", "title " + branchUniversalObject.getTitle());
                    Log.i("ContentMetaData", "metadata " + branchUniversalObject.getMetadata());
                }
            }
        }, this.getIntent().getData(), this);
    }


}
