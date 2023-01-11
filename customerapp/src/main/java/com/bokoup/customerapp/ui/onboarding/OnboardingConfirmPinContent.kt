package com.bokoup.customerapp.ui.onboarding

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bokoup.customerapp.R
import com.bokoup.customerapp.biometrics.showBiometricPrompt
import com.bokoup.customerapp.util.findActivity
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ActivityComponent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@EntryPoint
@InstallIn(ActivityComponent::class)
interface ViewModelFactoryProvider {
    fun noteDetailViewModelFactory(): OnboardingConfirmPinViewModel.Factory
}

@ExperimentalMaterial3Api
@Composable
fun OnboardingConfirmPinContent(
    pinToConfirm: String,
    onNavigateBack: () -> Unit,
    onCreatePinConfirmed: () -> Unit,
) {

    val factory = EntryPointAccessors.fromActivity(
        LocalContext.current as Activity,
        ViewModelFactoryProvider::class.java
    ).noteDetailViewModelFactory()

    val viewModel = viewModel(
        factory = OnboardingConfirmPinViewModel.provideFactory(factory, pinToConfirm)
    ) as OnboardingConfirmPinViewModel

    val textInput by viewModel.textInput.collectAsState()
    val isNextButtonEnabled by viewModel.isNextButtonEnabled.collectAsState(false)
    val shouldShowErrorState by viewModel.isError.collectAsState()

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val activity = LocalContext.current.findActivity()

    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            launch {
                viewModel.showBiometricPrompt.collectLatest {
                    val result = activity.showBiometricPrompt(it)
                    viewModel.onBiometricPromptResult(result)
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.onboarding_confirm_pin_title),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.size(24.dp))

        Text(
            text = stringResource(R.string.onboarding_confirm_pin_message),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.tertiary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(modifier = Modifier.size(48.dp))

        Box(modifier = Modifier.padding(horizontal = 32.dp)) {
            PinInputField(
                value = textInput,
                labelText = if (shouldShowErrorState) {
                    stringResource(R.string.onboarding_confirm_pin_error_does_not_match)
                } else {
                    stringResource(R.string.onboarding_enter_pin_hint)
                },
                isError = shouldShowErrorState,
                onValueChange = { newValue ->
                    viewModel.onTextInputChanged(newValue)
                }
            )
        }

        Spacer(modifier = Modifier.size(24.dp))

        Button(
            onClick = { viewModel.onNextButtonClicked() },
            enabled = isNextButtonEnabled
        ) {
            Text(text = stringResource(R.string.next))
        }
    }
}
