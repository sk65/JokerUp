package com.yefimoyevhen.jokerup.util

import android.app.Activity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class GoogleAdMobManager(
    private val callback: () -> Unit,
    private val activity: Activity,
    private val addView: AdView,
    private var mInterstitialAd: InterstitialAd? = null
) {
    init {
        MobileAds.initialize(activity) {}
        loadInterstitialAd()
        loadBannerAdd()
    }

    private fun loadBannerAdd() = addView.loadAd(AdRequest.Builder().build())

    private fun loadGoogleAdmob() {
        MobileAds.initialize(activity) {}
        loadBannerAdd()
        loadInterstitialAd()
    }

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            activity,
            AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                }
            })
    }

    fun showInterstitialAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                    loadGoogleAdmob()
                    callback.invoke()
                    // gameViewModel.startGame()
                }

                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    loadGoogleAdmob()
                }

                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    callback.invoke()
                    // gameViewModel.startGame()
                }

            }
            mInterstitialAd?.show(activity)
        } else {
            callback.invoke()
//            gameViewModel.startGame()
        }
    }

}