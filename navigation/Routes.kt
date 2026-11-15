package com.example.feederku.navigation

//buat view model dan kumpulin uistate
sealed class Routes(val route: String){
    data object OnBoarding : Routes("onboarding")
    data object Welcome : Routes("welcome")
    data object SignUp : Routes("signup")
    data object Login : Routes("login")
    data object Dashboard : Routes("dashboard")
    data object Profile : Routes("profile")
    data object LiveStreamFlow: Routes("livestream_flow")
    data object Map : Routes("map")
    data object Donation : Routes("donation")
    data object Chat : Routes("chat")
    data object LocationDetails : Routes("location_details/{locationId}") {
        fun createRoute(locationId: String) = "location_details/$locationId"
    }
}
