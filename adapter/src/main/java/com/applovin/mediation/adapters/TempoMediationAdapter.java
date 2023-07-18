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
import com.applovin.mediation.adapter.parameters.MaxAdapterParameters;
import com.applovin.mediation.adapter.parameters.MaxAdapterResponseParameters;
import com.applovin.sdk.AppLovinPrivacySettings;
import com.applovin.sdk.AppLovinSdk;

import com.tempoplatform.ads.Constants;
import com.tempoplatform.ads.InterstitialView;
import com.tempoplatform.ads.RewardedView;
import com.tempoplatform.ads.TempoAdListener;
import com.tempoplatform.ads.TempoUtils;

@Keep
public class TempoMediationAdapter extends MediationAdapterBase implements MaxInterstitialAdapter, MaxRewardedAdapter {

    private static final String LOG_TAG = TempoMediationAdapter.class.getSimpleName();
    private InterstitialView interstitialView;
    private RewardedView rewardedView;
    private boolean interstitialReady;
    private boolean rewardedReady;

    public MaxInterstitialAdapterListener interstitialListener;
    public MaxRewardedAdapterListener rewardedListener;

    private Boolean hasUserConsent;
    private Boolean isDoNotSell;
    private Boolean isAgeRestrictedUser;

    public TempoMediationAdapter(AppLovinSdk appLovinSdk) {
        super(appLovinSdk);
    }

    @Override
    public void initialize(MaxAdapterInitializationParameters maxAdapterInitializationParameters, Activity activity, final OnCompletionListener onCompletionListener) {
        TempoUtils.Say("TempoAdapter: init => " + maxAdapterInitializationParameters.getServerParameters(), true);
    }

    @Override
    public String getSdkVersion() {
        return Constants.SDK_VERSION;
    }

    @Override
    public String getAdapterVersion() {
        return AdapterConstants.ADAPTER_VERSION;
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
    }

    @Override
    public void loadInterstitialAd(MaxAdapterResponseParameters maxAdapterResponseParameters, final Activity activity, MaxInterstitialAdapterListener maxInterstitialAdapterListener) {
        TempoUtils.Say("TempoAdapter: loadInterstitialAd => " + maxAdapterResponseParameters.getCustomParameters(), true);

        // Obtaining consent from users directly is the responsibility of the client developers themselves. Returns NULL unless updated by developer.
        hasUserConsent = maxAdapterResponseParameters.hasUserConsent();
        isDoNotSell = maxAdapterResponseParameters.isDoNotSell();
        isAgeRestrictedUser = maxAdapterResponseParameters.isAgeRestrictedUser();
        TempoUtils.Say("TempoAdapter: " + hasUserConsent + "|" + isDoNotSell + "|" + isAgeRestrictedUser, true);

        String AppId = (String) maxAdapterResponseParameters.getCustomParameters().get(AdapterConstants.PARAM_APP_ID);
        String location = (String) maxAdapterResponseParameters.getCustomParameters().get(AdapterConstants.PARAM_GEO);
        String cpmFloorStr = (String) maxAdapterResponseParameters.getCustomParameters().get(AdapterConstants.PARAM_CPM_FLOOR);
        Float cpmFloor = cpmFloorStr != null ? Float.parseFloat(cpmFloorStr) : 0.0F;
        String placementId = maxAdapterResponseParameters.getThirdPartyAdPlacementId();

        interstitialListener = maxInterstitialAdapterListener;
        TempoAdListener tempoInterstitialListener = new TempoAdListener() {
            @Override
            public void onTempoAdFetchSucceeded() {
                TempoUtils.Say("TempoAdapter: onInterstitialAdFetchSucceeded",true);
                interstitialListener.onInterstitialAdLoaded();
                interstitialReady = true;
            }

            @Override
            public void onTempoAdFetchFailed() {
                TempoUtils.Say("TempoAdapter: onInterstitialAdFetchFailed",true);
                interstitialListener.onInterstitialAdLoadFailed(MaxAdapterError.UNSPECIFIED);
            }

            @Override
            public void onTempoAdDisplayed() {
                TempoUtils.Say("TempoAdapter: onInterstitialAdDisplayed",true);
                interstitialListener.onInterstitialAdDisplayed();
            }

            @Override
            public void onTempoAdClosed() {
                TempoUtils.Say("TempoAdapter: onInterstitialAdClosed",true);
                interstitialListener.onInterstitialAdHidden();
                interstitialReady = false;
            }

            @Override
            public String getTempoAdapterVersion() {
                TempoUtils.Say("TempoAdapter: getTempoAdapterVersion (Rewarded, SDK=" + Constants.SDK_VERSION + ", Adapter=" + getAdapterVersion() + ")");
                return getAdapterVersion();
            }

            @Override
            public String getTempoAdapterType() {
                TempoUtils.Say("TempoAdapter: getTempoAdapterType (Rewarded, Type: " + AdapterConstants.ADAPTER_TYPE + ")");
                return AdapterConstants.ADAPTER_TYPE;
            }

            @Override
            public Boolean hasUserConsent() {
                TempoUtils.Say("TempoAdapter: hasUserConsent (Interstitial, " + hasUserConsent + ")");
                return hasUserConsent;
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
        TempoUtils.Say("TempoAdapter: showInterstitialAd", true);
        if (interstitialReady) {
            interstitialView.showAd();
        }
    }

    @Override
    public void loadRewardedAd(MaxAdapterResponseParameters maxAdapterResponseParameters, final Activity activity, MaxRewardedAdapterListener maxRewardedAdapterListener) {
        TempoUtils.Say("TempoAdapter: loadRewardedAd => " + maxAdapterResponseParameters.getCustomParameters(), true);

        // Obtaining consent from users directly is the responsibility of the client developers themselves. Returns NULL unless updated by developer.
        hasUserConsent = maxAdapterResponseParameters.hasUserConsent();
        isDoNotSell = maxAdapterResponseParameters.isDoNotSell();
        isAgeRestrictedUser = maxAdapterResponseParameters.isAgeRestrictedUser();
        TempoUtils.Say("TempoAdapter: " + hasUserConsent + "|" + isDoNotSell + "|" + isAgeRestrictedUser, true);

        String AppId = (String) maxAdapterResponseParameters.getCustomParameters().get(AdapterConstants.PARAM_APP_ID);
        String location = (String) maxAdapterResponseParameters.getCustomParameters().get(AdapterConstants.PARAM_GEO);
        String cpmFloorStr = (String) maxAdapterResponseParameters.getCustomParameters().get(AdapterConstants.PARAM_CPM_FLOOR);
        Float cpmFloor = cpmFloorStr != null ? Float.parseFloat(cpmFloorStr) : 0.0F;
        String placementId = maxAdapterResponseParameters.getThirdPartyAdPlacementId();

        rewardedListener = maxRewardedAdapterListener;
        TempoAdListener tempoRewardedListener = new TempoAdListener() {
            @Override
            public void onTempoAdFetchSucceeded() {
                TempoUtils.Say("TempoAdapter: onRewardedAdFetchSucceeded",true);
                rewardedListener.onRewardedAdLoaded();
                rewardedReady = true;
            }

            @Override
            public void onTempoAdFetchFailed() {
                TempoUtils.Say("TempoAdapter: onRewardedAdFetchFailed",true);
                rewardedListener.onRewardedAdLoadFailed(MaxAdapterError.UNSPECIFIED);
            }

            @Override
            public void onTempoAdDisplayed() {
                TempoUtils.Say("TempoAdapter: onRewardedAdDisplayed",true);
                rewardedListener.onRewardedAdDisplayed();
            }

            @Override
            public void onTempoAdClosed() {
                TempoUtils.Say("TempoAdapter: MaxReward set",true);
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
                TempoUtils.Say("TempoAdapter: onRewardedAdClosed",true);
                rewardedListener.onRewardedAdHidden();
                rewardedReady = false;
            }

            @Override
            public String getTempoAdapterVersion() {
                TempoUtils.Say("TempoAdapter: getTempoAdapterVersion (Rewarded, SDK=" + Constants.SDK_VERSION + ", Adapter=" + getAdapterVersion() + ")");
                return getAdapterVersion();
            }

            @Override
            public String getTempoAdapterType() {
                TempoUtils.Say("TempoAdapter: getTempoAdapterType (Rewarded, Type: " + AdapterConstants.ADAPTER_TYPE + ")");
                return AdapterConstants.ADAPTER_TYPE ;
            }

            @Override
            public Boolean hasUserConsent() {
                TempoUtils.Say("TempoAdapter: hasUserConsent (Rewarded, " + hasUserConsent + ")");
                return hasUserConsent;
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
        TempoUtils.Say("TempoAdapter: showRewardedAd", true);
        if (rewardedReady) {
            rewardedView.showAd();
        }
    }
}