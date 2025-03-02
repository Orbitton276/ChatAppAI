package com.data.chatappai.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xff4FCE5D),
    secondary = PurpleGrey80,
    tertiary = Color.Green,
    tertiaryContainer = Color.Yellow,
    onTertiaryContainer = Color.Red

)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xff4FCE5D),
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun ChatAppAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val extendedColors = if (darkTheme) {
        ExtendedColors(
            selfContainer = Color(0xFF2D6A4F).copy(alpha = 0.6f),
            otherContainer = Color(0xff1E88E5).copy(alpha = 0.2f),
            ) // Dark blue
    } else {
        ExtendedColors(
            selfContainer = Color(0xFFD8F3DC),
            otherContainer = Color(0xFF81D4FA),
        ) // Dark blue
    }


    val colors = if (darkTheme) DarkColorScheme else LightColorScheme


    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colors,
            typography = Typography,
            content = content
        )
    }
}

@Immutable
data class ExtendedColors(
    val selfContainer: Color,
    val otherContainer: Color,
)

private val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        selfContainer = Color.Unspecified,
        otherContainer =  Color.Unspecified,
    )
}

val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    get() = LocalExtendedColors.current
