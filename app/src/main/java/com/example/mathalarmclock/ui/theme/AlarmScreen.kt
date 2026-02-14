package com.example.mathalarmclock.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mathalarmclock.R
import com.example.mathalarmclock.Utilities
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun AlarmScreen() {
    val context = LocalContext.current
    var hour by remember { mutableIntStateOf(12) }
    var minute by remember { mutableIntStateOf(0) }
    var isAlarmSet by remember { mutableStateOf(false) }
    var lastSetHour by remember { mutableIntStateOf(12) }
    var lastSetMinute by remember { mutableIntStateOf(0) }
    var lastSetTime by remember { mutableStateOf("Not set") }

    // Load saved alarm preferences when screen loads
    LaunchedEffect(Unit) {
        val prefs = AlarmPreferences(context)
        lastSetHour = prefs.getLastSetHour()
        lastSetMinute = prefs.getLastSetMinute()
        isAlarmSet = prefs.isAlarmSet()

        // Format last set time
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, lastSetHour)
            set(Calendar.MINUTE, lastSetMinute)
        }
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        lastSetTime = format.format(calendar.time)

        // Initialize current selection with last set time
        hour = lastSetHour
        minute = lastSetMinute
    }

    fun setAlarm() {
        Utilities.setAlarm(context, hour, minute)

        // Save alarm preferences
        val prefs = AlarmPreferences(context)
        prefs.saveAlarm(hour, minute, true)

        // Update UI state
        isAlarmSet = true
        lastSetHour = hour
        lastSetMinute = minute

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        lastSetTime = format.format(calendar.time)
    }

    fun cancelAlarm() {
        Utilities.cancelAlarm(context)

        // Update preferences
        val prefs = AlarmPreferences(context)
        prefs.saveAlarm(lastSetHour, lastSetMinute, false)

        // Update UI state
        isAlarmSet = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "Math Alarm Clock",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Current Alarm Status Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isAlarmSet) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = if (isAlarmSet) R.drawable.ic_alarm_on else R.drawable.ic_alarm_off),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = if (isAlarmSet) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.6f
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAlarmSet) "Alarm is Set" else "No Alarm Set",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isAlarmSet) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (isAlarmSet) {
                        Button(
                            onClick = { cancelAlarm() }, modifier = Modifier.height(36.dp)
                        ) {
                            Text("Cancel", fontSize = 14.sp)
                        }
                    }
                }

                if (isAlarmSet) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = lastSetTime,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 32.sp
                    )
                    Text(
                        text = "Alarm will ring at this time daily",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                } else {
                    Text(
                        text = "Set a new alarm below",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // Last Set Alarm Info (if different from current)
        if (!isAlarmSet && lastSetTime != "Not set") {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_history),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Last set: $lastSetTime",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Button(
                        onClick = {
                            hour = lastSetHour
                            minute = lastSetMinute
                        }, modifier = Modifier.height(32.dp)
                    ) {
                        Text("Use", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Time Selection Card
        Card(
            modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Set New Alarm Time",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Time display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = String.format(Locale.getDefault(), "%02d:%02d", hour, minute),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 48.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hour selector
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hour: ${String.format(Locale.getDefault(), "%02d", hour)}",
                        modifier = Modifier.width(80.dp),
                        fontWeight = FontWeight.Medium
                    )
                    Slider(
                        value = hour.toFloat(),
                        onValueChange = { hour = it.toInt() },
                        valueRange = 0f..23f,
                        modifier = Modifier.weight(1f),
                        steps = 23
                    )
                }

                // Minute selector
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Minute: ${String.format(Locale.getDefault(), "%02d", minute)}",
                        modifier = Modifier.width(80.dp),
                        fontWeight = FontWeight.Medium
                    )
                    Slider(
                        value = minute.toFloat(),
                        onValueChange = { minute = it.toInt() },
                        valueRange = 0f..59f,
                        modifier = Modifier.weight(1f),
                        steps = 59
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Set Alarm Button
                Button(
                    onClick = { setAlarm() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !(isAlarmSet && hour == lastSetHour && minute == lastSetMinute)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_alarm_set),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAlarmSet && hour == lastSetHour && minute == lastSetMinute) "Alarm Already Set"
                        else if (isAlarmSet) "Update Alarm to ${
                            String.format(
                                Locale.getDefault(), "%02d:%02d", hour, minute
                            )
                        }"
                        else "Set Alarm for ${
                            String.format(
                                Locale.getDefault(),
                                "%02d:%02d",
                                hour,
                                minute
                            )
                        }"
                    )
                }
            }
        }
    }
}