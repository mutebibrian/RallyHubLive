package com.waltsrally.rallyhublive.ui.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.waltsrally.rallyhublive.data.api.RetrofitClient
import com.waltsrally.rallyhublive.data.models.StageResult
import com.waltsrally.rallyhublive.data.models.UserInfo
import com.waltsrally.rallyhublive.ui.theme.*
import com.waltsrally.rallyhublive.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverRootScreen(user: UserInfo, auth: AuthViewModel) {
    var results  by remember { mutableStateOf<List<StageResult>>(emptyList()) }
    var loading  by remember { mutableStateOf(false) }
    var error    by remember { mutableStateOf<String?>(null) }
    val scope    = rememberCoroutineScope()

    fun load() {
        scope.launch {
            loading = true; error = null
            try {
                val resp = RetrofitClient.api.driverResults()
                val list = resp.results ?: emptyList()
                if (list.isNotEmpty()) results = list else error = "No results yet"
            } catch (e: Exception) {
                error = e.message
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(Unit) { load() }

    Scaffold(
        containerColor = RallyBlack,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("RallyHub Live", color = RallyWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Car #${user.car ?: ""} · ${user.name}", color = RallyGold, fontSize = 11.sp)
                    }
                },
                actions = {
                    IconButton(onClick = ::load) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = RallyGold)
                    }
                    TextButton(onClick = { auth.logout() }) {
                        Text("Logout", color = RallyRed, fontSize = 13.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RallyDark)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = RallyGold)
                }
                error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(error!!, color = RallyGray)
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = ::load,
                            colors  = ButtonDefaults.buttonColors(containerColor = RallyGold)
                        ) { Text("Retry", color = RallyBlack) }
                    }
                }
                results.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No results yet — finish your first stage!", color = RallyGray, fontSize = 14.sp)
                }
                else -> LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    itemsIndexed(results) { idx, result ->
                        DriverResultCard(pos = idx + 1, result = result)
                    }
                }
            }
        }
    }
}

@Composable
fun DriverResultCard(pos: Int, result: StageResult) {
    // server sends elapsed_seconds as a string; fall back to elapsed_ms if present
    val elapsed = result.elapsedSeconds?.toDoubleOrNull()?.let { formatElapsedSeconds(it) }
        ?: result.elapsedMs?.let { formatElapsedSeconds(it / 1000.0) }
        ?: "—"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(RallyCard)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text  = "$pos",
            color = RallyGold,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.width(32.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            if (!result.sectionCode.isNullOrEmpty()) {
                Text(result.sectionCode, color = RallyGold, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Text(result.sectionName ?: "Stage", color = RallyWhite, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            if (result.startTime != null && result.startTime.length >= 19) {
                Text("Start: ${result.startTime.substring(11, 19)}", color = RallyGray, fontSize = 12.sp)
            }
            if (result.marshalName != null) {
                Text("Marshal: ${result.marshalName}", color = RallyGray, fontSize = 11.sp)
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text  = elapsed,
                color = RallyGold,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                fontFamily = FontFamily.Monospace
            )
            if (result.isDnf == 1) {
                Text("DNF", color = RallyRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun formatElapsedSeconds(totalSec: Double): String {
    val minutes = (totalSec / 60).toLong()
    val seconds = (totalSec % 60).toLong()
    val hundredths = ((totalSec % 1) * 100).toInt()
    return "%d:%02d.%02d".format(minutes, seconds, hundredths)
}
