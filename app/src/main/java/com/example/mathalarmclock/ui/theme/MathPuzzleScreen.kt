package com.example.mathalarmclock.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MathPuzzleScreen(onCorrectAnswer: () -> Unit) {
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "WAKE UP!",
            fontSize = 32.sp,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Text(
            text = "2 + 2 = ?", fontSize = 48.sp, modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = {
                message = "Correct! Alarm stopped."
                onCorrectAnswer()
            }, modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Text("4", fontSize = 24.sp)
        }

        if (message.isNotEmpty()) {
            Text(
                text = message,
                modifier = Modifier.padding(top = 16.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}