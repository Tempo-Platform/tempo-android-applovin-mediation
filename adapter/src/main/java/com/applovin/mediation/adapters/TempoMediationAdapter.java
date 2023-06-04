package com.applovin.mediation.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Keep;

import com.applovin.mediation.MaxReward;
import com.applovin.mediation.adapter.MaxAdapterError;
import com.applovin.mediation.adapter.MaxInterstitialAdapter;
import com.applovin.mediation.adapter.MaxRewardedAdapter;
import com.applovin.mediation.adapter.listeners.MaxInterstitialAdapterListener;
import com.applovin.mediation.adapter.listeners.MaxRewardedAdapterListener;
import com.applovin.mediation.adapter.parameters.MaxAdapterInitializationParameters;
import com.applovin.mediation.adapter.parameters.MaxAdapterParameters;
import com.applovin.mediation.adapter.parameters.MaxAdapterResponseParameters;
import com.applovin.sdk.AppLovinPrivacySettings;
import com.applovin.sdk.AppLovinSdk;

import com.tempoplatform.ads.InterstitialAdListener;
import com.tempoplatform.ads.InterstitialView;
import com.tempoplatform.ads.RewardedAdListener;
import com.tempoplatform.ads.RewardedView;

@Keep
public class TempoMediationAdapter extends MediationAdapterBase implements MaxInterstitialAdapter, MaxRewardedAdapter {

    private static final String LOG_TAG = TempoMediationAdapter.class.getSimpleName();
    private InterstitialView interstitialView;
    private RewardedView rewardedView;
    private boolean interstitialReady;
    private boolean rewardedReady;
    private String dynSdkVersion = "1.0.2";

    public MaxInterstitialAdapterListener interstitialListener;
    public MaxRewardedAdapterListener rewardedListener;

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
        return dynSdkVersion;
    }

    @Override
    public String getAdapterVersion() {
        return "1.0.3";
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
//        interstitialView = null;
//        interstitialListener = null;
//        rewardedView = null;
//        rewardedListener = null;
    }

    @Override
    public void loadInterstitialAd(MaxAdapterResponseParameters maxAdapterResponseParameters, final Activity activity, MaxInterstitialAdapterListener maxInterstitialAdapterListener) {
        Log.d(LOG_TAG, "Loading Interstitial Ad, params: " + maxAdapterResponseParameters.getCustomParameters());
        maxAdapterResponseParameters.getThirdPartyAdPlacementId();

        String AppId = (String) maxAdapterResponseParameters.getCustomParameters().get("app_id");
        Log.d(LOG_TAG, "AppId: " + AppId);
        String location = (String) maxAdapterResponseParameters.getCustomParameters().get("geo");
        Log.d(LOG_TAG, "Location: " + location);
        String cpmFloorStr = (String) maxAdapterResponseParameters.getCustomParameters().get("cpm_floor");
        Log.d(LOG_TAG, "cpmFloor: " + (cpmFloorStr != null ? cpmFloorStr : "0.0"));
        Float cpmFloor = cpmFloorStr != null ? Float.parseFloat(cpmFloorStr) : 0.0F;
        String placementId = maxAdapterResponseParameters.getThirdPartyAdPlacementId();
        Log.d(LOG_TAG, "placementId: " + placementId);

        interstitialListener = maxInterstitialAdapterListener;
        InterstitialAdListener tempoInterstitialListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialAdFetchSucceeded() {
                Log.d(LOG_TAG, "Interstitial ad fetch succeeded");
                interstitialListener.onInterstitialAdLoaded();
                interstitialReady = true;
            }

            @Override
            public void onInterstitialAdFetchFailed() {
                Log.d(LOG_TAG, "Interstitial ad fetch failed");
                interstitialListener.onInterstitialAdLoadFailed(MaxAdapterError.UNSPECIFIED);
            }

            @Override
            public void onInterstitialAdDisplayed() {
                Log.d(LOG_TAG, "Interstitial ad fetch displayed");
                interstitialListener.onInterstitialAdDisplayed();
            }

            @Override
            public void onInterstitialAdClosed() {
                Log.d(LOG_TAG, "Interstitial ad closed");
                interstitialListener.onInterstitialAdHidden();
                interstitialReady = false;
            }

            @Override
            public String onVersionExchange(String sdkVersion) {
                Log.d(LOG_TAG, "Version exchange triggered (I)");
                dynSdkVersion = sdkVersion;
                return getAdapterVersion();
            }

            @Override
            public String onGetAdapterType() {
                Log.d(LOG_TAG, "Adapter Type requested (I)");
                return "APPLOVIN";
            }
        };
        activity.runOnUiThread(() -> {
            interstitialView = new InterstitialView(AppId, activity);
            if (location != null) {
                interstitialView.loadAd(activity, tempoInterstitialListener, cpmFloor, placementId, location);
            } else {
                interstitialView.loadAd(activity, tempoInterstitialListener, cpmFloor, placementId);
            }
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
        String location = (String) maxAdapterResponseParameters.getCustomParameters().get("geo");
        Log.d(LOG_TAG, "Location: " + location);
        String cpmFloorStr = (String) maxAdapterResponseParameters.getCustomParameters().get("cpm_floor");
        Log.d(LOG_TAG, "cpmFloor: " + (cpmFloorStr != null ? cpmFloorStr : "0.0"));
        Float cpmFloor = cpmFloorStr != null ? Float.parseFloat(cpmFloorStr) : 0.0F;
        String placementId = maxAdapterResponseParameters.getThirdPartyAdPlacementId();
        Log.d(LOG_TAG, "placementId: " + placementId);

        rewardedListener = maxRewardedAdapterListener;
        RewardedAdListener tempoRewardedListener = new RewardedAdListener() {
            @Override
            public void onRewardedAdFetchSucceeded() {
                Log.d(LOG_TAG, "Rewarded ad fetch succeeded");
                rewardedListener.onRewardedAdLoaded();
                rewardedReady = true;
            }

            @Override
            public void onRewardedAdFetchFailed() {
                Log.d(LOG_TAG, "Rewarded ad fetch failed");
                rewardedListener.onRewardedAdLoadFailed(MaxAdapterError.UNSPECIFIED);
            }

            @Override
            public void onRewardedAdDisplayed() {
                Log.d(LOG_TAG, "Rewarded ad fetch displayed");
                rewardedListener.onRewardedAdDisplayed();
            }

            @Override
            public void onRewardedAdClosed() {
                Log.d(LOG_TAG, "Rewarded ad Max reward set");
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
                Log.d(LOG_TAG, "Rewarded ad closed");
                rewardedListener.onRewardedAdHidden();
                rewardedReady = false;
            }

            @Override
            public String onVersionExchange(String sdkVersion) {
                Log.d(LOG_TAG, "Version exchange triggered (R)");
                dynSdkVersion = sdkVersion;
                return getAdapterVersion();
            }

            @Override
            public String onGetAdapterType() {
                Log.d(LOG_TAG, "Adapter Type requested (R)");
                return "APPLOVIN";
            }
        };
        activity.runOnUiThread(() -> {
            rewardedView = new RewardedView(AppId, activity);
            if (location != null) {
                rewardedView.loadAd(activity, tempoRewardedListener, cpmFloor, placementId, location);
            } else {
                rewardedView.loadAd(activity, tempoRewardedListener, cpmFloor, placementId);
            }
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