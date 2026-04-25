/* Copyright (c) 2025 Mohammad Sheraj *//* Math Alarm Clock is licensed under India PSL v1. You can use this software according to the terms and conditions of the India PSL v1. You may obtain a copy of India PSL v1 at: https://github.com/abirusabil123/discover/blob/main/IndiaPSL1 THIS SOFTWARE IS PROVIDED ON AN “AS IS” BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE. See the India PSL v1 for more details. */

package com.example.mathalarmclock.ui.theme

import android.content.Context
import android.content.SharedPreferences


private const val KEY_LAST_SET_HOUR = "last_set_hour"
private const val KEY_LAST_SET_MINUTE = "last_set_minute"
private const val KEY_IS_ALARM_SET = "is_alarm_set"
private const val KEY_REPEAT_DAYS = "repeat_days"
private const val KEY_CURRENT_HOUR = "current_hour"
private const val KEY_CURRENT_MINUTE = "current_minute"
private const val KEY_CURRENT_DAYS = "current_days"
private const val DEFAULT_HOUR = 12
private const val DEFAULT_MINUTE = 0

class AlarmPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)

    fun saveAlarm(hour: Int, minute: Int, isSet: Boolean, repeatDays: Set<Int> = emptySet()) {
        android.util.Log.d("AlarmPreferences", "Saving days: $repeatDays")
        prefs.edit().apply {
            putInt(KEY_LAST_SET_HOUR, hour)
            putInt(KEY_LAST_SET_MINUTE, minute)
            putBoolean(KEY_IS_ALARM_SET, isSet)
            putStringSet(KEY_REPEAT_DAYS, repeatDays.map { it.toString() }.toSet())
            apply()
        }
    }

    fun saveCurrentSelection(hour: Int, minute: Int, days: Set<Int>) {
        prefs.edit().apply {
            putInt(KEY_CURRENT_HOUR, hour)
            putInt(KEY_CURRENT_MINUTE, minute)
            putStringSet(KEY_CURRENT_DAYS, days.map { it.toString() }.toSet())
            apply()
        }
    }

    fun getCurrentHour(): Int = prefs.getInt(KEY_CURRENT_HOUR, DEFAULT_HOUR)
    fun getCurrentMinute(): Int = prefs.getInt(KEY_CURRENT_MINUTE, DEFAULT_MINUTE)
    fun getCurrentDays(): Set<Int> {
        val stringSet = prefs.getStringSet(KEY_CURRENT_DAYS, emptySet()) ?: emptySet()
        return stringSet.mapNotNull { it.toIntOrNull() }.toSet()
    }

    fun getLastSetHour(): Int = prefs.getInt(KEY_LAST_SET_HOUR, DEFAULT_HOUR)
    fun getLastSetMinute(): Int = prefs.getInt(KEY_LAST_SET_MINUTE, DEFAULT_MINUTE)
    fun isAlarmSet(): Boolean = prefs.getBoolean(KEY_IS_ALARM_SET, false)
    fun getRepeatDays(): Set<Int> {
        val stringSet = prefs.getStringSet(KEY_REPEAT_DAYS, emptySet()) ?: emptySet()
        val result = stringSet.mapNotNull { it.toIntOrNull() }.toSet()
        android.util.Log.d("AlarmPreferences", "Loading days: $result from $stringSet")
        return result
    }
}