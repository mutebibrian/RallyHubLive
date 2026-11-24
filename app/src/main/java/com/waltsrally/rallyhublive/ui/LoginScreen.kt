package com.waltsrally.rallyhublive.ui

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
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

// Light theme colors to match iOS
private val LightBg       = Color(0xFFF2F2F7)
private val CardBg        = Color.White
private val LabelGray     = Color(0xFF6C6C70)
private val FieldBg       = Color(0xFFF2F2F7)
private val TabSelected   = Color.White
private val TabUnselected = Color(0xFFE5E5EA)
private val TextDark      = Color(0xFF1C1C1E)
private val SubtitleGray  = Color(0xFF8E8E93)

@Composable
fun LoginScreen(viewModel: AuthViewModel) {
    var mode     by remember { mutableStateOf(LoginMode.MARSHAL) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loading  by viewModel.loading.collectAsState()
    val error    by viewModel.error.collectAsState()
    val focusMgr = LocalFocusManager.current

    LaunchedEffect(mode) {
        username = ""
        password = ""
        viewModel.clearError()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(72.dp))

            // ── Flag icon ─────────────────────────────────────────────────
            Text("🏁", fontSize = 52.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(12.dp))

            // ── RALLYHUB title ────────────────────────────────────────────
            Text(
                text          = "RALLYHUB",
                fontSize      = 32.sp,
                fontWeight    = FontWeight.ExtraBold,
                color         = RallyGold,
                letterSpacing = 3.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text     = "Marshal & Driver Live Portal",
                fontSize = 14.sp,
                color    = SubtitleGray
            )

            Spacer(Modifier.height(32.dp))

            // ── Tab switcher ──────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(TabUnselected)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                LoginMode.entries.forEach { m ->
                    val selected = mode == m
                    val label    = if (m == LoginMode.MARSHAL) "🎯  Marshal" else "🏎  Driver"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .then(
                                if (selected) Modifier.shadow(2.dp, RoundedCornerShape(10.dp))
                                else Modifier
                            )
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selected) TabSelected else Color.Transparent)
                            .clickable { mode = m }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = label,
                            color      = if (selected) TextDark else SubtitleGray,
                            fontSize   = 15.sp,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Login card ────────────────────────────────────────────────
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp),
                color    = CardBg,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Text(
                        text       = if (mode == LoginMode.MARSHAL) "Marshal Login" else "Driver Login",
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color      = RallyGold
                    )

                    Spacer(Modifier.height(16.dp))

                    // USERNAME
                    Text(
                        text          = "USERNAME",
                        fontSize      = 11.sp,
                        fontWeight    = FontWeight.SemiBold,
                        color         = LabelGray,
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value         = username,
                        onValueChange = { username = it },
                        singleLine    = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction    = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusMgr.moveFocus(FocusDirection.Down) }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = RallyGold,
                            unfocusedBorderColor = Color(0xFFD1D1D6),
                            focusedTextColor     = TextDark,
                            unfocusedTextColor   = TextDark,
                            focusedContainerColor   = FieldBg,
                            unfocusedContainerColor = FieldBg
                        ),
                        shape    = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(14.dp))

                    // PASSWORD
                    Text(
                        text          = "PASSWORD",
                        fontSize      = 11.sp,
                        fontWeight    = FontWeight.SemiBold,
                        color         = LabelGray,
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value         = password,
                        onValueChange = { password = it },
                        singleLine    = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction    = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusMgr.clearFocus()
                                if (username.isNotBlank() && password.isNotBlank() && !loading)
                                    viewModel.login(username, password)
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = RallyGold,
                            unfocusedBorderColor = Color(0xFFD1D1D6),
                            focusedTextColor     = TextDark,
                            unfocusedTextColor   = TextDark,
                            focusedContainerColor   = FieldBg,
                            unfocusedContainerColor = FieldBg
                        ),
                        shape    = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (error != null) {
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text     = error!!,
                            color    = RallyRed,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    // ── Login button ──────────────────────────────────────
                    Button(
                        onClick  = { viewModel.login(username, password) },
                        enabled  = username.isNotBlank() && password.isNotBlank() && !loading,
                        colors   = ButtonDefaults.buttonColors(
                            containerColor         = RallyGold,
                            disabledContainerColor = RallyGold.copy(alpha = 0.4f)
                        ),
                        shape    = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        if (loading) {
                            CircularProgressIndicator(
                                color       = Color.White,
                                strokeWidth = 2.dp,
                                modifier    = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                text       = if (mode == LoginMode.MARSHAL) "Login as Marshal" else "Login as Driver",
                                color      = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize   = 16.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Text(
                        text      = "Default admin: admin / Rally2026!",
                        color     = SubtitleGray,
                        fontSize  = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier  = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
