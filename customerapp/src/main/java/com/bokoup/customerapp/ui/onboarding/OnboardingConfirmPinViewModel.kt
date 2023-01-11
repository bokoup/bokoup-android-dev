package com.bokoup.customerapp.ui.onboarding

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class OnboardingConfirmPinViewModel @AssistedInject constructor(
    @Assisted private val pinToConfirm: String,
) : ViewModel() {

    private val _textInput = MutableStateFlow(TextFieldValue(""))
    val textInput = _textInput.asStateFlow()

    private val _isError = MutableStateFlow(false)
    val isError = _isError.asStateFlow()

    val isNextButtonEnabled = textInput.map { it.text.length >= OnboardingConstants.MIN_PIN_CODE_LENGTH }

    fun onTextInputChanged(newInputValue: TextFieldValue) {
        _textInput.value = newInputValue
        _isError.value = false
    }

    fun onNextButtonClicked() {
        if (textInput.value.text != pinToConfirm) {
            _isError.value = true
        } else {
            _isError.value = false

            // Coming soon: Persist pin code
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