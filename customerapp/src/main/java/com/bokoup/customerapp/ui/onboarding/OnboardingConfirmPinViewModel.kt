package com.bokoup.customerapp.ui.onboarding

import android.app.Application
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bokoup.customerapp.R
import com.bokoup.customerapp.applock.AppLockManager
import com.bokoup.customerapp.biometrics.AppLockBiometricManager
import com.bokoup.customerapp.biometrics.BiometricPromptResult
import com.bokoup.customerapp.dom.repo.AddressRepo
import com.bokoup.customerapp.flow.MutableEventFlow
import com.bokoup.customerapp.flow.asEventFlow
import com.bokoup.lib.Resource
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class OnboardingConfirmPinViewModel @AssistedInject constructor(
    application: Application,
    @Assisted private val pinToConfirm: String,
    private val addressRepo: AddressRepo,
    private val biometricManager: AppLockBiometricManager,
    private val appLockManager: AppLockManager,
) : AndroidViewModel(application) {

    private val _textInput = MutableStateFlow(TextFieldValue(""))
    val textInput = _textInput.asStateFlow()

    private val _isError = MutableStateFlow(false)
    val isError = _isError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val isNextButtonEnabled = textInput.map { it.text.length >= OnboardingConstants.MIN_PIN_CODE_LENGTH }

    private val _showBiometricPrompt = MutableEventFlow<PromptInfo>()
    val showBiometricPrompt = _showBiometricPrompt.asEventFlow()

    fun onTextInputChanged(newInputValue: TextFieldValue) {
        _textInput.value = newInputValue
        _isError.value = false
    }

    fun onNextButtonClicked() {
        if (textInput.value.text != pinToConfirm) {
            _isError.value = true
        } else {
            _isError.value = false

            if (!biometricManager.isAvailableOnDevice()) {
                savePinAndContinue()
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
    }

    fun onBiometricPromptResult(result: BiometricPromptResult) {
        if (result == BiometricPromptResult.SUCCESS) {
            savePinAndContinue()
        }
    }

    private fun savePinAndContinue() {
        appLockManager.updateCode(textInput.value.text)

        // Create a new, active wallet address. This will allow us into the app proper
        viewModelScope.launch {
            addressRepo.insertAddress(active = true).collectLatest { resource ->
                _isLoading.value = resource is Resource.Loading
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(pinToConfirm: String): OnboardingConfirmPinViewModel
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            pinToConfirm: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(pinToConfirm) as T
            }
        }
    }
}