package com.ascend.system.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingScreen(onSave: (String, Int, Double) -> Unit) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Onboarding", style = MaterialTheme.typography.headlineSmall)
        Text(
            text = "Required before access. Data is used for safety and workout logic only.",
            style = MaterialTheme.typography.bodyMedium
        )
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name or nickname") }
        )
        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Age") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Weight (kg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        error?.let { message ->
            Text(text = message, color = MaterialTheme.colorScheme.error)
        }
        Button(onClick = {
            val ageValue = age.toIntOrNull()
            val weightValue = weight.toDoubleOrNull()
            if (name.isBlank()) {
                error = "Name is required."
                return@Button
            }
            if (ageValue == null || ageValue !in 13..80) {
                error = "Age must be between 13 and 80."
                return@Button
            }
            if (weightValue == null || weightValue !in 30.0..200.0) {
                error = "Weight must be between 30kg and 200kg."
                return@Button
            }
            error = null
            onSave(name.trim(), ageValue, weightValue)
        }) {
            Text(text = "Save and Continue")
        }
    }
}
