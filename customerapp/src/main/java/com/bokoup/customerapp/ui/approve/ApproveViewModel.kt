package com.bokoup.customerapp.ui.approve

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bokoup.customerapp.dom.model.Address
import com.bokoup.customerapp.dom.repo.AddressRepo
import com.bokoup.customerapp.dom.repo.SolanaRepo
import com.bokoup.customerapp.dom.repo.TokenRepo
import com.bokoup.lib.ResourceFlowConsumer
import com.dgsd.ksol.core.model.KeyPair
import com.dgsd.ksol.core.model.TransactionSignature
import com.dgsd.ksol.solpay.model.SolPayTransactionInfo
import com.dgsd.ksol.solpay.model.SolPayTransactionRequestDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApproveViewModel @Inject constructor(
    private val tokenRepo: TokenRepo,
    private val addressRepo: AddressRepo,
    private val solanaRepo: SolanaRepo,
) : ViewModel() {
    private val addressConsumer = ResourceFlowConsumer<Address?>(viewModelScope)
    val appIdConsumer = ResourceFlowConsumer<SolPayTransactionRequestDetails>(viewModelScope)
    val transactionConsumer = ResourceFlowConsumer<SolPayTransactionInfo>(viewModelScope)
    val signatureConsumer = ResourceFlowConsumer<TransactionSignature>(viewModelScope)
    val errorConsumer = merge(
        appIdConsumer.error,
        addressConsumer.error,
        transactionConsumer.error,
        signatureConsumer.error
    )

    val activeWalletAddress = addressConsumer.data.mapNotNull { it?.id }

    private val _swipeComplete = MutableStateFlow(false)
    val swipeComplete =_swipeComplete.asStateFlow()

    fun getKeyPair() {
        viewModelScope.launch(Dispatchers.IO) {
            addressConsumer.collectFlow(addressRepo.getActiveAddress())
        }
    }

    fun getAppId(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            appIdConsumer.collectFlow(
                tokenRepo.getApiId(url)
            )
        }
    }

    fun getTokenTransaction(url: String, address: String) {
        viewModelScope.launch(Dispatchers.IO) {
            transactionConsumer.collectFlow(
                tokenRepo.getTokenTransaction(
                    url,
                    address
                )
            )
        }
    }

    fun signAndSend(transaction: String, keyPair: KeyPair) {
        viewModelScope.launch(Dispatchers.IO) {
            signatureConsumer.collectFlow(solanaRepo.signAndSend(transaction, keyPair))
        }
    }

    fun setSwipeComplete(value: Boolean) {
        _swipeComplete.value = value
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getKeyPair()
        }
    }
}