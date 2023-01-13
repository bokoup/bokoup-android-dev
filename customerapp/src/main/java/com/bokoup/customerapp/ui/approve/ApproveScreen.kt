package com.bokoup.customerapp.ui.approve


import android.app.Activity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bokoup.customerapp.biometrics.BiometricPromptResult
import com.bokoup.customerapp.biometrics.showBiometricPrompt
import com.bokoup.customerapp.nav.Screen
import com.bokoup.customerapp.ui.common.AppScreen
import com.bokoup.customerapp.util.findActivity
import com.bokoup.lib.Loading
import com.dgsd.ksol.core.model.TransactionSignature
import com.dgsd.ksol.solpay.model.SolPayTransactionInfo
import com.dgsd.ksol.solpay.model.SolPayTransactionRequestDetails
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ActivityComponent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@EntryPoint
@InstallIn(ActivityComponent::class)
interface ViewModelFactoryProvider {
    fun approveViewModelFactory(): ApproveViewModel.Factory
}

@Composable
@ExperimentalMaterialApi
@ExperimentalMaterial3Api
fun ApproveScreen(
    snackbarHostState: SnackbarHostState,
    openDrawer: () -> Unit,
    channel: Channel<String>,
    url: String,
    navigateToTokens: () -> Unit
) {
    val factory = EntryPointAccessors.fromActivity(
        LocalContext.current as Activity,
        ViewModelFactoryProvider::class.java
    ).approveViewModelFactory()

    val viewModel = viewModel(
        factory = ApproveViewModel.provideFactory(factory, url)
    ) as ApproveViewModel

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val activity = LocalContext.current.findActivity()

    val appId: SolPayTransactionRequestDetails? by viewModel.appIdConsumer.data.collectAsState()
    val transaction: SolPayTransactionInfo? by viewModel.transactionConsumer.data.collectAsState()
    val signature: TransactionSignature? by viewModel.signatureConsumer.data.collectAsState()
    val error: Throwable? by viewModel.errorConsumer.collectAsState(null)
    val swipeComplete: Boolean by viewModel.swipeComplete.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState(false)

    LaunchedEffect(error) {
        if (error != null) {
            channel.trySend(error!!.message.toString())
        }
    }

    LaunchedEffect(signature) {
        if (signature != null) {
            delay(1000)
                navigateToTokens()
            }
        }


    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            launch {
                viewModel.showBiometricPrompt.collectLatest {
                    val result = activity.showBiometricPrompt(it)
                    if (result == BiometricPromptResult.SUCCESS) {
                        viewModel.onUserAuthenticationConfirmation()
                    }
                }
            }
        }
    }
    AppScreen(
        snackbarHostState = snackbarHostState,
        openDrawer = openDrawer,
        screen = Screen.Approve,
        content = {
            if (isLoading) {
                Loading(isLoading = true)
            } else if (transaction != null) {
                ApproveContent(
                    padding = it,
                    appId = appId,
                    message = transaction!!.message,
                    onSwipe = {
                        viewModel.onUserConfirmation()
                    },
                    swipeComplete = swipeComplete,
                    setSwipeComplete = { value -> viewModel.setSwipeComplete(value) },
                    isComplete = signature != null || error != null,
                    signature = signature
                )
            }

        }
    )

}