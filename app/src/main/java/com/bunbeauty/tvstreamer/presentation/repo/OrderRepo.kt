package com.bunbeauty.tvstreamer.presentation.repo

import com.bunbeauty.tvstreamer.domain.model.Order
import com.bunbeauty.tvstreamer.domain.model.OrderError
import kotlinx.coroutines.flow.Flow

interface OrderRepo {
    suspend fun getOrderListFlow(token: String, cafeUuid: String): Flow<List<Order>>
    suspend fun getOrderErrorFlow(token: String, cafeUuid: String): Flow<OrderError>
    fun clearCache()
}
