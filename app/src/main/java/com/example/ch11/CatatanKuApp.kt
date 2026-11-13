package com.example.ch11

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ch11.ui.auth.LoginRoute
import com.example.ch11.ui.auth.RegisterRoute
import com.example.ch11.ui.edit.NoteEditRoute
import com.example.ch11.ui.list.NoteListRoute
import com.example.ch11.ui.session.SessionViewModel

@Composable
fun CatatanKuApp(
    sessionViewModel: SessionViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val user by sessionViewModel.currentUser.collectAsStateWithLifecycle()
    val isSignedIn = user != null

    // startDestination hanya dibaca sekali saat NavHost pertama dibuat
    val startDestination = remember {
        if (isSignedIn) Routes.NoteList.route else Routes.Login.route
    }

    LaunchedEffect(isSignedIn) {
        val route = navController.currentBackStackEntry?.destination?.route
        val onAuthScreen = route in authRoutes
        if (isSignedIn && onAuthScreen) {
            navController.navigateClearingBackStack(Routes.NoteList.route)
        } else if (!isSignedIn && !onAuthScreen) {
            navController.navigateClearingBackStack(Routes.Login.route)
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.Login.route) {
            LoginRoute(onNavigateToRegister = { navController.navigate(Routes.Register.route) })
        }

        composable(Routes.Register.route) {
            RegisterRoute(onNavigateBack = { navController.popBackStack() })
        }

        composable(Routes.NoteList.route) {
            NoteListRoute(
                userEmail = user?.email,
                onAddNote = { navController.navigate(Routes.NoteEdit.createRoute()) },
                onEditNote = { id -> navController.navigate(Routes.NoteEdit.createRoute(id)) },
                onLogout = sessionViewModel::logout
            )
        }

        composable(
            route = Routes.NoteEdit.route,
            arguments = listOf(navArgument(ARG_NOTE_ID) { type = NavType.StringType })
        ) {
            NoteEditRoute(onNavigateBack = { navController.popBackStack() })
        }
    }
}

// Sudah disediakan. Sama dengan popUpTo(0) di buku: seluruh back stack dikosongkan, sehingga
// tombol Back tidak bisa kembali ke layar milik sesi sebelumnya.
private fun NavHostController.navigateClearingBackStack(route: String) {
    navigate(route) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
}
