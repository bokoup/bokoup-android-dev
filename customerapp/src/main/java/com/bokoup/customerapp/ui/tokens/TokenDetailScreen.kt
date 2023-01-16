package com.bokoup.customerapp.ui.tokens


import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import com.bokoup.customerapp.nav.Screen
import com.bokoup.customerapp.ui.common.AppScreen
import com.dgsd.ksol.core.model.TransactionSignature

@Composable
@ExperimentalMaterial3Api
fun TokenDetailScreen(
    snackbarHostState: SnackbarHostState,
    openDrawer: () -> Unit,
    tokenId: String,
    onTransactionClicked: (TransactionSignature) -> Unit,
) {
    AppScreen(
        snackbarHostState = snackbarHostState,
        openDrawer = openDrawer,
        screen = Screen.TokenDetail,
        content = {
            TokenDetailContent(
                padding = it,
                tokenId = tokenId,
                onTransactionClicked = onTransactionClicked
            )
        }
    )
}