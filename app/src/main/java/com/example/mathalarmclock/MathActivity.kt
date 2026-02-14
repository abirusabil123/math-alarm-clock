package com.example.mathalarmclock

import android.app.NotificationManager
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.mathalarmclock.ui.theme.MathAlarmClockTheme
import com.example.mathalarmclock.ui.theme.MathPuzzleScreen

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
    }

    override fun onDestroy() {
        super.onDestroy()
        // DO NOT stop the alarm here - it should continue if activity is destroyed without solving
        // The alarm will keep playing until user solves it and calls stopAlarm()
    }
}
