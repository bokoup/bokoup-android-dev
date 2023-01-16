package com.bokoup.customerapp.ui.tokens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bokoup.customerapp.MintByIdQuery
import com.bokoup.customerapp.R
import com.bokoup.customerapp.dom.model.BokoupTransaction
import com.bokoup.customerapp.dom.repo.AddressRepo
import com.bokoup.customerapp.dom.repo.DataRepo
import com.bokoup.lib.Resource
import com.bokoup.lib.ResourceFlowConsumer
import com.bokoup.lib.flatMapSuccess
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.ksol.core.model.TransactionSignature
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class TokenDetailViewModel @AssistedInject constructor(
    application: Application,
    @Assisted private val tokenId: String,
    private val dataRepo: DataRepo,
    addressRepo: AddressRepo,
) : AndroidViewModel(application) {

    private val transactionResourceConsumer =
        ResourceFlowConsumer<List<BokoupTransaction>>(viewModelScope)
    private val tokenInfoResourceConsumer =
        ResourceFlowConsumer<MintByIdQuery.Mint>(viewModelScope)

    val mintInfo = tokenInfoResourceConsumer.data.filterNotNull()

    val transaction = transactionResourceConsumer.data.filterNotNull()

    val isLoading =
        combine(
            tokenInfoResourceConsumer.isLoading,
            transactionResourceConsumer.isLoading,
        ) { tokenInfoLoading, transactionsLoading ->
            tokenInfoLoading || transactionsLoading
        }

    val error =
        combine(
            tokenInfoResourceConsumer.error,
            transactionResourceConsumer.error,
        ) { tokenInfoError, transactionsError ->
            tokenInfoError ?: transactionsError
        }.map {
            if (it == null) {
                null
            } else {
                application.getString(R.string.error_loading_transaction)
            }
        }

    init {
        val mintToken = PublicKey.fromBase58(tokenId)
        transactionResourceConsumer.collectFlow(
            addressRepo.getActiveAddress()
                .flatMapSuccess { address ->
                    if (address == null) {
                        flowOf(Resource.Success(emptyList()))
                    } else {
                        dataRepo.getTransactionByAddressAndToken(
                            address,
                            mintToken
                        )
                    }
                }
        )

        tokenInfoResourceConsumer.collectFlow(
            dataRepo.getMintTokenInfo(mintToken)
        )
    }


    @AssistedFactory
    interface Factory {
        fun create(tokenId: String): TokenDetailViewModel
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            tokenId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(tokenId) as T
            }
        }
    }
}