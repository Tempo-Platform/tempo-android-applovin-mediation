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

    public MaxInterstitialAdapterListener maxInterstitialListener;
    public MaxRewardedAdapterListener maxRewardedListener;

    private Boolean hasUserConsent;
    private Boolean isDoNotSell;
    private Boolean isAgeRestrictedUser;

    public TempoMediationAdapter(AppLovinSdk appLovinSdk) {
        super(appLovinSdk);
    }

    @Override
    public void initialize(MaxAdapterInitializationParameters maxInitParams, Activity activity, final OnCompletionListener onCompletionListener) {
        TempoUtils.Say("TempoAdapter: init => " + maxInitParams.getServerParameters());
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
    public void loadInterstitialAd(MaxAdapterResponseParameters maxResponseParams, final Activity activity, MaxInterstitialAdapterListener maxIntListener) {
        TempoUtils.Say("TempoAdapter: loadInterstitialAd => " + maxResponseParams.getCustomParameters());

        // Obtaining consent from users directly is the responsibility of the client developers themselves. Returns NULL unless updated by developer.
        hasUserConsent = maxResponseParams.hasUserConsent();
        isDoNotSell = maxResponseParams.isDoNotSell();
        isAgeRestrictedUser = maxResponseParams.isAgeRestrictedUser();
        TempoUtils.Say("TempoAdapter: " + hasUserConsent + "|" + isDoNotSell + "|" + isAgeRestrictedUser, true);

        String AppId = (String) maxResponseParams.getCustomParameters().get(AdapterConstants.PARAM_APP_ID);
        String cpmFloorStr = (String) maxResponseParams.getCustomParameters().get(AdapterConstants.PARAM_CPM_FLOOR);
        Float cpmFloor = cpmFloorStr != null ? Float.parseFloat(cpmFloorStr) : 0.0F;
        String placementId = maxResponseParams.getThirdPartyAdPlacementId();

        maxInterstitialListener = maxIntListener;
        TempoAdListener tempoInterstitialListener = new TempoAdListener() {
            @Override
            public void onTempoAdFetchSucceeded() {
                TempoUtils.Say("TempoAdapter: onTempoAdFetchSucceeded (I)",true);

                maxInterstitialListener.onInterstitialAdLoaded();
                interstitialReady = true;
            }

            @Override
            public void onTempoAdFetchFailed(String reason) {

                TempoUtils.Say("TempoAdapter: onTempoAdFetchFailed (I)",true);

                maxInterstitialListener.onInterstitialAdLoadFailed(MaxAdapterError.UNSPECIFIED);
            }

            @Override
            public void onTempoAdDisplayed() {

                TempoUtils.Say("TempoAdapter: onTempoAdDisplayed (I)",true);

                maxInterstitialListener.onInterstitialAdDisplayed();
            }

            @Override
            public void onTempoAdShowFailed(String reason) {

                TempoUtils.Say("TempoAdapter: onTempoAdShowFailed (I=[" + reason + "])", true);

                maxInterstitialListener.onInterstitialAdDisplayFailed(MaxAdapterError.UNSPECIFIED);
            }

            @Override
            public void onTempoAdClosed() {

                TempoUtils.Say("TempoAdapter: onTempoAdClosed (I)",true);

                maxInterstitialListener.onInterstitialAdHidden();
                interstitialReady = false;
            }

            @Override
            public String getTempoAdapterVersion() {

                TempoUtils.Say("TempoAdapter: getTempoAdapterVersion (I, SDK=" + Constants.SDK_VERSION + ", Adapter=" + getAdapterVersion() + ")");

                return getAdapterVersion();
            }

            @Override
            public String getTempoAdapterType() {

                TempoUtils.Say("TempoAdapter: getTempoAdapterType (I, Type: " + AdapterConstants.ADAPTER_TYPE + ")");
                return AdapterConstants.ADAPTER_TYPE;

            }

            @Override
            public Boolean hasUserConsent() {
                TempoUtils.Say("TempoAdapter: hasUserConsent (I, " + hasUserConsent + ")");
                return hasUserConsent;
            }
        };
        activity.runOnUiThread(() -> {
            interstitialView = new InterstitialView(AppId, activity);
            interstitialView.loadAd(activity, tempoInterstitialListener, cpmFloor, placementId);
        });
    }

    @Override
    public void showInterstitialAd(MaxAdapterResponseParameters maxResponseParams, Activity activity, MaxInterstitialAdapterListener maxIntListener) {
        TempoUtils.Say("TempoAdapter: showInterstitialAd");
        if (interstitialReady) {
            interstitialView.showAd();
        }
    }

    @Override
    public void loadRewardedAd(MaxAdapterResponseParameters maxResponseParams, final Activity activity, MaxRewardedAdapterListener maxRewListener) {
        TempoUtils.Say("TempoAdapter: loadRewardedAd => " + maxResponseParams.getCustomParameters());

        // Obtaining consent from users directly is the responsibility of the client developers themselves. Returns NULL unless updated by developer.
        hasUserConsent = maxResponseParams.hasUserConsent();
        isDoNotSell = maxResponseParams.isDoNotSell();
        isAgeRestrictedUser = maxResponseParams.isAgeRestrictedUser();
        TempoUtils.Say("TempoAdapter: " + hasUserConsent + "|" + isDoNotSell + "|" + isAgeRestrictedUser);

        String AppId = (String) maxResponseParams.getCustomParameters().get(AdapterConstants.PARAM_APP_ID);
        String cpmFloorStr = (String) maxResponseParams.getCustomParameters().get(AdapterConstants.PARAM_CPM_FLOOR);
        Float cpmFloor = cpmFloorStr != null ? Float.parseFloat(cpmFloorStr) : 0.0F;
        String placementId = maxResponseParams.getThirdPartyAdPlacementId();


        maxRewardedListener = maxRewListener;
        TempoAdListener tempoRewardedListener = new TempoAdListener() {
            @Override
            public void onTempoAdFetchSucceeded() {
                TempoUtils.Say("TempoAdapter: onTempoAdFetchSucceeded (R)",true);

                maxRewardedListener.onRewardedAdLoaded();
                rewardedReady = true;
            }

            @Override
            public void onTempoAdFetchFailed(String reason) {
                TempoUtils.Say("TempoAdapter: onTempoAdFetchFailed (R=[" + reason + "])) ", true);

                maxRewardedListener.onRewardedAdLoadFailed(MaxAdapterError.UNSPECIFIED);
            }

            @Override
            public void onTempoAdDisplayed() {
                TempoUtils.Say("TempoAdapter: onTempoAdDisplayed (R)",true);

                maxRewardedListener.onRewardedAdDisplayed();
            }

            @Override
            public void onTempoAdShowFailed(String reason) {
                TempoUtils.Say("TempoAdapter: onTempoAdShowFailed (R=[" + reason + "]) ", true);

                maxRewardedListener.onRewardedAdDisplayFailed(MaxAdapterError.UNSPECIFIED);
            }

            @Override
            public void onTempoAdClosed() {
                TempoUtils.Say("TempoAdapter: MaxReward set",true);

                maxRewardedListener.onUserRewarded(new MaxReward() {
                    @Override
                    public String getLabel() {
                        return "TempoReward";
                    }

                    @Override
                    public int getAmount() {
                        return MaxReward.DEFAULT_AMOUNT;
                    }
                });

                TempoUtils.Say("TempoAdapter: onTempoAdClosed (R)",true);
                maxRewardedListener.onRewardedAdHidden();
                rewardedReady = false;
            }

            @Override
            public String getTempoAdapterVersion() {
                TempoUtils.Say("TempoAdapter: getTempoAdapterVersion (R, SDK=" + Constants.SDK_VERSION + ", Adapter=" + getAdapterVersion() + ")");

                return getAdapterVersion();
            }

            @Override
            public String getTempoAdapterType() {
                TempoUtils.Say("TempoAdapter: getTempoAdapterType (R, Type: " + AdapterConstants.ADAPTER_TYPE + ")");
                return AdapterConstants.ADAPTER_TYPE ;

            }

            @Override
            public Boolean hasUserConsent() {
                TempoUtils.Say("TempoAdapter: hasUserConsent (R, " + hasUserConsent + ")");
                return hasUserConsent;
            }
        };

        activity.runOnUiThread(() -> {
            rewardedView = new RewardedView(AppId, activity);
            rewardedView.loadAd(activity, tempoRewardedListener, cpmFloor, placementId);
        });
    }

    @Override
    public void showRewardedAd(MaxAdapterResponseParameters maxResponseParams, Activity activity, MaxRewardedAdapterListener maxRewListener) {
        TempoUtils.Say("TempoAdapter: showRewardedAd");
        if (rewardedReady) {
            rewardedView.showAd();
        }
    }
}