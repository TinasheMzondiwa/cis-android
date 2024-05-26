package com.tinashe.hymnal.ui.support

import android.app.Activity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchaseHistoryRecord
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchaseHistoryParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.consumePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchaseHistory
import com.tinashe.hymnal.BuildConfig
import hymnal.android.coroutines.DispatcherProvider
import hymnal.content.model.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import hymnal.l10n.R as L10nR

sealed class BillingData {
    data class Message(val status: Status, val errorRes: Int? = null) : BillingData()
    data class InAppProducts(val products: List<DonateProduct>) : BillingData()
    data class SubscriptionProducts(val products: List<DonateProduct>) : BillingData()
    data class DeepLink(val url: String) : BillingData()
    data object None : BillingData()
}

interface BillingManager {
    fun setup(coroutineScope: CoroutineScope, activity: Activity): Flow<BillingData>
    fun sync()
    fun initiatePurchase(product: DonateProduct, activity: Activity)
}

@Singleton
internal class BillingManagerImpl @Inject constructor(
    private val dispatcherProvider: DispatcherProvider
) : BillingManager, PurchasesUpdatedListener, BillingClientStateListener {

    private var coroutineScope: CoroutineScope? = null
    private var billingClient: BillingClient? = null
    private var purchaseHistory = emptySet<PurchaseHistoryRecord>()
    private var productDetailsList = mutableSetOf<ProductDetails>()

    private val billingData = MutableStateFlow<BillingData>(BillingData.None)

    override fun setup(coroutineScope: CoroutineScope, activity: Activity): Flow<BillingData> {
        this.coroutineScope = coroutineScope

        if (billingClient?.isReady != true) {
            billingClient = BillingClient.newBuilder(activity)
                .setListener(this)
                .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
                .build()

            billingClient?.startConnection(this)
        }

        return billingData
    }

    override fun sync() {
        coroutineScope?.launch(dispatcherProvider.default) {
            queryPurchases()
        }
    }

    override fun initiatePurchase(product: DonateProduct, activity: Activity) {
        if (purchaseHistory.find { it.sku == product.sku } != null) {
            coroutineScope?.launch {
                if (product.type == BillingClient.ProductType.SUBS) {
                    val url = "$SUBS_BASE_URL?${product.sku}&package=${BuildConfig.APPLICATION_ID}"
                    billingData.emit(BillingData.DeepLink(url))
                } else {
                    billingData.emit(
                        BillingData.Message(
                            Status.ERROR,
                            L10nR.string.error_item_already_owned
                        )
                    )
                }
            }
            return
        }

        val productDetails = productDetailsList.find { it.productId == product.sku } ?: return
        val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull()
            ?.offerToken

        val flowBuilder = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
        offerToken?.let { flowBuilder.setOfferToken(it) }

        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(flowBuilder.build()))
            .build()

        val responseCode = try {
            billingClient?.launchBillingFlow(activity, flowParams)?.responseCode
        } catch (e: Exception) {
            Timber.e(e)
            BillingClient.BillingResponseCode.ERROR
        }

        if (responseCode != BillingClient.BillingResponseCode.OK) {
            coroutineScope?.launch {
                billingData.emit(
                    BillingData.Message(
                        Status.ERROR,
                        L10nR.string.error_billing_client_unavailable
                    )
                )
            }
        }
    }

    private suspend fun queryPurchases() {
        val inAppHistory = billingClient?.queryPurchaseHistory(
            QueryPurchaseHistoryParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )?.let {
            it.purchaseHistoryRecordList ?: emptyList()
        } ?: emptyList()

        val subsHistory = billingClient?.queryPurchaseHistory(
            QueryPurchaseHistoryParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )?.let {
            it.purchaseHistoryRecordList ?: emptyList()
        } ?: emptyList()

        purchaseHistory = inAppHistory.union(subsHistory)
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        coroutineScope?.launch(dispatcherProvider.default) {
            if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                purchases.forEach { purchase ->
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        handlePurchase(purchase)
                    }
                    billingData.emit(
                        BillingData.Message(
                            Status.SUCCESS,
                            L10nR.string.success_purchase
                        )
                    )
                }
            } else if (result.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                billingData.emit(
                    BillingData.Message(
                        Status.ERROR,
                        L10nR.string.error_item_already_owned
                    )
                )
            } else {
                billingData.emit(BillingData.Message(Status.ERROR))
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

        coroutineScope?.launch(dispatcherProvider.default) {
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                queryOneTimeDonations()
                queryRecurringDonations()
                queryPurchases()
            } else {
                Timber.e(result.debugMessage)
                billingData.emit(
                    BillingData.Message(
                        Status.ERROR,
                        L10nR.string.error_billing_client_unavailable
                    )
                )
            }
        }
    }

    private suspend fun queryOneTimeDonations() {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(inAppDonations)
            .build()

        val result = billingClient?.queryProductDetails(params)
        val products =
            if (result?.billingResult?.responseCode == BillingClient.BillingResponseCode.OK) {
                result.productDetailsList?.sortedBy { it.productId } ?: emptyList()
            } else {
                Timber.e(result?.billingResult?.debugMessage)
                emptyList()
            }
        productDetailsList.addAll(products)
        val donateProducts = products.map {
            DonateProduct(
                it.productId,
                it.productType,
                it.formattedPrice(BillingClient.ProductType.INAPP)
            )
        }
        billingData.emit(BillingData.InAppProducts(donateProducts))
    }

    private suspend fun queryRecurringDonations() {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(subscriptions)
            .build()

        val result = billingClient?.queryProductDetails(params)
        val subs =
            if (result?.billingResult?.responseCode == BillingClient.BillingResponseCode.OK) {
                result.productDetailsList ?: emptyList()
            } else {
                Timber.e(result?.billingResult?.debugMessage)
                emptyList()
            }
        productDetailsList.addAll(subs)

        val donateProducts = subs.map {
            DonateProduct(
                it.productId,
                it.productType,
                it.formattedPrice(BillingClient.ProductType.SUBS)
            )
        }

        billingData.emit(BillingData.SubscriptionProducts(donateProducts))
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
        ).map {
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(it)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        }
        private val subscriptions = listOf(
            "subscription_1",
            "subscription_3"
        ).map {
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(it)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        }
    }
}

/**
 * Get the [PurchaseHistoryRecord]'s product sku.
 */
private val PurchaseHistoryRecord.sku: String? get() = products.firstOrNull()

/**
 * Get the [ProductDetails]'s formatted price.
 */
private fun ProductDetails.formattedPrice(
    productType: String
): String = when (productType) {
    BillingClient.ProductType.INAPP -> oneTimePurchaseOfferDetails?.formattedPrice ?: ""
    BillingClient.ProductType.SUBS -> subscriptionOfferDetails?.firstOrNull()
        ?.pricingPhases
        ?.pricingPhaseList
        ?.firstOrNull()
        ?.formattedPrice ?: ""
    else -> ""
}
