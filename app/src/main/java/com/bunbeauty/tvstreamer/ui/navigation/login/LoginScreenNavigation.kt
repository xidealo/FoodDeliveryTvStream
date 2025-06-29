package com.bunbeauty.tvstreamer.ui.navigation.login

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.bunbeauty.tvstreamer.ui.screen.LoginRoute

fun NavGraphBuilder.loginScreenRoute(
    navigateToOrderList: () -> Unit
) {
    composable<LoginScreenDestination> {
        LoginRoute(
            navigateToOrderList = navigateToOrderList
        )
    }
}
