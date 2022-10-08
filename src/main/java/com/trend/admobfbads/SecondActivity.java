package com.trend.admobfbads;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.LinearLayout;
import com.facebook.ads.AdSize;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SecondActivity extends AppCompatActivity {

    private com.facebook.ads.AdView fbBannerAd;
    private AdView mAdView;
    String AdmobAdsShowValue="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        AudienceNetworkAds.initialize(this);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("AdsController");
        dbRef.child("Admob").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    AdmobAdsShowValue = dataSnapshot.child("AdmobAdPermitted").getValue().toString();
                    if (AdmobAdsShowValue.equals("yes")) {
                        LoadAdmobBannerAd();
                    }else{
                        LoadFbBannerAd();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void LoadFbBannerAd(){
        fbBannerAd = new com.facebook.ads.AdView(this, getString(R.string.fb_bannerad_id), AdSize.BANNER_HEIGHT_50);
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);
        adContainer.addView(fbBannerAd);
        fbBannerAd.loadAd();
    }
    @Override
    protected void onDestroy() {
        if (fbBannerAd != null) {
            fbBannerAd.destroy();
        }
        super.onDestroy();
    }

    public void LoadAdmobBannerAd(){
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd((adRequest));
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdOpened() {
            }
            @Override
            public void onAdClicked() {
            }

        });

    }

}