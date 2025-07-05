package com.bunbeauty.tvstreamer.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.bunbeauty.tvstreamer.common.extension.launchSafe
import com.bunbeauty.tvstreamer.domain.usecase.HasSessionUseCase
import com.bunbeauty.tvstreamer.domain.usecase.LoginUseCase
import com.bunbeauty.tvstreamer.presentation.base.BaseStateViewModel
import com.bunbeauty.tvstreamer.presentation.login.Login

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val hasSessionUseCase: HasSessionUseCase
) : BaseStateViewModel<Login.DataState, Login.Action, Login.Event>(
    initState = Login.DataState(
        username = "",
        password = "",
        hasError = false,
        loading = true
    )
) {

    init {
        viewModelScope.launchSafe(
            block = {
                if (hasSessionUseCase()) {
                    sendEvent {
                        Login.Event.GoToOrderList
                    }
                } else {
                    setState {
                        copy(loading = false)
                    }
                }
            },
            onError = { error ->
                Log.d("MyTag", "initLogin: ${error.stackTrace}")
            }
        )
    }

    override fun reduce(action: Login.Action, dataState: Login.DataState) {
        when (action) {
            Login.Action.LoginClick -> loginClick(
                username = dataState.username,
                password = dataState.password
            )

            is Login.Action.PasswordChanged -> changePassword(password = action.password)

            is Login.Action.UsernameChanged -> changeUsername(username = action.username)
        }
    }

    private fun changePassword(password: String) {
        setState {
            copy(
                password = password
            )
        }
    }

    private fun changeUsername(username: String) {
        setState {
            copy(
                username = username
            )
        }
    }

    private fun loginClick(username: String, password: String) {
        viewModelScope.launchSafe(
            block = {
                loginUseCase.invoke(username = username, password = password)
                sendEvent {
                    Login.Event.GoToOrderList
                }
                setState {
                    copy(
                        loading = false
                    )
                }
            },
            onError = {
                setState {
                    copy(
                        hasError = true
                    )
                }
            }
        )
    }
}
