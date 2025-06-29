package com.bunbeauty.tvstreamer.data

import com.bunbeauty.tvstreamer.common.ApiResult
import com.bunbeauty.tvstreamer.data.model.OrderServer
import com.bunbeauty.tvstreamer.data.model.ServerList
import com.bunbeauty.tvstreamer.data.model.user.UserAuthorizationRequest
import com.bunbeauty.tvstreamer.data.model.user.UserAuthorizationResponse
import com.bunbeauty.tvstreamer.data.model.user.UserResponse
import kotlinx.coroutines.flow.Flow

interface FoodDeliveryApi {

    // LOGIN
    suspend fun login(
        userAuthorizationRequest: UserAuthorizationRequest
    ): ApiResult<UserAuthorizationResponse>
    suspend fun getUser(token: String): ApiResult<UserResponse>

    // ORDER
    suspend fun getOrderListByCafeUuid(
        token: String,
        cafeUuid: String
    ): ApiResult<ServerList<OrderServer>>

    suspend fun getUpdatedOrderFlowByCafeUuid(
        token: String,
        cafeUuid: String
    ): Flow<ApiResult<OrderServer>>

    suspend fun unsubscribeOnOrderList(message: String)
}
