package com.bokoup.merchantapp.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
@ExperimentalMaterial3Api
fun SettingsContent(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    padding: PaddingValues,
) {
    val mnemonic by viewModel.mnemonicConsumer.data.collectAsState(null)
    val device by viewModel.deviceConsumer.data.collectAsState(null)
    val addressQrCode by viewModel.addressQrCodeConsumer.data.collectAsState(null)
    val serialQrCode by viewModel.serialQrCodeConsumer.data.collectAsState(null)


    val (mnemonicVisible, setMnemonicVisible) = remember {
        mutableStateOf(false)
    }

    val (shareAddressState, setShareAddressState) = remember {
        mutableStateOf(false)
    }
    val (shareSerialState, setShareSerialState) = remember {
        mutableStateOf(false)
    }

    val (mnemonicDialogState, setMnemonicDialogState) = remember {
        mutableStateOf(false)
    }

    var mnemonicDisplay by remember {
        mutableStateOf("************")
    }

    LaunchedEffect(Unit) {
        viewModel.getMnemonic()
    }

    LaunchedEffect(mnemonic) {
        viewModel.getDevice()
    }

    LaunchedEffect(mnemonic, mnemonicVisible) {
        mnemonicDisplay = if (mnemonicVisible) {
            mnemonic?.joinToString() ?: ""
        } else {
            "************"
        }
    }

    LaunchedEffect(device) {
        if (device != null) {
            viewModel.getQrCode(device!!.name, viewModel.serialQrCodeConsumer)
            viewModel.getQrCode(device!!.owner, viewModel.addressQrCodeConsumer)
        }
    }


    Row(
        modifier = modifier
            .padding(padding)
            .fillMaxSize()
            .padding(horizontal = 32.dp),
    ) {
        Row(modifier = Modifier.weight(0.7f)) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Address:",
                        modifier = Modifier.weight(0.2f),
                    )
                    Text(
                        text = device?.owner ?: "",
                        modifier = Modifier.weight(0.7f),
                    )
                    IconButton(onClick = {setShareAddressState(true)}, modifier = Modifier.weight(0.1f)) {
                        Icon(Icons.Filled.Share, contentDescription = "share address")
                    }
                }
                Row(
                    modifier = Modifier.padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Mnemonic:",
                        modifier = Modifier.weight(0.2f),
                    )
                    Text(
                        text = mnemonicDisplay,
                        modifier = Modifier.weight(0.7f),
                    )
                    IconButton(onClick = {setMnemonicVisible(!mnemonicVisible)}, modifier = Modifier.weight(0.05f)) {
                        if (mnemonicVisible) {
                            Icon(Icons.Filled.VisibilityOff, contentDescription = "hide mnemonic")
                        } else {
                            Icon(Icons.Filled.Visibility, contentDescription = "show mnemonic")
                        }

                    }
                    IconButton(onClick = {setMnemonicDialogState(true)}, modifier = Modifier.weight(0.05f)) {
                        Icon(Icons.Filled.Refresh, contentDescription = "generate new mnemonic")
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Device Serial:",
                        modifier = Modifier.weight(0.2f),
                    )
                    Text(
                        text = device?.name ?: "",
                        modifier = Modifier.weight(0.7f),
                    )
                    IconButton(onClick = {setShareSerialState(true)}, modifier = Modifier.weight(0.1f)) {
                        Icon(Icons.Filled.Share, contentDescription = "share serial")
                    }
                }
            }
        }
        Row(modifier = Modifier.weight(0.3f)) {
            Spacer(modifier = Modifier.fillMaxWidth())
        }
        if (shareAddressState && addressQrCode != null) {
            QRCodeDialog(
                setDialogState = setShareAddressState,
                qrCode = addressQrCode!!,
                title = "Copy Address",
                description = "scan to copy address",
            )
        }
        if (shareSerialState && serialQrCode != null) {
            QRCodeDialog(
                setDialogState = setShareSerialState,
                qrCode = serialQrCode!!,
                title = "Copy Device Serial",
                description = "scan to copy device serial",
            )
        }
        if (mnemonicDialogState) {
            MnemonicDialog(
                setCardState = setMnemonicDialogState,
                generateMnemonic = {viewModel.getMnemonic(true)}
            )
        }
    }

}
