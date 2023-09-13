package com.thryan.secondclass

import android.os.Bundle
import android.util.Log
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
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.thryan.secondclass.ui.info.Info
import com.thryan.secondclass.ui.info.InfoViewModel
import com.thryan.secondclass.ui.login.Login
import com.thryan.secondclass.ui.login.LoginViewModel
import com.thryan.secondclass.ui.page.Page
import com.thryan.secondclass.ui.page.PageViewModel
import com.thryan.secondclass.ui.theme.SecondClassTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var navigator: Navigator

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
                        navigator = navigator
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
    navigator: Navigator
) {
    navigator.setController(navController)

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        Log.i("Main", "NavBuilder")
        composable("login") {
            val viewModel = hiltViewModel<LoginViewModel>()
            Login(viewModel = viewModel)
        }
        composable(
            "page?twfid={twfid}&account={account}&password={password}",
            arguments = listOf(
                navArgument("twfid") { type = NavType.StringType },
                navArgument("account") { type = NavType.StringType },
                navArgument("password") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            Log.i("Main", "我他妈recompose")
            //非常好注入，❤来自驾驶学校
            val pageViewModel = hiltViewModel<PageViewModel>(backStackEntry)
            Page(viewModel = pageViewModel)
        }
        composable(
            "info?id={id}&twfid={twfid}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            Log.i("Main", "我他妈recompose")
            val infoViewModel = hiltViewModel<InfoViewModel>(backStackEntry)
            Info(
                navController,
                infoViewModel
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