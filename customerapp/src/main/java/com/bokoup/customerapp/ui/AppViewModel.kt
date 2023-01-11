package com.bokoup.customerapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bokoup.customerapp.dom.model.Address
import com.bokoup.customerapp.dom.repo.AddressRepo
import com.bokoup.lib.ResourceFlowConsumer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    addressRepo: AddressRepo,
): ViewModel() {

    private val activeWalletFlowConsumer = ResourceFlowConsumer<Address?>(viewModelScope)

    val hasActiveWalletAddress = activeWalletFlowConsumer.data.map { it != null }

    init {
        activeWalletFlowConsumer.collectFlow(
            addressRepo.getActiveAddress()
        )
    }
}