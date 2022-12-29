package com.bihar.pratidin.AdsManager;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bihar.pratidin.BuildConfig;
import com.bihar.pratidin.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;


public class GoogleAds {

    public static InterstitialAd mInterstitialAd;

    public static void loadGoogleFullscreen(Context context)    {

        final String FULLSCREEN_ID = context.getString(R.string.full_id);

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(
                context,
                FULLSCREEN_ID,
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("TAG", "onAdLoaded");

                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.d("TAG", "The ad was dismissed.");
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when fullscreen content failed to show.
                                Log.d("TAG", "The ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                                Log.d("TAG", "The ad was shown.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("TAG", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }

    public static void loadNativeAds(Activity activity, final View view, final ViewGroup viewGroup, final NativeAdView nativeAdView) {
        nativeAdView.setMediaView((MediaView) nativeAdView.findViewById(R.id.media_view));
        nativeAdView.setHeadlineView(nativeAdView.findViewById(R.id.primary));
        nativeAdView.setBodyView(nativeAdView.findViewById(R.id.secondary));
        nativeAdView.setCallToActionView(nativeAdView.findViewById(R.id.cta));
        nativeAdView.setIconView(nativeAdView.findViewById(R.id.icon));
        nativeAdView.setAdvertiserView(nativeAdView.findViewById(R.id.tertiary));

        AdLoader.Builder builder = new AdLoader.Builder((Context) activity,
                activity.getString(R.string.native_id))
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        GoogleAds.populateNativeAdView(nativeAd, nativeAdView);
                        viewGroup.setVisibility(View.VISIBLE);
                        ((View) viewGroup.getParent().getParent()).setVisibility(View.VISIBLE);
                        if (view != null) {
                            view.setVisibility(View.VISIBLE);
                        }
                    }
                }).withAdListener(new AdListener(){
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError adError) {
                        // Handle the failure by logging, altering the UI, and so on.
                        Log.e("adError ",adError.getMessage());
                    }
                });
        if (BuildConfig.APPLICATION_ID.equals(activity.getPackageName())) {
            builder.build().loadAd(new AdRequest.Builder().build());
        }
    }

    public static void populateNativeAdView(NativeAd nativeAd, NativeAdView nativeAdView) {
        ((TextView) nativeAdView.getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView) nativeAdView.getBodyView()).setText(nativeAd.getBody());
        ((TextView) nativeAdView.getCallToActionView()).setText(nativeAd.getCallToAction());
        NativeAd.Image icon = nativeAd.getIcon();
        if (icon == null) {
            nativeAdView.getIconView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) nativeAdView.getIconView()).setImageDrawable(icon.getDrawable());
            nativeAdView.getIconView().setVisibility(View.VISIBLE);
        }
        if (nativeAd.getAdvertiser() == null) {
            nativeAdView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) nativeAdView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            nativeAdView.getAdvertiserView().setVisibility(View.VISIBLE);
        }
        nativeAdView.setNativeAd(nativeAd);
    }

    public static void hideNativeAds(Activity activity) {
        ((View) ((ViewGroup) activity.findViewById(R.id.admob_native_container)).getParent()).setVisibility(View.GONE);
    }
}

