package com.applovin.mediation.adapters;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.Keep;

import com.applovin.mediation.adapter.MaxAdapterError;
import com.applovin.mediation.adapter.MaxInterstitialAdapter;
import com.applovin.mediation.adapter.listeners.MaxInterstitialAdapterListener;
import com.applovin.mediation.adapter.parameters.MaxAdapterInitializationParameters;
import com.applovin.mediation.adapter.parameters.MaxAdapterResponseParameters;
import com.applovin.sdk.AppLovinSdk;
import com.tempoplatform.ads.AdListener;
import com.tempoplatform.ads.InterstitialView;

@Keep
public class TempoMediationAdapter extends MediationAdapterBase implements MaxInterstitialAdapter {

    private static final String LOG_TAG = TempoMediationAdapter.class.getSimpleName();
    private InterstitialView interstitialView;
    private boolean ready;

    private MaxInterstitialAdapterListener listener;

    public TempoMediationAdapter(AppLovinSdk appLovinSdk) {
        super(appLovinSdk);
    }

    @Override
    public void initialize(MaxAdapterInitializationParameters maxAdapterInitializationParameters, Activity activity, final OnCompletionListener onCompletionListener) {
        Log.d(LOG_TAG, "Initialized Tempo Adapter");
        Log.d(LOG_TAG, "Params: " + maxAdapterInitializationParameters.getServerParameters());
    }


    @Override
    public void loadInterstitialAd(MaxAdapterResponseParameters maxAdapterResponseParameters, final Activity activity, MaxInterstitialAdapterListener maxInterstitialAdapterListener) {
        Log.d(LOG_TAG, "Loading Interstitial Ad");
        String AppId = (String) maxAdapterResponseParameters.getServerParameters().get("app_id");
        Log.d(LOG_TAG, "AppId: " + AppId);
        listener = maxInterstitialAdapterListener;
        final AdListener tempoListener = new AdListener() {
            @Override
            public void onAdFetchSucceeded() {
                Log.d(LOG_TAG, "Ad fetch succeeded");
                listener.onInterstitialAdLoaded();
                ready = true;
            }

            @Override
            public void onAdFetchFailed() {
                Log.d(LOG_TAG, "Ad fetch failed");
                listener.onInterstitialAdLoadFailed(new MaxAdapterError(1));
            }

            @Override
            public void onInterstitialDisplayed() {
                Log.d(LOG_TAG, "Ad fetch displayed");
                listener.onInterstitialAdDisplayed();
            }

            @Override
            public void onAdClosed() {
                Log.d(LOG_TAG, "Ad closed");
                listener.onInterstitialAdHidden();
                ready = false;
            }
        };
        activity.runOnUiThread(() -> {
            interstitialView = new InterstitialView("8", activity);
            interstitialView.loadAd(activity, tempoListener);
        });
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
        interstitialView = null;
        listener = null;
    }

    @Override
    public void showInterstitialAd(MaxAdapterResponseParameters maxAdapterResponseParameters, Activity activity, MaxInterstitialAdapterListener maxInterstitialAdapterListener) {
        Log.d(LOG_TAG, "showInterstitial");
        if (ready) {
            interstitialView.showAd();
        }
    }
}