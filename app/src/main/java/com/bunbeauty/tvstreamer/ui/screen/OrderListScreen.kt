package com.bunbeauty.tvstreamer.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.bunbeauty.tvstreamer.presentation.OrderListViewModel
import com.bunbeauty.tvstreamer.presentation.orderlist.OrderList
import com.bunbeauty.tvstreamer.ui.theme.AdminTheme
import com.bunbeauty.tvstreamer.ui.theme.black
import com.bunbeauty.tvstreamer.ui.theme.bold
import org.koin.androidx.compose.koinViewModel
import kotlin.collections.forEach

@Composable
fun OrderListRoute(
    navigateToLogin: () -> Unit,
    viewModel: OrderListViewModel = koinViewModel(),
) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()
    val onAction = remember {
        { event: OrderList.Action ->
            viewModel.onAction(event)
        }
    }

    val effects by viewModel.events.collectAsStateWithLifecycle()
    val consumeEffects = remember {
        {
            viewModel.consumeEvents(effects)
        }
    }

    LifecycleStartEffect(Unit) {
        onAction(OrderList.Action.StartObserveOrders)
        onStopOrDispose {
            onAction(OrderList.Action.StopObserveOrders)
        }
    }
    OrderListScreen(state = viewState, onAction = onAction)

    OrderListEffect(
        effects = effects,
        navigateToLogin = navigateToLogin,
        consumeEffects = consumeEffects
    )
}

@Composable
fun OrderListScreen(
    state: OrderList.DataState,
    onAction: (OrderList.Action) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        shape = RectangleShape
    ) {
        Column {
            if (state.hasConnectionError) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = AdminTheme.colors.main.primary
                )
            }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 16.dp, bottom = 8.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .background(AdminTheme.colors.order.accepted)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Готовится",
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .padding(vertical = 4.dp),
                            style = AdminTheme.typography.bodyLarge.bold,
                            color = AdminTheme.colors.order.onOrder
                        )
                    }

                    FlowColumn(
                        maxLines = 2,
                        horizontalArrangement = spacedBy(8.dp),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        state.preparingOrderList.forEach { order ->
                            Text(
                                modifier = Modifier
                                    .width(50.dp)
                                    .padding(
                                        top = 8.dp
                                    ),
                                text = order.code,
                                style = AdminTheme.typography.bodyLarge.bold
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier =
                            Modifier
                                .background(AdminTheme.colors.order.done)
                                .fillMaxWidth()
                    ) {
                        Text(
                            text = "Готовы",
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .padding(vertical = 4.dp),
                            style = AdminTheme.typography.bodyLarge.bold,
                            color = AdminTheme.colors.order.onOrder
                        )
                    }
                    Column(
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        state.doneOrderList.forEach { order ->
                            Text(
                                modifier = Modifier
                                    .padding(
                                        top = 16.dp
                                    )
                                    .width(70.dp),
                                text = order.code,
                                style = AdminTheme.typography.titleLarge.black,
                                color = AdminTheme.colors.order.done
                            )
                        }
                    }
                }
            }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clickable(
                            onClick = {
                                onAction(OrderList.Action.LogoutClick)
                            }
                        ),
                    text = "Добро пожаловать! Заказы из мобильного приложения \uD83D\uDCF1",
                    style = AdminTheme.typography.titleLarge.black,
                    color = AdminTheme.colors.main.onSurface,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun OrderListEffect(
    effects: List<OrderList.Event>,
    navigateToLogin: () -> Unit,
    consumeEffects: () -> Unit,
) {
    LaunchedEffect(effects) {
        effects.forEach { effect ->
            when (effect) {
                OrderList.Event.Back -> navigateToLogin()
            }
        }
        consumeEffects()
    }
}
