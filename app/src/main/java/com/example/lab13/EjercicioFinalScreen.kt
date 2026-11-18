package com.example.lab13

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab13.ui.theme.Lab13Theme
import kotlinx.coroutines.delay

enum class GameState {
    MENU,
    PLAYING,
    GAME_OVER
}

@Composable
fun EjercicioFinalScreen(modifier: Modifier = Modifier) {
    var gameState by remember { mutableStateOf(GameState.MENU) }
    var score by remember { mutableIntStateOf(0) }
    var level by remember { mutableIntStateOf(1) }
    var lives by remember { mutableIntStateOf(3) }

    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var showFeedback by remember { mutableStateOf(false) }
    var flashGreen by remember { mutableStateOf(false) }
    var flashRed by remember { mutableStateOf(false) }

    // Automatic reset of feedback flash effects
    LaunchedEffect(showFeedback) {
        if (showFeedback) {
            delay(800)
            showFeedback = false
            flashGreen = false
            flashRed = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Main Title Header
        Text(
            text = "Ejercicio Final",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Minijuego de Precisión (Timing Bar)",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // Game Container with AnimatedContent transitions between GameStates
        AnimatedContent(
            targetState = gameState,
            transitionSpec = {
                (slideInVertically(animationSpec = tween(500)) { height -> height } + fadeIn(animationSpec = tween(500)))
                    .togetherWith(slideOutVertically(animationSpec = tween(500)) { height -> -height } + fadeOut(animationSpec = tween(500)))
            },
            label = "GameStateTransition"
        ) { state ->
            when (state) {
                GameState.MENU -> MenuScreen(
                    onStartGame = {
                        score = 0
                        level = 1
                        lives = 3
                        gameState = GameState.PLAYING
                    }
                )

                GameState.PLAYING -> PlayingScreen(
                    score = score,
                    level = level,
                    lives = lives,
                    showFeedback = showFeedback,
                    feedbackMessage = feedbackMessage,
                    flashGreen = flashGreen,
                    flashRed = flashRed,
                    onStopPressed = { cursorRatio, targetHalfWidthRatio ->
                        val hit = cursorRatio in (0.5f - targetHalfWidthRatio)..(0.5f + targetHalfWidthRatio)
                        if (hit) {
                            score += 100
                            level += 1
                            feedbackMessage = "¡CRÍTICO! +100 pts"
                            flashGreen = true
                            flashRed = false
                        } else {
                            lives -= 1
                            feedbackMessage = "¡FALLASTE! -1 Vida"
                            flashRed = true
                            flashGreen = false
                            if (lives <= 0) {
                                gameState = GameState.GAME_OVER
                            }
                        }
                        showFeedback = true
                    }
                )

                GameState.GAME_OVER -> GameOverScreen(
                    finalScore = score,
                    finalLevel = level,
                    onRestart = {
                        score = 0
                        level = 1
                        lives = 3
                        gameState = GameState.PLAYING
                    }
                )
            }
        }
    }
}

@Composable
private fun MenuScreen(onStartGame: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Reglas del Juego:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "1. Detén el cursor justo sobre la zona verde para sumar 100 puntos y subir de nivel.\n" +
                            "2. Cada nivel reduce el tamaño de la zona verde y acelera el cursor.\n" +
                            "3. Si fallas perderás 1 vida. ¡Tienes 3 vidas!",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onStartGame,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(
                text = "¡Iniciar Juego!",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun PlayingScreen(
    score: Int,
    level: Int,
    lives: Int,
    showFeedback: Boolean,
    feedbackMessage: String?,
    flashGreen: Boolean,
    flashRed: Boolean,
    onStopPressed: (cursorRatio: Float, targetHalfWidthRatio: Float) -> Unit
) {
    val trackWidthDp = 300.dp
    val trackHeightDp = 48.dp

    // 1. animateDpAsState for target zone width
    val targetZoneWidth by animateDpAsState(
        targetValue = (80 - (level - 1) * 10).coerceAtLeast(24).dp,
        animationSpec = tween(durationMillis = 400),
        label = "TargetWidthAnimation"
    )

    // 2. rememberInfiniteTransition for continuous cursor movement
    val infiniteTransition = rememberInfiniteTransition(label = "CursorMovement")
    val cursorPositionRatio by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (1200 - (level - 1) * 80).coerceAtLeast(350),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CursorPosition"
    )

    // 3. animateColorAsState for track border/flash feedback
    val trackBorderColor by animateColorAsState(
        targetValue = when {
            flashGreen -> Color(0xFF2E7D32)
            flashRed -> Color(0xFFD32F2F)
            else -> MaterialTheme.colorScheme.outline
        },
        animationSpec = tween(durationMillis = 300),
        label = "TrackBorderColorAnimation"
    )

    val targetHalfWidthRatio = (targetZoneWidth.value / trackWidthDp.value) / 2f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Score, Level & Lives Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Puntos: $score", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = "Nivel: $level", color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                }
                Row {
                    repeat(3) { index ->
                        Text(
                            text = if (index < lives) "❤️" else "🖤",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4. AnimatedVisibility for floating Feedback Chip (directly inside ColumnScope)
        AnimatedVisibility(
            visible = showFeedback && feedbackMessage != null,
            enter = fadeIn(animationSpec = tween(300)) + scaleIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300)) + shrinkVertically(animationSpec = tween(300))
        ) {
            feedbackMessage?.let { msg ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (flashGreen) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = msg,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        if (!showFeedback) {
            Spacer(modifier = Modifier.height(36.dp))
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Timing Track Area
        Box(
            modifier = Modifier
                .width(trackWidthDp)
                .height(trackHeightDp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(width = 3.dp, color = trackBorderColor, shape = RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Target Zone (Green)
            Box(
                modifier = Modifier
                    .width(targetZoneWidth)
                    .height(trackHeightDp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF4CAF50).copy(alpha = 0.8f))
            )

            // Cursor Line (Moving continuously)
            val cursorOffset = (trackWidthDp.value - 12f) * cursorPositionRatio
            Box(
                modifier = Modifier
                    .offset(x = cursorOffset.dp - (trackWidthDp / 2) + 6.dp)
                    .width(10.dp)
                    .height(trackHeightDp - 8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Action Stop Button
        Button(
            onClick = { onStopPressed(cursorPositionRatio, targetHalfWidthRatio) },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(
                text = "¡DETENER!",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun GameOverScreen(
    finalScore: Int,
    finalLevel: Int,
    onRestart: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¡JUEGO TERMINADO!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Puntaje Final: $finalScore pts",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Text(
                text = "Alcanzaste el Nivel: $finalLevel",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onRestart,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text(
                    text = "Reintentar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EjercicioFinalScreenPreview() {
    Lab13Theme {
        EjercicioFinalScreen()
    }
}
