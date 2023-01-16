package com.bokoup.customerapp.ui.tokens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.bokoup.customerapp.R
import com.bokoup.lib.Loading

@Composable
@ExperimentalMaterial3Api
fun TokensContent(
    padding: PaddingValues,
    viewModel: TokensViewModel = hiltViewModel()
) {
    val uriHandler = LocalUriHandler.current

    val tokenAccounts by viewModel.tokenAccounts.collectAsState(emptyList())

    val isLoading by viewModel.isLoading.collectAsState(false)

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Loading(isLoading = true)
        }
    } else if (tokenAccounts.isEmpty()) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Text(
                text = stringResource(R.string.tokens_empty_message),
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (tokenAccount in tokenAccounts) {
                val link =
                    "https://explorer.solana.com/address/" + tokenAccount.mintObject?.id + "?cluster=devnet"
                ElevatedCard(
                    modifier = Modifier
                        .width(284.dp)
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    PaddingValues(
                                        start = 16.dp,
                                        end = 16.dp,
                                        top = 8.dp
                                    )
                                ),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = tokenAccount.mintObject?.promoObject?.metadataObject!!.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            TextButton(onClick = { uriHandler.openUri(link) }) {
                                Text(text = tokenAccount.mintObject.id.slice(0..8) ?: "")
                            }
                        }
                        AsyncImage(
                            model = tokenAccount.mintObject?.promoObject?.metadataObject?.image,
                            modifier = Modifier
                                .padding(6.dp)
                                .width(324.dp),
                            contentDescription = null
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(
                                text = tokenAccount.mintObject?.promoObject?.metadataObject?.description.toString(),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Divider(thickness = 1.dp)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "amount:",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = tokenAccount.amount.toString(),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "delegated amount:",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = tokenAccount.delegatedAmount.toString(),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

            }
        }
    }
}


