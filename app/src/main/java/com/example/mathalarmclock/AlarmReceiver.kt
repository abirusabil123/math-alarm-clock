package com.example.mathalarmclock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Use setContentView to show activity instead of wake lock
        val mathIntent = Intent(context, MathActivity::class.java)
        mathIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(mathIntent)
    }
}