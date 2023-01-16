package com.bokoup.customerapp.ui.tokens

import android.app.Activity
import android.text.format.DateUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CreditScore
import androidx.compose.material.icons.rounded.Redeem
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.bokoup.customerapp.R
import com.bokoup.customerapp.dom.model.BokoupTransaction
import com.bokoup.customerapp.ui.transaction.TransactionItem
import com.bokoup.lib.Loading
import com.dgsd.ksol.core.model.TransactionSignature
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ActivityComponent


@Composable
@ExperimentalMaterial3Api
fun TokenDetailTransaction(
    transaction: BokoupTransaction,
    onTransactionClicked: (TransactionSignature) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(
                horizontal = 16.dp, vertical = 4.dp
            )
            .clickable {
                onTransactionClicked.invoke(transaction.signature)
            },
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
                        R.string.token_details_received_description,
                    )
                    BokoupTransaction.Type.REDEEMED -> stringResource(
                        R.string.token_details_redeemed_description,
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
                            DateUtils.FORMAT_SHOW_DATE or
                            DateUtils.FORMAT_SHOW_TIME
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


