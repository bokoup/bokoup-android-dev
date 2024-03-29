package com.bokoup.customerapp.nav

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.bokoup.customerapp.ui.approve.ApproveScreen
import com.bokoup.customerapp.ui.scan.ScanScreen
import com.bokoup.customerapp.ui.share.ShareScreen
import com.bokoup.customerapp.ui.tokens.TokenDetailScreen
import com.bokoup.customerapp.ui.tokens.TokensScreen
import com.bokoup.customerapp.ui.trades.TradesScreen
import com.bokoup.customerapp.ui.transaction.TransactionScreen
import com.bokoup.customerapp.ui.transactions.TransactionsScreen
import com.bokoup.customerapp.ui.wallet.WalletScreen
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow

@Composable
@ExperimentalMaterial3Api
@ExperimentalMaterialApi
fun NavGraph(navController: NavHostController, openDrawer: () -> Unit) {
    val channel = Channel<String>(Channel.CONFLATED)
    val snackbarHostState by remember { mutableStateOf(SnackbarHostState()) }

    LaunchedEffect(channel) {
        channel.consumeAsFlow().collect { message ->
            val result = snackbarHostState.showSnackbar(
                message = message, withDismissAction = true, duration = SnackbarDuration.Indefinite
            )
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    /* action has been performed */
                }
                SnackbarResult.Dismissed -> {
                }
            }
        }
    }

    NavHost(
        navController = navController, startDestination = Screen.Tokens.name
    ) {
        composable(
            route = Screen.Tokens.name
        ) {
            TokensScreen(
                openDrawer = openDrawer,
                snackbarHostState = snackbarHostState,
                onTokenClicked = {
                    navController.navigate("${Screen.TokenDetail.name}/$it")
                }
            )
        }
        composable(
            route = Screen.Wallet.name
        ) {
            WalletScreen(
                openDrawer = openDrawer, snackbarHostState = snackbarHostState, channel = channel
            )
        }
        composable(
            route = Screen.Trades.name
        ) {
            TradesScreen(
                openDrawer = openDrawer,
                snackbarHostState = snackbarHostState,
                onTradeListingClicked = {
                    navController.navigate("${Screen.TokenDetail.name}/${it.tokenInfo.address.toBase58String()}")
                }
            )
        }
        composable(
            route = Screen.Transactions.name
        ) {
            TransactionsScreen(
                openDrawer = openDrawer,
                snackbarHostState = snackbarHostState,
                onTransactionClicked = {
                    navController.navigate(
                        "${Screen.Transaction.name}?signature=${it}"
                    )
                }
            )
        }
        composable(
            route = "${Screen.Transaction.name}?signature={signature}"
        ) { backStackEntry ->
            val transactionSignature = backStackEntry.arguments?.getString("signature").orEmpty()
            TransactionScreen(
                openDrawer = openDrawer,
                snackbarHostState = snackbarHostState,
                transactionSignature = transactionSignature,
                openTokenDetails = {
                    navController.navigate(
                        "${Screen.TokenDetail}/${it.toBase58String()}"
                    )
                }
            )
        }
        composable(
            route = Screen.Share.name
        ) {
            ShareScreen(
                openDrawer = openDrawer,
                snackbarHostState = snackbarHostState,
            )
        }
        composable(
            route = Screen.Scan.name
        ) {
            ScanScreen(openDrawer = openDrawer,
                    snackbarHostState = snackbarHostState,
                    navigateToApprove = { url ->
                        navController.navigate(
                            "${Screen.Approve.name}?url=${url}"
                        )
                    })
        }
        composable(
            route = "${Screen.Approve.name}?url={url}",
            arguments = listOf(
                navArgument("url") { type = NavType.StringType; nullable = false },
            ),
        ) {
            ApproveScreen(
                openDrawer = openDrawer,
                snackbarHostState = snackbarHostState,
                channel = channel,
                url = checkNotNull(it.arguments?.getString("url")),
                navigateToTokens = { navController.navigate(Screen.Tokens.name) }
            )
        }
        composable(
            route = "${Screen.TokenDetail}/{tokenId}",
            arguments = listOf(navArgument("tokenId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val tokenId = checkNotNull(backStackEntry.arguments?.getString("tokenId"))
            TokenDetailScreen(
                openDrawer = openDrawer,
                snackbarHostState = snackbarHostState,
                tokenId = tokenId,
                onTransactionClicked = {
                    navController.navigate(
                        "${Screen.Transaction.name}?signature=${it}"
                    )
                }
            )
        }
    }
}