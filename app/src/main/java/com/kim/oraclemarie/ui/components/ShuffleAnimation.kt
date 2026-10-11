package com.kim.oraclemarie.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kim.oraclemarie.R
import com.kim.oraclemarie.ui.theme.SacredGold
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ShuffleAnimation(onDone: () -> Unit) {
    val cardCount = 10

    val txAnims = remember { List(cardCount) { Animatable(0f) } }
    val tyAnims = remember { List(cardCount) { Animatable(0f) } }
    val rzAnims = remember { List(cardCount) { Animatable(0f) } }
    val contentAlpha = remember { Animatable(0f) }
    val glowAlpha = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        // Phase 1 — Apparition du paquet
        contentAlpha.animateTo(1f, tween(400, easing = FastOutSlowInEasing))

        // Phase 2 — Séparation en deux demi-paquets
        for (i in 0 until cardCount) {
            val isLeft = i < 5
            val dir = if (isLeft) -1f else 1f
            val stackPos = if (isLeft) i else i - 5
            launch {
                delay(stackPos * 40L)
                launch { txAnims[i].animateTo(dir * 105f, tween(650, easing = FastOutSlowInEasing)) }
                rzAnims[i].animateTo(dir * 14f, tween(650, easing = FastOutSlowInEasing))
            }
        }
        delay(950)

        // Phase 3 — Riffle : interfoliation alternée gauche/droite
        val interleaveOrder = listOf(0, 5, 1, 6, 2, 7, 3, 8, 4, 9)
        for (cardIdx in interleaveOrder) {
            launch {
                tyAnims[cardIdx].animateTo(-18f, tween(180, easing = FastOutSlowInEasing))
                launch { txAnims[cardIdx].animateTo(0f, tween(300, easing = FastOutSlowInEasing)) }
                launch { rzAnims[cardIdx].animateTo(0f, tween(300, easing = FastOutSlowInEasing)) }
                tyAnims[cardIdx].animateTo(0f, tween(200, easing = FastOutSlowInEasing))
            }
            delay(130)
        }
        delay(800)

        // Phase 4 — Éventail mystique
        for (i in 0 until cardCount) {
            val angleDeg = -56f + i * (112f / 9f)
            val angleRad = Math.toRadians(angleDeg.toDouble())
            val targetX = (sin(angleRad) * 130.0).toFloat()
            val targetY = ((cos(angleRad) - 1.0) * 130.0).toFloat()
            launch {
                launch { txAnims[i].animateTo(targetX, tween(580, easing = FastOutSlowInEasing)) }
                launch { tyAnims[i].animateTo(targetY, tween(580, easing = FastOutSlowInEasing)) }
                rzAnims[i].animateTo(angleDeg, tween(580, easing = FastOutSlowInEasing))
            }
        }
        delay(720)

        // Phase 5 — Vague ondulante sur l'éventail
        for (i in 0 until cardCount) {
            val currentY = tyAnims[i].value
            launch {
                tyAnims[i].animateTo(currentY - 22f, tween(170))
                tyAnims[i].animateTo(currentY, tween(170))
            }
            delay(55)
        }
        delay(500)

        // Phase 6 — Refermeture avec halo doré
        launch { glowAlpha.animateTo(1f, tween(300)) }
        for (i in 0 until cardCount) {
            launch {
                launch { txAnims[i].animateTo(0f, tween(750, easing = FastOutSlowInEasing)) }
                launch { tyAnims[i].animateTo(0f, tween(750, easing = FastOutSlowInEasing)) }
                rzAnims[i].animateTo(0f, tween(750, easing = FastOutSlowInEasing))
            }
        }
        delay(620)
        launch { glowAlpha.animateTo(0f, tween(300)) }
        delay(350)

        // Agrandissement progressif
        scaleAnim.animateTo(1.2f, tween(900, easing = FastOutSlowInEasing))

        onDone()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(440.dp)
            .alpha(contentAlpha.value),
        contentAlignment = Alignment.Center
    ) {
        (0 until cardCount).forEach { i ->
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(300.dp)
                    .graphicsLayer {
                        translationX = txAnims[i].value * density
                        translationY = tyAnims[i].value * density
                        rotationZ = rzAnims[i].value
                        scaleX = scaleAnim.value
                        scaleY = scaleAnim.value
                    }
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, SacredGold.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            ) {
                Image(
                    painter = painterResource(R.drawable.card_back),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = glowAlpha.value * 0.3f }
                        .background(SacredGold)
                )
            }
        }
    }
}
