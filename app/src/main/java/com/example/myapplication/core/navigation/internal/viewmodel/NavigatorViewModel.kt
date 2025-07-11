package com.example.myapplication.core.navigation.internal.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myapplication.core.navigation.internal.navigator.InternalNavigator
import com.example.myapplication.core.navigation.internal.navigator.InternalRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
internal class NavigatorViewModel @Inject constructor(
    navigator: InternalNavigator,
) : ViewModel() {
    val sideEffect by lazy(LazyThreadSafetyMode.NONE) {
        navigator.channel.receiveAsFlow()
            .map { navigator ->
                when (navigator) {
                    is InternalRoute.Navigate -> RouteSideEffect.Navigate(
                        navigator.route,
                        navigator.saveState,
                        navigator.launchSingleTop,
                    )

                    InternalRoute.NavigateBack -> RouteSideEffect.NavigateBack
                }
            }
    }
}