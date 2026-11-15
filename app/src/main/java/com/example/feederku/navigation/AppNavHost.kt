package com.example.feederku.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.feederku.views.auth.pages.LoginRoute
import com.example.feederku.views.auth.pages.SignUpRoute
import com.example.feederku.views.auth.pages.WelcomeScreen
import com.example.feederku.views.dashboard.DashboardRoute
import com.example.feederku.views.livestream.LiveStreamRoute
import com.example.feederku.views.map.pages.MapScreen
import com.example.feederku.views.map.pages.LocationDetailsScreen
import com.example.feederku.views.onboarding.OnBoardingRoute
import com.example.feederku.views.profile.ProfileScreen
import com.example.feederku.views.community.ChatRoute
import com.example.feederku.views.community.CommunityListScreen
import com.example.feederku.views.donation.*
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = if (FirebaseAuth.getInstance().currentUser != null) {
        Routes.Dashboard.route
    } else {
        Routes.OnBoarding.route
    }
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Routes.OnBoarding.route) {
            OnBoardingRoute(
                onFinish = {
                    navController.navigate(Routes.Welcome.route) {
                        popUpTo(Routes.OnBoarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Welcome.route){
            WelcomeScreen(
                onSignUpClick = {
                    navController.navigate(Routes.SignUp.route)
                },
                onLoginClick = {
                    navController.navigate(Routes.Login.route)
                }
            )
        }

        composable(Routes.SignUp.route) {
            SignUpRoute(
                onNavigateToLogin= {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Welcome.route) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onLoginClick = {
                    navController.navigate(Routes.Login.route){
                        popUpTo(Routes.SignUp.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Login.route) {
            LoginRoute(
                onLoginSuccess = { navController.goToDashboard() },
                onBackClick = { navController.goBack() },
                onSignUpClick = {
                    navController.navigate(Routes.SignUp.route){
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Routes.Dashboard.route){
            DashboardRoute(
                onProfileClick = {
                    navController.navigate(Routes.Profile.route)
                },
                onGoLiveClick = {
                    navController.navigate(Routes.LiveStreamFlow.route)
                },
                 onLiveStreamClick = { stream ->
                    navController.navigate(Routes.LiveStreamFlow.route)
                },
                onNavigateToMap = {
                    navController.navigate(Routes.Map.route)
                },
                onNavigateToDonation = {
                    navController.navigate(Routes.Donation.route)
                },
                onNavigateToChat = {
                    navController.navigate(Routes.CommunityList.route)
                }
            )
        }

        composable(Routes.Map.route) {
            MapScreen(
                onNavigateToDetails = { id: String ->
                    navController.navigate(Routes.LocationDetails.createRoute(id))
                },
                onNavigateToHome = {
                    navController.navigate(Routes.Dashboard.route) {
                        popUpTo(Routes.Dashboard.route) { inclusive = true }
                    }
                },
                onNavigateToDonation = {
                    navController.navigate(Routes.Donation.route)
                },
                onNavigateToChat = {
                    navController.navigate(Routes.CommunityList.route)
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.LocationDetails.route) { backStackEntry ->
            val locationId = backStackEntry.arguments?.getString("locationId") ?: ""
            LocationDetailsScreen(
                locationId = locationId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.Profile.route){
            ProfileScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSettingsClick = {

                }
            )
        }

        composable(Routes.LiveStreamFlow.route) {
            LiveStreamRoute(
                onBackToHome = {
                    navController.popBackStack(Routes.Dashboard.route, inclusive = false)
                }
            )
        }

        composable(Routes.CommunityList.route) {
            CommunityListScreen(
                onNavigateToChat = { groupId -> navController.navigate(Routes.Chat.createRoute(groupId)) },
                onNavigateToHome = { navController.navigate(Routes.Dashboard.route) },
                onNavigateToMap = { navController.navigate(Routes.Map.route) },
                onNavigateToDonation = { navController.navigate(Routes.Donation.route) }
            )
        }

        composable(Routes.Chat.route) {
            ChatRoute()
        }
        
        // --- Donation Flow ---
        
        composable(Routes.Donation.route) {
            DonationRoute(
                onNavigateToDetails = { id -> navController.navigate(Routes.DonationDetails.createRoute(id)) },
                onNavigateToRegister = { navController.navigate(Routes.DonationRegistration.route) },
                onNavigateToHome = { navController.navigate(Routes.Dashboard.route) },
                onNavigateToMap = { navController.navigate(Routes.Map.route) },
                onNavigateToChat = { navController.navigate(Routes.CommunityList.route) }
            )
        }

        composable(Routes.DonationDetails.route) { backStackEntry ->
            val campaignId = backStackEntry.arguments?.getString("campaignId") ?: ""
            val donationViewModel: DonationViewModel = viewModel(factory = DonationViewModel.Factory)
            DonationDetailsScreen(
                campaignId = campaignId,
                onBackClick = { navController.popBackStack() },
                onDonateClick = { id -> navController.navigate(Routes.DonationPaymentMethod.createRoute(id)) },
                onHomeClick = { navController.navigate(Routes.Dashboard.route) },
                onMapClick = { navController.navigate(Routes.Map.route) },
                onChatClick = { navController.navigate(Routes.CommunityList.route) },
                viewModel = donationViewModel
            )
        }

        composable(Routes.DonationPaymentMethod.route) { backStackEntry ->
            val campaignId = backStackEntry.arguments?.getString("campaignId") ?: ""
            val donationViewModel: DonationViewModel = viewModel(factory = DonationViewModel.Factory)
            DonationPaymentMethodScreen(
                campaignId = campaignId,
                onBackClick = { navController.popBackStack() },
                onContinueClick = { method -> 
                    if (method == "Credit/Debit Card") {
                        navController.navigate(Routes.DonationCardDetails.createRoute(campaignId))
                    } else {
                        navController.navigate(Routes.DonationPayment.createRoute(campaignId))
                    }
                },
                viewModel = donationViewModel
            )
        }

        composable(Routes.DonationCardDetails.route) { backStackEntry ->
            val campaignId = backStackEntry.arguments?.getString("campaignId") ?: ""
            val donationViewModel: DonationViewModel = viewModel(factory = DonationViewModel.Factory)
            DonationCardDetailsScreen(
                campaignId = campaignId,
                onBackClick = { navController.popBackStack() },
                onContinueClick = { navController.navigate(Routes.DonationPayment.createRoute(campaignId)) },
                viewModel = donationViewModel
            )
        }

        composable(Routes.DonationPayment.route) { backStackEntry ->
            val campaignId = backStackEntry.arguments?.getString("campaignId") ?: ""
            val donationViewModel: DonationViewModel = viewModel(factory = DonationViewModel.Factory)
            DonationPaymentScreen(
                campaignId = campaignId,
                onBackClick = { navController.popBackStack() },
                onConfirmClick = { navController.navigate(Routes.DonationProcessing.route) },
                viewModel = donationViewModel
            )
        }

        composable(Routes.DonationProcessing.route) {
            DonationProcessingScreen(
                onProcessingFinished = { navController.navigate(Routes.DonationSuccess.createRoute("donation")) }
            )
        }

        composable(Routes.DonationRegistration.route) {
            val donationViewModel: DonationViewModel = viewModel(factory = DonationViewModel.Factory)
            DonationRegistrationScreen(
                viewModel = donationViewModel,
                onBackClick = { navController.popBackStack() },
                onSuccess = { navController.navigate(Routes.NewCampaignPost.route) }
            )
        }

        composable(Routes.NewCampaignPost.route) {
            NewCampaignPostScreen(
                onBackClick = { navController.popBackStack() },
                onPublishClick = { navController.navigate(Routes.DonationSuccess.createRoute("campaign")) }
            )
        }

        composable(Routes.DonationSuccess.route) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "donation"
            DonationSuccessScreen(
                type = type,
                onBackToCampaigns = {
                    navController.navigate(Routes.Donation.route) {
                        popUpTo(Routes.Donation.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

private fun NavHostController.goToDashboard() {
    navigate(Routes.Dashboard.route) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}

private fun NavHostController.goBack() {
    if (previousBackStackEntry != null) popBackStack()
}
