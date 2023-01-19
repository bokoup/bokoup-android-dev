package com.bokoup.customerapp.ui.transactions

import android.app.Application
import android.icu.text.NumberFormat
import androidx.compose.ui.res.pluralStringResource
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bokoup.customerapp.R
import com.bokoup.customerapp.dom.model.BokoupTransaction
import com.bokoup.customerapp.dom.repo.AddressRepo
import com.bokoup.customerapp.dom.repo.DataRepo
import com.bokoup.lib.Resource
import com.bokoup.lib.ResourceFlowConsumer
import com.bokoup.lib.flatMapSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    application: Application,
    private val dataRepo: DataRepo,
    addressRepo: AddressRepo,
) : AndroidViewModel(application) {

    private val transactionsResourceConsumer =
        ResourceFlowConsumer<List<BokoupTransaction>>(viewModelScope)

    val transactions = transactionsResourceConsumer.data.filterNotNull()

    val summaryText = transactions.map { txns ->
        val redeemedTxns = txns.filter { it.type == BokoupTransaction.Type.REDEEMED }
        val receivedTxns = txns.filter { it.type == BokoupTransaction.Type.RECEIVED }
        if (receivedTxns.isEmpty() || redeemedTxns.isEmpty()) {
            null
        } else {
            val totalSaved = redeemedTxns.sumOf { it.discountValue ?: BigDecimal.ZERO }
            if (totalSaved <= BigDecimal.ZERO) {
                null
            } else {
                application.resources.getQuantityString(
                    R.plurals.transactions_summary_template,
                    receivedTxns.size,
                    receivedTxns.size,
                    redeemedTxns.size,
                    NumberFormat.getCurrencyInstance().format(totalSaved)
                )
            }
        }
    }

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