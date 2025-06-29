package com.bunbeauty.tvstreamer.presentation.login

import com.bunbeauty.tvstreamer.presentation.base.BaseAction
import com.bunbeauty.tvstreamer.presentation.base.BaseDataState
import com.bunbeauty.tvstreamer.presentation.base.BaseEvent

interface Login {
    data class DataState(
        val username: String,
        val password: String,
        val hasError: Boolean,
        val loading: Boolean
    ) : BaseDataState

    sealed interface Action : BaseAction {
        data object LoginClick : Action
        data class UsernameChanged(val username: String) : Action
        data class PasswordChanged(val password: String) : Action
    }

    sealed interface Event : BaseEvent {
        data object GoToOrderList : Event
    }
}
