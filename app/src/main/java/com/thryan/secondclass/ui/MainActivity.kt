package com.thryan.secondclass.ui

import android.content.Context
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
import com.thryan.secondclass.ui.page.PageIntent
import com.thryan.secondclass.ui.page.PageViewModel
import com.thryan.secondclass.ui.theme.SecondClassTheme


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
    var pageViewModel: PageViewModel? = null
    var infoViewModel: InfoViewModel? = null
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        Log.i("Main", "NavBuilder")
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
            Log.i("Main", "我他妈recompose")
            val twfid = backStackEntry.arguments?.getString("twfid")
            val account = backStackEntry.arguments?.getString("account")
            //防止viewModel多次创建多次发起网络请求
            if (pageViewModel == null|| checkNotNull(account) != pageViewModel!!.account) {
                pageViewModel = PageViewModel(
                    navController,
                    twfid = checkNotNull(twfid),
                    account = checkNotNull(account)
                )
                pageViewModel!!.send(PageIntent.Init)
            }
            Page(
                viewModel = pageViewModel!!
            )
        }
        composable(
            "info?id={id}&twfid={twfid}&token={token}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
                navArgument("twfid") { type = NavType.StringType },
                navArgument("token") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            Log.i("Main", "我他妈recompose")
            val id = backStackEntry.arguments?.getString("id")
            val twfid = backStackEntry.arguments?.getString("twfid")
            val token = backStackEntry.arguments?.getString("token")
            if (infoViewModel == null || checkNotNull(id) != infoViewModel!!.id) {
                infoViewModel = InfoViewModel(
                    id = checkNotNull(id),
                    twfid = checkNotNull(twfid),
                    token = checkNotNull(token)
                )
            }
            Info(
                navController,
                infoViewModel!!
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