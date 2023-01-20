package com.vangelnum.testfirebase.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vangelnum.testfirebase.MainViewModel
import com.vangelnum.testfirebase.R
import com.vangelnum.testfirebase.Screens
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


@Composable
fun Navigation(
    navController: NavHostController,
    myViewModel: MainViewModel,
) {

    val auth = Firebase.auth
    val currentUser = auth.currentUser
    var startDestination = Screens.Register.route
    val uid = currentUser?.uid
    val context = LocalContext.current

    if (currentUser != null && currentUser.isEmailVerified) {
        startDestination = Screens.Main.route
    }

    val items = listOf(
        Screens.Main,
        Screens.Favourite,
        Screens.Notification,
        Screens.Add
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination



    var showAppBar by rememberSaveable {
        mutableStateOf(true)
    }
    var showBottomBar by rememberSaveable {
        mutableStateOf(true)
    }
    when (navBackStackEntry?.destination?.route) {
        Screens.WatchPhoto.route + "/{url}" -> {
            showAppBar = true
            showBottomBar = false
        }
        Screens.Login.route -> {
            showAppBar = false
            showBottomBar = false
        }
        Screens.Register.route -> {
            showAppBar = false
            showBottomBar = false
        }
        else -> {
            showAppBar = true
            showBottomBar = true
        }
    }




    Scaffold(
        topBar = {
            if (showAppBar) {
                if (currentDestination?.route != Screens.WatchPhoto.route + "/{url}") {
                    TopAppBar(elevation = 0.dp) {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(painter = painterResource(id = R.drawable.ic_baseline_menu_24),
                                contentDescription = "menu")
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val myCollection =
                                        Firebase.firestore.collection("developer").document(uid!!)
                                    val querySnapShot = myCollection.get().await()
                                    withContext(Dispatchers.Main) {
                                        navController.navigate(route = Screens.Developer.route)
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context,
                                            "Error: ${e.message.toString()}",
                                            Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }) {
                            Icon(painter = painterResource(id = R.drawable.ic_round_favorite_24),
                                contentDescription = "admin_panel")
                        }
                    }
                } else {
                    TopAppBar(elevation = 0.dp) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(painter = painterResource(id = R.drawable.ic_baseline_arrow_back_24), contentDescription = "back")
                        }
                    }
                }
            }
        },
        bottomBar = {
            if (showBottomBar) {
                BottomNavigation(elevation = 0.dp) {
                    items.forEach { screen ->
                        if (currentDestination != null) {
                            BottomNavigationItem(
                                icon = {
                                    Icon(painter = painterResource(id = screen.icon),
                                        contentDescription = null)
                                },
                                label = {
                                    Text(text = screen.title,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1)
                                },
                                selected = currentDestination.hierarchy.any {
                                    it.route == screen.route
                                },
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)) {
            composable(route = Screens.Register.route) {
                RegisterScreen(auth,
                    onNavigateToLogin = { navController.navigate(route = Screens.Login.route) },
                    onNavigateToMain = { navController.navigate(route = Screens.Main.route) })
            }
            composable(route = Screens.Login.route) {
                LoginScreen(onNavigateToRegister = { navController.navigate(route = Screens.Register.route) },
                    { navController.navigate(route = Screens.Main.route) }, auth)
            }
            composable(route = Screens.Main.route) {
                MainScreen(viewModel = myViewModel, navController)
            }
            composable(route = Screens.Favourite.route) {
                FavouriteScreen(viewModel = myViewModel)
            }
            composable(route = Screens.Add.route) {
                AddPhotoScreen(auth = auth)
            }
            composable(route = Screens.Developer.route) {
                DeveloperScreen(viewModel = myViewModel, auth = auth)
            }
            composable(route = Screens.Notification.route) {
                NotificationScreen()
            }
            composable(route = Screens.WatchPhoto.route + "/{url}", arguments = listOf(
                navArgument("url") {
                    type = NavType.StringType
                }
            )) { entry ->
                WatchPhotoScreen(url = entry.arguments?.getString("url"), navController)
            }

        }
    }
}
