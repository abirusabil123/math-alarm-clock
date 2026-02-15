/* Copyright (c) 2025 Mohammad Sheraj *//* Math Alarm Clock is licensed under India PSL v1. You can use this software according to the terms and conditions of the India PSL v1. You may obtain a copy of India PSL v1 at: https://github.com/abirusabil123/discover/blob/main/IndiaPSL1 THIS SOFTWARE IS PROVIDED ON AN “AS IS” BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE. See the India PSL v1 for more details. */

package com.example.mathalarmclock.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

@Composable
fun MathPuzzleScreen(onCorrectAnswer: () -> Unit) {
    var message by remember { mutableStateOf("") }
    var question by remember { mutableStateOf("") }
    var correctAnswer by remember { mutableIntStateOf(0) }
    var options by remember { mutableStateOf(listOf<Int>()) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }

    // Define the question generation function inside the composable
    fun generateNewQuestion() {
        val operation = Random.nextInt(4) // 0: +, 1: -, 2: *, 3: /

        when (operation) {
            0 -> { // Addition with larger numbers
                val num1 = Random.nextInt(10, 50)
                val num2 = Random.nextInt(10, 50)
                question = "$num1 + $num2"
                correctAnswer = num1 + num2
            }

            1 -> { // Subtraction with positive result
                val num1 = Random.nextInt(20, 100)
                val num2 = Random.nextInt(1, num1 - 10)
                question = "$num1 - $num2"
                correctAnswer = num1 - num2
            }

            2 -> { // Multiplication
                val num1 = Random.nextInt(5, 15)
                val num2 = Random.nextInt(5, 15)
                question = "$num1 × $num2"
                correctAnswer = num1 * num2
            }

            3 -> { // Division with integer result
                val divisor = Random.nextInt(2, 12)
                val quotient = Random.nextInt(2, 20)
                val dividend = divisor * quotient
                question = "$dividend ÷ $divisor"
                correctAnswer = quotient
            }
        }

        // Generate 3 wrong options and 1 correct
        val wrongOptions = mutableSetOf<Int>()
        while (wrongOptions.size < 3) {
            val offset = when (Random.nextInt(3)) {
                0 -> Random.nextInt(5, 20) * (if (Random.nextBoolean()) 1 else -1)
                1 -> Random.nextInt(1, 10) * (if (Random.nextBoolean()) 1 else -1)
                else -> Random.nextInt(2, 8) * (if (Random.nextBoolean()) 2 else -2)
            }

            val wrongOption = correctAnswer + offset
            // Ensure wrong option is positive, not equal to correct answer, and not already in set
            if (wrongOption > 0 && wrongOption != correctAnswer) {
                wrongOptions.add(wrongOption)
            }
        }

        // Combine correct and wrong options and shuffle
        options = (wrongOptions + correctAnswer).shuffled()
    }

    // Generate new question when screen is first shown
    LaunchedEffect(Unit) {
        generateNewQuestion()
    }

    fun checkAnswer(answer: Int) {
        selectedOption = answer
        if (answer == correctAnswer) {
            message = "✓ Correct! Well done!"
            onCorrectAnswer()
        } else {
            message = "✗ Try again!"
            // Generate new question after wrong answer
            generateNewQuestion()
            selectedOption = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "WAKE UP!",
            fontSize = 32.sp,
            color = Color.Red,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Solve to stop alarm",
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Text(
            text = question, fontSize = 48.sp, modifier = Modifier.padding(bottom = 32.dp)
        )

        // Display options in a 2x2 grid
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()
        ) {
            // First row - 2 buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                options.take(2).forEach { option ->
                    Button(
                        onClick = { checkAnswer(option) },
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp),
                        enabled = selectedOption == null
                    ) {
                        Text(option.toString(), fontSize = 24.sp)
                    }
                }
            }

            // Second row - remaining 2 buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                options.drop(2).forEach { option ->
                    Button(
                        onClick = { checkAnswer(option) },
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp),
                        enabled = selectedOption == null
                    ) {
                        Text(option.toString(), fontSize = 24.sp)
                    }
                }
            }
        }

        if (message.isNotEmpty()) {
            Text(
                text = message,
                modifier = Modifier.padding(top = 24.dp),
                color = if (message.startsWith("✓")) Color.Green
                else Color.Red,
                fontSize = 18.sp
            )
        }
    }
}