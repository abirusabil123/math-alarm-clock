/* Copyright (c) 2025 Mohammad Sheraj *//* Math Alarm Clock is licensed under India PSL v1. You can use this software according to the terms and conditions of the India PSL v1. You may obtain a copy of India PSL v1 at: https://github.com/abirusabil123/discover/blob/main/IndiaPSL1 THIS SOFTWARE IS PROVIDED ON AN “AS IS” BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE. See the India PSL v1 for more details. */

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
                Utilities.setAlarm(context, hour, minute, repeatDays, false)
            }
        }
    }
}