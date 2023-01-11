package com.bokoup.customerapp.ui.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@ExperimentalMaterial3Api
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
            OnboardingEnterPinContent(
                onNavigateBack = { navController.navigateUp() },
                onConfirmPinClicked = { pinToConfirm ->
                    navController.navigate(OnboardingScreen.ConfirmPin.name)
                }
            )
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