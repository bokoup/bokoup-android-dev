package com.bokoup.customerapp.ui.onboarding

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import com.bokoup.customerapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinInputField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit
) {
    OutlinedTextField(
        value = value,
        singleLine = true,
        maxLines = 1,
        textStyle = MaterialTheme.typography.headlineLarge.copy(
            textAlign = TextAlign.Center,
            lineHeight = TextUnit.Unspecified,
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done,
        ),
        visualTransformation = PasswordVisualTransformation(),
        label = {
            Text(
                text = stringResource(R.string.onboarding_enter_pin_hint),
            )
        },
        onValueChange = onValueChange
    )
}