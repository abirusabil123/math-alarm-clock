/* Copyright (c) 2025 Mohammad Sheraj *//* Math Alarm Clock is licensed under India PSL v1. You can use this software according to the terms and conditions of the India PSL v1. You may obtain a copy of India PSL v1 at: https://github.com/abirusabil123/discover/blob/main/IndiaPSL1 THIS SOFTWARE IS PROVIDED ON AN “AS IS” BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE. See the India PSL v1 for more details. */


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
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        // DO NOT stop the alarm here - it should continue if activity is destroyed without solving
        // The alarm will keep playing until user solves it and calls stopAlarm()
    }
}
