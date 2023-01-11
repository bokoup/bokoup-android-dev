package com.bokoup.customerapp.ui.onboarding

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinInputField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    labelText: String,
    isError: Boolean = false,
) {
    OutlinedTextField(
        value = value,
        isError = isError,
        singleLine = true,
        maxLines = 1,
        textStyle = MaterialTheme.typography.headlineLarge.copy(
            textAlign = TextAlign.Center,
            lineHeight = TextUnit.Unspecified,
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done,
        ),
        visualTransformation = PasswordVisualTransformation(),
        label = {
            Text(text = labelText)
        },
        onValueChange = onValueChange
    )
}