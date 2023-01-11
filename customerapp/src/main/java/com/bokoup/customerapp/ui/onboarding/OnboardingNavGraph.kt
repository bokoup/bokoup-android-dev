package com.bokoup.customerapp.ui.onboarding

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

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
                    navController.navigate("${OnboardingScreen.ConfirmPin.name}?pinToConfirm=$pinToConfirm")
                }
            )
        }

        composable(
            route = "${OnboardingScreen.ConfirmPin.name}?pinToConfirm={pinToConfirm}",
            arguments = listOf(
                navArgument("pinToConfirm") { type = NavType.StringType; nullable = false }
            )
        ) {
            OnboardingConfirmPinContent(
                pinToConfirm = checkNotNull(it.arguments?.getString("pinToConfirm")),
                onNavigateBack = { navController.navigateUp() },
                onCreatePinConfirmed = {}
            )
        }
    }
}