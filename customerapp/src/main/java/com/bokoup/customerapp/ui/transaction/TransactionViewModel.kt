package com.bokoup.customerapp.ui.transaction

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bokoup.customerapp.R
import com.bokoup.customerapp.dom.model.BokoupTransaction
import com.bokoup.customerapp.dom.repo.DataRepo
import com.bokoup.lib.ResourceFlowConsumer
import com.dgsd.ksol.core.model.TransactionSignature
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class TransactionViewModel @AssistedInject constructor(
    application: Application,
    @Assisted private val transactionSignature: TransactionSignature,
    private val dataRepo: DataRepo,
) : AndroidViewModel(application) {

    private val transactionResourceConsumer =
        ResourceFlowConsumer<BokoupTransaction>(viewModelScope)

    val transaction = transactionResourceConsumer.data.filterNotNull()

    val isLoading = transactionResourceConsumer.isLoading

    val error = transactionResourceConsumer.error.map {
        if (it == null) {
            null
        } else {
            application.getString(R.string.error_loading_transaction)
        }
    }

    init {
        transactionResourceConsumer.collectFlow(
            dataRepo.getTransaction(transactionSignature)
        )
    }


    @AssistedFactory
    interface Factory {
        fun create(transactionSignature: TransactionSignature): TransactionViewModel
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            transactionSignature: TransactionSignature
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(transactionSignature) as T
            }
        }
    }
}