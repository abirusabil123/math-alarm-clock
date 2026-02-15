/* Copyright (c) 2025 Mohammad Sheraj *//* Math Alarm Clock is licensed under India PSL v1. You can use this software according to the terms and conditions of the India PSL v1. You may obtain a copy of India PSL v1 at: https://github.com/abirusabil123/discover/blob/main/IndiaPSL1 THIS SOFTWARE IS PROVIDED ON AN “AS IS” BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE. See the India PSL v1 for more details. */

package com.example.mathalarmclock.ui.theme

import android.content.Context
import android.content.SharedPreferences

class AlarmPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_LAST_SET_HOUR = "last_set_hour"
        private const val KEY_LAST_SET_MINUTE = "last_set_minute"
        private const val KEY_IS_ALARM_SET = "is_alarm_set"
        private const val KEY_REPEAT_DAYS = "repeat_days"
        private const val DEFAULT_HOUR = 12
        private const val DEFAULT_MINUTE = 0
    }

    fun saveAlarm(hour: Int, minute: Int, isSet: Boolean, repeatDays: Set<Int> = emptySet()) {
        prefs.edit().apply {
            putInt(KEY_LAST_SET_HOUR, hour)
            putInt(KEY_LAST_SET_MINUTE, minute)
            putBoolean(KEY_IS_ALARM_SET, isSet)
            putStringSet(KEY_REPEAT_DAYS, repeatDays.map { it.toString() }.toSet())
            apply()
        }
    }

    fun getLastSetHour(): Int = prefs.getInt(KEY_LAST_SET_HOUR, DEFAULT_HOUR)
    fun getLastSetMinute(): Int = prefs.getInt(KEY_LAST_SET_MINUTE, DEFAULT_MINUTE)
    fun isAlarmSet(): Boolean = prefs.getBoolean(KEY_IS_ALARM_SET, false)
    fun getRepeatDays(): Set<Int> {
        return prefs.getStringSet(KEY_REPEAT_DAYS, emptySet())?.mapNotNull { it.toIntOrNull() }
            ?.toSet() ?: emptySet()
    }
}