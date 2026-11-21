package com.waltsrally.rallyhublive.ui.marshal

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.waltsrally.rallyhublive.data.models.UserInfo
import com.waltsrally.rallyhublive.ui.theme.*
import com.waltsrally.rallyhublive.viewmodel.AuthViewModel
import com.waltsrally.rallyhublive.viewmodel.MarshalViewModel

private data class NavItem(val label: String, val icon: ImageVector)

private val MARSHAL_TABS = listOf(
    NavItem("Start",    Icons.Default.Star),
    NavItem("Finish",   Icons.Default.PlayArrow),
    NavItem("Sections", Icons.Default.Place),
    NavItem("Board",    Icons.Default.List),
    NavItem("Admin",    Icons.Default.Settings)
)

@Composable
fun MarshalRootScreen(user: UserInfo, auth: AuthViewModel) {
    val vm = remember { MarshalViewModel() }
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(user.token) { vm.init(user.token) }

    Scaffold(
        containerColor = RallyBlack,
        topBar = {
            MarshalTopBar(userName = user.name, onLogout = { auth.logout() })
        },
        bottomBar = {
            NavigationBar(containerColor = RallyDark) {
                MARSHAL_TABS.forEachIndexed { idx, item ->
                    NavigationBarItem(
                        selected = selectedTab == idx,
                        onClick  = { selectedTab = idx },
                        icon     = { Icon(item.icon, contentDescription = item.label) },
                        label    = { Text(item.label, fontSize = 10.sp) },
                        colors   = NavigationBarItemDefaults.colors(
                            selectedIconColor   = RallyGold,
                            selectedTextColor   = RallyGold,
                            unselectedIconColor = RallyGray,
                            unselectedTextColor = RallyGray,
                            indicatorColor      = RallyBlack
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> StartMarshalScreen(vm = vm)
                1 -> FinishMarshalScreen(vm = vm)
                2 -> SectionsStatusScreen(vm = vm)
                3 -> BoardScreen(vm = vm)
                4 -> AdminScreen(vm = vm)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarshalTopBar(userName: String, onLogout: () -> Unit) {
    TopAppBar(
        title = {
            Column {
                Text("RallyHub Live", color = RallyWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Marshal: $userName", color = RallyGold, fontSize = 11.sp)
            }
        },
        actions = {
            TextButton(onClick = onLogout) {
                Text("Logout", color = RallyRed, fontSize = 13.sp)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = RallyDark)
    )
}
