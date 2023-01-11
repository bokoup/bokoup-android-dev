package com.bokoup.customerapp.ui.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun OnboardingNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = OnboardingScreen.Welcome.name
    ) {
        composable(OnboardingScreen.Welcome.name) {
            OnboardingWelcomeContent(
                onGetStarted = { navController.navigate(OnboardingScreen.EnterPin.name) }
            )
        }

        composable(OnboardingScreen.EnterPin.name) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text("Coming soon: Enter Pin!")
            }
        }

        composable(OnboardingScreen.ConfirmPin.name) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text("Coming soon: Confirm Pin!")
            }
        }
    }
}