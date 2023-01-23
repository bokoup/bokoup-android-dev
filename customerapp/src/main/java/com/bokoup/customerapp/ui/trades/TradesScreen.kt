package com.bokoup.customerapp.ui.trades


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.bokoup.customerapp.R
import com.bokoup.customerapp.dom.model.BokoupTradeListing
import com.bokoup.customerapp.nav.Screen
import com.bokoup.customerapp.ui.common.AppScreen

@Composable
@ExperimentalMaterial3Api
fun TradesScreen(
    snackbarHostState: SnackbarHostState,
    openDrawer: () -> Unit,
    onTradeListingClicked: (BokoupTradeListing) -> Unit,
) {
    AppScreen(
        snackbarHostState = snackbarHostState,
        openDrawer = openDrawer,
        screen = Screen.Trades,
        content = { TradesContent(padding = it, onTradeListingClicked) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = {
                    Text(
                        stringResource(R.string.sell_token),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                icon = {
                    Icon(
                        Icons.Filled.Sell,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentDescription = stringResource(R.string.sell_token)
                    )
                },
                onClick = {
                    // TODO: Implement token sell flow
                }
            )
        }
    )

}