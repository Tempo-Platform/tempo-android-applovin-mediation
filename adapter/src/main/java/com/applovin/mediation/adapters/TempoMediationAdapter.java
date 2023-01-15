package com.applovin.mediation.adapters;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.Keep;

import com.applovin.mediation.MaxReward;
import com.applovin.mediation.adapter.MaxAdapterError;
import com.applovin.mediation.adapter.MaxInterstitialAdapter;
import com.applovin.mediation.adapter.MaxRewardedAdapter;
import com.applovin.mediation.adapter.listeners.MaxInterstitialAdapterListener;
import com.applovin.mediation.adapter.listeners.MaxRewardedAdapterListener;
import com.applovin.mediation.adapter.parameters.MaxAdapterInitializationParameters;
import com.applovin.mediation.adapter.parameters.MaxAdapterResponseParameters;
import com.applovin.sdk.AppLovinSdk;
import com.tempoplatform.ads.AdListener;
import com.tempoplatform.ads.InterstitialView;

@Keep
public class TempoMediationAdapter extends MediationAdapterBase implements MaxInterstitialAdapter, MaxRewardedAdapter {

    private static final String LOG_TAG = TempoMediationAdapter.class.getSimpleName();
    private InterstitialView interstitialView;
    private boolean ready;

    private MaxInterstitialAdapterListener interstitialListener;
    private MaxRewardedAdapterListener rewardedListener;

    public TempoMediationAdapter(AppLovinSdk appLovinSdk) {
        super(appLovinSdk);
    }

    @Override
    public void initialize(MaxAdapterInitializationParameters maxAdapterInitializationParameters, Activity activity, final OnCompletionListener onCompletionListener) {
        Log.d(LOG_TAG, "Initialized Tempo Adapter");
        Log.d(LOG_TAG, "Params: " + maxAdapterInitializationParameters.getServerParameters());
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
        interstitialListener = null;
        rewardedListener = null;
    }

    @Override
    public void loadInterstitialAd(MaxAdapterResponseParameters maxAdapterResponseParameters, final Activity activity, MaxInterstitialAdapterListener maxInterstitialAdapterListener) {
        Log.d(LOG_TAG, "Loading Interstitial Ad, params: " + maxAdapterResponseParameters.getCustomParameters());
        String AppId = (String) maxAdapterResponseParameters.getCustomParameters().get("app_id");
        Log.d(LOG_TAG, "AppId: " + AppId);
        String cpmFloorStr = (String) maxAdapterResponseParameters.getCustomParameters().get("cpm_floor");
        Log.d(LOG_TAG, "cpmFloor: " + (cpmFloorStr != null ? cpmFloorStr : "0.0"));
        Float cpmFloor = cpmFloorStr != null ? Float.parseFloat(cpmFloorStr) : 0.0F;
        interstitialListener = maxInterstitialAdapterListener;
        final AdListener tempoListener = new AdListener() {
            @Override
            public void onAdFetchSucceeded() {
                Log.d(LOG_TAG, "Ad fetch succeeded");
                interstitialListener.onInterstitialAdLoaded();
                ready = true;
            }

            @Override
            public void onAdFetchFailed() {
                Log.d(LOG_TAG, "Ad fetch failed");
                interstitialListener.onInterstitialAdLoadFailed(new MaxAdapterError(1));
            }

            @Override
            public void onInterstitialDisplayed() {
                Log.d(LOG_TAG, "Ad fetch displayed");
                interstitialListener.onInterstitialAdDisplayed();
            }

            @Override
            public void onAdClosed() {
                Log.d(LOG_TAG, "Ad closed");
                interstitialListener.onInterstitialAdHidden();
                ready = false;
            }
        };
        Log.d(LOG_TAG, "cpmFloor: " + cpmFloor);
        activity.runOnUiThread(() -> {
            interstitialView = new InterstitialView(AppId, activity);
            interstitialView.loadAd(activity, tempoListener, cpmFloor);
        });
    }

    @Override
    public void showInterstitialAd(MaxAdapterResponseParameters maxAdapterResponseParameters, Activity activity, MaxInterstitialAdapterListener maxInterstitialAdapterListener) {
        Log.d(LOG_TAG, "showInterstitial");
        if (ready) {
            interstitialView.showAd();
        }
    }

    @Override
    public void loadRewardedAd(MaxAdapterResponseParameters maxAdapterResponseParameters, final Activity activity, MaxRewardedAdapterListener maxRewardedAdapterListener) {
        Log.d(LOG_TAG, "Loading Rewarded Ad, params: " + maxAdapterResponseParameters.getCustomParameters());
        String AppId = (String) maxAdapterResponseParameters.getCustomParameters().get("app_id");
        Log.d(LOG_TAG, "AppId: " + AppId);
        String cpmFloorStr = (String) maxAdapterResponseParameters.getCustomParameters().get("cpm_floor");
        Log.d(LOG_TAG, "cpmFloor: " + (cpmFloorStr != null ? cpmFloorStr : "0.0"));
        Float cpmFloor = cpmFloorStr != null ? Float.parseFloat(cpmFloorStr) : 0.0F;
        rewardedListener = maxRewardedAdapterListener;
        final AdListener tempoListener = new AdListener() {
            @Override
            public void onAdFetchSucceeded() {
                Log.d(LOG_TAG, "Ad fetch succeeded");
                rewardedListener.onRewardedAdLoaded();
                ready = true;
            }

            @Override
            public void onAdFetchFailed() {
                Log.d(LOG_TAG, "Ad fetch failed");
                rewardedListener.onRewardedAdLoadFailed(new MaxAdapterError(1));
            }

            @Override
            public void onInterstitialDisplayed() {
                Log.d(LOG_TAG, "Ad fetch displayed");
                rewardedListener.onRewardedAdDisplayed();
            }

            @Override
            public void onAdClosed() {
                Log.d(LOG_TAG, "Max reward set");
                rewardedListener.onUserRewarded(new MaxReward() {
                    @Override
                    public String getLabel() {
                        return "TempoReward";
                    }

                    @Override
                    public int getAmount() {
                        return MaxReward.DEFAULT_AMOUNT;
                    }
                });
                Log.d(LOG_TAG, "Ad closed");
                rewardedListener.onRewardedAdHidden();
                ready = false;
            }
        };
        Log.d(LOG_TAG, "cpmFloor: " + cpmFloor);
        activity.runOnUiThread(() -> {
            interstitialView = new InterstitialView(AppId, activity);
            interstitialView.loadAd(activity, tempoListener, cpmFloor);
        });
    }

    @Override
    public void showRewardedAd(MaxAdapterResponseParameters maxAdapterResponseParameters, Activity activity, MaxRewardedAdapterListener maxRewardedAdapterListener) {
        Log.d(LOG_TAG, "showRewarded");
        if (ready) {
            interstitialView.showAd();
        }
    }
}