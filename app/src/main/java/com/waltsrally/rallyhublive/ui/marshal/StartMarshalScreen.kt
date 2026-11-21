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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.waltsrally.rallyhublive.data.models.CarEntry
import com.waltsrally.rallyhublive.data.models.RhlSection
import com.waltsrally.rallyhublive.ui.theme.*
import com.waltsrally.rallyhublive.viewmodel.MarshalViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun StartMarshalScreen(vm: MarshalViewModel) {
    val sections        by vm.sections.collectAsState()
    val selectedSection by vm.selectedSection.collectAsState()
    val cars            by vm.cars.collectAsState()
    val loading         by vm.loading.collectAsState()
    val flashedCar      by vm.flashedCar.collectAsState()

    var showSectionPicker by remember { mutableStateOf(false) }
    var alreadyStartedCar by remember { mutableStateOf<String?>(null) }

    // Live clock
    var clock by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        while (true) {
            clock = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            kotlinx.coroutines.delay(1000)
        }
    }

    // Load cars when section changes
    LaunchedEffect(selectedSection) {
        if (selectedSection != null) vm.loadAllCars()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RallyBlack)
    ) {
        // ── Header ─────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(RallyDark)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column {
                Text(
                    text  = clock,
                    color = RallyGold,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
                Spacer(Modifier.height(8.dp))
                // Section selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(RallyCard)
                        .clickable { showSectionPicker = true }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text  = selectedSection?.sectionCode ?: "—",
                            color = RallyGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text  = selectedSection?.sectionName ?: "Select section",
                            color = RallyWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Text("▼", color = RallyGray, fontSize = 12.sp)
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text  = "TAP CAR WHEN IT LEAVES THE RAMP",
                    color = RallyGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
            }
        }

        // ── Car grid ───────────────────────────────────────────────────────────
        if (loading && cars.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RallyGold)
            }
        } else if (cars.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No cars loaded", color = RallyGray)
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
                    StartCarCell(
                        car        = car,
                        isFlashed  = flashedCar == car.carNumber,
                        onTap      = {
                            if (car.startTime != null) {
                                alreadyStartedCar = car.carNumber
                            } else {
                                vm.recordActualStart(car.carNumber)
                            }
                        }
                    )
                }
            }
        }
    }

    // ── Section picker dialog ─────────────────────────────────────────────────
    if (showSectionPicker) {
        AlertDialog(
            onDismissRequest = { showSectionPicker = false },
            containerColor   = RallyDark,
            title = { Text("Select Section", color = RallyWhite) },
            text = {
                Column {
                    sections.forEach { section ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    vm.selectSection(section)
                                    showSectionPicker = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text  = section.sectionCode,
                                color = RallyGold,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(56.dp)
                            )
                            Text(
                                text  = section.sectionName,
                                color = RallyWhite,
                                fontSize = 14.sp
                            )
                        }
                        HorizontalDivider(color = RallyBorder, thickness = 0.5.dp)
                    }
                }
            },
            confirmButton = {}
        )
    }

    // ── Already-started toast ─────────────────────────────────────────────────
    if (alreadyStartedCar != null) {
        val car = cars.find { it.carNumber == alreadyStartedCar }
        AlertDialog(
            onDismissRequest = { alreadyStartedCar = null },
            containerColor   = RallyDark,
            title = { Text("Already Started", color = RallyWhite) },
            text  = {
                Text(
                    "Car #${car?.carNumber} already started at ${car?.startTime?.substring(11, 19) ?: "—"}",
                    color = RallyGray
                )
            },
            confirmButton = {
                TextButton(onClick = { alreadyStartedCar = null }) {
                    Text("OK", color = RallyGold)
                }
            }
        )
    }
}

@Composable
fun StartCarCell(
    car:       CarEntry,
    isFlashed: Boolean,
    onTap:     () -> Unit
) {
    val hasStarted  = car.startTime != null
    val bgColor by animateColorAsState(
        targetValue = when {
            isFlashed  -> RallyGreen
            hasStarted -> Color(0xFF1A3A1A)
            else       -> RallyCard
        },
        animationSpec = tween(300),
        label = "cellBg"
    )
    val borderColor = when {
        isFlashed  -> RallyGreen
        hasStarted -> Color(0xFF2D6A2D)
        else       -> RallyBorder
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable(onClick = onTap),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text  = car.carNumber,
                color = if (hasStarted) RallyGreen else RallyWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            if (hasStarted) {
                Text(
                    text  = car.startTime!!.substring(11, 19),
                    color = RallyGreen,
                    fontSize = 10.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text  = car.driverName.split(" ").lastOrNull() ?: "",
                    color = RallyGray,
                    fontSize = 10.sp,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
