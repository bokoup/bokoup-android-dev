package com.bokoup.customerapp.ui.transactions


import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import com.bokoup.customerapp.nav.Screen
import com.bokoup.customerapp.ui.common.AppScreen

@Composable
@ExperimentalMaterial3Api
fun TransactionsScreen(
    snackbarHostState: SnackbarHostState,
    openDrawer: () -> Unit
) {
    AppScreen(
        snackbarHostState = snackbarHostState,
        openDrawer = openDrawer,
        screen = Screen.Transactions,
        content = { TransactionsContent(padding = it) }
    )

}