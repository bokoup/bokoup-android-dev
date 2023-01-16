package com.bokoup.customerapp.ui.transaction


import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import com.bokoup.customerapp.nav.Screen
import com.bokoup.customerapp.ui.common.AppScreen
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.ksol.core.model.TransactionSignature

@Composable
@ExperimentalMaterial3Api
fun TransactionScreen(
    snackbarHostState: SnackbarHostState,
    openDrawer: () -> Unit,
    transactionSignature: TransactionSignature,
    openTokenDetails: (PublicKey) -> Unit,
) {
    AppScreen(
        snackbarHostState = snackbarHostState,
        openDrawer = openDrawer,
        screen = Screen.Transaction,
        content = {
            TransactionContent(
                padding = it,
                transactionSignature = transactionSignature,
                openTokenDetails = openTokenDetails,
            )
        }
    )

}