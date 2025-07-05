package com.bunbeauty.tvstreamer.domain.usecase

import com.bunbeauty.tvstreamer.presentation.repo.DataStoreRepo

class LogoutUseCase(
    private val dataStoreRepo: DataStoreRepo
) {
    suspend operator fun invoke() {
        return dataStoreRepo.clearCache()
    }
}
