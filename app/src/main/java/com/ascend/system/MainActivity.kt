package com.ascend.system

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ascend.system.auth.AuthManager
import com.ascend.system.core.UserProfile
import com.ascend.system.core.UserProgress
import com.ascend.system.data.FirebaseRepository
import com.ascend.system.ui.AppDestination
import com.ascend.system.ui.HomeScreen
import com.ascend.system.ui.LoginScreen
import com.ascend.system.ui.OnboardingScreen
import com.ascend.system.ui.RankScreen
import com.ascend.system.ui.WorkoutScreen
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val authManager = remember { AuthManager() }
            val repository = remember { FirebaseRepository() }
            val scope = rememberCoroutineScope()
            var currentDestination by remember { mutableStateOf(AppDestination.Login) }
            var currentUserId by remember { mutableStateOf(authManager.currentUserId()) }
            var profileState by remember { mutableStateOf<UserProfile?>(null) }
            var progressState by remember { mutableStateOf<UserProgress?>(null) }
            val webClientId = stringResource(id = R.string.google_web_client_id)

            val loginLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->
                scope.launch {
                    runCatching {
                        authManager.handleSignInResult(result.data)
                    }.onSuccess { uid ->
                        currentUserId = uid
                        val loadedProfile = repository.loadProfile(uid)
                        profileState = loadedProfile
                        progressState = repository.loadProgress(uid)
                        currentDestination = if (loadedProfile == null) {
                            AppDestination.Onboarding
                        } else {
                            AppDestination.Home
                        }
                    }
                }
            }

            LaunchedEffect(currentUserId) {
                if (currentUserId == null) {
                    currentDestination = AppDestination.Login
                } else {
                    val loadedProfile = repository.loadProfile(currentUserId!!)
                    profileState = loadedProfile
                    progressState = repository.loadProgress(currentUserId!!)
                    currentDestination = if (loadedProfile == null) {
                        AppDestination.Onboarding
                    } else {
                        AppDestination.Home
                    }
                }
            }

            Surface(color = MaterialTheme.colorScheme.background) {
                Box(modifier = Modifier.fillMaxSize()) {
                    when (currentDestination) {
                        AppDestination.Login -> {
                            LoginScreen(
                                onLogin = {
                                    val intent = authManager.signInIntent(this@MainActivity, webClientId)
                                    loginLauncher.launch(intent)
                                }
                            )
                        }
                        AppDestination.Onboarding -> {
                            OnboardingScreen(
                                onSave = { name, age, weight ->
                                    val uid = currentUserId ?: return@OnboardingScreen
                                    scope.launch {
                                        val profile = UserProfile(uid, name, age, weight)
                                        repository.saveProfile(profile)
                                        profileState = profile
                                        val progress = UserProgress(lastActiveDate = LocalDate.now())
                                        repository.saveProgress(uid, progress)
                                        progressState = progress
                                        currentDestination = AppDestination.Home
                                    }
                                }
                            )
                        }
                        AppDestination.Home -> {
                            HomeScreen(
                                profile = profileState,
                                progress = progressState,
                                onWorkout = { currentDestination = AppDestination.Workout },
                                onRank = { currentDestination = AppDestination.Rank }
                            )
                        }
                        AppDestination.Workout -> {
                            WorkoutScreen(
                                onBack = { currentDestination = AppDestination.Home },
                                onWorkoutSaved = { entry ->
                                    val uid = currentUserId ?: return@WorkoutScreen
                                    scope.launch {
                                        repository.saveWorkout(uid, entry)
                                    }
                                }
                            )
                        }
                        AppDestination.Rank -> {
                            RankScreen(
                                progress = progressState,
                                onBack = { currentDestination = AppDestination.Home }
                            )
                        }
                    }
                }
            }
        }
    }
}
