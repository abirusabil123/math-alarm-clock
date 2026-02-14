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
        private const val DEFAULT_HOUR = 12
        private const val DEFAULT_MINUTE = 0
    }

    fun saveAlarm(hour: Int, minute: Int, isSet: Boolean) {
        prefs.edit().apply {
            putInt(KEY_LAST_SET_HOUR, hour)
            putInt(KEY_LAST_SET_MINUTE, minute)
            putBoolean(KEY_IS_ALARM_SET, isSet)
            apply()
        }
    }

    fun getLastSetHour(): Int = prefs.getInt(KEY_LAST_SET_HOUR, DEFAULT_HOUR)

    fun getLastSetMinute(): Int = prefs.getInt(KEY_LAST_SET_MINUTE, DEFAULT_MINUTE)

    fun isAlarmSet(): Boolean = prefs.getBoolean(KEY_IS_ALARM_SET, false)
}