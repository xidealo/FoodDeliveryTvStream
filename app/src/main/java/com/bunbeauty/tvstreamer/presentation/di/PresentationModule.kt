package com.bunbeauty.tvstreamer.presentation.di

import com.bunbeauty.tvstreamer.presentation.LoginViewModel
import com.bunbeauty.tvstreamer.presentation.OrderListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun presentationModule() = module {
    viewModel {
        OrderListViewModel(
            getOrderListFlow = get(),
            getOrderErrorFlow = get(),
            getDoneOrderListUseCase = get(),
            getPreparingOrderListUseCase = get()
        )
    }
    viewModel {
        LoginViewModel(
            loginUseCase = get(),
            hasSessionUseCase = get()
        )
    }
}
