package com.thryan.secondclass.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.thryan.secondclass.ui.login.Login
import com.thryan.secondclass.ui.theme.SecondClassTheme
import com.thryan.secondclass.ui.login.LoginViewModel
import com.thryan.secondclass.ui.page.PageViewModel
import com.thryan.secondclass.ui.page.Page


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            SecondClassTheme {
                TransparentSystemBars()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost(
                        Modifier.fillMaxSize(),
                        context = applicationContext
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "login",
    context: Context
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") {
            Login(viewModel = LoginViewModel(context, navController))
        }
        composable(
            "page?twfid={twfid}&account={account}",
            arguments = listOf(
                navArgument("twfid") { type = NavType.StringType },
                navArgument("account") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val twfid = backStackEntry.arguments?.getString("twfid")
            val account = backStackEntry.arguments?.getString("account")

            Page(
                PageViewModel(
                    navController,
                    twfid = checkNotNull(twfid),
                    account = checkNotNull(account)
                )
            )
        }
    }
}

@Composable
fun TransparentSystemBars() {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isSystemInDarkTheme()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons,
            isNavigationBarContrastEnforced = false,
        )
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SecondClassTheme {
    }
}