package com.applovin.mediation.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.Keep;
import androidx.lifecycle.Lifecycle;

import com.applovin.mediation.adapter.MaxAdapterError;
import com.applovin.mediation.adapter.MaxInterstitialAdapter;
import com.applovin.mediation.adapter.listeners.MaxAdViewAdapterListener;
import com.applovin.mediation.adapter.listeners.MaxInterstitialAdapterListener;
import com.applovin.mediation.adapter.parameters.MaxAdapterInitializationParameters;
import com.applovin.mediation.adapter.parameters.MaxAdapterResponseParameters;
import com.applovin.sdk.AppLovinSdk;
import com.tempoplatform.ads.AdListener;
import com.tempoplatform.ads.InterstitialView;

@Keep
public class TempoMediationAdapter extends MediationAdapterBase implements MaxInterstitialAdapter {

    private static final String LOG_TAG = TempoMediationAdapter.class.getSimpleName();
    private InterstitialView interstitialView = new InterstitialView();
    private boolean ready;

    private MaxInterstitialAdapterListener listener;
    private MaxAdViewAdapterListener mBannerListener;

    public TempoMediationAdapter(AppLovinSdk appLovinSdk) {
        super(appLovinSdk);
    }

    @Override
    public void initialize(MaxAdapterInitializationParameters maxAdapterInitializationParameters, Activity activity, final OnCompletionListener onCompletionListener) {
        Log.d(LOG_TAG, "initialization");
        System.out.println(LOG_TAG);
        System.out.println("TempoSDK: Initialized");
    }


    @Override
    public void loadInterstitialAd(MaxAdapterResponseParameters maxAdapterResponseParameters, final Activity activity, MaxInterstitialAdapterListener maxInterstitialAdapterListener) {
        Log.d(LOG_TAG, "load interstitial ad");

        String appkey = maxAdapterResponseParameters.getThirdPartyAdPlacementId();
        Log.d(LOG_TAG, "load interstitial ad 2");
        listener = maxInterstitialAdapterListener;
        Log.d(LOG_TAG, "load interstitial ad 3");
        final AdListener listener = new AdListener() {
            @Override
            public void onAdFetchSucceeded() {
                Log.d(LOG_TAG, "Ad fetch succeeded");
                super.onAdFetchSucceeded();
                TempoMediationAdapter.this.listener.onInterstitialAdLoaded();
                ready = true;
            }

            @Override
            public void onAdFetchFailed() {
                Log.d(LOG_TAG, "Ad fetch failed");
                super.onAdFetchFailed();
                TempoMediationAdapter.this.listener.onInterstitialAdLoadFailed(new MaxAdapterError(1));
            }

            @Override
            public void onInterstitialDisplayed() {
                Log.d(LOG_TAG, "Ad fetch displayed");
                super.onInterstitialDisplayed();
                TempoMediationAdapter.this.listener.onInterstitialAdDisplayed();
            }

            @Override
            public void onAdClosed() {
                Log.d(LOG_TAG, "Ad fetch closed");
                ready = false;
                super.onAdClosed();
            }
        };
        Log.d(LOG_TAG, "load interstitial ad 5");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                interstitialView.loadAd(activity, listener);
            }
        });


        Log.d(LOG_TAG, "load interstitial ad 6");
        ready = true;
    }

    @Override
    public String getSdkVersion() {
        return "8.0.1";
    }

    @Override
    public String getAdapterVersion() {
        return "8.0.1";
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
//        interstitialView = null;
    }

    @Override
    public void showInterstitialAd(MaxAdapterResponseParameters maxAdapterResponseParameters, Activity activity, MaxInterstitialAdapterListener maxInterstitialAdapterListener) {
        Log.d(LOG_TAG, "showInterstitial");
        if (ready) {
            interstitialView.showAd();
        }
    }
}