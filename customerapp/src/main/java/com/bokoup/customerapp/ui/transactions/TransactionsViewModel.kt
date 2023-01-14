package com.bokoup.customerapp.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bokoup.customerapp.dom.model.BokoupTransaction
import com.bokoup.customerapp.dom.repo.AddressRepo
import com.bokoup.customerapp.dom.repo.DataRepo
import com.bokoup.lib.Resource
import com.bokoup.lib.ResourceFlowConsumer
import com.bokoup.lib.flatMapSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val dataRepo: DataRepo,
    addressRepo: AddressRepo,
) : ViewModel() {

    private val transactionsResourceConsumer =
        ResourceFlowConsumer<List<BokoupTransaction>>(viewModelScope)

    val transactions = transactionsResourceConsumer.data.filterNotNull()

    val isLoading = transactionsResourceConsumer.isLoading

    init {
        transactionsResourceConsumer.collectFlow(
            addressRepo.getActiveAddress()
                .flatMapSuccess { address ->
                    if (address == null) {
                        flowOf(Resource.Success(emptyList()))
                    } else {
                        dataRepo.getTransactionsByAccount(address)
                    }
                }
        )
    }

}