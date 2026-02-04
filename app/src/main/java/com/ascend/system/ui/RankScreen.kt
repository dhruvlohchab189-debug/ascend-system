package com.ascend.system.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ascend.system.core.RankRulebook
import com.ascend.system.core.ExerciseRequirement
import com.ascend.system.core.UserProgress

@Composable
fun RankScreen(progress: UserProgress?, onBack: () -> Unit) {
    val currentRank = progress?.rank
    val nextRank = RankRulebook.requirements.firstOrNull { it.rank.ordinal == (currentRank?.ordinal ?: 0) + 1 }

    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Rank Status", style = MaterialTheme.typography.headlineSmall)
        Text(text = "Current Rank: ${currentRank ?: "E"}")

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Next Rank Requirements", style = MaterialTheme.typography.titleMedium)
                if (nextRank == null) {
                    Text(text = "Top rank achieved. Maintenance required.")
                } else {
                    Text(text = "Streak: ${nextRank.streakMinimum} days")
                    Text(text = "Integrity: ${nextRank.integrityMinimum} minimum")
                    nextRank.exercises.forEach { (exercise, requirement) ->
                        Text(text = "${exercise.name}: ${formatRequirement(requirement)}")
                    }
                }
            }
        }

        Button(onClick = { /* rank evaluation flow in v1 */ }) {
            Text(text = "Start Rank Evaluation")
        }
        TextButton(onClick = onBack) { Text("Back") }
    }
}

private fun formatRequirement(requirement: ExerciseRequirement): String {
    val parts = listOfNotNull(
        requirement.reps?.let { "$it reps" },
        requirement.durationSeconds?.let { "$it sec" },
        requirement.distanceKm?.let { "$it km" }
    )
    return parts.joinToString(separator = ", ")
}
