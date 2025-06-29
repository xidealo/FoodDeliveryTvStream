package com.bunbeauty.tvstreamer.ui.theme

import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun MyApplicationTheme(
    isInDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalOverscrollFactory provides null,
        LocalAdminColors provides LightAdminColors,
        LocalAdminDimensions provides AdminDimensions(),
        LocalAdminTypography provides AdminTypography(),
        content = content
    )
}
