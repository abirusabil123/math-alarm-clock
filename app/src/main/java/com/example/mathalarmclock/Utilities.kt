/* Copyright (c) 2025 Mohammad Sheraj */
/* Math Alarm Clock is licensed under India PSL v1. You can use this software according to the terms and conditions of the India PSL v1. You may obtain a copy of India PSL v1 at: https://github.com/abirusabil123/discover/blob/main/IndiaPSL1 THIS SOFTWARE IS PROVIDED ON AN “AS IS” BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE. See the India PSL v1 for more details. */

package com.example.mathalarmclock

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.ui.text.intl.Locale
import java.util.Calendar

class Utilities {
    companion object {
        fun setAlarm(context: Context, hour: Int, minute: Int) {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)

                if (before(Calendar.getInstance())) {
                    add(Calendar.DAY_OF_MONTH, 1)
                }
            }

            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // Check if we can set exact alarms (Android 12+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent
                    )
                    Toast.makeText(
                        context, "Alarm set for ${
                            String.format(
                                Locale.current.region, "%02d:%02d", hour, minute
                            )
                        }", Toast.LENGTH_LONG
                    ).show()
                } else {
                    // Request permission
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    context.startActivity(intent)
                    Toast.makeText(
                        context, "Please grant exact alarm permission", Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                // For older Android versions
                try {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent
                    )
                    Toast.makeText(
                        context, "Alarm set for ${
                            String.format(
                                Locale.current.region, "%02d:%02d", hour, minute
                            )
                        }", Toast.LENGTH_LONG
                    ).show()
                } catch (e: SecurityException) {
                    Toast.makeText(context, "Cannot set exact alarm", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }
        }

        fun cancelAlarm(context: Context) {
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)

            // Also stop any currently playing alarm
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

            Toast.makeText(context, "Alarm cancelled", Toast.LENGTH_SHORT).show()
        }
    }
}