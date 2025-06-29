package com.bunbeauty.tvstreamer.domain.usecase

import com.bunbeauty.tvstreamer.domain.model.Order
import com.bunbeauty.tvstreamer.domain.model.OrderStatus

class GetPreparingOrderListUseCase {
    operator fun invoke(orderList: List<Order>): List<Order> {
        return orderList
            .filter { order -> order.orderStatus == OrderStatus.PREPARING }
            .take(20)
            .reversed()
    }
}