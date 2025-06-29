package com.bunbeauty.tvstreamer.ui.theme.item.button

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bunbeauty.tvstreamer.ui.theme.AdminTheme
import com.bunbeauty.tvstreamer.ui.theme.item.button.AdminButtonDefaults.getButtonElevation
import com.bunbeauty.tvstreamer.ui.theme.item.rememberMultipleEventsCutter
import com.bunbeauty.tvstreamer.ui.theme.medium

@Composable
fun LoadingButton(
    text: String,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    hasShadow: Boolean = true
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
            colors = AdminButtonDefaults.mainButtonColors,
            shape = AdminButtonDefaults.buttonShape,
            elevation = getButtonElevation(hasShadow),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(AdminTheme.dimensions.smallProgressBarSize),
                    color = AdminTheme.colors.main.onDisabled
                )
            } else {
                Text(
                    text = text,
                    style = AdminTheme.typography.labelLarge.medium,
                    color = AdminTheme.colors.main.onPrimary
                )
            }
        }
    }
}
