package com.bunbeauty.tvstreamer.ui.theme.item.button

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bunbeauty.tvstreamer.R
import com.bunbeauty.tvstreamer.ui.theme.AdminTheme
import com.bunbeauty.tvstreamer.ui.theme.item.rememberMultipleEventsCutter
import com.bunbeauty.tvstreamer.ui.theme.medium

@Composable
fun MainButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    @StringRes textStringId: Int? = null,
    text: String? = null,
    colors: ButtonColors = AdminButtonDefaults.mainButtonColors,
    elevated: Boolean = true,
    isEnabled: Boolean = true
) {
    CompositionLocalProvider(
        LocalMinimumInteractiveComponentSize provides 0.dp
    ) {
        val multipleEventsCutter = rememberMultipleEventsCutter()
        Button(
            modifier = modifier.fillMaxWidth(),
            onClick = {
                multipleEventsCutter.processEvent(onClick)
            },
            colors = colors,
            shape = AdminButtonDefaults.buttonShape,
            elevation = AdminButtonDefaults.getButtonElevation(elevated),
            enabled = isEnabled
        ) {
            val buttonText = text ?: textStringId?.let {
                stringResource(it)
            } ?: ""
            Text(
                text = buttonText,
                style = AdminTheme.typography.labelLarge.medium,
                color = AdminTheme.colors.main.onPrimary
            )
        }
    }
}

@Preview
@Composable
private fun MainButtonPreview() {
    AdminTheme {
        MainButton(
            textStringId = R.string.action_retry,
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun MainButtonDisabledPreview() {
    AdminTheme {
        MainButton(
            textStringId = R.string.action_retry,
            isEnabled = false,
            onClick = {}
        )
    }
}
