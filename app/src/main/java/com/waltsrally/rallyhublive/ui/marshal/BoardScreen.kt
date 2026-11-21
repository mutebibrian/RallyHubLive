package com.waltsrally.rallyhublive.ui.marshal

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.waltsrally.rallyhublive.data.models.StageResult
import com.waltsrally.rallyhublive.ui.theme.*
import com.waltsrally.rallyhublive.viewmodel.MarshalViewModel

@Composable
fun BoardScreen(vm: MarshalViewModel) {
    val results  by vm.results.collectAsState()
    val sections by vm.sections.collectAsState()
    val selected by vm.selectedSection.collectAsState()
    var showSectionPicker by remember { mutableStateOf(false) }

    LaunchedEffect(selected) { vm.loadResults() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RallyBlack)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(RallyDark)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Results Board", color = RallyWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(selected?.sectionName ?: "—", color = RallyGold, fontSize = 13.sp)
            }
            IconButton(onClick = { vm.loadResults() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = RallyGold)
            }
        }

        // Column headers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(RallyCard)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text("POS", color = RallyGray, fontSize = 11.sp, modifier = Modifier.width(36.dp))
            Text("CAR", color = RallyGray, fontSize = 11.sp, modifier = Modifier.width(44.dp))
            Text("DRIVER", color = RallyGray, fontSize = 11.sp, modifier = Modifier.weight(1f))
            Text("TIME", color = RallyGray, fontSize = 11.sp, modifier = Modifier.width(70.dp))
        }

        HorizontalDivider(color = RallyBorder)

        if (results.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No results yet", color = RallyGray)
            }
        } else {
            LazyColumn {
                itemsIndexed(results) { idx, result ->
                    ResultRow(pos = idx + 1, result = result)
                    HorizontalDivider(color = RallyBorder, thickness = 0.5.dp)
                }
            }
        }
    }

    if (showSectionPicker) {
        SectionPickerDialog(sections = sections, onSelect = { vm.selectSection(it); showSectionPicker = false }, onDismiss = { showSectionPicker = false })
    }
}

@Composable
fun ResultRow(pos: Int, result: StageResult) {
    val posColor = when (pos) {
        1    -> Color(0xFFFFD700)
        2    -> Color(0xFFC0C0C0)
        3    -> Color(0xFFCD7F32)
        else -> RallyGray
    }
    val elapsed = result.elapsedMs?.let { formatElapsed(it) } ?: "—"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text  = "$pos",
            color = posColor,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            modifier = Modifier.width(36.dp)
        )
        Text(
            text  = result.carNumber,
            color = RallyWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.width(44.dp)
        )
        Text(
            text     = result.driverName,
            color    = RallyWhite,
            fontSize = 13.sp,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )
        Text(
            text  = elapsed,
            color = RallyGold,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.width(70.dp)
        )
    }
}

private fun formatElapsed(ms: Double): String {
    val totalSeconds = (ms / 1000).toLong()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val millis  = ((ms % 1000) / 10).toInt()
    return "%d:%02d.%02d".format(minutes, seconds, millis)
}
