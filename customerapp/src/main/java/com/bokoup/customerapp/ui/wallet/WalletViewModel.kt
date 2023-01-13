package com.bokoup.customerapp.ui.wallet

import android.app.Application
import androidx.biometric.BiometricPrompt
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bokoup.customerapp.R
import com.bokoup.customerapp.biometrics.AppLockBiometricManager
import com.bokoup.customerapp.dom.model.Address
import com.bokoup.customerapp.dom.repo.AddressRepo
import com.bokoup.customerapp.flow.MutableEventFlow
import com.bokoup.customerapp.flow.asEventFlow
import com.bokoup.lib.ResourceFlowConsumer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import javax.inject.Inject

//https://github.com/android/security-samples/tree/main/FileLocker
@HiltViewModel
class WalletViewModel @Inject constructor(
    application: Application,
    private val repo: AddressRepo,
    private val biometricManager: AppLockBiometricManager,
) : AndroidViewModel(application) {
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
    val insertAddressConsumer = ResourceFlowConsumer<Unit>(viewModelScope)
    val addressesConsumer = ResourceFlowConsumer<List<Address>>(viewModelScope)
    val errorConsumer = merge(
        addressesConsumer.error,
        insertAddressConsumer.error,
    )

    private val _showBiometricPrompt = MutableEventFlow<BiometricPrompt.PromptInfo>()
    val showBiometricPrompt = _showBiometricPrompt.asEventFlow()

    init {
        getAddresses()
    }

    fun onCreateNewWalletClicked() = viewModelScope.launch(dispatcher) {
        if (!biometricManager.isAvailableOnDevice()) {
            // TODO: Prompt for pin code first
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

    fun onUserAuthenticationConfirmed() {
        insertAddressConsumer.collectFlow(repo.insertAddress())
    }

    fun getAddresses() = addressesConsumer.collectFlow(
        repo.getAddresses()
    )

    fun updateActive(id: String) = viewModelScope.launch(dispatcher) {
        repo.updateActive(id)
        getAddresses()
    }


}
