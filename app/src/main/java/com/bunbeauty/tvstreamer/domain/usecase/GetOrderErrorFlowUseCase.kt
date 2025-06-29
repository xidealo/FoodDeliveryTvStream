package com.bunbeauty.tvstreamer.domain.usecase

import com.bunbeauty.tvstreamer.domain.NoCafeException
import com.bunbeauty.tvstreamer.domain.NoTokenException
import com.bunbeauty.tvstreamer.presentation.repo.OrderRepo
import com.bunbeauty.tvstreamer.domain.model.OrderError
import com.bunbeauty.tvstreamer.presentation.repo.DataStoreRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class GetOrderErrorFlowUseCase(
    private val dataStoreRepo: DataStoreRepo,
    private val orderRepo: OrderRepo,
) {

    suspend operator fun invoke(): Flow<OrderError> {
        val token = dataStoreRepo.getToken() ?: throw NoTokenException()
        val cafeUuid = dataStoreRepo.cafeUuid.firstOrNull() ?: throw NoCafeException()

        return orderRepo.getOrderErrorFlow(
            token = token,
            cafeUuid = cafeUuid
        )
    }
}
