package com.example.mathalarmclock

import android.app.NotificationManager
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mathalarmclock.ui.theme.MathAlarmClockTheme

class MathActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Dismiss notification when activity starts
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.cancel(1)

        // Keep screen on
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            MathAlarmClockTheme {
                MathPuzzleScreen(
                    onCorrectAnswer = {
                        stopAlarm()
                    })
            }
        }
    }

    private fun stopAlarm() {
        // Only stop when math is solved
        try {
            AlarmReceiver.mediaPlayer?.stop()
            AlarmReceiver.mediaPlayer?.release()
            AlarmReceiver.mediaPlayer = null

            AlarmReceiver.vibrator?.cancel()
            AlarmReceiver.vibrator = null
            AlarmReceiver.isAlarmPlaying = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        // DO NOT stop the alarm here - it should continue if activity is destroyed without solving
        // The alarm will keep playing until user solves it and calls stopAlarm()
    }
}

@Composable
fun MathPuzzleScreen(onCorrectAnswer: () -> Unit) {
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "WAKE UP!",
            fontSize = 32.sp,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Text(
            text = "2 + 2 = ?", fontSize = 48.sp, modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = {
                message = "Correct! Alarm stopped."
                onCorrectAnswer()
            }, modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Text("4", fontSize = 24.sp)
        }

        if (message.isNotEmpty()) {
            Text(
                text = message,
                modifier = Modifier.padding(top = 16.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}