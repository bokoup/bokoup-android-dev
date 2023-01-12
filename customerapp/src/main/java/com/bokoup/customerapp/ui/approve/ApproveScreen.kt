package com.bokoup.customerapp.ui.approve


import android.util.Log
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.bokoup.customerapp.nav.Screen
import com.bokoup.customerapp.ui.common.AppScreen
import com.dgsd.ksol.core.model.TransactionSignature
import com.dgsd.ksol.solpay.model.SolPayTransactionInfo
import com.dgsd.ksol.solpay.model.SolPayTransactionRequestDetails
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay

@Composable
@ExperimentalMaterialApi
@ExperimentalMaterial3Api
fun ApproveScreen(
    viewModel: ApproveViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    openDrawer: () -> Unit,
    channel: Channel<String>,
    url: String,
    navigateToTokens: () -> Unit
) {

    val appId: SolPayTransactionRequestDetails? by viewModel.appIdConsumer.data.collectAsState()
    val transaction: SolPayTransactionInfo? by viewModel.transactionConsumer.data.collectAsState()
    val signature: TransactionSignature? by viewModel.signatureConsumer.data.collectAsState()
    val error: Throwable? by viewModel.errorConsumer.collectAsState(null)
    val swipeComplete: Boolean by viewModel.swipeComplete.collectAsState()
    val activeWalletAddress by viewModel.activeWalletAddress.collectAsState(initial = null)

    LaunchedEffect(error) {
        if (error != null) {
            channel.trySend(error!!.message.toString())
        }
    }

    LaunchedEffect(key1 = activeWalletAddress) {
        val walletAddress = activeWalletAddress
        if (walletAddress != null) {
            viewModel.getAppId(url)
            viewModel.getTokenTransaction(url, walletAddress)
        }
    }

    LaunchedEffect(signature) {
        if (signature != null) {
            delay(1000)
                navigateToTokens()
            }
        }

    AppScreen(
        snackbarHostState = snackbarHostState,
        openDrawer = openDrawer,
        screen = Screen.Approve,
        content = {
            if (transaction != null) {
                ApproveContent(
                    padding = it,
                    appId = appId,
                    message = transaction!!.message,
                    onSwipe = {
                        // Coming soon: Show biometric prompt, sign and send
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