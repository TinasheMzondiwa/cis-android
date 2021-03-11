package com.tinashe.hymnal.ui.support

import android.app.Activity
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tinashe.hymnal.data.model.constants.Status
import com.tinashe.hymnal.extensions.arch.SingleLiveEvent
import com.tinashe.hymnal.extensions.arch.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SupportViewModel @Inject constructor(
    private val billingManager: BillingManager
) : ViewModel() {

    private val mutablePurchaseResult = SingleLiveEvent<Pair<Status, @StringRes Int?>>()
    val purchaseResultLiveData: LiveData<Pair<Status, Int?>> = mutablePurchaseResult.asLiveData()

    private val mutableDeepLinkUrl = SingleLiveEvent<String>()
    val deepLinkLiveData: LiveData<String> = mutableDeepLinkUrl.asLiveData()

    private val mutableInAppProducts = MutableLiveData<List<DonateProduct>>()
    val inAppProductsLiveData: LiveData<List<DonateProduct>> = mutableInAppProducts.asLiveData()

    private val mutableSubscriptions = MutableLiveData<List<DonateProduct>>()
    val subscriptionsLiveData: LiveData<List<DonateProduct>> = mutableSubscriptions.asLiveData()

    fun loadData(activity: Activity) = viewModelScope.launch {
        billingManager.setup(viewModelScope, activity).collect { billingData ->
            when (billingData) {
                is BillingData.DeepLink -> {
                    mutableDeepLinkUrl.postValue(billingData.url)
                }
                is BillingData.InAppProducts -> {
                    mutableInAppProducts.postValue(billingData.products)
                }
                is BillingData.Message -> {
                    mutablePurchaseResult.postValue(billingData.status to billingData.errorRes)
                }
                is BillingData.SubscriptionProducts -> {
                    mutableSubscriptions.postValue(billingData.products)
                }
            }
        }
    }

    fun viewResumed() {
        billingManager.sync()
    }

    fun initiatePurchase(product: DonateProduct, activity: Activity) {
        billingManager.initiatePurchase(product, activity)
    }
}
