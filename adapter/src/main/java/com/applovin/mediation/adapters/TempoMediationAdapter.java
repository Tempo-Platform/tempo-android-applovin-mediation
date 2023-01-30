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
    private InterstitialView rewardedView;
    private boolean interstitialReady;
    private boolean rewardedReady;

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
        return "0.2.3";
    }

    @Override
    public String getAdapterVersion() {
        return "0.2.0";
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        interstitialView = null;
        interstitialListener = null;
        rewardedView = null;
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
        final AdListener tempoInterstitialListener = new AdListener() {
            @Override
            public void onAdFetchSucceeded(Boolean isInterstitial) {
                Log.d(LOG_TAG, (isInterstitial ? "Interstitial" : "Rewarded") + " InterstitialAd fetch succeeded");
                interstitialListener.onInterstitialAdLoaded();
                interstitialReady = true;
            }

            @Override
            public void onAdFetchFailed(Boolean isInterstitial) {
                Log.d(LOG_TAG, (isInterstitial ? "Interstitial" : "Rewarded") + " InterstitialAd fetch failed");
                interstitialListener.onInterstitialAdLoadFailed(new MaxAdapterError(1));
            }

            @Override
            public void onInterstitialDisplayed(Boolean isInterstitial) {
                Log.d(LOG_TAG, (isInterstitial ? "Interstitial" : "Rewarded") + " InterstitialAd fetch displayed");
                interstitialListener.onInterstitialAdDisplayed();
            }

            @Override
            public void onAdClosed(Boolean isInterstitial) {
                Log.d(LOG_TAG, (isInterstitial ? "Interstitial" : "Rewarded") + " InterstitialAd closed");
                interstitialListener.onInterstitialAdHidden();
                interstitialReady = false;
            }
        };
        Log.d(LOG_TAG, "cpmFloor: " + cpmFloor);
        activity.runOnUiThread(() -> {
            interstitialView = new InterstitialView(AppId, activity);
            interstitialView.loadAd(activity, tempoInterstitialListener, cpmFloor, true);
        });
    }

    @Override
    public void showInterstitialAd(MaxAdapterResponseParameters maxAdapterResponseParameters, Activity activity, MaxInterstitialAdapterListener maxInterstitialAdapterListener) {
        Log.d(LOG_TAG, "showInterstitial");
        if (interstitialReady) {
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
        final AdListener tempoRewardedListener = new AdListener() {
            @Override
            public void onAdFetchSucceeded(Boolean isInterstitial) {
                Log.d(LOG_TAG, (isInterstitial ? "Interstitial" : "Rewarded") + " RewardedAd fetch succeeded");
                rewardedListener.onRewardedAdLoaded();
                rewardedReady = true;
            }

            @Override
            public void onAdFetchFailed(Boolean isInterstitial) {
                Log.d(LOG_TAG, (isInterstitial ? "Interstitial" : "Rewarded") + " RewardedAd fetch failed");
                rewardedListener.onRewardedAdLoadFailed(new MaxAdapterError(1));
            }

            @Override
            public void onInterstitialDisplayed(Boolean isInterstitial) {
                Log.d(LOG_TAG, (isInterstitial ? "Interstitial" : "Rewarded") + " RewardedAd fetch displayed");
                rewardedListener.onRewardedAdDisplayed();
            }

            @Override
            public void onAdClosed(Boolean isInterstitial) {
                Log.d(LOG_TAG, (isInterstitial ? "Interstitial" : "Rewarded") + " RewardedAd Max reward set");
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
                Log.d(LOG_TAG, (isInterstitial ? "Interstitial" : "Rewarded") + " RewardedAd closed");
                rewardedListener.onRewardedAdHidden();
                rewardedReady = false;
            }
        };
        Log.d(LOG_TAG, "cpmFloor: " + cpmFloor);
        activity.runOnUiThread(() -> {
            rewardedView = new InterstitialView(AppId, activity);
            rewardedView.loadAd(activity, tempoRewardedListener, cpmFloor, false);
        });
    }

    @Override
    public void showRewardedAd(MaxAdapterResponseParameters maxAdapterResponseParameters, Activity activity, MaxRewardedAdapterListener maxRewardedAdapterListener) {
        Log.d(LOG_TAG, "showRewarded");
        if (rewardedReady) {
            rewardedView.showAd();
        }
    }
}