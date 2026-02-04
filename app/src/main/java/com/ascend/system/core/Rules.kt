package com.ascend.system.core

import java.time.Duration
import java.time.LocalDate

object RankRulebook {
    val requirements: List<RankRequirements> = listOf(
        RankRequirements(
            rank = RankTier.E,
            streakMinimum = 0,
            integrityMinimum = 50,
            exercises = mapOf(
                ExerciseType.PUSH_UPS to ExerciseRequirement(reps = 10),
                ExerciseType.SQUATS to ExerciseRequirement(reps = 20),
                ExerciseType.PLANK to ExerciseRequirement(durationSeconds = 30),
                ExerciseType.RUN_WALK to ExerciseRequirement(durationSeconds = 300)
            )
        ),
        RankRequirements(
            rank = RankTier.D,
            streakMinimum = 3,
            integrityMinimum = 60,
            exercises = mapOf(
                ExerciseType.PUSH_UPS to ExerciseRequirement(reps = 20),
                ExerciseType.SQUATS to ExerciseRequirement(reps = 40),
                ExerciseType.PLANK to ExerciseRequirement(durationSeconds = 60),
                ExerciseType.RUN_WALK to ExerciseRequirement(distanceKm = 1.0)
            )
        ),
        RankRequirements(
            rank = RankTier.C,
            streakMinimum = 5,
            integrityMinimum = 70,
            exercises = mapOf(
                ExerciseType.PUSH_UPS to ExerciseRequirement(reps = 30),
                ExerciseType.SQUATS to ExerciseRequirement(reps = 60),
                ExerciseType.PLANK to ExerciseRequirement(durationSeconds = 120),
                ExerciseType.RUN_WALK to ExerciseRequirement(distanceKm = 2.0)
            )
        ),
        RankRequirements(
            rank = RankTier.B,
            streakMinimum = 7,
            integrityMinimum = 80,
            exercises = mapOf(
                ExerciseType.PUSH_UPS to ExerciseRequirement(reps = 40),
                ExerciseType.SQUATS to ExerciseRequirement(reps = 100),
                ExerciseType.PLANK to ExerciseRequirement(durationSeconds = 180),
                ExerciseType.RUN_WALK to ExerciseRequirement(distanceKm = 3.0)
            )
        ),
        RankRequirements(
            rank = RankTier.A,
            streakMinimum = 10,
            integrityMinimum = 85,
            exercises = mapOf(
                ExerciseType.PUSH_UPS to ExerciseRequirement(reps = 50),
                ExerciseType.SQUATS to ExerciseRequirement(reps = 150),
                ExerciseType.PLANK to ExerciseRequirement(durationSeconds = 240),
                ExerciseType.RUN_WALK to ExerciseRequirement(distanceKm = 5.0)
            )
        ),
        RankRequirements(
            rank = RankTier.S,
            streakMinimum = 14,
            integrityMinimum = 90,
            exercises = mapOf(
                ExerciseType.PUSH_UPS to ExerciseRequirement(reps = 70),
                ExerciseType.SQUATS to ExerciseRequirement(reps = 200),
                ExerciseType.PLANK to ExerciseRequirement(durationSeconds = 300),
                ExerciseType.RUN_WALK to ExerciseRequirement(distanceKm = 8.0)
            )
        )
    )
}

object XpRules {
    fun xpForWorkout(entry: WorkoutEntry): Int {
        val base = when (entry.exercise) {
            ExerciseType.PUSH_UPS -> (entry.reps ?: 0) * 5
            ExerciseType.SQUATS -> (entry.reps ?: 0) * 3
            ExerciseType.PLANK -> ((entry.durationSeconds ?: 0) / 10) * 4
            ExerciseType.RUN_WALK -> ((entry.distanceKm ?: 0.0) * 50).toInt()
        }
        return base.coerceAtLeast(5)
    }

    fun levelForXp(xp: Int): Int = (xp / 500) + 1
}

object IntegrityRules {
    fun adjustIntegrity(
        currentScore: Int,
        workout: WorkoutEntry,
        elapsedSeconds: Int,
        validationFailed: Boolean
    ): Int {
        var score = currentScore
        if (validationFailed) {
            score -= 10
        }
        if (workout.manualEntry) {
            score -= 5
        }
        if (elapsedSeconds < 10) {
            score -= 10
        }
        return score.coerceIn(0, 100)
    }
}

data class InactivityPenalty(
    val streakReset: Boolean,
    val xpMultiplierRemoved: Boolean,
    val xpReductionPercent: Int,
    val rankDowngrade: Int,
    val integrityLoss: Int,
    val lockRankUpgrades: Boolean
)

object InactivityRules {
    fun evaluate(lastActive: LocalDate, today: LocalDate): InactivityPenalty {
        val days = Duration.between(lastActive.atStartOfDay(), today.atStartOfDay()).toDays()
        return when {
            days >= 30 -> InactivityPenalty(
                streakReset = true,
                xpMultiplierRemoved = true,
                xpReductionPercent = 70,
                rankDowngrade = 5,
                integrityLoss = 30,
                lockRankUpgrades = true
            )
            days >= 14 -> InactivityPenalty(
                streakReset = true,
                xpMultiplierRemoved = true,
                xpReductionPercent = 50,
                rankDowngrade = 3,
                integrityLoss = 20,
                lockRankUpgrades = true
            )
            days >= 7 -> InactivityPenalty(
                streakReset = true,
                xpMultiplierRemoved = true,
                xpReductionPercent = 30,
                rankDowngrade = 1,
                integrityLoss = 15,
                lockRankUpgrades = true
            )
            days >= 5 -> InactivityPenalty(
                streakReset = false,
                xpMultiplierRemoved = false,
                xpReductionPercent = 20,
                rankDowngrade = 0,
                integrityLoss = 10,
                lockRankUpgrades = true
            )
            days >= 3 -> InactivityPenalty(
                streakReset = true,
                xpMultiplierRemoved = true,
                xpReductionPercent = 0,
                rankDowngrade = 0,
                integrityLoss = 5,
                lockRankUpgrades = false
            )
            days >= 2 -> InactivityPenalty(
                streakReset = false,
                xpMultiplierRemoved = false,
                xpReductionPercent = 0,
                rankDowngrade = 0,
                integrityLoss = 0,
                lockRankUpgrades = false
            )
            else -> InactivityPenalty(
                streakReset = false,
                xpMultiplierRemoved = false,
                xpReductionPercent = 0,
                rankDowngrade = 0,
                integrityLoss = 0,
                lockRankUpgrades = false
            )
        }
    }
}
