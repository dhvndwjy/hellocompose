package com.example.feederku.navigation

//buat view model dan kumpulin uistate
sealed class Routes(val route: String){
    data object OnBoarding : Routes("onboarding")
    data object Welcome : Routes("welcome")
    data object SignUp : Routes("signup")
    data object Login : Routes("login")
    data object Dashboard : Routes("dashboard")
    data object Profile : Routes("profile")
    data object Settings : Routes("settings")
    data object AccountInformation : Routes("settings/account_info")
    data object EditProfile : Routes("settings/edit_profile")
    data object Notifications : Routes("settings/notifications")
    data object PrivacySecurity : Routes("settings/privacy_security")
    data object ChangePassword : Routes("settings/change_password")
    data object Language : Routes("settings/language")
    data object HelpSupport : Routes("settings/help_support")
    data object AboutFeederKu : Routes("settings/about")
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
    data object Chat : Routes("chat")
    data object CommunityList : Routes("community_list")
    data object PublishPost : Routes("publish_post")
    data object Request : Routes("request")
    data object DirectMessage : Routes("direct_message/{friendName}") {
        fun createRoute(friendName: String) = "direct_message/$friendName"
    }
}
