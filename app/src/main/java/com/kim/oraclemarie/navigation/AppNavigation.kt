package com.kim.oraclemarie.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.ui.platform.LocalContext
import com.kim.oraclemarie.data.SettingsPreferences
import com.kim.oraclemarie.ui.card.CardDetailScreen
import com.kim.oraclemarie.ui.gallery.GalleryScreen
import com.kim.oraclemarie.ui.home.HomeScreen
import com.kim.oraclemarie.ui.journal.JournalScreen
import com.kim.oraclemarie.ui.onboarding.OnboardingScreen
import com.kim.oraclemarie.ui.paroles.ParolesMarieScreen
import com.kim.oraclemarie.ui.portal.PortalScreen
import com.kim.oraclemarie.ui.prayer.PrayerScreen
import com.kim.oraclemarie.ui.settings.SettingsScreen
import com.kim.oraclemarie.ui.troiscartes.TroisCartesScreen
import com.kim.oraclemarie.ui.theme.DarkNavy

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val startDestination = if (SettingsPreferences(context).onboardingDone) "portal" else "onboarding"

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
    ) {
        composable("onboarding") {
            OnboardingScreen(onDone = {
                SettingsPreferences(context).onboardingDone = true
                navController.navigate("portal") {
                    popUpTo("onboarding") { inclusive = true }
                }
            })
        }
        composable("portal") {
            PortalScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable("home") {
            HomeScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToCardDetail = { cardId, tab -> navController.navigate("card_detail/$cardId?tab=$tab") }
            )
        }
        composable(
            route = "card_detail/{cardId}?tab={initialTab}",
            arguments = listOf(
                navArgument("cardId") { type = NavType.IntType },
                navArgument("initialTab") { type = NavType.IntType; defaultValue = 0 }
            )
        ) { backStackEntry ->
            val cardId = backStackEntry.arguments?.getInt("cardId") ?: return@composable
            val initialTab = backStackEntry.arguments?.getInt("initialTab") ?: 0
            CardDetailScreen(
                cardId = cardId,
                initialTab = initialTab,
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable("gallery") {
            GalleryScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToCardDetail = { cardId -> navController.navigate("card_detail/$cardId") }
            )
        }
        composable("paroles") {
            ParolesMarieScreen(onNavigateBack = { navController.navigateUp() })
        }
        composable("prayers") {
            PrayerScreen(onNavigateBack = { navController.navigateUp() })
        }
        composable("journal") {
            JournalScreen(onNavigateBack = { navController.navigateUp() })
        }
        composable("trois_cartes") {
            TroisCartesScreen(onNavigateBack = { navController.navigateUp() })
        }
        composable("settings") {
            SettingsScreen(onNavigateBack = { navController.navigateUp() })
        }
    }
}
