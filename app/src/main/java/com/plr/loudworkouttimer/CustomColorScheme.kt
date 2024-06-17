package com.plr.loudworkouttimer

import androidx.compose.ui.graphics.Color

class CustomColorScheme private constructor (
    var primary: Color,
    var secondary: Color,
    var tertiary: Color,
    var background: Color,
    var surface: Color,
    var error: Color,
    var primaryContainer: Color
    ) {

    companion object {
        private var INSTANCE: CustomColorScheme? = null

        fun getInstance(): CustomColorScheme? {
            return INSTANCE
        }

        fun getInstance(
            primaryColor: Color,
            secondaryColor: Color,
            tertiaryColor: Color,
            backgroundColor: Color,
            surfaceColor: Color,
            errorColor: Color,
            primaryContainerColor: Color): CustomColorScheme? {

            if (INSTANCE == null) {
                INSTANCE = CustomColorScheme(primaryColor, secondaryColor, tertiaryColor, backgroundColor, surfaceColor, errorColor, primaryContainerColor)
            }

            return INSTANCE
        }
    }
}