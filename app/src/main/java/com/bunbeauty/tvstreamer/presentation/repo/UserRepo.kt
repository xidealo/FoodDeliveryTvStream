package com.bunbeauty.tvstreamer.presentation.repo

import com.bunbeauty.tvstreamer.common.ApiResult
import com.bunbeauty.tvstreamer.data.model.user.UserAuthorizationResponse

interface UserRepo {
    suspend fun login(
        username: String,
        password: String,
    )
}