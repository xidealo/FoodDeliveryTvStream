package com.bunbeauty.tvstreamer.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navOptions
import com.bunbeauty.tvstreamer.ui.navigation.login.loginScreenRoute
import com.bunbeauty.tvstreamer.ui.navigation.orderlist.navigateToOrderListScreen
import com.bunbeauty.tvstreamer.ui.navigation.orderlist.orderListScreenRoute

internal val emptyNavOptions = navOptions { }

fun NavGraphBuilder.tvNavGraphBuilder(
    navController: NavController
) {
    loginScreenRoute(
        navigateToOrderList = {
            navController.navigateToOrderListScreen(
                emptyNavOptions
            )
        }
    )
    orderListScreenRoute()
}
