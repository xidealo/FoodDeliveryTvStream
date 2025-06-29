package com.bunbeauty.tvstreamer.presentation.orderlist

import com.bunbeauty.tvstreamer.domain.model.Order
import com.bunbeauty.tvstreamer.presentation.base.BaseAction
import com.bunbeauty.tvstreamer.presentation.base.BaseDataState
import com.bunbeauty.tvstreamer.presentation.base.BaseEvent

interface OrderList {
    data class DataState(
        val refreshing: Boolean,
        val orderListState: State,
        val orderList: List<Order>,
        val preparingOrderList: List<Order>,
        val doneOrderList: List<Order>,
        val hasConnectionError: Boolean,
        val loadingOrderList: Boolean
    ) : BaseDataState {
        enum class State {
            LOADING,
            SUCCESS,
            ERROR
        }
    }

    sealed interface Action : BaseAction {
        data object StartObserveOrders : Action
        data object StopObserveOrders : Action
    }

    sealed interface Event : BaseEvent {
        data object ScrollToTop : Event
        data class OpenOrderDetailsEvent(val orderUuid: String, val orderCode: String) : Event
        data class CancelNotification(val notificationId: Int) : Event
    }
}
