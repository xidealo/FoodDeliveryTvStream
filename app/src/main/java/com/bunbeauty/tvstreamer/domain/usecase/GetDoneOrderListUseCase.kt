package com.bunbeauty.tvstreamer.domain.usecase

import com.bunbeauty.tvstreamer.domain.model.Order
import com.bunbeauty.tvstreamer.domain.model.OrderStatus

class GetDoneOrderListUseCase {
    operator fun invoke(orderList: List<Order>): List<Order> {
        return orderList
            .filter { order -> order.orderStatus == OrderStatus.DONE }
            .take(10)
            .reversed()
    }
}