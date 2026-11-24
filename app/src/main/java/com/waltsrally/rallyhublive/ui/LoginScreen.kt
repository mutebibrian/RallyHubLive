package com.waltsrally.rallyhublive.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.waltsrally.rallyhublive.ui.theme.*
import com.waltsrally.rallyhublive.viewmodel.AuthViewModel

private enum class LoginMode { MARSHAL, DRIVER }

@Composable
fun LoginScreen(viewModel: AuthViewModel) {
    var mode    by remember { mutableStateOf(LoginMode.MARSHAL) }
    var pin     by remember { mutableStateOf("") }
    val loading by viewModel.loading.collectAsState()
    val error   by viewModel.error.collectAsState()

    // Clear PIN and error when switching mode
    LaunchedEffect(mode) {
        pin = ""
        viewModel.clearError()
    }

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
            // ── Logo ───────────────────────────────────────────────────────
            Text("🏁", fontSize = 56.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(10.dp))
            Text(
                text       = "RallyHub Live",
                fontSize   = 28.sp,
                fontWeight = FontWeight.Bold,
                color      = RallyWhite
            )
            Text(
                text     = "KCB NRC 7 • Hoima 2026",
                fontSize = 13.sp,
                color    = RallyGray,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(Modifier.height(36.dp))

            // ── Role selector ──────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(RallyDark)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                LoginMode.entries.forEach { m ->
                    val selected = mode == m
                    val label    = if (m == LoginMode.MARSHAL) "🦺  Marshal" else "🚗  Driver"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(9.dp))
                            .background(if (selected) RallyCard else Color.Transparent)
                            .border(
                                width  = if (selected) 1.dp else 0.dp,
                                color  = if (selected) RallyGold.copy(alpha = 0.5f) else Color.Transparent,
                                shape  = RoundedCornerShape(9.dp)
                            )
                            .clickable { mode = m }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = label,
                            color      = if (selected) RallyGold else RallyGray,
                            fontSize   = 15.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            // ── PIN field ──────────────────────────────────────────────────
            val pinLabel = if (mode == LoginMode.MARSHAL) "Marshal PIN" else "Driver PIN"
            OutlinedTextField(
                value         = pin,
                onValueChange = { pin = it },
                label         = { Text(pinLabel) },
                singleLine    = true,
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
                    cursorColor          = RallyGold,
                    focusedTextColor     = RallyWhite,
                    unfocusedTextColor   = RallyWhite
                ),
                modifier = Modifier.fillMaxWidth()
            )

            if (error != null) {
                Text(
                    text     = error!!,
                    color    = RallyRed,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            // ── Sign In button ─────────────────────────────────────────────
            val btnColor = if (mode == LoginMode.MARSHAL) RallyGold else RallyGreen
            Button(
                onClick  = { viewModel.login(pin) },
                enabled  = pin.isNotBlank() && !loading,
                colors   = ButtonDefaults.buttonColors(
                    containerColor         = btnColor,
                    disabledContainerColor = btnColor.copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        color       = RallyBlack,
                        strokeWidth = 2.dp,
                        modifier    = Modifier.size(20.dp)
                    )
                } else {
                    val btnLabel = if (mode == LoginMode.MARSHAL) "Sign In as Marshal" else "Sign In as Driver"
                    Text(
                        btnLabel,
                        color      = RallyBlack,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text     = if (mode == LoginMode.MARSHAL)
                               "Enter the PIN assigned to your marshal post"
                           else
                               "Enter your driver PIN to view your stage times",
                color    = RallyGray,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
