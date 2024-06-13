package com.applovin.mediation.adapters;

// Generic
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Keep;

// AppLovin SDK
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.adapter.MaxAdapterError;
import com.applovin.mediation.adapter.MaxInterstitialAdapter;
import com.applovin.mediation.adapter.MaxRewardedAdapter;
import com.applovin.mediation.adapter.listeners.MaxInterstitialAdapterListener;
import com.applovin.mediation.adapter.listeners.MaxRewardedAdapterListener;
import com.applovin.mediation.adapter.parameters.MaxAdapterInitializationParameters;
import com.applovin.mediation.adapter.parameters.MaxAdapterResponseParameters;
import com.applovin.sdk.AppLovinSdk;

// Tempo SDK
import com.tempoplatform.ads.Constants;
import com.tempoplatform.ads.TempoAdListener;
import com.tempoplatform.ads.TempoUtils;
import com.tempoplatform.ads.InterstitialView;
import com.tempoplatform.ads.RewardedView;

@Keep
public class TempoMediationAdapter extends MediationAdapterBase implements MaxInterstitialAdapter, MaxRewardedAdapter {

    private static final String LOG_TAG = TempoMediationAdapter.class.getSimpleName();
    private InterstitialView interstitialView;
    private RewardedView rewardedView;
    private boolean interstitialReady;
    private boolean rewardedReady;

//    public MaxInterstitialAdapterListener maxInterstitialListener;
//    public MaxRewardedAdapterListener maxRewardedListener;

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

        // Extract parameters from response
        Bundle customParametersBundle = maxResponseParams.getCustomParameters();
        String appId = getAppId(customParametersBundle);
        Float cpmFloor = getCpmFloor(customParametersBundle);
        String placementId = maxResponseParams.getThirdPartyAdPlacementId();

        // Create listener for interstitial ad events
        TempoAdListener tempoInterstitialListener = createInterstitialAdListener(maxIntListener);

        // Load the ad on the UI thread
        activity.runOnUiThread(() -> {
            interstitialView = new InterstitialView(appId, activity);
            interstitialView.loadAd(activity, tempoInterstitialListener, cpmFloor, placementId);
        });
    }

    @Override
    public void showInterstitialAd(MaxAdapterResponseParameters maxResponseParams, Activity activity, MaxInterstitialAdapterListener maxIntListener) {
        TempoUtils.Say("TempoAdapter: showInterstitialAd");
        if (interstitialView != null && interstitialReady) {
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

        // Extract parameters from response
        Bundle customParametersBundle = maxResponseParams.getCustomParameters();
        String appId = getAppId(customParametersBundle);
        Float cpmFloor = getCpmFloor(customParametersBundle);
        String placementId = maxResponseParams.getThirdPartyAdPlacementId();

        // Create listener for interstitial ad events
        TempoAdListener tempoRewardedListener = createRewardedAdListener(maxRewListener);

        // Load the ad on the UI thread
        activity.runOnUiThread(() -> {
            rewardedView = new RewardedView(appId, activity);
            rewardedView.loadAd(activity, tempoRewardedListener, cpmFloor, placementId);
        });
    }

    @Override
    public void showRewardedAd(MaxAdapterResponseParameters maxResponseParams, Activity activity, MaxRewardedAdapterListener maxRewListener) {
        TempoUtils.Say("TempoAdapter: showRewardedAd");
        if (rewardedView != null && rewardedReady) {
            rewardedView.showAd();
        }
    }

    /**
     *  Configures Interstitial Ad listeners and callbacks
     */
    private TempoAdListener createInterstitialAdListener(MaxInterstitialAdapterListener maxIntListener) {
        return new TempoAdListener() {
            @Override
            public void onTempoAdFetchSucceeded() {
                TempoUtils.Say("TempoAdapter: onTempoAdFetchSucceeded (I)", true);
                maxIntListener.onInterstitialAdLoaded();
                interstitialReady = true;
            }

            @Override
            public void onTempoAdFetchFailed(String reason) {
                TempoUtils.Say("TempoAdapter: onTempoAdFetchFailed (I) - Reason: " + reason, true);
                maxIntListener.onInterstitialAdLoadFailed(MaxAdapterError.UNSPECIFIED);
            }

            @Override
            public void onTempoAdDisplayed() {
                TempoUtils.Say("TempoAdapter: onTempoAdDisplayed (I)", true);
                maxIntListener.onInterstitialAdDisplayed();
            }

            @Override
            public void onTempoAdShowFailed(String reason) {
                TempoUtils.Say("TempoAdapter: onTempoAdShowFailed (I) - Reason: " + reason, true);
                maxIntListener.onInterstitialAdDisplayFailed(MaxAdapterError.UNSPECIFIED);
            }

            @Override
            public void onTempoAdClosed() {
                TempoUtils.Say("TempoAdapter: onTempoAdClosed (I)", true);
                maxIntListener.onInterstitialAdHidden();
                interstitialReady = false;
            }

            @Override
            public String getTempoAdapterVersion() {
                String versionInfo = "SDK=" + Constants.SDK_VERSION + ", Adapter=" + getAdapterVersion();
                TempoUtils.Say("TempoAdapter: getTempoAdapterVersion (I, " + versionInfo + ")");
                return getAdapterVersion();
            }

            @Override
            public String getTempoAdapterType() {
                String adapterType = AdapterConstants.ADAPTER_TYPE;
                TempoUtils.Say("TempoAdapter: getTempoAdapterType (I, Type: " + adapterType + ")");
                return adapterType;
            }

            @Override
            public Boolean hasUserConsent() {
                TempoUtils.Say("TempoAdapter: hasUserConsent (I, " + hasUserConsent + ")");
                return hasUserConsent;
            }
        };
    }

    /**
     *  Configures Rewarded Ad listeners and callbacks
     */
    private TempoAdListener createRewardedAdListener(MaxRewardedAdapterListener maxRewListener) {
        return new TempoAdListener() {
            @Override
            public void onTempoAdFetchSucceeded() {
                TempoUtils.Say("TempoAdapter: onTempoAdFetchSucceeded (R)",true);
                maxRewListener.onRewardedAdLoaded();
                rewardedReady = true;
            }

            @Override
            public void onTempoAdFetchFailed(String reason) {
                TempoUtils.Say("TempoAdapter: onTempoAdFetchFailed (R=[" + reason + "])) ", true);
                maxRewListener.onRewardedAdLoadFailed(MaxAdapterError.UNSPECIFIED);
            }

            @Override
            public void onTempoAdDisplayed() {
                TempoUtils.Say("TempoAdapter: onTempoAdDisplayed (R)",true);
                maxRewListener.onRewardedAdDisplayed();
            }

            @Override
            public void onTempoAdShowFailed(String reason) {
                TempoUtils.Say("TempoAdapter: onTempoAdShowFailed (R=[" + reason + "]) ", true);
                maxRewListener.onRewardedAdDisplayFailed(MaxAdapterError.UNSPECIFIED);
            }

            @Override
            public void onTempoAdClosed() {
                TempoUtils.Say("TempoAdapter: MaxReward set",true);
                maxRewListener.onUserRewarded(new MaxReward() {
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
                maxRewListener.onRewardedAdHidden();
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
    }

    /**
     *  Attempts to get valid App ID string from customParametersBundle
     */
    private String getAppId(Bundle customParametersBundle) {
        if (customParametersBundle == null) {
            return null;
        }
        return customParametersBundle.getString(AdapterConstants.PARAM_APP_ID);
    }

    /**
     *  Attempts to get valid CMP Floor float from customParametersBundle
     */
    private Float getCpmFloor(Bundle customParametersBundle) {
        if (customParametersBundle == null) {
            return 0.0f;
        }
        String cpmFloorStr = customParametersBundle.getString(AdapterConstants.PARAM_CPM_FLOOR);
        if (cpmFloorStr != null && !cpmFloorStr.isEmpty()) {
            try {
                return Float.parseFloat(cpmFloorStr);
            } catch (NumberFormatException e) {
                TempoUtils.Warn("Invalid CPM floor value: " + cpmFloorStr, true);
            }
        }
        return 0.0f;
    }
}