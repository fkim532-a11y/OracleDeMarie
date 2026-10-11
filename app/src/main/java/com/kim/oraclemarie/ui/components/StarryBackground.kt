package com.kim.oraclemarie.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.random.Random

private val NightBlack = Color(0xFF080810)

private data class Star(val x: Float, val y: Float, val radius: Float, val alpha: Float)
private data class NebulaBlob(
    val x: Float, val y: Float,
    val radius: Float, val alpha: Float,
    val color: Color
)

@Composable
fun StarryBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val stars = remember {
        val rng = Random(42)
        List(120) {
            Star(
                x = rng.nextFloat(),
                y = rng.nextFloat(),
                radius = rng.nextFloat() * 1.5f + 0.5f,
                alpha = rng.nextFloat() * 0.75f + 0.25f
            )
        }
    }

    val nebulaBlobs = remember {
        val rng = Random(77)
        buildList {
            // Nébuleuse bleue — haut gauche (collée au bord)
            repeat(9) {
                add(NebulaBlob(
                    x = rng.nextFloat() * 0.22f,
                    y = rng.nextFloat() * 0.35f,
                    radius = rng.nextFloat() * 0.13f + 0.06f,
                    alpha = rng.nextFloat() * 0.25f + 0.15f,
                    color = Color(0xFF2C4A9A)
                ))
            }
            // Nébuleuse violette — haut droit
            repeat(8) {
                add(NebulaBlob(
                    x = 0.60f + rng.nextFloat() * 0.40f,
                    y = rng.nextFloat() * 0.35f,
                    radius = rng.nextFloat() * 0.16f + 0.07f,
                    alpha = rng.nextFloat() * 0.28f + 0.15f,
                    color = Color(0xFF5A1A7A)
                ))
            }
            // Nébuleuse rose — bas droit
            repeat(7) {
                add(NebulaBlob(
                    x = 0.55f + rng.nextFloat() * 0.45f,
                    y = 0.60f + rng.nextFloat() * 0.40f,
                    radius = rng.nextFloat() * 0.14f + 0.06f,
                    alpha = rng.nextFloat() * 0.25f + 0.14f,
                    color = Color(0xFF6A1A40)
                ))
            }
            // Nébuleuse bleu foncé — milieu gauche (collée au bord)
            repeat(6) {
                add(NebulaBlob(
                    x = rng.nextFloat() * 0.18f,
                    y = 0.40f + rng.nextFloat() * 0.35f,
                    radius = rng.nextFloat() * 0.11f + 0.05f,
                    alpha = rng.nextFloat() * 0.20f + 0.10f,
                    color = Color(0xFF1A2A60)
                ))
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NightBlack)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Nébuleuses — blobs superposés pour texture organique
            nebulaBlobs.forEach { blob ->
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            blob.color.copy(alpha = blob.alpha),
                            blob.color.copy(alpha = blob.alpha * 0.3f),
                            Color.Transparent
                        ),
                        center = Offset(blob.x * size.width, blob.y * size.height),
                        radius = blob.radius * size.width
                    ),
                    radius = blob.radius * size.width,
                    center = Offset(blob.x * size.width, blob.y * size.height)
                )
            }

            // Étoiles
            stars.forEach { star ->
                drawCircle(
                    color = Color.White.copy(alpha = star.alpha),
                    radius = star.radius.dp.toPx(),
                    center = Offset(star.x * size.width, star.y * size.height)
                )
            }
        }
        content()
    }
}
