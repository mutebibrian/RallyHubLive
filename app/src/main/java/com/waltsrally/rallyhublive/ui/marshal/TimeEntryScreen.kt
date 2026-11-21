package com.waltsrally.rallyhublive.ui.marshal

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.waltsrally.rallyhublive.data.models.CarEntry
import com.waltsrally.rallyhublive.ui.theme.*
import com.waltsrally.rallyhublive.viewmodel.MarshalViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun TimeEntryScreen(vm: MarshalViewModel) {
    val sections        by vm.sections.collectAsState()
    val selectedSection by vm.selectedSection.collectAsState()
    val cars            by vm.cars.collectAsState()
    val loading         by vm.loading.collectAsState()
    val flashedCar      by vm.flashedCar.collectAsState()

    var showSectionPicker by remember { mutableStateOf(false) }

    var clock by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        while (true) {
            clock = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            kotlinx.coroutines.delay(1000)
        }
    }

    LaunchedEffect(selectedSection) {
        if (selectedSection != null) vm.loadAllCars()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RallyBlack)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(RallyDark)
                .padding(16.dp)
        ) {
            Column {
                Text(clock, color = RallyGold, fontSize = 30.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(RallyCard)
                        .clickable { showSectionPicker = true }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(selectedSection?.sectionCode ?: "—", color = RallyGold, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text(selectedSection?.sectionName ?: "Select section", color = RallyWhite, fontSize = 15.sp)
                    }
                    Text("▼", color = RallyGray, fontSize = 12.sp)
                }
                Spacer(Modifier.height(8.dp))
                Text("TAP CAR TO RECORD FINISH TIME", color = RallyGray, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
            }
        }

        // Grid
        if (loading && cars.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RallyGold)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement   = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(cars) { car ->
                    FinishCarCell(
                        car       = car,
                        isFlashed = flashedCar == car.carNumber,
                        onTap     = { vm.recordFinishTime(car.carNumber) }
                    )
                }
            }
        }
    }

    if (showSectionPicker) {
        SectionPickerDialog(sections = sections, onSelect = { vm.selectSection(it); showSectionPicker = false }, onDismiss = { showSectionPicker = false })
    }
}

@Composable
fun FinishCarCell(car: CarEntry, isFlashed: Boolean, onTap: () -> Unit) {
    val hasFinished = car.startTime != null  // reuse startTime field — adapt if finish_time field differs
    val bgColor by animateColorAsState(
        targetValue = if (isFlashed) RallyGold else if (hasFinished) Color(0xFF1A1A3A) else RallyCard,
        animationSpec = tween(300), label = "bg"
    )
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(1.dp, if (hasFinished) RallyBlue else RallyBorder, RoundedCornerShape(10.dp))
            .clickable(onClick = onTap),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(car.carNumber, color = RallyWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(
                car.driverName.split(" ").lastOrNull() ?: "",
                color = RallyGray, fontSize = 10.sp, maxLines = 1, textAlign = TextAlign.Center
            )
        }
    }
}
