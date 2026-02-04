package com.ascend.system.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ascend.system.core.ExerciseType
import com.ascend.system.core.WorkoutEntry
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(onBack: () -> Unit, onWorkoutSaved: (WorkoutEntry) -> Unit) {
    var exercise by remember { mutableStateOf(ExerciseType.PUSH_UPS) }
    var reps by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var distance by remember { mutableStateOf("") }
    var manualEntry by remember { mutableStateOf(false) }
    var timerRunning by remember { mutableStateOf(false) }
    var timerSeconds by remember { mutableStateOf(0) }

    LaunchedEffect(timerRunning) {
        while (timerRunning) {
            kotlinx.coroutines.delay(1000)
            timerSeconds += 1
        }
    }

    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Workout", style = MaterialTheme.typography.headlineSmall)
        Text(text = "Timers and validation reduce fake completion. Manual entry is tracked.")

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { exercise = ExerciseType.PUSH_UPS }) { Text("Push-ups") }
            Button(onClick = { exercise = ExerciseType.SQUATS }) { Text("Squats") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { exercise = ExerciseType.PLANK }) { Text("Plank") }
            Button(onClick = { exercise = ExerciseType.RUN_WALK }) { Text("Run/Walk") }
        }

        when (exercise) {
            ExerciseType.PUSH_UPS, ExerciseType.SQUATS -> {
                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it },
                    label = { Text("Reps") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            ExerciseType.PLANK -> {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { timerRunning = true }) { Text("Start Timer") }
                    Button(onClick = { timerRunning = false }) { Text("Stop Timer") }
                    Text(text = "${timerSeconds}s")
                }
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Duration (sec)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            ExerciseType.RUN_WALK -> {
                OutlinedTextField(
                    value = distance,
                    onValueChange = { distance = it },
                    label = { Text("Distance (km)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Duration (sec)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = manualEntry, onCheckedChange = { manualEntry = it })
            Text(text = "Manual entry")
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = {
                val entry = WorkoutEntry(
                    exercise = exercise,
                    reps = reps.toIntOrNull(),
                    durationSeconds = duration.toIntOrNull() ?: timerSeconds,
                    distanceKm = distance.toDoubleOrNull(),
                    manualEntry = manualEntry,
                    completedAt = Instant.now()
                )
                onWorkoutSaved(entry)
                reps = ""
                duration = ""
                distance = ""
                timerSeconds = 0
                timerRunning = false
                manualEntry = false
            }) {
                Text(text = "Save Workout")
            }
            TextButton(onClick = onBack) { Text("Back") }
        }
    }
}
