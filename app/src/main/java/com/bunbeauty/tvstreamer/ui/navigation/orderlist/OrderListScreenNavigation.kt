package com.bunbeauty.tvstreamer.ui.navigation.orderlist

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.bunbeauty.tvstreamer.ui.screen.OrderListRoute

fun NavController.navigateToOrderListScreen(navOptions: NavOptions) =
    navigate(route = OrderListScreenDestination, navOptions)

fun NavGraphBuilder.orderListScreenRoute(
    navigateToLogin: () -> Unit
) {
    composable<OrderListScreenDestination> {
        OrderListRoute(
            navigateToLogin = navigateToLogin
        )
    }
}
