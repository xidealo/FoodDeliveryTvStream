package com.bunbeauty.tvstreamer.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.bunbeauty.tvstreamer.common.extension.launchSafe
import com.bunbeauty.tvstreamer.domain.usecase.GetDoneOrderListUseCase
import com.bunbeauty.tvstreamer.domain.usecase.GetOrderErrorFlowUseCase
import com.bunbeauty.tvstreamer.domain.usecase.GetOrderListFlowUseCase
import com.bunbeauty.tvstreamer.domain.usecase.GetPreparingOrderListUseCase
import com.bunbeauty.tvstreamer.domain.usecase.LogoutUseCase
import com.bunbeauty.tvstreamer.presentation.base.BaseStateViewModel
import com.bunbeauty.tvstreamer.presentation.orderlist.OrderList
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class OrderListViewModel(
    private val getOrderListFlow: GetOrderListFlowUseCase,
    private val getOrderErrorFlow: GetOrderErrorFlowUseCase,
    private val getPreparingOrderListUseCase: GetPreparingOrderListUseCase,
    private val getDoneOrderListUseCase: GetDoneOrderListUseCase,
    private val logoutUseCase: LogoutUseCase
) : BaseStateViewModel<OrderList.DataState, OrderList.Action, OrderList.Event>(
    initState = OrderList.DataState(
        refreshing = false,
        hasConnectionError = false,
        preparingOrderList = emptyList(),
        doneOrderList = emptyList(),
        orderListState = OrderList.DataState.State.LOADING,
        loadingOrderList = false,
        orderList = emptyList()
    )
) {

    private var orderListJob: Job? = null
    private var orderErrorJob: Job? = null

    override fun reduce(action: OrderList.Action, dataState: OrderList.DataState) {
        when (action) {
            OrderList.Action.StartObserveOrders -> {
                stopObservingOrderList()
                observeOrderList()
            }

            OrderList.Action.StopObserveOrders -> stopObservingOrderList()
            OrderList.Action.LogoutClick -> logout()
        }
    }

    private fun observeOrderList() {
        setState {
            copy(
                hasConnectionError = false,
                loadingOrderList = true
            )
        }

        if (orderListJob != null) return

        orderListJob = viewModelScope.launchSafe(
            onError = {
                setState {
                    copy(
                        refreshing = false,
                        hasConnectionError = true,
                        loadingOrderList = false,
                        orderListState = OrderList.DataState.State.ERROR
                    )
                }
            },
            block = {
                setState {
                    copy(
                        hasConnectionError = false,
                        orderListState = OrderList.DataState.State.SUCCESS
                    )
                }

                getOrderListFlow().collect { orderList ->
                    setState {
                        copy(
                            doneOrderList = getDoneOrderListUseCase(orderList),
                            preparingOrderList = getPreparingOrderListUseCase(orderList),
                            orderList = orderList,
                            refreshing = false,
                            loadingOrderList = false,
                            orderListState = OrderList.DataState.State.SUCCESS
                        )
                    }
                }
            }
        )

        orderErrorJob = viewModelScope.launchSafe(
            block = {
                getOrderErrorFlow().collect {
                    setState {
                        copy(
                            hasConnectionError = true
                        )
                    }
                    onAction(OrderList.Action.StopObserveOrders)
                    delay(5000)
                    onAction(OrderList.Action.StartObserveOrders)
                }
            },
            onError = { error ->
                Log.d("MyTag", "getOrderErrorFlow: ${error.stackTrace}")
            }
        )
    }

    private fun logout() {
        viewModelScope.launchSafe(
            block = {
                logoutUseCase()
                sendEvent {
                    OrderList.Event.Back
                }
            },
            onError = { error ->
                Log.d("MyTag", "getOrderErrorFlow: ${error.stackTrace}")
            }
        )
    }

    private fun stopObservingOrderList() {
        orderListJob?.cancel()
        orderErrorJob?.cancel()

        orderListJob = null
        orderErrorJob = null
    }
}
