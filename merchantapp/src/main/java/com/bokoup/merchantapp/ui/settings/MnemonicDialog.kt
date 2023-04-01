package com.bokoup.merchantapp.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
@ExperimentalMaterial3Api
fun MnemonicDialog(
    setCardState: (Boolean) -> Unit,
    generateMnemonic: () -> Unit,
) {
    AlertDialog(
        icon = { Icon(Icons.Filled.Refresh, "generate new mnemonic") },
        onDismissRequest = {
            setCardState(false)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    generateMnemonic()
                    setCardState(false)
                }
            ) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    // close the dialog
                    setCardState(false)
                }
            ) {
                Text(text = "Dismiss")
            }
        },
        title = {
            Text(text = "Generate new mnemonic and address")
        },
        text = {
            Text(text = "This will replace your existing address with a new one and cannot be undone.")
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        shape = RoundedCornerShape(5.dp),
    )
}
