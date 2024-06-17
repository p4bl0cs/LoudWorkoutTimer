@file:OptIn(ExperimentalMaterial3Api::class)

package com.plr.loudworkouttimer

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.plr.loudworkouttimer.ui.theme.LoudWorkoutTimerTheme

class TimerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoudWorkoutTimerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TimerScreen()
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TimerScreen() {
    val mainContext = LocalContext.current

    val currentView = LocalView.current
    val currentIntent = (currentView.context as Activity).intent
    val currentActivity = LocalContext.current as Activity
    val vm = CustomTimer(
        currentIntent.getIntExtra("seconds", 15),
        currentIntent.getIntExtra("sets", 2),
        currentIntent.getIntExtra("restTime", 10),
        mainContext.applicationContext as Application)

    val progress by vm.progress.collectAsState()
    val isEnabled by vm.isEnabled.collectAsState()

    val currentTime by vm.currentTime.collectAsState()

    val currentStatus by vm.currentStatus.collectAsState()

    val currentSet by vm.currentSet.collectAsState()

    val currentTimerColor by vm.currentTimerColor.collectAsState()

    DisposableEffect(Unit) {
        currentView.keepScreenOn = true
        onDispose {
            currentView.keepScreenOn = false
        }
    }

    BackHandler(enabled = true, onBack = {
        vm.stopTimer()
        currentActivity.finish()
    })

    fun startPauseTimer() {
        if (!isEnabled) {
            if (currentStatus == "COMPLETED") {
                currentActivity.onBackPressed()
            }

            return
        }

        if (vm.isRunning.value) {
            vm.cancelTimer()
        }
        else {
            vm.startTimer()
        }

        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = currentIntent.getStringExtra("workOutName").orEmpty())
                },
                navigationIcon = {
                    IconButton(onClick = {
                        currentActivity.onBackPressed()
                    }) {
                        Icon(Icons.Filled.ArrowBack, "backIcon")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.tertiary,
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) {
        Column {
            Row(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.padding(top = 180.dp),
                    contentAlignment = Alignment.Center) {
                    Text(modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 20.dp),
                        text = currentSet,
                        color = currentTimerColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.em)

                    CircularProgressIndicator(
                        color = currentTimerColor,
                        modifier = Modifier
                            .size(320.dp)
                            .clickable { startPauseTimer() },
                        progress = progress,
                        strokeWidth = 15.dp
                    )

                    Box(Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = currentTime,
                            color = currentTimerColor,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.em)
                    }
                }
            }

            Row(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.size(50.dp))
            }

            Row(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(text = currentStatus,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.em)
            }
        }
    }
}