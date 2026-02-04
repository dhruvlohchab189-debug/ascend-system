package com.ascend.system.core

import java.time.Instant
import java.time.LocalDate

enum class RankTier {
    E, D, C, B, A, S
}

data class UserProfile(
    val userId: String,
    val displayName: String,
    val age: Int,
    val weightKg: Double
)

data class UserProgress(
    val level: Int = 1,
    val xp: Int = 0,
    val rank: RankTier = RankTier.E,
    val streakDays: Int = 0,
    val integrityScore: Int = 100,
    val lastActiveDate: LocalDate = LocalDate.now(),
    val xpMultiplier: Double = 1.0,
    val rankLocked: Boolean = false
)

data class WorkoutEntry(
    val exercise: ExerciseType,
    val reps: Int? = null,
    val durationSeconds: Int? = null,
    val distanceKm: Double? = null,
    val manualEntry: Boolean,
    val completedAt: Instant
)

data class RankTestResult(
    val attemptedRank: RankTier,
    val passed: Boolean,
    val completedAt: Instant,
    val metrics: Map<ExerciseType, WorkoutMetric>
)

enum class ExerciseType {
    PUSH_UPS,
    SQUATS,
    PLANK,
    RUN_WALK
}

data class WorkoutMetric(
    val reps: Int? = null,
    val durationSeconds: Int? = null,
    val distanceKm: Double? = null
)

data class ExerciseRequirement(
    val reps: Int? = null,
    val durationSeconds: Int? = null,
    val distanceKm: Double? = null
)

data class RankRequirements(
    val rank: RankTier,
    val streakMinimum: Int,
    val integrityMinimum: Int,
    val exercises: Map<ExerciseType, ExerciseRequirement>
)
