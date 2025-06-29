package com.bunbeauty.tvstreamer.domain.usecase

import com.bunbeauty.tvstreamer.presentation.repo.DataStoreRepo

// TODO tests
class HasSessionUseCase(
    private val dataStoreRepo: DataStoreRepo
) {
    suspend operator fun invoke(): Boolean {
        return dataStoreRepo.getToken() != null
    }
}
