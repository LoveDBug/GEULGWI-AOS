package com.example.myapplication.feature.main.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.myapplication.feature.main.MainTab
import kotlinx.collections.immutable.ImmutableList

// enum class TabTheme(val isDarkMode: Boolean, val backgroundColor: Color, val color: Color) {
//    LIGHT(false, MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.outline),
//    DARK(false, MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.outline),
// }
@Composable
internal fun MainBottomBar(
    visible: Boolean,
    tabs: ImmutableList<MainTab>,
    currentTab: MainTab?,
    onTabSelected: (MainTab) -> Unit,
) {
    val (backgroundColor, iconTint) =
        if (currentTab == MainTab.REELS || currentTab == MainTab.POST) {
            Color(0xFF1C1B1F) to MaterialTheme.colorScheme.surface
        } else {
            Color.White to MaterialTheme.colorScheme.onSurface
        }

    if (currentTab != MainTab.POST) {
        Box(modifier = Modifier.background(backgroundColor)) {
            Column {
                if (currentTab != MainTab.REELS) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline,
                        thickness = (0.2).dp,
                    )
                }
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn() + slideIn { IntOffset(0, it.height) },
                    exit = fadeOut() + slideOut { IntOffset(0, it.height) },
                ) {
                    Row(
                        modifier =
                            Modifier
                                .navigationBarsPadding()
                                .fillMaxWidth()
                                .height(64.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        tabs.forEach { tab ->
                            MainBottomBarItem(
                                tab = tab,
                                selected = tab == currentTab,
                                iconTint = iconTint,
                                onClick = {
                                    if (tab != currentTab) {
                                        onTabSelected(tab)
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.MainBottomBarItem(
    tab: MainTab,
    selected: Boolean,
    iconTint: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .weight(1f)
                .fillMaxHeight()
                .selectable(
                    selected = selected,
                    indication = null,
                    role = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onClick,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(tab.iconResId),
                contentDescription = tab.contentDescription,
                tint = if (selected) iconTint else Color.Gray,
            )
        }
    }
}
