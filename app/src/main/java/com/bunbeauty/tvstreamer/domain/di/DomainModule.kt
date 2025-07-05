package com.bunbeauty.tvstreamer.domain.di

import com.bunbeauty.tvstreamer.domain.usecase.GetDoneOrderListUseCase
import com.bunbeauty.tvstreamer.domain.usecase.GetOrderErrorFlowUseCase
import com.bunbeauty.tvstreamer.domain.usecase.GetOrderListFlowUseCase
import com.bunbeauty.tvstreamer.domain.usecase.GetPreparingOrderListUseCase
import com.bunbeauty.tvstreamer.domain.usecase.HasSessionUseCase
import com.bunbeauty.tvstreamer.domain.usecase.LoginUseCase
import com.bunbeauty.tvstreamer.domain.usecase.LogoutUseCase
import org.koin.dsl.module

fun domainModule() = module {
    factory {
        GetOrderListFlowUseCase(
            orderRepo = get(),
            dataStoreRepo = get()
        )
    }
    factory {
        GetOrderErrorFlowUseCase(
            orderRepo = get(),
            dataStoreRepo = get()
        )
    }
    factory {
        LoginUseCase(userRepo = get())
    }
    factory {
        GetPreparingOrderListUseCase()
    }
    factory {
        GetDoneOrderListUseCase()
    }
    factory {
        HasSessionUseCase(dataStoreRepo = get())
    }
    factory {
        LogoutUseCase(dataStoreRepo = get())
    }
}
