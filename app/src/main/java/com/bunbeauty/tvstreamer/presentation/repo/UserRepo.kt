package com.bunbeauty.tvstreamer.presentation.repo

interface UserRepo {
    suspend fun login(
        username: String,
        password: String
    )
}
