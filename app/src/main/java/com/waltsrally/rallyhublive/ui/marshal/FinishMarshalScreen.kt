package com.waltsrally.rallyhublive.ui.marshal

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.waltsrally.rallyhublive.data.models.CarEntry
import com.waltsrally.rallyhublive.ui.theme.*
import com.waltsrally.rallyhublive.viewmodel.MarshalViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun FinishMarshalScreen(vm: MarshalViewModel) {
    val sections        by vm.sections.collectAsState()
    val selectedSection by vm.selectedSection.collectAsState()
    val startedCars     by vm.startedCars.collectAsState()
    val loading         by vm.loading.collectAsState()
    val pollCountdown   by vm.pollCountdown.collectAsState()

    val inTransitCars = startedCars.filter { !it.hasFinished }
    val finishedCars  = startedCars.filter { it.hasFinished }

    var showSectionPicker by remember { mutableStateOf(false) }
    var finishedExpanded  by remember { mutableStateOf(false) }
    var confirmDNF        by remember { mutableStateOf<String?>(null) }

    // Live clock
    var clock by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        while (true) {
            clock = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            kotlinx.coroutines.delay(1000)
        }
    }

    // Start/stop polling when this screen is active
    LaunchedEffect(selectedSection) {
        if (selectedSection != null) {
            vm.loadAllCars()
            vm.startFinishPolling()
        }
    }
    DisposableEffect(Unit) {
        onDispose { vm.stopFinishPolling() }
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text  = clock,
                        color = RallyGold,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    // Poll countdown badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(RallyCard)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text  = "↻ ${pollCountdown}s",
                            color = if (pollCountdown <= 3) RallyGreen else RallyGray,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
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
                            fontSize = 15.sp
                        )
                    }
                    Text("▼", color = RallyGray, fontSize = 12.sp)
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text  = "IN TRANSIT: ${inTransitCars.size}   FINISHED: ${finishedCars.size}",
                    color = RallyGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
            }
        }

        // ── Body ───────────────────────────────────────────────────────────────
        if (loading && startedCars.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RallyGold)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {

                // ── IN TRANSIT section ─────────────────────────────────────────
                if (inTransitCars.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(RallyCard)
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text  = if (startedCars.isEmpty()) "No cars started yet" else "All cars finished ✓",
                                color = RallyGray,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    item {
                        Text(
                            text  = "IN TRANSIT",
                            color = RallyGray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                    items(inTransitCars, key = { it.carNumber }) { car ->
                        InTransitCard(
                            car     = car,
                            onFinish = { vm.recordFinishTime(car.carNumber) },
                            onDNF    = { confirmDNF = car.carNumber }
                        )
                    }
                }

                // ── FINISHED section ───────────────────────────────────────────
                if (finishedCars.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(RallyCard)
                                .clickable { finishedExpanded = !finishedExpanded }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(RallyBlue)
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    text  = "FINISHED — ${finishedCars.size} car${if (finishedCars.size != 1) "s" else ""}",
                                    color = RallyWhite,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Icon(
                                imageVector = if (finishedExpanded) Icons.Default.KeyboardArrowUp
                                              else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = RallyGray
                            )
                        }
                    }

                    if (finishedExpanded) {
                        items(finishedCars, key = { "fin_${it.carNumber}" }) { car ->
                            FinishedCarRow(car = car)
                        }
                    }
                }
            }
        }
    }

    // ── Section picker dialog ─────────────────────────────────────────────────
    if (showSectionPicker) {
        SectionPickerDialog(
            sections  = sections,
            onSelect  = { vm.selectSection(it); showSectionPicker = false },
            onDismiss = { showSectionPicker = false }
        )
    }

    // ── DNF confirmation dialog ───────────────────────────────────────────────
    confirmDNF?.let { carNum ->
        AlertDialog(
            onDismissRequest = { confirmDNF = null },
            containerColor   = RallyDark,
            title = {
                Text("Mark Car #$carNum DNF?", color = RallyWhite)
            },
            text = {
                Text(
                    "This will record the car as Did Not Finish (DNF) for this stage.",
                    color = RallyGray
                )
            },
            confirmButton = {
                TextButton(onClick = { vm.recordDNF(carNum); confirmDNF = null }) {
                    Text("CONFIRM DNF", color = RallyRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDNF = null }) {
                    Text("Cancel", color = RallyGray)
                }
            }
        )
    }
}

// ── In Transit card ───────────────────────────────────────────────────────────

@Composable
fun InTransitCard(
    car:     CarEntry,
    onFinish: () -> Unit,
    onDNF:   () -> Unit
) {
    // Pulsing dot animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue  = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotScale"
    )

    val startDisplay = car.startTime?.substring(11, 19) ?: "—"
    val lastName = car.driverName.split(" ").lastOrNull() ?: car.driverName

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0D2B0D))
            .border(1.dp, Color(0xFF1E5C1E), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Pulsing green dot
        Box(
            modifier = Modifier
                .size(12.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(RallyGreen)
        )

        Spacer(Modifier.width(12.dp))

        // Car info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text  = "Car #${car.carNumber}",
                color = RallyGreen,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text  = lastName,
                color = RallyWhite,
                fontSize = 13.sp
            )
            Text(
                text  = "Started $startDisplay",
                color = RallyGray,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        // Action buttons
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = onFinish,
                colors  = ButtonDefaults.buttonColors(containerColor = RallyGreen),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("FINISH", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            OutlinedButton(
                onClick = onDNF,
                border  = androidx.compose.foundation.BorderStroke(1.dp, RallyRed),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("DNF", color = RallyRed, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

// ── Finished car row ──────────────────────────────────────────────────────────

@Composable
fun FinishedCarRow(car: CarEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0D0D2B))
            .border(1.dp, Color(0xFF1E1E5C), RoundedCornerShape(10.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text  = "#${car.carNumber}",
                color = RallyBlue,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(44.dp)
            )
            Text(
                text  = car.driverName,
                color = RallyWhite,
                fontSize = 13.sp
            )
        }
        Text(
            text  = "✓ Timed",
            color = RallyBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
