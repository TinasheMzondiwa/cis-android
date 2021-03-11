package com.tinashe.hymnal.ui.support

import android.app.Activity
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchaseHistoryRecord
import com.android.billingclient.api.PurchaseHistoryResponseListener
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.consumePurchase
import com.android.billingclient.api.querySkuDetails
import com.tinashe.hymnal.BuildConfig
import com.tinashe.hymnal.R
import com.tinashe.hymnal.data.model.constants.Status
import com.tinashe.hymnal.extensions.arch.SingleLiveEvent
import com.tinashe.hymnal.extensions.arch.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SupportViewModel @Inject constructor() :
    ViewModel(),
    PurchasesUpdatedListener,
    BillingClientStateListener {

    private val mutablePurchaseResult = SingleLiveEvent<Pair<Status, @StringRes Int?>>()
    val purchaseResultLiveData: LiveData<Pair<Status, Int?>> = mutablePurchaseResult.asLiveData()

    private val mutableDeepLinkUrl = SingleLiveEvent<String>()
    val deepLinkLiveData: LiveData<String> = mutableDeepLinkUrl.asLiveData()

    private val mutableInAppProducts = MutableLiveData<List<SkuDetails>>()
    val inAppProductsLiveData: LiveData<List<SkuDetails>> = mutableInAppProducts.asLiveData()

    private val mutableSubscriptions = MutableLiveData<List<SkuDetails>>()
    val subscriptionsLiveData: LiveData<List<SkuDetails>> = mutableSubscriptions.asLiveData()

    private var billingClient: BillingClient? = null
    private var purchaseHistory = emptyList<PurchaseHistoryRecord>()

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

    fun viewResumed() {
        queryPurchases()
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            purchases.forEach { purchase ->
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    handlePurchase(purchase)
                }
                mutablePurchaseResult.postValue(Status.SUCCESS to R.string.success_purchase)
            }
        } else if (result.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            mutablePurchaseResult.postValue(Status.ERROR to R.string.error_item_already_owned)
        } else {
            mutablePurchaseResult.postValue(Status.ERROR to null)
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (!purchase.isAcknowledged) {
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            val consumeParams =
                ConsumeParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
            viewModelScope.launch(Dispatchers.IO) {
                billingClient?.acknowledgePurchase(params)
                billingClient?.consumePurchase(consumeParams)
            }
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
            queryPurchases()
        } else {
            Timber.e(result.debugMessage)
            mutablePurchaseResult.postValue(Status.ERROR to R.string.error_billing_client_unavailable)
        }
    }

    private fun queryOneTimeDonations() {
        val params = SkuDetailsParams.newBuilder()
            .setSkusList(inAppDonations)
            .setType(BillingClient.SkuType.INAPP)
            .build()

        viewModelScope.launch(Dispatchers.IO) {
            val result = billingClient?.querySkuDetails(params)
            val products =
                if (result?.billingResult?.responseCode == BillingClient.BillingResponseCode.OK) {
                    result.skuDetailsList?.sortedBy { it.priceAmountMicros }
                } else {
                    Timber.e(result?.billingResult?.debugMessage)
                    emptyList()
                }
            mutableInAppProducts.postValue(products)
        }
    }

    private fun queryRecurringDonations() = viewModelScope.launch(Dispatchers.IO) {
        val params = SkuDetailsParams.newBuilder()
            .setSkusList(subscriptions)
            .setType(BillingClient.SkuType.SUBS)
            .build()

        val result = billingClient?.querySkuDetails(params)
        val subs =
            if (result?.billingResult?.responseCode == BillingClient.BillingResponseCode.OK) {
                result.skuDetailsList
            } else {
                Timber.e(result?.billingResult?.debugMessage)
                emptyList()
            }
        mutableSubscriptions.postValue(subs)
    }

    private fun queryPurchases() {
        val listener = PurchaseHistoryResponseListener { _, purchases ->
            Timber.i("Purchases: $purchases")
            purchaseHistory = purchases ?: emptyList()
        }
        billingClient?.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP, listener)
        billingClient?.queryPurchaseHistoryAsync(BillingClient.SkuType.SUBS, listener)
    }

    fun initiatePurchase(product: SkuDetails, activity: Activity) {
        if (purchaseHistory.find { it.sku == product.sku } != null) {
            if (product.type == BillingClient.SkuType.SUBS) {
                val url = "$SUBS_BASE_URL?${product.sku}&package=${BuildConfig.APPLICATION_ID}"
                mutableDeepLinkUrl.postValue(url)
            } else {
                mutablePurchaseResult.postValue(Status.ERROR to R.string.error_item_already_owned)
            }
            return
        }
        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(product)
            .build()
        val result = billingClient?.launchBillingFlow(activity, flowParams)

        Timber.i("Billing Response: ${result?.responseCode}")
        Timber.i("Billing Response message: ${result?.debugMessage}")
    }

    companion object {
        private const val SUBS_BASE_URL = "https://play.google.com/store/account/subscriptions"
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
