package com.plr.loudworkouttimer

import android.app.Application
import android.os.CountDownTimer
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import kotlin.time.Duration.Companion.seconds

class CustomTimer(initialSeconds: Int, initialReps: Int, restSeconds: Int, app: Application) : AndroidViewModel(app) {
    private val colorScheme = CustomColorScheme.getInstance()

    private var _timer: CountDownTimer? = null
    private val tts: Tts = Tts()
    private var _isResting = false
    private var _isDownCounting: Boolean? = null

    private val _isEnabled = MutableStateFlow(true)
    val isEnabled = _isEnabled.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private val _seconds = MutableStateFlow(initialSeconds)

    private val _progress = MutableStateFlow(0F)
    val progress = _progress.asStateFlow()

    private val _currentStatus = MutableStateFlow("<status>")
    val currentStatus = _currentStatus.asStateFlow()

    private val _currentTime = MutableStateFlow("")
    val currentTime = _currentTime.asStateFlow()

    private val _currentSet = MutableStateFlow("")
    val currentSet = _currentSet.asStateFlow()

    private val _currentTimerColor = MutableStateFlow(colorScheme!!.primary)
    val currentTimerColor = _currentTimerColor.asStateFlow()

    var set: Int = initialReps

    val step: Float = 1 / 60f

    init {
        _currentStatus.value = "Touch to start"
        _currentTime.value = formatSecondsToTime()
        _progress.value = if (60 - initialSeconds % 60 == 60) 0F else (60 - initialSeconds % 60) * step
        val totalSeconds = (initialSeconds * (if (initialReps > 0) initialReps else 1)) + (if (initialReps > 1) (restSeconds * (initialReps - 1)) else 0 ) + (set * 2) + 3

        if (initialReps > 0) {
            _currentSet.value = "x$set"
        }

        _timer = object : CountDownTimer(totalSeconds * 1000L, 1000) {

            override fun onTick(millisRemaining: Long) {
                if (_isDownCounting == null) {
                    _currentTimerColor.value = colorScheme!!.secondary
                    _isEnabled.value = false
                    _isDownCounting = true
                    _seconds.value = 3
                    _currentTime.value = _seconds.value.toString()
                    _currentStatus.value = "Ready?"
                    tts.textToSpeech(app.applicationContext, "Ready?")

                    return
                }
                else if (_isDownCounting == true) {
                    _seconds.value--
                    _currentTime.value = if (_seconds.value == 0) "GO!" else _seconds.value.toString()

                    if (_seconds.value == 1) {
                        tts.textToSpeech(app.applicationContext, "Lets go!")
                        _currentStatus.value = "Lets go!"
                    }
                    else if (_seconds.value == 0) {
                        _seconds.value = initialSeconds
                        _currentTime.value = formatSecondsToTime()
                        _currentTimerColor.value = colorScheme!!.primaryContainer
                        _isDownCounting = false
                        _isEnabled.value = true
                        _currentStatus.value = ""
                    }

                    return
                }

                if (_isResting) {
                    if (_seconds.value == 0) {
                        _seconds.value = restSeconds
                        _currentStatus.value = "Break Time"
                    } else {
                        _seconds.value--

                        when (_seconds.value) {
                            3 -> {
                                tts.textToSpeech(app, "Ready?")
                            }
                            1 -> {
                                tts.textToSpeech(app.applicationContext, "Lets go!")
                            }
                            0 -> {
                                _currentSet.value = "x${set}"
                                _isResting = false
                                _seconds.value = initialSeconds
                                _currentStatus.value = ""
                                _currentTime.value = formatSecondsToTime()
                                _progress.value = if (60 - initialSeconds % 60 == 60) 0F else (60 - initialSeconds % 60) * step
                                _currentTimerColor.value = colorScheme!!.primaryContainer
                            }
                        }
                    }
                } else {
                    _progress.value += step
                    _seconds.value--

                    if (_seconds.value == 0) {
                        if (set > 1) {
                            set--
                            val isPlural = isNumberPlural(set)
                            tts.textToSpeech(app.applicationContext, "Break time.\n$set ${(if (isPlural) "sets" else "set")} left.")
                            _isResting = true
                            _currentSet.value = ""
                            _currentTime.value = formatSecondsToTime()
                            _currentTimerColor.value = colorScheme!!.surface
                        }
                        else {
                            _timer?.cancel()
                            finishTimer()
                        }

                        return
                    }

                    if (_seconds.value < 60) {
                        if (_seconds.value in 1..5) {
                            tts.textToSpeech(app.applicationContext, _seconds.value.toString())
                        } else if (_seconds.value == 45 || _seconds.value == 30 || _seconds.value == 10) {
                            tts.textToSpeech(app.applicationContext, "${_seconds.value} seconds left.")
                        }
                    }

                    if (_seconds.value >= 60) {
                        if (_seconds.value >= 90) {
                            if (_seconds.value % 30 == 0 && (_seconds.value / 30) % 2 != 0) {
                                val minutesLeft = _seconds.value / 60
                                val isPlural = isNumberPlural(minutesLeft)
                                tts.textToSpeech(app.applicationContext, "$minutesLeft ${(if (isPlural) "minutes" else "minute")} and thirty seconds left.")
                            }
                        }

                        if (_seconds.value % 60 == 0) {
                            val minutesLeft = _seconds.value / 60
                            val isPlural = isNumberPlural(minutesLeft)
                            tts.textToSpeech(app.applicationContext, "$minutesLeft ${(if (isPlural) "minutes" else "minute")} left.")
                        }

                        if ((_seconds.value?: -1) % 60 == 0) {
                            _progress.value = 0f
                        }
                    }
                }

                _currentTime.value = formatSecondsToTime()
            }

            override fun onFinish() {
                finishTimer()
            }
        }
    }

    fun startTimer() {
        _isRunning.value = true
        _currentStatus.value = if (_isResting) "Break Time" else ""
        _currentTimerColor.value = if (_isResting) colorScheme!!.surface else colorScheme!!.primaryContainer
        _timer?.start()
    }

    fun cancelTimer() {
        _isRunning.value = false
        _currentStatus.value = "PAUSED"
        _currentTimerColor.value = colorScheme!!.error
        _timer?.cancel()
    }

    fun stopTimer() {
        _isRunning.value = false
        _currentStatus.value = ""
        _timer?.cancel()
    }

    private fun finishTimer() {
        _isEnabled.value = false
        tts.tts?.shutdown()
        _currentTime.value = "0"
        _currentSet.value = ""
        _currentStatus.value = "COMPLETED"
        _currentTimerColor.value = colorScheme!!.primary
        tts.textToSpeech(this.getApplication(), "COMPLETED")
        _progress.value = 1F
    }

    private fun formatSecondsToTime(): String {
        if (_seconds.value >= 60) {
            return _seconds.value.seconds.toComponents { minutes, seconds, _ ->
                String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    minutes,
                    seconds,
                )
            }
        }

        return _seconds.value.toString()
    }

    private fun isNumberPlural(number: Int): Boolean {
        return number > 1
    }
}