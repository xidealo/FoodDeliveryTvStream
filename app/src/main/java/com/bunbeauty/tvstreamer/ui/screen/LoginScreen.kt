package com.bunbeauty.tvstreamer.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.bunbeauty.tvstreamer.presentation.LoginViewModel
import com.bunbeauty.tvstreamer.presentation.login.Login
import com.bunbeauty.tvstreamer.ui.theme.AdminTheme
import com.bunbeauty.tvstreamer.ui.theme.item.AdminTextField
import com.bunbeauty.tvstreamer.ui.theme.item.AdminTextFieldDefaults.keyboardOptions
import com.bunbeauty.tvstreamer.ui.theme.item.button.LoadingButton
import com.bunbeauty.tvstreamer.ui.theme.item.button.MainButton
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginRoute(
    viewModel: LoginViewModel = koinViewModel(),
    navigateToOrderList: () -> Unit,
) {

    val viewState by viewModel.state.collectAsStateWithLifecycle()
    val onAction = remember {
        { event: Login.Action ->
            viewModel.onAction(event)
        }
    }

    val effects by viewModel.events.collectAsStateWithLifecycle()
    val consumeEffects = remember {
        {
            viewModel.consumeEvents(effects)
        }
    }

    LoginEffect(
        effects = effects,
        navigateToOrderList = navigateToOrderList,
        consumeEffects = consumeEffects,
    )
    LoginScreen(onAction = onAction, state = viewState)
}

@Composable
fun LoginScreen(
    state: Login.DataState,
    onAction: (Login.Action) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        shape = RectangleShape
    ) {
        if (state.loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    color = AdminTheme.colors.main.primary,
                    modifier = Modifier.size(42.dp)
                )
            }
        } else {
            Column {
                AdminTextField(
                    value = state.username,
                    labelText = "Логин",
                    onValueChange = { username ->
                        onAction(Login.Action.UsernameChanged(username = username))
                    }
                )

                AdminTextField(
                    modifier = Modifier.padding(
                        top = 16.dp
                    ),
                    value = state.password,
                    labelText = "Пароль",
                    onValueChange = { password ->
                        onAction(Login.Action.PasswordChanged(password = password))
                    },
                    keyboardOptions = keyboardOptions(
                        keyboardType = KeyboardType.Password
                    )
                )

                if (state.hasError) {
                    Text(
                        modifier = Modifier.padding(top = 16.dp),
                        text = "Ошибка авторизации",
                        color = AdminTheme.colors.main.error
                    )
                }

                MainButton(
                    modifier = Modifier.padding(top = 16.dp),
                    text = "Войти",
                    onClick = {
                        onAction(Login.Action.LoginClick)
                    }
                )
            }
        }
    }
}

@Composable
private fun LoginEffect(
    effects: List<Login.Event>,
    navigateToOrderList: () -> Unit,
    consumeEffects: () -> Unit,
) {
    LaunchedEffect(effects) {
        effects.forEach { effect ->
            when (effect) {
                Login.Event.GoToOrderList -> navigateToOrderList()
            }
        }
        consumeEffects()
    }
}
