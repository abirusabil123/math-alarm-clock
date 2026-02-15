package com.example.mathalarmclock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.mathalarmclock.ui.theme.AlarmPreferences

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = AlarmPreferences(context)
            if (prefs.isAlarmSet()) {
                val hour = prefs.getLastSetHour()
                val minute = prefs.getLastSetMinute()
                val repeatDays = prefs.getRepeatDays()

                // Reschedule the alarm
                Utilities.setAlarm(context, hour, minute, repeatDays)
            }
        }
    }
}