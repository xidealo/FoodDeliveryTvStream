package com.bunbeauty.tvstreamer.data.repository.user

import com.bunbeauty.tvstreamer.common.ApiResult
import com.bunbeauty.tvstreamer.data.FoodDeliveryApi
import com.bunbeauty.tvstreamer.data.model.user.UserAuthorizationRequest
import com.bunbeauty.tvstreamer.presentation.repo.DataStoreRepo
import com.bunbeauty.tvstreamer.presentation.repo.UserRepo

class LoginException : Exception("Login failed")

class UserRepoImpl(
    private val foodDeliveryApi: FoodDeliveryApi,
    private val dataStoreRepo: DataStoreRepo,
) : UserRepo {

    override suspend fun login(
        username: String,
        password: String,
    ) {
        when (
            val result = foodDeliveryApi.login(
                UserAuthorizationRequest(
                    username = username,
                    password = password
                )
            )
        ) {
            is ApiResult.Success -> {
                with(dataStoreRepo) {
                    saveToken(result.data.token)
                    saveCafeUuid(result.data.cafeUuid)
                    saveCompanyUuid(result.data.companyUuid)
                    saveUsername(username)
                }
            }

            is ApiResult.Error -> throw LoginException()
        }
    }
}