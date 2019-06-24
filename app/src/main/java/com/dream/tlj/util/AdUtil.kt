package com.dream.tlj.util

import android.content.Context
import com.dream.tlj.App
import com.dream.tlj.common.Const
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.orhanobut.logger.Logger

object AdUtil {

    private val APP_ID = "ca-app-pub-3940256099942544~3347511713"
    const val AD_TYPE = 1
    const val AD_REWARD_VALUE = 10
    const val AD_TYPE_BANNER = 1
    const val AD_TYPE_REWARD = 2
    private val TEST_DEVICE = "ADBE798A67A69052D99E2C361A4D5FB9"
    private val AD_BANNER = "ca-app-pub-7773568607801592/4839008651"
    private val AD_REWARD = "ca-app-pub-7773568607801592/7278431121"
    private var mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(App.instance)

    private val rewardedVideoAdListener = object : RewardedVideoAdListener {
        override fun onRewardedVideoAdClosed() {
            reloadRewardAd()
        }

        override fun onRewardedVideoAdLeftApplication() {
            Logger.d("onRewardedVideoAdLeftApplication")

        }

        override fun onRewardedVideoAdLoaded() {
            Logger.d("onRewardedVideoAdLoaded")
        }

        override fun onRewardedVideoAdOpened() {
            Logger.d("onRewardedVideoAdOpened")

        }

        override fun onRewardedVideoCompleted() {
            Logger.d("onRewardedVideoCompleted")

        }

        override fun onRewarded(item: RewardItem?) {
            Logger.d(item)
            item?.let {
                SharedPreferencesUtils.addReward(App.instance, it.amount)
            }
        }

        override fun onRewardedVideoStarted() {
            Logger.d("onRewardedVideoStarted")

        }

        override fun onRewardedVideoAdFailedToLoad(p0: Int) {
            Logger.d("onRewardedVideoAdFailedToLoad")

        }

    }

    fun showBannerAd(view: AdView) {
        if (Const.DEBUG) {
            view.loadAd(AdRequest.Builder().addTestDevice(TEST_DEVICE).build())
        } else {
            view.loadAd(AdRequest.Builder().build())
        }
    }

    fun loadRewardAd() {
        mRewardedVideoAd?.rewardedVideoAdListener = rewardedVideoAdListener;
        if (Const.DEBUG) {
            mRewardedVideoAd?.loadAd(AD_REWARD,
                    AdRequest.Builder().addTestDevice(TEST_DEVICE).build())
        } else {
            mRewardedVideoAd?.loadAd(AD_REWARD,
                    AdRequest.Builder().build())
        }
    }

    private fun reloadRewardAd() {
        if (Const.DEBUG) {
            mRewardedVideoAd?.loadAd(AD_REWARD,
                    AdRequest.Builder().addTestDevice(TEST_DEVICE).build())
        } else {
            mRewardedVideoAd?.loadAd(AD_REWARD,
                    AdRequest.Builder().build())
        }
    }

    fun showRewardAd() {
        mRewardedVideoAd?.let {
            if (it.isLoaded) {
                Logger.d("show ad")
                it.show()
            } else {
                Logger.d("not load ad")
            }
        }
    }

    fun isLoaded(): Boolean {
        mRewardedVideoAd?.let {
            return it.isLoaded
        }
        return false;
    }

    fun init(context: Context) {
        MobileAds.initialize(context, APP_ID)
    }

    fun pause(context: Context) {
        mRewardedVideoAd?.pause(context)
    }

    fun resume(context: Context) {
        mRewardedVideoAd?.resume(context)
    }

}

