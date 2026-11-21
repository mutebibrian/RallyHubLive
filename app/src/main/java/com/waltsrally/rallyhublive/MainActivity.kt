package com.waltsrally.rallyhublive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.waltsrally.rallyhublive.ui.LoginScreen
import com.waltsrally.rallyhublive.ui.driver.DriverRootScreen
import com.waltsrally.rallyhublive.ui.marshal.MarshalRootScreen
import com.waltsrally.rallyhublive.ui.theme.RallyHubTheme
import com.waltsrally.rallyhublive.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RallyHubTheme {
                val auth: AuthViewModel = viewModel()
                val user by auth.user.collectAsState()

                when {
                    user == null -> LoginScreen(viewModel = auth)
                    user!!.role == "driver" -> DriverRootScreen(user = user!!, auth = auth)
                    else -> MarshalRootScreen(user = user!!, auth = auth)
                }
            }
        }
    }
}
