package com.example.myapplication.core.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    /*
    *  navbar에 종속되어 있지 않은 화면들
    * */
}


sealed interface BottomTabRoute : Route {

    @Serializable
    data object Home : BottomTabRoute

    @Serializable
    data object Library : BottomTabRoute

    @Serializable
    data object Post : BottomTabRoute

    @Serializable
    data object Reels : BottomTabRoute

    @Serializable
    data object Profile : BottomTabRoute

}
