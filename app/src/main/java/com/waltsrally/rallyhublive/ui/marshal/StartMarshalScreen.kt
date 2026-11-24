package com.waltsrally.rallyhublive.ui.marshal

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.waltsrally.rallyhublive.data.models.CarEntry
import com.waltsrally.rallyhublive.data.models.RhlSection
import com.waltsrally.rallyhublive.ui.theme.*
import com.waltsrally.rallyhublive.viewmodel.MarshalViewModel
import java.time.LocalTime
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
    var focusCar          by remember { mutableStateOf<CarEntry?>(null) }

    // Header clock (1-second resolution is fine for the list view)
    var clock by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        while (true) {
            clock = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
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
        // ── Header ─────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(RallyDark)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column {
                Text(
                    text       = clock,
                    color      = RallyGold,
                    fontSize   = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(Modifier.height(8.dp))
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
                            text       = selectedSection?.sectionCode ?: "—",
                            color      = RallyGold,
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text       = selectedSection?.sectionName ?: "Select section",
                            color      = RallyWhite,
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Text("▼", color = RallyGray, fontSize = 12.sp)
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text          = "TAP A CAR TO OPEN START SCREEN",
                    color         = RallyGray,
                    fontSize      = 11.sp,
                    fontWeight    = FontWeight.SemiBold,
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
                columns             = GridCells.Fixed(3),
                contentPadding      = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier            = Modifier.fillMaxSize()
            ) {
                items(cars) { car ->
                    StartCarCell(
                        car       = car,
                        isFlashed = flashedCar == car.carNumber,
                        onTap     = {
                            if (car.startTime != null) {
                                alreadyStartedCar = car.carNumber
                            } else {
                                focusCar = car          // open focus overlay
                            }
                        }
                    )
                }
            }
        }
    }

    // ── Full-screen focus overlay ─────────────────────────────────────────────
    focusCar?.let { car ->
        StartFocusOverlay(
            car      = car,
            vm       = vm,
            onDismiss = { focusCar = null }
        )
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
                                text       = section.sectionCode,
                                color      = RallyGold,
                                fontSize   = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier   = Modifier.width(56.dp)
                            )
                            Text(
                                text     = section.sectionName,
                                color    = RallyWhite,
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
                    "Car #${car?.carNumber} already started at " +
                    "${car?.startTime?.substring(11, 19) ?: "—"}",
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

// ─────────────────────────────────────────────────────────────────────────────
// Full-screen start focus overlay
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun StartFocusOverlay(
    car:      CarEntry,
    vm:       MarshalViewModel,
    onDismiss: () -> Unit
) {
    // Live clock at 10 ms resolution
    var now by remember { mutableStateOf(LocalTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = LocalTime.now()
            kotlinx.coroutines.delay(10)
        }
    }

    val liveClock = remember(now) {
        val nano = now.nano
        val us   = nano / 1_000
        "%02d:%02d:%02d.%06d".format(now.hour, now.minute, now.second, us)
    }

    var tapped by remember { mutableStateOf(false) }
    val isBusy by vm.loading.collectAsState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress      = true,
            dismissOnClickOutside   = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ── Top bar ──────────────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = RallyGold
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Text(
                        text          = "START LINE",
                        color         = Color.White.copy(alpha = 0.4f),
                        fontSize      = 11.sp,
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Spacer(Modifier.weight(1f))
                    Spacer(Modifier.width(48.dp))   // balance the icon button
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                Spacer(Modifier.weight(1f))

                // ── Live clock ───────────────────────────────────────────────
                Text(
                    text          = "LIVE CLOCK",
                    color         = RallyGold.copy(alpha = 0.7f),
                    fontSize      = 11.sp,
                    fontWeight    = FontWeight.Bold,
                    letterSpacing = 3.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text       = liveClock,
                    color      = RallyGold,
                    fontSize   = 38.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    maxLines   = 1
                )

                Spacer(Modifier.height(32.dp))

                // ── Car badge ────────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .background(RallyGold.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                        .border(1.dp, RallyGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 20.dp, vertical = 7.dp)
                ) {
                    Text(
                        text       = "CAR #${car.carNumber}",
                        color      = RallyGold,
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(Modifier.height(14.dp))

                // ── Driver name ──────────────────────────────────────────────
                Text(
                    text       = car.driverName.ifBlank { "Car ${car.carNumber}" },
                    color      = Color.White,
                    fontSize   = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign  = TextAlign.Center,
                    maxLines   = 2,
                    modifier   = Modifier.padding(horizontal = 24.dp)
                )

                // ── Co-driver ────────────────────────────────────────────────
                if (car.coDriverName.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text          = "CO-DRIVER",
                            color         = Color.White.copy(alpha = 0.35f),
                            fontSize      = 11.sp,
                            fontWeight    = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text       = car.coDriverName,
                            color      = Color.White.copy(alpha = 0.75f),
                            fontSize   = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // ── Car class ────────────────────────────────────────────────
                if (car.carClass.isNotBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text       = car.carClass,
                        color      = RallyGray,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.weight(1f))

                // ── START button ─────────────────────────────────────────────
                Button(
                    onClick = {
                        if (!tapped) {
                            tapped = true
                            vm.recordActualStart(car.carNumber)
                            onDismiss()
                        }
                    },
                    enabled  = !isBusy && !tapped,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp)
                        .height(88.dp),
                    shape  = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor         = if (tapped) RallyGreen.copy(alpha = 0.5f) else RallyGreen,
                        disabledContainerColor = RallyGreen.copy(alpha = 0.4f)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Text(
                        text       = "🏁  START",
                        fontSize   = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = Color.White
                    )
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    text      = "Tap START the instant the car's front wheel crosses the ramp line",
                    color     = Color.White.copy(alpha = 0.35f),
                    fontSize  = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier  = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Car cell (grid tile)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun StartCarCell(
    car:       CarEntry,
    isFlashed: Boolean,
    onTap:     () -> Unit
) {
    val hasStarted = car.startTime != null
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
                text       = car.carNumber,
                color      = if (hasStarted) RallyGreen else RallyWhite,
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold
            )
            if (hasStarted) {
                Text(
                    text       = car.startTime!!.substring(11, 19),
                    color      = RallyGreen,
                    fontSize   = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    textAlign  = TextAlign.Center
                )
            } else {
                // chevron hint — marshal knows a tap opens the focus screen
                Text(
                    text      = "›",
                    color     = RallyGreen,
                    fontSize  = 20.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text      = car.driverName.split(" ").lastOrNull() ?: "",
                    color     = RallyGray,
                    fontSize  = 10.sp,
                    maxLines  = 1,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
