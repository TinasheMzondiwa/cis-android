package com.tinashe.hymnal.ui.support

import android.app.Activity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchaseHistoryRecord
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.consumePurchase
import com.android.billingclient.api.queryPurchaseHistory
import com.android.billingclient.api.querySkuDetails
import com.tinashe.hymnal.BuildConfig
import com.tinashe.hymnal.R
import com.tinashe.hymnal.data.model.constants.Status
import com.tinashe.hymnal.extensions.coroutines.SchedulerProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber

sealed class BillingData {
    data class Message(val status: Status, val errorRes: Int? = null) : BillingData()
    data class InAppProducts(val products: List<DonateProduct>) : BillingData()
    data class SubscriptionProducts(val products: List<DonateProduct>) : BillingData()
    data class DeepLink(val url: String) : BillingData()
}

interface BillingManager {
    fun setup(coroutineScope: CoroutineScope, activity: Activity): Flow<BillingData>
    fun sync()
    fun initiatePurchase(product: DonateProduct, activity: Activity)
}

class BillingManagerImpl(
    private val schedulerProvider: SchedulerProvider
) : BillingManager, PurchasesUpdatedListener, BillingClientStateListener {

    private var coroutineScope: CoroutineScope? = null
    private var billingClient: BillingClient? = null
    private var purchaseHistory = emptySet<PurchaseHistoryRecord>()
    private var skuDetailsList = mutableSetOf<SkuDetails>()

    private val billingDataChannel = BroadcastChannel<BillingData>(Channel.CONFLATED)

    override fun setup(coroutineScope: CoroutineScope, activity: Activity): Flow<BillingData> {
        this.coroutineScope = coroutineScope

        if (billingClient?.isReady != true) {
            billingClient = BillingClient.newBuilder(activity)
                .setListener(this)
                .enablePendingPurchases()
                .build()

            billingClient?.startConnection(this)
        }

        return billingDataChannel.asFlow()
            .catch { Timber.e(it) }
    }

    override fun sync() {
        coroutineScope?.launch(schedulerProvider.io) {
            queryPurchases()
        }
    }

    override fun initiatePurchase(product: DonateProduct, activity: Activity) {
        if (purchaseHistory.find { it.sku == product.sku } != null) {
            coroutineScope?.launch {
                if (product.type == BillingClient.SkuType.SUBS) {
                    val url = "$SUBS_BASE_URL?${product.sku}&package=${BuildConfig.APPLICATION_ID}"
                    billingDataChannel.send(BillingData.DeepLink(url))
                } else {
                    billingDataChannel.send(
                        BillingData.Message(
                            Status.ERROR, R.string.error_item_already_owned
                        )
                    )
                }
            }
            return
        }

        val skuDetails = skuDetailsList.find { it.sku == product.sku } ?: return
        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .build()
        val result = billingClient?.launchBillingFlow(activity, flowParams)

        if (result?.responseCode != BillingClient.BillingResponseCode.OK) {
            coroutineScope?.launch {
                billingDataChannel.send(
                    BillingData.Message(
                        Status.ERROR,
                        R.string.error_billing_client_unavailable
                    )
                )
            }
        }
    }

    private suspend fun queryPurchases() {
        val inAppHistory = billingClient?.queryPurchaseHistory(BillingClient.SkuType.INAPP)?.let {
            it.purchaseHistoryRecordList ?: emptyList()
        } ?: emptyList()
        val subsHistory = billingClient?.queryPurchaseHistory(BillingClient.SkuType.SUBS)?.let {
            it.purchaseHistoryRecordList ?: emptyList()
        } ?: emptyList()

        purchaseHistory = inAppHistory.union(subsHistory)
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        coroutineScope?.launch(schedulerProvider.io) {
            if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                purchases.forEach { purchase ->
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        handlePurchase(purchase)
                    }
                    billingDataChannel.send(
                        BillingData.Message(
                            Status.SUCCESS, R.string.success_purchase
                        )
                    )
                }
            } else if (result.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                billingDataChannel.send(
                    BillingData.Message(
                        Status.ERROR, R.string.error_item_already_owned
                    )
                )
            } else {
                billingDataChannel.send(BillingData.Message(Status.ERROR))
            }
        }
    }

    private suspend fun handlePurchase(purchase: Purchase) {
        if (!purchase.isAcknowledged) {
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            val consumeParams =
                ConsumeParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
            billingClient?.acknowledgePurchase(params)
            billingClient?.consumePurchase(consumeParams)
        }
    }

    override fun onBillingSetupFinished(result: BillingResult) {
        Timber.i("Billing SetupFinished: ${result.responseCode}")

        coroutineScope?.launch(schedulerProvider.io) {
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                queryOneTimeDonations()
                queryRecurringDonations()
                queryPurchases()
            } else {
                Timber.e(result.debugMessage)
                billingDataChannel.send(
                    BillingData.Message(
                        Status.ERROR,
                        R.string.error_billing_client_unavailable
                    )
                )
            }
        }
    }

    private suspend fun queryOneTimeDonations() {
        val params = SkuDetailsParams.newBuilder()
            .setSkusList(inAppDonations)
            .setType(BillingClient.SkuType.INAPP)
            .build()

        val result = billingClient?.querySkuDetails(params)
        val products =
            if (result?.billingResult?.responseCode == BillingClient.BillingResponseCode.OK) {
                result.skuDetailsList?.sortedBy { it.priceAmountMicros } ?: emptyList()
            } else {
                Timber.e(result?.billingResult?.debugMessage)
                emptyList()
            }
        skuDetailsList.addAll(products)
        val donateProducts = products.map {
            DonateProduct(it.sku, it.type, it.price)
        }
        billingDataChannel.send(BillingData.InAppProducts(donateProducts))
    }

    private suspend fun queryRecurringDonations() {
        val params = SkuDetailsParams.newBuilder()
            .setSkusList(subscriptions)
            .setType(BillingClient.SkuType.SUBS)
            .build()

        val result = billingClient?.querySkuDetails(params)
        val subs =
            if (result?.billingResult?.responseCode == BillingClient.BillingResponseCode.OK) {
                result.skuDetailsList ?: emptyList()
            } else {
                Timber.e(result?.billingResult?.debugMessage)
                emptyList()
            }
        skuDetailsList.addAll(subs)
        val donateProducts = subs.map {
            DonateProduct(it.sku, it.type, it.price)
        }

        billingDataChannel.send(BillingData.SubscriptionProducts(donateProducts))
    }

    override fun onBillingServiceDisconnected() {
        Timber.e("Billing disconnected")
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
