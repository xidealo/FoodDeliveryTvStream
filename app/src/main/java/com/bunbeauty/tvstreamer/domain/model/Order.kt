package com.bunbeauty.tvstreamer.domain.model

data class Order(
    val uuid: String,
    val code: String,
    val time: Long,
    val deferredTime: Long?,
    val timeZone: String,
    val orderStatus: OrderStatus
)
