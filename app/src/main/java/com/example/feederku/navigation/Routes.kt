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
    data object DonationDetails : Routes("donation_details/{campaignId}") {
        fun createRoute(campaignId: String) = "donation_details/$campaignId"
    }
    data object DonationRegistration : Routes("donation_registration")
    data object DonationPaymentMethod : Routes("donation_payment_method/{campaignId}") {
        fun createRoute(campaignId: String) = "donation_payment_method/$campaignId"
    }
    data object DonationCardDetails : Routes("donation_card_details/{campaignId}") {
        fun createRoute(campaignId: String) = "donation_card_details/$campaignId"
    }
    data object DonationProcessing : Routes("donation_processing")
    data object DonationPayment : Routes("donation_payment/{campaignId}") {
        fun createRoute(campaignId: String) = "donation_payment/$campaignId"
    }
    data object DonationSuccess : Routes("donation_success/{type}") {
        fun createRoute(type: String) = "donation_success/$type"
    }
    data object CommunityList : Routes("community_list")
    data object Chat : Routes("chat/{groupId}") {
        fun createRoute(groupId: String) = "chat/$groupId"
    }
    data object LocationDetails : Routes("location_details/{locationId}") {
        fun createRoute(locationId: String) = "location_details/$locationId"
    }
    data object NewCampaignPost : Routes("new_campaign_post")
}
