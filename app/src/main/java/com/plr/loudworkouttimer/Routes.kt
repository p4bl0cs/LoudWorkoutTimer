package com.plr.loudworkouttimer

object Routes {
    const val MAIN_SCREEN = "MainScreen"
    const val TIMER_SCREEN = "Timer/{${Values.TIMER_DEFAULT_ID}}"
    const val ABOUT_SCREEN = "About"

    fun getTimerPath(customValue: Int?): String =
        if (customValue != null && customValue > -1)
            "Timer/$customValue"
        else MAIN_SCREEN

    object Values {
        const val TIMER_DEFAULT_ID = 0
    }
}