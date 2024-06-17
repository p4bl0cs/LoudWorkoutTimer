@file:OptIn(ExperimentalMaterial3Api::class)

package com.plr.loudworkouttimer

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.plr.loudworkouttimer.ui.theme.LoudWorkoutTimerTheme
import java.util.Locale
import kotlin.time.Duration.Companion.seconds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoudWorkoutTimerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun ComposableLifecycle(
    lifeCycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onEvent: (LifecycleOwner, Lifecycle.Event) -> Unit
) {
    DisposableEffect(lifeCycleOwner) {
        val observer = LifecycleEventObserver { source, event ->
            onEvent(source, event)
        }
        lifeCycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    var showContextMenu by remember {
        mutableStateOf(false)
    }

    val openDialog = remember { mutableStateOf(false) }

    val mainContext = LocalContext.current
    val timerRepo = TimerRepo(mainContext)

    val data = remember { TimerRepo(mainContext).GetTimerInfoList()}

    val titleFontSize = 6.em
    val subTitleFontSize = 3.5.em

    var selectedTimerInfo: TimerInfo by remember {
        mutableStateOf(TimerInfo("", 0, 0, 0, null))
    }

    ComposableLifecycle { _, event ->
        if (event == Lifecycle.Event.ON_RESUME) {
            refreshData(data, timerRepo)
        }
    }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(
                    text = "Delete Timer",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text(
                    text = "Do you really want to delete '${selectedTimerInfo.name}'?",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = subTitleFontSize
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        timerRepo.DeleteTimerInfo(selectedTimerInfo.id!!)
                        refreshData(data, timerRepo)
                        openDialog.value = false
                    }
                ) {
                    Text(
                        text = "Delete",
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        openDialog.value = false
                    }
                ) {
                    Text(
                        text = "Cancel",
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Loud Workout Timer")
                },
                colors = topAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.tertiary,
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(onClick = {
                        showContextMenu = !showContextMenu
                    }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Open Options"
                        )
                    }

                    DropdownMenu(
                        expanded = showContextMenu,
                        onDismissRequest = { showContextMenu = false },
                    ) {
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(
                                    Icons.Default.ThumbUp,
                                    "Rate",
                                    tint = MaterialTheme.colorScheme.primary
                                ) },
                            text = {
                                Text("Rate this app")
                            },
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.plr.loudworkouttimer"))
                                mainContext.startActivity(intent)
                                showContextMenu = false
                            },
                        )
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = "About",
                                    tint = MaterialTheme.colorScheme.primary
                                ) },
                            text = {
                                Text(text = "About")
                            },
                            onClick = {
                                val intent = Intent(mainContext, AboutActivity::class.java)
                                mainContext.startActivity(intent)
                                showContextMenu = false
                            },
                        )
                    }
                }
            )
        },

        content = {
            LazyColumn(
                modifier = Modifier
                    .padding(top = 80.dp, bottom = 80.dp)
                    .background(color = MaterialTheme.colorScheme.tertiary),
                verticalArrangement = Arrangement.spacedBy(10.dp),

            ){
                items(data) {t ->
                    Card (
                        modifier = Modifier
                            .clickable {
                                val intent = Intent(mainContext, TimerActivity::class.java)
                                intent.putExtra("seconds", t.initialSeconds)
                                intent.putExtra("sets", t.initialSets)
                                intent.putExtra("restTime", t.breakSeconds)
                                mainContext.startActivity(intent)
                            }
                            .padding(start = 10.dp, end = 10.dp),
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row (
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 10.dp
                            )
                        ) {
                            Column (modifier = Modifier.fillMaxWidth(0.67f)) {
                                Text(
                                    t.name,
                                    fontSize = titleFontSize,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                )

                                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.primary)

                                Text(
                                    formatSecondsToTime(t.initialSeconds) + " x" +t.initialSets,
                                    fontSize = subTitleFontSize,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(Modifier.weight(1f))

                            FilledIconButton(
                                onClick = {
                                    val intent = Intent(mainContext, AddUpdateActivity::class.java)
                                    intent.putExtra("operationName", "Edit '${t.name}'")
                                    intent.putExtra("timerName", t.name)
                                    intent.putExtra("seconds", t.initialSeconds)
                                    intent.putExtra("sets", t.initialSets)
                                    intent.putExtra("restTime", t.breakSeconds)
                                    intent.putExtra("id", t.id)
                                    mainContext.startActivity(intent)
                                }) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "Edit",
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                            }

                            FilledIconButton(
                                onClick = {
                                    selectedTimerInfo = t
                                    openDialog.value = true
                                }) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    }
                }
            }
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val itemCount = timerRepo.GetTimerInfoList().size

                    if (itemCount == 16) {
                        Toast.makeText(
                            mainContext,
                            "Max timers limit reached (16).",
                            Toast.LENGTH_LONG
                        ).show()

                        return@FloatingActionButton
                    }

                    val intent = Intent(mainContext, AddUpdateActivity::class.java)
                    intent.putExtra("operationName", "Add new timer")
                    mainContext.startActivity(intent)
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Filled.Add, contentDescription = "Add new Timer",
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
        }

    )
}

private fun refreshData(data: SnapshotStateList<TimerInfo>, timerRepo: TimerRepo) {
    data.clear()
    data.addAll(timerRepo.GetTimerInfoList())
}

private fun formatSecondsToTime(totalSeconds: Int): String {
    if (totalSeconds >= 60) {
        return totalSeconds.seconds.toComponents { minutes, seconds, _ ->
            String.format(
                Locale.getDefault(),
                "%02d:%02d",
                minutes,
                seconds,
            )
        }
    }

    return "00:$totalSeconds"
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LoudWorkoutTimerTheme {
        MainScreen()
    }
}