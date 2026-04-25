/* Copyright (c) 2025 Mohammad Sheraj *//* Math Alarm Clock is licensed under India PSL v1. You can use this software according to the terms and conditions of the India PSL v1. You may obtain a copy of India PSL v1 at: https://github.com/abirusabil123/discover/blob/main/IndiaPSL1 THIS SOFTWARE IS PROVIDED ON AN “AS IS” BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE. See the India PSL v1 for more details. */

package com.example.mathalarmclock

import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.mathalarmclock.ui.theme.MathAlarmClockTheme
import com.example.mathalarmclock.ui.theme.MathPuzzleScreen

class MathActivity : ComponentActivity() {
    private var hour: Int = 0
    private var minute: Int = 0
    private var repeatDays: Array<Int> = emptyArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get alarm details
        hour = intent.getIntExtra("hour", 0)
        minute = intent.getIntExtra("minute", 0)
        // Modern type-safe way to get Serializable
        repeatDays = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("repeatDays", Array<Int>::class.java) ?: emptyArray()
        } else {
            @Suppress("DEPRECATION", "UNCHECKED_CAST")
            (intent.getSerializableExtra("repeatDays") as? Array<Int>) ?: emptyArray()
        }

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
        // Stop alarm service
        val serviceIntent = Intent(this, AlarmService::class.java)
        stopService(serviceIntent)

        // If it's a repeating alarm, set the next occurrence
        if (repeatDays.isNotEmpty()) {
            Utilities.setAlarm(this, hour, minute, repeatDays.toSet(), false)
        }

        finish()
    }
}