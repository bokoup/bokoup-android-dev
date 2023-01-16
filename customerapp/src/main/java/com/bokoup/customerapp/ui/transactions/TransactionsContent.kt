package com.bokoup.customerapp.ui.transactions

import android.text.format.DateUtils
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CreditScore
import androidx.compose.material.icons.rounded.Redeem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.bokoup.customerapp.R
import com.bokoup.customerapp.dom.model.BokoupTransaction
import com.bokoup.lib.Loading

@Composable
@ExperimentalMaterial3Api
fun TransactionsContent(
    padding: PaddingValues, viewModel: TransactionsViewModel = hiltViewModel()
) {

    val transactions by viewModel.transactions.collectAsState(emptyList())

    val isLoading by viewModel.isLoading.collectAsState(false)

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Loading(isLoading = true)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items = transactions) { transaction ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(
                        horizontal = 16.dp, vertical = 4.dp
                    ),
                ) {
                    Icon(
                        imageVector = when (transaction.type) {
                            BokoupTransaction.Type.RECEIVED -> Icons.Rounded.Redeem
                            BokoupTransaction.Type.REDEEMED -> Icons.Rounded.CreditScore
                        },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                    )

                    Spacer(Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f, fill = true)
                    ) {
                        Text(
                            text = when (transaction.type) {
                                BokoupTransaction.Type.RECEIVED -> stringResource(
                                    R.string.transaction_received_description_template,
                                    transaction.tokenInfo.name
                                )
                                BokoupTransaction.Type.REDEEMED -> stringResource(
                                    R.string.transaction_redeemed_description_template,
                                    transaction.tokenInfo.name
                                )
                            },
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = DateUtils.formatDateTime(
                                LocalContext.current,
                                transaction.timestamp.toEpochSecond() * 1000,
                                DateUtils.FORMAT_ABBREV_ALL or
                                        DateUtils.FORMAT_SHOW_WEEKDAY or
                                        DateUtils.FORMAT_ABBREV_TIME or
                                        DateUtils.FORMAT_SHOW_DATE
                            ),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = transaction.merchantName,
                            maxLines = 1,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    AsyncImage(
                        model = transaction.tokenInfo.imageUrl,
                        placeholder = painterResource(id = R.drawable.ic_bokoup_logo),
                        contentDescription = null,
                    )
                }
            }
        }
    }
}



