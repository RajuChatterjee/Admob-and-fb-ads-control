package com.trend.admobfbads;

import static com.facebook.ads.CacheFlag.ALL;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.ads.Ad;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private InterstitialAd adMobIntAd;
    private com.facebook.ads.InterstitialAd fbIntAd;
    String AdmobAdsShowValue="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AudienceNetworkAds.initialize(this);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("AdsController");
        dbRef.child("Admob").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    AdmobAdsShowValue = dataSnapshot.child("AdmobAdPermitted").getValue().toString();
                    //minor  changes here
                    if (AdmobAdsShowValue.equals("yes")){
                        LoadAdmobInterstitialAds();
                    }else {
                        LoadFbInterstitialAds();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //copy it and paste above in datasnapshot

        if (AdmobAdsShowValue.equals("yes")){
            LoadAdmobInterstitialAds();
        }else {
            LoadFbInterstitialAds();
        }

        Button secondActivityButton = findViewById(R.id.button);
        secondActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (adMobIntAd != null) {
                    adMobIntAd.show(MainActivity.this);
                    adMobIntAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                            startActivity(intent);
                        }
                        @Override
                        public void onAdShowedFullScreenContent() {
                            adMobIntAd = null;
                        }
                    });
                } else{
                    if (AdmobAdsShowValue.equals("no") && fbIntAd.isAdLoaded() && !fbIntAd.isAdInvalidated()) {
                        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                        startActivity(intent);
                        fbIntAd.show();
                    }else {
                        Intent intent = new Intent(MainActivity.this, SecondActivity.class);

                        startActivity(intent);
                    }
                }
            }
        });
    }

    private void LoadFbInterstitialAds() {

        fbIntAd = new com.facebook.ads.InterstitialAd(this, getString(R.string.fb_int_ad));
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                fbIntAd.loadAd();
            }

            @Override
            public void onError(Ad ad, com.facebook.ads.AdError adError) {
                fbIntAd.loadAd();

            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };
        fbIntAd.loadAd(fbIntAd.buildLoadAdConfig()
                .withAdListener(interstitialAdListener)
                .withCacheFlags(ALL)
                .build());
    }
    @Override
    protected void onDestroy() {
        if (fbIntAd != null) {
            fbIntAd.destroy();
        }
        super.onDestroy();
    }

    private void LoadAdmobInterstitialAds() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, getString(R.string.admob_int_ad), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                adMobIntAd = interstitialAd;
            }
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                adMobIntAd = null;
            }
        });
    }

}