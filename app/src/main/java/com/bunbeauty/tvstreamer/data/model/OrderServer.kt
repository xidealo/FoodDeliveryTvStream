package com.bunbeauty.tvstreamer.data.model
import kotlinx.serialization.Serializable

@Serializable
class OrderServer(
    val uuid: String,
    val code: String,
    val status: String,
    val time: Long,
    val timeZone: String,
    val deferredTime: Long?
)
