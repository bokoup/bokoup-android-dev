package com.bokoup.customerapp.ui.approve

import android.app.Application
import androidx.biometric.BiometricPrompt
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bokoup.customerapp.R
import com.bokoup.customerapp.biometrics.AppLockBiometricManager
import com.bokoup.customerapp.dom.model.Address
import com.bokoup.customerapp.dom.model.AddressWithPrivateKey
import com.bokoup.customerapp.dom.repo.AddressRepo
import com.bokoup.customerapp.dom.repo.SolanaRepo
import com.bokoup.customerapp.dom.repo.TokenRepo
import com.bokoup.customerapp.flow.MutableEventFlow
import com.bokoup.customerapp.flow.asEventFlow
import com.bokoup.lib.Resource
import com.bokoup.lib.ResourceFlowConsumer
import com.dgsd.ksol.core.model.TransactionSignature
import com.dgsd.ksol.solpay.model.SolPayTransactionInfo
import com.dgsd.ksol.solpay.model.SolPayTransactionRequestDetails
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class)
class ApproveViewModel @AssistedInject constructor(
    application: Application,
    @Assisted private val transactionUrl: String,
    private val tokenRepo: TokenRepo,
    private val addressRepo: AddressRepo,
    private val solanaRepo: SolanaRepo,
    private val biometricManager: AppLockBiometricManager,
) : AndroidViewModel(application) {
    private val addressWithPrivateKeyConsumer = ResourceFlowConsumer<AddressWithPrivateKey?>(viewModelScope)
    val appIdConsumer = ResourceFlowConsumer<SolPayTransactionRequestDetails>(viewModelScope)
    val transactionConsumer = ResourceFlowConsumer<SolPayTransactionInfo>(viewModelScope)
    val signatureConsumer = ResourceFlowConsumer<TransactionSignature>(viewModelScope)

    val isLoading = combine(
        appIdConsumer.isLoading,
        addressWithPrivateKeyConsumer.isLoading,
        transactionConsumer.isLoading,
        signatureConsumer.isLoading,
    ) { apiConsumerLoading, addressLoading, transactionConsumerLoading, signatureLoading ->
        apiConsumerLoading || addressLoading || transactionConsumerLoading || signatureLoading
    }

    val errorConsumer = merge(
        appIdConsumer.error,
        addressWithPrivateKeyConsumer.error,
        transactionConsumer.error,
        signatureConsumer.error
    )

    private val _swipeComplete = MutableStateFlow(false)
    val swipeComplete =_swipeComplete.asStateFlow()

    private val _showBiometricPrompt = MutableEventFlow<BiometricPrompt.PromptInfo>()
    val showBiometricPrompt = _showBiometricPrompt.asEventFlow()

    init {
        appIdConsumer.collectFlow(tokenRepo.getApiId(transactionUrl))

        val transactionFlow = addressRepo.getActiveAddress()
            .filterIsInstance<Resource.Success<Address?>>()
            .map { it.data?.id }
            .filterNotNull()
            .take(1)
            .flatMapLatest { tokenRepo.getTokenTransaction(transactionUrl, it) }
        transactionConsumer.collectFlow(transactionFlow)

        combine(
            addressWithPrivateKeyConsumer.data.filterNotNull(),
            transactionConsumer.data.filterNotNull(),
        ) { addressWithPrivateKey, transactionInfo ->
            addressWithPrivateKey.asKeyPair() to transactionInfo.transaction
        }.onEach { (keyPair, transaction) ->
            signatureConsumer.collectFlow(solanaRepo.signAndSend(transaction, keyPair))
        }.launchIn(viewModelScope)
    }

    fun onUserConfirmation() {
        if (!biometricManager.isAvailableOnDevice()) {
            // TODO: Prompt for pin code first
            onUserAuthenticationConfirmation()
        } else {
            val context = getApplication<Application>()
            _showBiometricPrompt.tryEmit(
                biometricManager.createPrompt(
                    title = context.getString(R.string.biometric_prompt_title),
                    description = context.getString(R.string.biometric_prompt_message),
                )
            )
        }
    }

    fun onUserAuthenticationConfirmation() {
        addressWithPrivateKeyConsumer.collectFlow(
            addressRepo.getActiveAddressWithPrivateKey()
        )
    }

    fun setSwipeComplete(value: Boolean) {
        _swipeComplete.value = value
    }

    @AssistedFactory
    interface Factory {
        fun create(url: String): ApproveViewModel
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            url: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(url) as T
            }
        }
    }
}