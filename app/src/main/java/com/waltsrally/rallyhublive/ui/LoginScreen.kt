package com.waltsrally.rallyhublive.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.waltsrally.rallyhublive.ui.theme.*
import com.waltsrally.rallyhublive.viewmodel.AuthViewModel

@Composable
fun LoginScreen(viewModel: AuthViewModel) {
    var pin by remember { mutableStateOf("") }
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RallyBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            // Logo / Title
            Text(
                text = "🏁",
                fontSize = 56.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "RallyHub Live",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = RallyWhite
            )
            Text(
                text = "KCB NRC 7 • Hoima 2026",
                fontSize = 13.sp,
                color = RallyGray,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(Modifier.height(40.dp))

            // PIN field
            OutlinedTextField(
                value = pin,
                onValueChange = { pin = it },
                label = { Text("Marshal / Driver PIN") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction    = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { if (pin.isNotBlank() && !loading) viewModel.login(pin) }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = RallyGold,
                    unfocusedBorderColor = RallyBorder,
                    focusedLabelColor    = RallyGold,
                    cursorColor          = RallyGold
                ),
                modifier = Modifier.fillMaxWidth()
            )

            if (error != null) {
                Text(
                    text = error!!,
                    color = RallyRed,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { viewModel.login(pin) },
                enabled = pin.isNotBlank() && !loading,
                colors  = ButtonDefaults.buttonColors(containerColor = RallyGold),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        color  = RallyBlack,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        "Sign In",
                        color      = RallyBlack,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp
                    )
                }
            }
        }
    }
}
