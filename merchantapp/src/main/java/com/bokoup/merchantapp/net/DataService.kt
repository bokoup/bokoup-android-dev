package com.bokoup.merchantapp.net

import android.content.SharedPreferences
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.bokoup.merchantapp.*
import com.bokoup.merchantapp.model.SharedPrefKeys
import com.bokoup.merchantapp.model.key

class DataService(private val apolloClient: ApolloClient, sharedPref: SharedPreferences) {

    suspend fun fetchPromos(deviceOwner: String, deviceName: String): List<PromoListQuery.Promo> {
        val response = apolloClient.query(PromoListQuery(deviceOwner, deviceName)).execute()
        return response.data?.promo ?: emptyList()
    }

    suspend fun fetchTokenAccounts(tokenOwner: String, deviceOwner: String, deviceName: String): List<TokenAccountListQuery.TokenAccount> {
        val response = apolloClient.query(TokenAccountListQuery(Optional.Present(deviceName), Optional.Present(deviceOwner), Optional.Present(tokenOwner), )).execute()
        return response.data?.tokenAccount ?: emptyList()
    }


    private val deviceOwner = sharedPref.getString(SharedPrefKeys.DeviceOwner.key, "")
    private val deviceName = sharedPref.getString(SharedPrefKeys.DeviceSerial.key, "")
    val promoSubscription = apolloClient.subscription(PromoListSubscription(deviceOwner!!, deviceName!!))
    fun getDelegateTokenSubscription() = apolloClient.subscription(DelegateTokenSubscription())

    suspend fun fetchDelegatedToken(orderId: String, publicKeyString: String): DelegatedTokenQuery.DelegatePromoToken? {

        val response = apolloClient.query(DelegatedTokenQuery(Optional.Present(mapOf("orderId" to orderId)), Optional.Present(publicKeyString))).execute()
        return response.data?.delegatePromoToken?.firstOrNull()
    }

    suspend fun fetchDevice(deviceOwner: String, deviceName: String): DeviceQuery.Device? {
        val response = apolloClient.query(DeviceQuery(deviceOwner, deviceName)).execute()
        return response.data?.device?.firstOrNull()
    }

}