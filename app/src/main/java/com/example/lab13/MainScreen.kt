package com.example.lab13

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.lab13.ui.theme.Lab13Theme

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }

    val navItems = listOf(
        "Ex 1",
        "Ex 2",
        "Ex 3",
        "Ex 4",
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                navItems.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        label = { Text(text = title) },
                        icon = {
                            Text(
                                text = (index + 1).toString(),
                                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                            )
                        },
                    )
                }
            }
        },
    ) { innerPadding ->
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                fadeIn(animationSpec = tween(400)).togetherWith(fadeOut(animationSpec = tween(400)))
            },
            modifier = Modifier.padding(innerPadding),
            label = "ScreenTransition",
        ) { page ->
            when (page) {
                0 -> Ejercicio1Screen()
                1 -> Ejercicio2Screen()
                2 -> Ejercicio3Screen()
                3 -> Ejercicio4Screen()
                else -> Ejercicio1Screen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    Lab13Theme {
        MainScreen()
    }
}
