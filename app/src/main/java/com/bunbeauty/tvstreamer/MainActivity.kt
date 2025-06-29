package com.bunbeauty.tvstreamer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.bunbeauty.tvstreamer.ui.navigation.login.LoginScreenDestination
import com.bunbeauty.tvstreamer.ui.navigation.tvNavGraphBuilder
import com.bunbeauty.tvstreamer.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = LoginScreenDestination
                ) {
                    tvNavGraphBuilder(
                        navController = navController
                    )
                }
            }
        }
    }
}
