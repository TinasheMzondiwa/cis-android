package com.tinashe.hymnal.ui.support

import android.app.Activity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.android.billingclient.api.querySkuDetails
import com.tinashe.hymnal.data.model.constants.Status
import com.tinashe.hymnal.extensions.arch.SingleLiveEvent
import com.tinashe.hymnal.extensions.arch.asLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class SupportViewModel @ViewModelInject constructor() :
    ViewModel(),
    PurchasesUpdatedListener,
    BillingClientStateListener {

    private val mutablePurchaseResult = SingleLiveEvent<Pair<Status, String?>>()
    val purchaseResultLiveData: LiveData<Pair<Status, String?>> = mutablePurchaseResult.asLiveData()

    private val mutableInAppProducts = MutableLiveData<List<SkuDetails>>()
    val inAppProductsLiveData: LiveData<List<SkuDetails>> = mutableInAppProducts.asLiveData()

    private val mutableSubscriptions = MutableLiveData<List<SkuDetails>>()
    val subscriptionsLiveData: LiveData<List<SkuDetails>> = mutableSubscriptions.asLiveData()

    private var billingClient: BillingClient? = null

    fun loadData(activity: Activity) {
        if (billingClient?.isReady == true) {
            return
        }

        billingClient = BillingClient.newBuilder(activity)
            .setListener(this)
            .enablePendingPurchases()
            .build()

        billingClient?.startConnection(this)
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            purchases.forEach { purchase ->
                val consumeParams =
                    ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()

                billingClient?.consumeAsync(consumeParams) { billingResult, _ ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        // Handle the success of the consume operation.
                    }
                }
            }
        } else if (result.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
        } else if (result.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            // Handle any other error codes.
        } else {
        }
    }

    override fun onBillingServiceDisconnected() {
        Timber.e("Billing disconnected")
    }

    override fun onBillingSetupFinished(result: BillingResult) {
        Timber.i("Billing SetupFinished: ${result.responseCode}")

        if (result.responseCode == BillingClient.BillingResponseCode.OK) {
            queryOneTimeDonations()
            queryRecurringDonations()
        } else {
            Timber.e(result.debugMessage)
        }
    }

    private fun queryOneTimeDonations() {
        val params = SkuDetailsParams.newBuilder()
            .setSkusList(inAppDonations)
            .setType(BillingClient.SkuType.INAPP)
            .build()

        viewModelScope.launch(Dispatchers.IO) {
            val result = billingClient?.querySkuDetails(params)
            if (result?.billingResult?.responseCode == BillingClient.BillingResponseCode.OK) {
                mutableInAppProducts.postValue(result.skuDetailsList)
            } else {
                Timber.e(result?.billingResult?.debugMessage)
            }
        }
    }

    private fun queryRecurringDonations() {
        val params = SkuDetailsParams.newBuilder()
            .setSkusList(subscriptions)
            .setType(BillingClient.SkuType.SUBS)
            .build()

        viewModelScope.launch(Dispatchers.IO) {
            val result = billingClient?.querySkuDetails(params)
            if (result?.billingResult?.responseCode == BillingClient.BillingResponseCode.OK) {
                mutableSubscriptions.postValue(result.skuDetailsList)
            } else {
                Timber.e(result?.billingResult?.debugMessage)
            }
        }
    }

    fun initiatePurchase(product: SkuDetails, activity: Activity) {
        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(product)
            .build()
        val result = billingClient?.launchBillingFlow(activity, flowParams)

        Timber.i("Billing Response: ${result?.responseCode}")
        Timber.i("Billing Response message: ${result?.debugMessage}")

        if (result?.responseCode != BillingClient.BillingResponseCode.OK) {
            // show default error
        }
    }

    companion object {
        private val inAppDonations = listOf(
            "donate_1",
            "donate_5",
            "donate_10",
            "donate_25",
            "donate_50",
            "donate_100"
        )
        private val subscriptions = listOf(
            "subscription_1",
            "subscription_3"
        )
    }
}
