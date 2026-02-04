package com.ascend.system.data

import com.ascend.system.core.RankTestResult
import com.ascend.system.core.UserProfile
import com.ascend.system.core.UserProgress
import com.ascend.system.core.WorkoutEntry
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun userDocument(userId: String) = firestore.collection("users").document(userId)

    suspend fun saveProfile(profile: UserProfile) {
        userDocument(profile.userId).set(profile).await()
    }

    suspend fun saveProgress(userId: String, progress: UserProgress) {
        userDocument(userId).collection("progress").document("current").set(progress).await()
    }

    suspend fun saveWorkout(userId: String, workout: WorkoutEntry) {
        userDocument(userId).collection("workouts").add(workout).await()
    }

    suspend fun saveRankTest(userId: String, test: RankTestResult) {
        userDocument(userId).collection("rankTests").add(test).await()
    }

    suspend fun loadProfile(userId: String): UserProfile? {
        val snapshot = userDocument(userId).get().await()
        return snapshot.toObject(UserProfile::class.java)
    }

    suspend fun loadProgress(userId: String): UserProgress? {
        val snapshot = userDocument(userId).collection("progress").document("current").get().await()
        return snapshot.toObject(UserProgress::class.java)
    }
}
