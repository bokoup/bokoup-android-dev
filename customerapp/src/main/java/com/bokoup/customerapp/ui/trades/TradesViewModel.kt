package com.bokoup.customerapp.ui.trades

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bokoup.customerapp.dom.model.BokoupTradeListing
import com.bokoup.customerapp.dom.repo.DataRepo
import com.bokoup.lib.ResourceFlowConsumer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class TradesViewModel @Inject constructor(
    application: Application,
    private val dataRepo: DataRepo,
) : AndroidViewModel(application) {

    private val tradeListingsResourceConsumer =
        ResourceFlowConsumer<List<BokoupTradeListing>>(viewModelScope)

    val tradeListings = tradeListingsResourceConsumer.data.filterNotNull()

    val isLoading = tradeListingsResourceConsumer.isLoading

    init {
        tradeListingsResourceConsumer.collectFlow(dataRepo.getTradeListings())
    }

}