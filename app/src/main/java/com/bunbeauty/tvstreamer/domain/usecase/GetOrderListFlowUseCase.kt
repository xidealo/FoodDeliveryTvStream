package com.bunbeauty.tvstreamer.domain.usecase

import com.bunbeauty.tvstreamer.domain.NoCafeException
import com.bunbeauty.tvstreamer.domain.NoTokenException
import com.bunbeauty.tvstreamer.presentation.repo.OrderRepo
import com.bunbeauty.tvstreamer.domain.model.Order
import com.bunbeauty.tvstreamer.domain.model.OrderStatus
import com.bunbeauty.tvstreamer.presentation.repo.DataStoreRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class GetOrderListFlowUseCase(
    private val dataStoreRepo: DataStoreRepo,
    private val orderRepo: OrderRepo,
) {

    suspend operator fun invoke(): Flow<List<Order>> {
        val token = dataStoreRepo.getToken() ?: throw NoTokenException()
        val cafeUuid = dataStoreRepo.cafeUuid.firstOrNull() ?: throw NoCafeException()

        return orderRepo.getOrderListFlow(
            token = token,
            cafeUuid = cafeUuid
        ).map { orderList ->
            orderList.filter { order ->
                order.orderStatus != OrderStatus.CANCELED
            }
        }
    }
}
