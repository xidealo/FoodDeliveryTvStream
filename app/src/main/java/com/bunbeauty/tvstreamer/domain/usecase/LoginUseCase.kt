package com.bunbeauty.tvstreamer.domain.usecase

import com.bunbeauty.tvstreamer.presentation.repo.UserRepo

class LoginUseCase(
    private val userRepo: UserRepo
) {
    suspend operator fun invoke(
        username: String,
        password: String
    ) {
        userRepo.login(
            username = username,
            password = password
        )
    }
}
