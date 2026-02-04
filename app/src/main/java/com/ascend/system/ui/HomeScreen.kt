package com.ascend.system.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ascend.system.core.UserProfile
import com.ascend.system.core.UserProgress

@Composable
fun HomeScreen(
    profile: UserProfile?,
    progress: UserProgress?,
    onWorkout: () -> Unit,
    onRank: () -> Unit
) {
    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "System Status", style = MaterialTheme.typography.headlineSmall)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Name: ${profile?.displayName ?: "--"}")
                Text(text = "Level: ${progress?.level ?: 1}")
                Text(text = "XP: ${progress?.xp ?: 0}")
                Text(text = "Rank: ${progress?.rank ?: "E"}")
                Text(text = "Streak: ${progress?.streakDays ?: 0} days")
                Text(text = "Integrity: ${progress?.integrityScore ?: 100}")
            }
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Stats", style = MaterialTheme.typography.titleMedium)
                Text(text = "Strength: derived from push-ups/squats performance")
                Text(text = "Stamina: derived from plank and run metrics")
                Text(text = "Agility: derived from run pacing consistency")
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onWorkout) {
                Text(text = "Log Workout")
            }
            Button(onClick = onRank) {
                Text(text = "Rank Status")
            }
        }
    }
}
