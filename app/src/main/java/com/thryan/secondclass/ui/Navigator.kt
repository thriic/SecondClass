package com.thryan.secondclass.ui

import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder

class Navigator {

    private var navController: NavHostController? = null

    fun setController(controller: NavHostController) {
        navController = controller
    }

    fun clear() {
        navController = null
    }

    fun navigate(route: String, builder: (NavOptionsBuilder.() -> Unit)? = null) {
        if (builder == null) {
            navController?.navigate(route)
        } else {
            navController?.navigate(route, builder)
        }
    }
}