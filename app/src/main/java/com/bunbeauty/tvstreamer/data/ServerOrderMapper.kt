package com.bunbeauty.tvstreamer.data

import com.bunbeauty.tvstreamer.data.model.OrderServer
import com.bunbeauty.tvstreamer.domain.model.Order
import com.bunbeauty.tvstreamer.domain.model.OrderStatus

class ServerOrderMapper {

    fun mapOrder(orderServer: OrderServer): Order {
        return Order(
            uuid = orderServer.uuid,
            code = orderServer.code,
            time = orderServer.time,
            deferredTime = orderServer.deferredTime,
            timeZone = orderServer.timeZone,
            orderStatus = getOrderStatus(orderServer.status)
        )
    }

    private fun getOrderStatus(statusName: String): OrderStatus {
        return getOrderStatusNullable(statusName) ?: OrderStatus.NOT_ACCEPTED
    }

    private fun getOrderStatusNullable(statusName: String): OrderStatus? {
        val hasStatus = OrderStatus.entries.any { orderStatus ->
            orderStatus.name == statusName
        }
        return if (hasStatus) {
            OrderStatus.valueOf(statusName)
        } else {
            null
        }
    }
}
