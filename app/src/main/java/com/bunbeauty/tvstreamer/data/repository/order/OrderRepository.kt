package com.bunbeauty.tvstreamer.data.repository.order

import android.util.Log
import com.bunbeauty.tvstreamer.common.ApiResult
import com.bunbeauty.tvstreamer.common.Constants.ORDER_TAG
import com.bunbeauty.tvstreamer.data.FoodDeliveryApi
import com.bunbeauty.tvstreamer.data.ServerOrderMapper
import com.bunbeauty.tvstreamer.data.model.OrderServer
import com.bunbeauty.tvstreamer.domain.model.Order
import com.bunbeauty.tvstreamer.domain.model.OrderError
import com.bunbeauty.tvstreamer.presentation.repo.OrderRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onCompletion

class ServerConnectionException : Exception()

class OrderRepository(
    private val foodDeliveryApi: FoodDeliveryApi,
    private val serverOrderMapper: ServerOrderMapper
) : OrderRepo {

    private var cachedOrderList: List<Order>? = null

    override suspend fun getOrderListFlow(token: String, cafeUuid: String): Flow<List<Order>> {
        val orderList = getOrderListByCafeUuid(
            token = token,
            cafeUuid = cafeUuid
        )
        cachedOrderList = orderList
        val updatedOrderListFlow = foodDeliveryApi.getUpdatedOrderFlowByCafeUuid(token, cafeUuid)
            .filterIsInstance<ApiResult.Success<OrderServer>>()
            .map { successApiResult ->
                val order = serverOrderMapper.mapOrder(successApiResult.data)
                val updatedOrderList = updateOrderList(
                    orderList = cachedOrderList.orEmpty(),
                    newOrder = order
                )
                cachedOrderList = updatedOrderList
                updatedOrderList
            }.onCompletion {
                foodDeliveryApi.unsubscribeOnOrderList("onCompletion")
            }

        return merge(
            flowOf(orderList),
            updatedOrderListFlow
        )
    }

    override suspend fun getOrderErrorFlow(token: String, cafeUuid: String): Flow<OrderError> {
        return foodDeliveryApi.getUpdatedOrderFlowByCafeUuid(token, cafeUuid)
            .filterIsInstance<ApiResult.Error<OrderServer>>()
            .map { errorApiResult ->
                OrderError(errorApiResult.apiError.message)
            }
    }

    private suspend fun getOrderListByCafeUuid(token: String, cafeUuid: String): List<Order> {
        return when (val result = foodDeliveryApi.getOrderListByCafeUuid(token, cafeUuid)) {
            is ApiResult.Success -> {
                result.data
                    .results
                    .map(serverOrderMapper::mapOrder)
            }

            is ApiResult.Error -> {
                Log.e(
                    ORDER_TAG,
                    "getOrderListByCafeUuid ${result.apiError.message} ${result.apiError.code}"
                )
                throw ServerConnectionException()
            }
        }
    }

    override fun clearCache() {
        cachedOrderList = null
    }

    private fun updateOrderList(orderList: List<Order>, newOrder: Order): List<Order> {
        val isExisted = orderList.any { order ->
            order.uuid == newOrder.uuid
        }
        return if (isExisted) {
            orderList.map { order ->
                if (order.uuid == newOrder.uuid) {
                    newOrder
                } else {
                    order
                }
            }
        } else {
            buildList {
                add(newOrder)
                addAll(orderList)
            }
        }
    }
}
