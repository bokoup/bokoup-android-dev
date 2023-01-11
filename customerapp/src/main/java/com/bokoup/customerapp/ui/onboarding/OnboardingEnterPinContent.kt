package com.bokoup.customerapp.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bokoup.customerapp.R

private const val MIN_PIN_CODE_LENGTH = 4

@ExperimentalMaterial3Api
@Composable
fun OnboardingEnterPinContent(
    onNavigateBack: () -> Unit,
    onConfirmPinClicked: (String) -> Unit
) {

    var textInput by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.onboarding_enter_pin_title),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.size(24.dp))

        Text(
            text = stringResource(R.string.onboarding_enter_pin_message),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.tertiary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(modifier = Modifier.size(48.dp))

        Box(modifier = Modifier.padding(horizontal = 32.dp)) {
            PinInputField(
                value = textInput,
                onValueChange = { newValue ->
                    textInput = newValue
                }
            )
        }

        Spacer(modifier = Modifier.size(24.dp))

        Button(
            onClick = { onConfirmPinClicked.invoke(textInput.text) },
            enabled = textInput.text.length >= MIN_PIN_CODE_LENGTH
        ) {
            Text(text = stringResource(R.string.next))
        }
    }
}
