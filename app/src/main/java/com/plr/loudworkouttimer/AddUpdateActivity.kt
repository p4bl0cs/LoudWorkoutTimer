@file:OptIn(ExperimentalMaterial3Api::class)

package com.plr.loudworkouttimer

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.chargemap.compose.numberpicker.NumberPicker
import com.plr.loudworkouttimer.ui.theme.LoudWorkoutTimerTheme

class AddUpdateActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoudWorkoutTimerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AddUpdateScreen()
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddUpdateScreen() {
    val currentView = LocalView.current
    val currentIntent = (currentView.context as Activity).intent
    val currentActivity = LocalContext.current as Activity

    val _timerInfo = TimerInfo(
        currentIntent.getStringExtra("timerName").orEmpty(),
        currentIntent.getIntExtra("seconds", 30),
        currentIntent.getIntExtra("sets", 1),
        currentIntent.getIntExtra("restTime", 15),
        currentIntent.getIntExtra("id", 0))

    var _timerName by remember { mutableStateOf(_timerInfo.name) }
    var _minutes by remember { mutableStateOf(_timerInfo.initialSeconds / 60) }
    var _seconds by remember { mutableStateOf(_timerInfo.initialSeconds % 60) }

    var _restMinutes by remember { mutableStateOf(_timerInfo.breakSeconds / 60) }
    var _restSeconds by remember { mutableStateOf(_timerInfo.breakSeconds % 60) }

    var _reps by remember { mutableStateOf(_timerInfo.initialSets) }

    val labelFontSize = 4.5.em
    val numberFontSize = 8.em
    val sectionSeparationSize = 8.dp

    val mainContext = LocalContext.current
    val timerRepo = TimerRepo(mainContext)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = currentIntent.getStringExtra("operationName").orEmpty())
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
            Row (
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(start = 20.dp, top = 80.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Workout time",
                    textAlign = TextAlign.Right,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = labelFontSize,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            HorizontalDivider(modifier = Modifier.padding(start = 20.dp), thickness = 1.dp, color = MaterialTheme.colorScheme.primary)

            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .offset(y = -15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center) {
                Text(
                    text = "Minutes",
                    textAlign = TextAlign.Right,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = labelFontSize
                )

                NumberPicker(
                    dividersColor = MaterialTheme.colorScheme.primary,
                    value = _minutes,
                    range = 0..59,
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = numberFontSize,
                        fontWeight = FontWeight.Bold),
                    onValueChange = {
                        _minutes = it
                    }
                )

                Text(
                    modifier = Modifier.padding(start = 20.dp),
                    text = "Seconds",
                    textAlign = TextAlign.Right,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = labelFontSize
                )

                NumberPicker(
                    dividersColor = MaterialTheme.colorScheme.primary,
                    value = _seconds,
                    range = 10..59,
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = numberFontSize,
                        fontWeight = FontWeight.Bold),
                    onValueChange = {
                        _seconds = it
                    }
                )
            }

            Row (
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(start = 20.dp, top = sectionSeparationSize),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Break time",
                    textAlign = TextAlign.Right,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = labelFontSize,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            HorizontalDivider(modifier = Modifier.padding(start = 20.dp), thickness = 1.dp, color = MaterialTheme.colorScheme.primary)

            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .offset(y = -15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center) {
                Text(
                    text = "Minutes",
                    textAlign = TextAlign.Right,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = labelFontSize
                )

                NumberPicker(
                    dividersColor = MaterialTheme.colorScheme.primary,
                    value = _restMinutes,
                    range = 0..14,
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = numberFontSize,
                        fontWeight = FontWeight.Bold),
                    onValueChange = {
                        _restMinutes = it
                    }
                )

                Text(
                    modifier = Modifier.padding(start = 20.dp),
                    text = "Seconds",
                    textAlign = TextAlign.Right,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = labelFontSize
                )

                NumberPicker(
                    dividersColor = MaterialTheme.colorScheme.primary,
                    value = _restSeconds,
                    range = 10..59,
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = numberFontSize,
                        fontWeight = FontWeight.Bold),
                    onValueChange = {
                        _restSeconds = it
                    }
                )
            }

            Row (
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(start = 20.dp, top = sectionSeparationSize),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Sets",
                    textAlign = TextAlign.Right,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = labelFontSize,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            HorizontalDivider(modifier = Modifier.padding(start = 20.dp), thickness = 1.dp, color = MaterialTheme.colorScheme.primary)

            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .offset(y = -15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center) {
                NumberPicker(
                    dividersColor = MaterialTheme.colorScheme.primary,
                    value = _reps,
                    range = 1..20,
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = numberFontSize,
                        fontWeight = FontWeight.Bold),
                    onValueChange = {
                        _reps = it
                    }
                )
            }

            Row (
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(start = 20.dp, top = sectionSeparationSize),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Name",
                    textAlign = TextAlign.Right,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = labelFontSize,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            HorizontalDivider(modifier = Modifier.padding(start = 20.dp), thickness = 1.dp, color = MaterialTheme.colorScheme.primary)

            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(start = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = _timerName,
                    maxLines = 1,
                    singleLine = true,
                    textStyle = TextStyle(fontSize =  labelFontSize, fontWeight = FontWeight.Bold),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.tertiary,
                        focusedContainerColor = MaterialTheme.colorScheme.tertiary,
                        focusedTextColor = MaterialTheme.colorScheme.primary,
                        unfocusedTextColor = MaterialTheme.colorScheme.primary
                    ),
                    onValueChange = { if (it.length <= 128) _timerName = it }
                )
            }

            Row (
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(top = 30.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                ElevatedButton(
                    colors = ButtonDefaults.buttonColors(contentColor = MaterialTheme.colorScheme.tertiary),

                    onClick = {
                        if (_timerName.trim().isEmpty()) {
                            Toast.makeText(
                                mainContext,
                                "Name cannot be empty",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@ElevatedButton
                        }


                        val nameChanged = _timerInfo.id != null && _timerInfo.id!! > 0 && _timerInfo.name.lowercase() != _timerName.lowercase().trim()

                        if (!timerRepo.ExistNoDuplicates(_timerName.trim())) {
                            if (nameChanged || _timerInfo.id!! == 0) {
                                Toast.makeText(
                                    mainContext,
                                    "Name already exists",
                                    Toast.LENGTH_LONG
                                ).show()
                                return@ElevatedButton
                            }
                        }

                        _timerInfo.name = _timerName
                        _timerInfo.initialSets = _reps
                        _timerInfo.initialSeconds = (_minutes * 60) + _seconds
                        _timerInfo.breakSeconds = (_restMinutes * 60) + _restSeconds

                        val result = timerRepo.AddOrUpdateTimerInfo(_timerInfo)

                        if (result == null) {
                            Toast.makeText(mainContext, "A problem occurred", Toast.LENGTH_LONG).show()
                            return@ElevatedButton
                        }

                        Toast.makeText(mainContext, "Save successful", Toast.LENGTH_SHORT).show()
                        currentActivity.onBackPressed()
                    }
                ) {
                    Text(
                        text = "Save",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}