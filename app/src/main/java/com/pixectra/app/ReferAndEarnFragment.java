package com.pixectra.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

/**
 * Created by yugan on 1/10/2018.
 */

public class ReferAndEarnFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.refer_earn_fragment, null);
        view.findViewById(R.id.refer_earn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = user.getUid();
                String link = "http://pixectra.com?user=" + "universal&coupon=DISC10";
                FirebaseDynamicLinks.getInstance().createDynamicLink()
                        .setLink(Uri.parse(link))
                        .setDynamicLinkDomain("v2xqe.app.goo.gl")
                        .setAndroidParameters(
                                new DynamicLink.AndroidParameters.Builder("com.pixectra.app")
                                        .setMinimumVersion(1)
                                        .build())
//                        .setIosParameters(
//                                new DynamicLink.IosParameters.Builder("com.example.ios")
//                                        .setAppStoreId("123456789")
//                                        .setMinimumVersion("1.0.1")
//                                        .build())
                        .buildShortDynamicLink()
                        .addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
                            @Override
                            public void onSuccess(ShortDynamicLink shortDynamicLink) {
                                Uri mInvitationUrl = shortDynamicLink.getShortLink();
                                Log.d("prashu", mInvitationUrl.toString());
                                Toast.makeText(getActivity(), mInvitationUrl.toString(), Toast.LENGTH_SHORT).show();
                                String referrerName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                                String subject = String.format("%s wants you to make memories with Pixectra", referrerName);
                                String invitationLink = mInvitationUrl.toString();
                                String msg = "the perfect app for you and your memories. The app that helps you free your photos and puts them into a monthly photo book Use my referrer link: "
                                        + invitationLink;
                                String msgHtml = String.format("<p>Let's Explore yourself with Pixectra! Use my "
                                        + "<a href=\"%s\">referrer link</a>!</p>", invitationLink);

                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("text/plain"); // only email apps should handle this
                                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                                intent.putExtra(Intent.EXTRA_TEXT, msg);
                                intent.putExtra(Intent.EXTRA_HTML_TEXT, msgHtml);
                                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                    startActivity(intent);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });

            }
        });
        return view;
    }
}
