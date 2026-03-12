package com.example.mathalarmclock

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object AlarmState {
    var isPlaying by mutableStateOf(false)
}