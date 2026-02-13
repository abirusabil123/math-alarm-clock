package com.example.mathalarmclock

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mathalarmclock.ui.theme.MathAlarmClockTheme
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MathAlarmClockTheme {
                AlarmScreen()
            }
        }
    }
}

@Composable
fun AlarmScreen() {
    val context = LocalContext.current
    var hour by remember { mutableIntStateOf(12) }
    var minute by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Set Alarm Time",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Hour selector
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Hour: $hour", modifier = Modifier.width(80.dp))
            Slider(
                value = hour.toFloat(),
                onValueChange = { hour = it.toInt() },
                valueRange = 0f..23f,
                modifier = Modifier.weight(1f)
            )
        }

        // Minute selector
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Minute: $minute", modifier = Modifier.width(80.dp))
            Slider(
                value = minute.toFloat(),
                onValueChange = { minute = it.toInt() },
                valueRange = 0f..59f,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { setAlarm(context, hour, minute) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Set Alarm for $hour:$minute")
        }
    }
}

private fun setAlarm(context: Context, hour: Int, minute: Int) {
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
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            Toast.makeText(context, "Alarm set for $hour:$minute", Toast.LENGTH_LONG).show()
        } else {
            // Request permission
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            context.startActivity(intent)
            Toast.makeText(context, "Please grant exact alarm permission", Toast.LENGTH_LONG).show()
        }
    } else {
        // For older Android versions
        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            Toast.makeText(context, "Alarm set for $hour:$minute", Toast.LENGTH_LONG).show()
        } catch (e: SecurityException) {
            Toast.makeText(context, "Cannot set exact alarm", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
}