package com.bokoup.customerapp.ui.trades

import android.icu.text.NumberFormat
import android.text.format.DateUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CreditScore
import androidx.compose.material.icons.rounded.Redeem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bokoup.customerapp.R
import com.bokoup.customerapp.dom.model.BokoupTradeListing
import com.bokoup.customerapp.dom.model.BokoupTransaction
import com.dgsd.ksol.core.model.TransactionSignature

@Composable
fun TradeListingItem(
    tradeListing: BokoupTradeListing,
    onClick: (BokoupTradeListing) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(
                horizontal = 16.dp, vertical = 4.dp
            )
            .clickable {
                onClick.invoke(tradeListing)
            },
    ) {
        AsyncImage(
            model = tradeListing.tokenInfo.imageUrl,
            modifier = Modifier.size(56.dp),
            placeholder = painterResource(id = R.drawable.ic_bokoup_logo),
            contentDescription = null,
        )

        Column(
            modifier = Modifier
                .weight(1f, fill = true)
                .padding(horizontal = 16.dp),
        ) {
            Text(
                text = tradeListing.tokenInfo.name,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )


            if (!tradeListing.tokenInfo.description.isNullOrEmpty()) {
                Spacer(Modifier.height(4.dp))

                Text(
                    text = tradeListing.tokenInfo.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Text(
            text = NumberFormat.getCurrencyInstance().format(tradeListing.price),
            maxLines = 1,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}