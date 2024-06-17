@file:OptIn(ExperimentalMaterial3Api::class)

package com.plr.loudworkouttimer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.plr.loudworkouttimer.ui.theme.LoudWorkoutTimerTheme

class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoudWorkoutTimerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AboutScreen()
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AboutScreen() {
    val currentActivity = LocalContext.current as Activity

    val manager = LocalContext.current.packageManager
    val info = manager?.getPackageInfo(
        LocalContext.current.packageName, 0
    )
    val versionName = info?.versionName
    val versionNumber = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        info?.longVersionCode
    } else {
        info?.versionCode
    }

    val titleFontSize = 6.em
    val subTitleFontSize = 3.5.em

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "About")
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
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
           Text(
               text = "Loud Workout Timer v${versionName}",
               textAlign = TextAlign.Center,
               color = MaterialTheme.colorScheme.primary,
               fontSize = titleFontSize,
               fontWeight = FontWeight.ExtraBold
           )

            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.primary)

            Row (horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Full Version",
                    textAlign = TextAlign.Right,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = subTitleFontSize,
                )
            }

            Text(
                modifier = Modifier.padding(top = 40.dp),
                text = "Thank you for purchasing the full version of Loud Workout Timer.\n\n" +
                        "This app was created to keep track with your workout times, sets and breaks without needing to keep an eye on your device's clock.\n\n" +
                        "Focus on your routine and let the app tell you how much you have left.\n\n" +
                        "Please leave any comments or suggestions on the app's Play Store page. ",
                textAlign = TextAlign.Left,
                color = MaterialTheme.colorScheme.primary,
                fontSize = subTitleFontSize,
            )

            ElevatedButton(
                modifier = Modifier.padding(top = 20.dp),
                colors = ButtonDefaults.buttonColors(contentColor = MaterialTheme.colorScheme.tertiary),
                onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.plr.loudworkouttimer"))
                currentActivity.startActivity(intent)
            }) {
                Row () {
                    Icon(
                        Icons.Default.ThumbUp,
                        "Rate",
                        tint = MaterialTheme.colorScheme.tertiary
                    )

                    Spacer(modifier = Modifier.width(width = 10.dp))

                    Text(
                        text = "Rate this app",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}