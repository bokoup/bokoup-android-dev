package com.bokoup.customerapp.ui.wallet

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.bokoup.customerapp.R
import com.bokoup.customerapp.biometrics.BiometricPromptResult
import com.bokoup.customerapp.biometrics.showBiometricPrompt
import com.bokoup.customerapp.dom.model.Address
import com.bokoup.customerapp.nav.Screen
import com.bokoup.customerapp.ui.common.AppScreen
import com.bokoup.customerapp.util.findActivity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
@ExperimentalMaterial3Api
fun WalletScreen(
    viewModel: WalletViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    openDrawer: () -> Unit,
    channel: Channel<String>
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val activity = LocalContext.current.findActivity()

    val addresses: List<Address>? by viewModel.addressesConsumer.data.collectAsState()
    val isLoadingAddreses: Boolean by viewModel.addressesConsumer.isLoading.collectAsState()
    val isLoadingInsert: Boolean by viewModel.insertAddressConsumer.isLoading.collectAsState()
    val error: Throwable? by viewModel.errorConsumer.collectAsState(null)

    LaunchedEffect(viewModel.addressesConsumer) {
        if (error != null) {
            channel.trySend(error!!.message.toString())
        }
    }

    LaunchedEffect(isLoadingInsert) {
        viewModel.getAddresses()
    }

    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            launch {
                viewModel.showBiometricPrompt.collectLatest {
                    val result = activity.showBiometricPrompt(it)
                    if (result == BiometricPromptResult.SUCCESS) {
                        viewModel.onUserAuthenticationConfirmed()
                    }
                }
            }
        }
    }

    AppScreen(
        snackbarHostState = snackbarHostState,
        openDrawer = openDrawer,
        screen = Screen.Wallet,
        content = {
            WalletContent(
                padding = it,
                addresses = addresses,
                updateActive = { id -> viewModel.updateActive(id) },
                isLoading = isLoadingAddreses || isLoadingInsert,
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(
                    stringResource(R.string.create_wallet),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                ) },
                icon = {
                    Icon(
                        Icons.Filled.Add,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentDescription = stringResource(R.string.create_wallet)
                    )
                },
                onClick = { viewModel.onCreateNewWalletClicked() }
            )
        },
    )
}
