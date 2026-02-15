/* Copyright (c) 2025 Mohammad Sheraj *//* Math Alarm Clock is licensed under India PSL v1. You can use this software according to the terms and conditions of the India PSL v1. You may obtain a copy of India PSL v1 at: https://github.com/abirusabil123/discover/blob/main/IndiaPSL1 THIS SOFTWARE IS PROVIDED ON AN “AS IS” BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE. See the India PSL v1 for more details. */

package com.example.mathalarmclock.ui.theme

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mathalarmclock.R
import com.example.mathalarmclock.Utilities
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Preview
@Composable
fun AlarmScreen() {
    val context = LocalContext.current
    var hour by remember { mutableIntStateOf(12) }
    var minute by remember { mutableIntStateOf(0) }
    var isAlarmSet by remember { mutableStateOf(false) }
    var lastSetHour by remember { mutableIntStateOf(12) }
    var lastSetMinute by remember { mutableIntStateOf(0) }
    var lastSetTime by remember { mutableStateOf("Not set") }
    var repeatDays by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var lastSetRepeatDays by remember { mutableStateOf<Set<Int>>(emptySet()) }

    // Load saved alarm preferences when screen loads
    LaunchedEffect(Unit) {
        val prefs = AlarmPreferences(context)
        lastSetHour = prefs.getLastSetHour()
        lastSetMinute = prefs.getLastSetMinute()
        lastSetRepeatDays = prefs.getRepeatDays()  // Add this line
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
        repeatDays = lastSetRepeatDays  // Add this line
    }

    fun setAlarm() {
        Utilities.setAlarm(context, hour, minute, repeatDays)  // Add repeatDays here

        // Save alarm preferences
        val prefs = AlarmPreferences(context)
        prefs.saveAlarm(hour, minute, true, repeatDays)  // Add repeatDays here

        // Update UI state
        isAlarmSet = true
        lastSetHour = hour
        lastSetMinute = minute
        lastSetRepeatDays = repeatDays

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
        prefs.saveAlarm(lastSetHour, lastSetMinute, false, emptySet())  // Add emptySet()

        // Update UI state
        isAlarmSet = false
        repeatDays = emptySet()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(24.dp))

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
                        text = when {
                        lastSetRepeatDays.isEmpty() -> "Alarm will ring once"
                        lastSetRepeatDays.size == 7 -> "Alarm rings every day"
                        else -> {
                            val dayNames = mapOf(
                                1 to "Sun",
                                2 to "Mon",
                                3 to "Tue",
                                4 to "Wed",
                                5 to "Thu",
                                6 to "Fri",
                                7 to "Sat"
                            )
                            val days = lastSetRepeatDays.sorted().map { dayNames[it] }
                                .joinToString(", ")
                            "Rings on: $days"
                        }
                    },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 4.dp))
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

                // Add after minute selector, before the Set Alarm button
                Text(
                    text = "Repeat:",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                // Days in two rows (Monday first)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val days = listOf("Mon" to 2, "Tue" to 3, "Wed" to 4, "Thu" to 5)
                    days.forEach { (dayName, dayValue) ->
                        DaySelectionChip(
                            day = dayName, isSelected = repeatDays.contains(dayValue), onToggle = {
                                repeatDays = if (repeatDays.contains(dayValue)) {
                                    repeatDays - dayValue
                                } else {
                                    repeatDays + dayValue
                                }
                            })
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val days = listOf("Fri" to 6, "Sat" to 7, "Sun" to 1)
                    days.forEach { (dayName, dayValue) ->
                        DaySelectionChip(
                            day = dayName, isSelected = repeatDays.contains(dayValue), onToggle = {
                                repeatDays = if (repeatDays.contains(dayValue)) {
                                    repeatDays - dayValue
                                } else {
                                    repeatDays + dayValue
                                }
                            })
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Set Alarm Button
                Button(
                    onClick = { setAlarm() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !(isAlarmSet && hour == lastSetHour && minute == lastSetMinute && repeatDays == lastSetRepeatDays)
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
                                Locale.getDefault(), "%02d:%02d", hour, minute
                            )
                        }"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun DaySelectionChip(
    day: String, isSelected: Boolean, onToggle: () -> Unit
) {
    androidx.compose.material3.AssistChip(
        onClick = onToggle,
        label = { Text(day) },
        colors = androidx.compose.material3.AssistChipDefaults.assistChipColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    )
}