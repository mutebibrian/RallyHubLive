package com.waltsrally.rallyhublive.ui.marshal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.waltsrally.rallyhublive.data.models.RhlSection
import com.waltsrally.rallyhublive.ui.theme.*
import com.waltsrally.rallyhublive.viewmodel.MarshalViewModel

@Composable
fun SectionsStatusScreen(vm: MarshalViewModel) {
    val sections by vm.sections.collectAsState()
    val selected by vm.selectedSection.collectAsState()

    LaunchedEffect(Unit) { vm.loadSections() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RallyBlack)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(RallyDark)
                .padding(16.dp)
        ) {
            Column {
                Text("Sections", color = RallyWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("KCB NRC 7 • Hoima 2026", color = RallyGray, fontSize = 13.sp)
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sections) { section ->
                SectionCard(
                    section  = section,
                    isActive = selected?.id == section.id,
                    onTap    = { vm.selectSection(section) }
                )
            }
        }
    }
}

@Composable
fun SectionCard(section: RhlSection, isActive: Boolean, onTap: () -> Unit) {
    val statusColor = when (section.status) {
        "running"  -> RallyGold
        "complete" -> RallyGreen
        else       -> RallyGray
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isActive) Color(0xFF1E1A00) else RallyCard)
            .border(1.dp, if (isActive) RallyGold else RallyBorder, RoundedCornerShape(10.dp))
            .clickable(onClick = onTap)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Status dot
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(statusColor)
        )
        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(section.sectionName, color = RallyWhite, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Text(
                "${section.sectionCode}  •  ${section.distanceKm} km  •  Leg ${section.leg}",
                color = RallyGray, fontSize = 12.sp
            )
        }

        Text(
            text  = section.status.replaceFirstChar { it.uppercase() },
            color = statusColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun SectionPickerDialog(
    sections: List<RhlSection>,
    onSelect: (RhlSection) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = RallyDark,
        title = { Text("Select Section", color = RallyWhite) },
        text = {
            Column {
                sections.forEach { section ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(section) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(section.sectionCode, color = RallyGold, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(60.dp))
                        Text(section.sectionName, color = RallyWhite, fontSize = 14.sp)
                    }
                    HorizontalDivider(color = RallyBorder, thickness = 0.5.dp)
                }
            }
        },
        confirmButton = {}
    )
}
