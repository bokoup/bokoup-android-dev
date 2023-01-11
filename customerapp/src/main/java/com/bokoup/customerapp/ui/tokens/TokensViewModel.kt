package com.bokoup.customerapp.ui.tokens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bokoup.customerapp.dom.repo.AddressRepo
import com.bokoup.customerapp.dom.repo.DataRepo
import com.bokoup.lib.ResourceFlowConsumer
import com.bokoup.lib.mapData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TokensViewModel @Inject constructor(
    private val dataRepo: DataRepo,
    private val addressRepo: AddressRepo,
) : ViewModel() {
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    val addressConsumer = ResourceFlowConsumer<String?>(viewModelScope)
    val tokenAccountSubscription = dataRepo.tokenAccountSubscriptionFlow
    fun getActiveAdress() = viewModelScope.launch(dispatcher) {
        addressConsumer.collectFlow(
            addressRepo.getActiveAddress().mapData { it?.id }
        )
    }

    init {
        getActiveAdress()
    }

}