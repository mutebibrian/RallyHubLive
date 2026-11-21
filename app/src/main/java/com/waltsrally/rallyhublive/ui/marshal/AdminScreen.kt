package com.waltsrally.rallyhublive.ui.marshal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.waltsrally.rallyhublive.data.models.CarEntry
import com.waltsrally.rallyhublive.ui.theme.*
import com.waltsrally.rallyhublive.viewmodel.MarshalViewModel

@Composable
fun AdminScreen(vm: MarshalViewModel) {
    val cars     by vm.cars.collectAsState()
    val selected by vm.selectedSection.collectAsState()
    val sections by vm.sections.collectAsState()
    val toast    by vm.toastMessage.collectAsState()
    var showSectionPicker by remember { mutableStateOf(false) }
    var editCar by remember { mutableStateOf<CarEntry?>(null) }

    LaunchedEffect(selected) { vm.loadAllCars() }

    LaunchedEffect(toast) {
        if (toast != null) {
            kotlinx.coroutines.delay(3000)
            vm.clearToast()
        }
    }

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
                Text("Admin", color = RallyWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Set / override start times", color = RallyGray, fontSize = 12.sp)
            }
            TextButton(onClick = { showSectionPicker = true }) {
                Text(selected?.sectionCode ?: "Section ▼", color = RallyGold)
            }
        }

        // Toast
        if (toast != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RallyGreen)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(toast!!, color = RallyBlack, fontWeight = FontWeight.SemiBold)
            }
        }

        LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(cars) { car ->
                AdminCarRow(car = car, onEdit = { editCar = car })
            }
        }
    }

    if (showSectionPicker) {
        SectionPickerDialog(sections = sections, onSelect = { vm.selectSection(it); showSectionPicker = false }, onDismiss = { showSectionPicker = false })
    }

    editCar?.let { car ->
        SetStartTimeDialog(
            car      = car,
            onSet    = { time -> vm.setAdminStartTime(car.carNumber, time); editCar = null },
            onDismiss = { editCar = null }
        )
    }
}

@Composable
fun AdminCarRow(car: CarEntry, onEdit: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(RallyCard)
            .clickable(onClick = onEdit)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text  = car.carNumber,
            color = RallyGold,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.width(44.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(car.driverName, color = RallyWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(car.carClass, color = RallyGray, fontSize = 12.sp)
        }
        if (car.startTime != null) {
            Text(
                text  = car.startTime.substring(11, 19),
                color = RallyGreen,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace
            )
        } else {
            Text("Set time", color = RallyBlue, fontSize = 13.sp)
        }
    }
}

@Composable
fun SetStartTimeDialog(car: CarEntry, onSet: (String) -> Unit, onDismiss: () -> Unit) {
    var timeText by remember { mutableStateOf(car.startTime?.substring(11, 19) ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = RallyDark,
        title = { Text("Set Start Time — Car #${car.carNumber}", color = RallyWhite) },
        text  = {
            Column {
                Text(car.driverName, color = RallyGray, fontSize = 13.sp)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = timeText,
                    onValueChange = { timeText = it },
                    label = { Text("HH:MM:SS") },
                    placeholder = { Text("08:30:00") },
                    singleLine  = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = RallyGold,
                        unfocusedBorderColor = RallyBorder,
                        focusedLabelColor    = RallyGold
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (timeText.isNotBlank()) onSet(timeText) },
                enabled = timeText.isNotBlank()
            ) {
                Text("Set", color = RallyGold, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = RallyGray)
            }
        }
    )
}
