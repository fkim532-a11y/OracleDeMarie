package com.kim.oraclemarie.ui.portal

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import com.kim.oraclemarie.R
import com.kim.oraclemarie.ui.components.StarryBackground
import com.kim.oraclemarie.ui.theme.MarianBlue
import com.kim.oraclemarie.ui.theme.SacredGold
import com.kim.oraclemarie.ui.theme.SoftWhite

private val NightBlack = Color(0xFF080810)

@Composable
fun PortalScreen(onNavigate: (String) -> Unit) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val context = LocalContext.current

    val marieAlpha = remember { Animatable(0f) }
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    LaunchedEffect(Unit) {
        marieAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2400, easing = FastOutSlowInEasing)
        )
        delay(150)
        val hintPx = with(density) { 50.dp.roundToPx() }
        scrollState.animateScrollTo(
            value = hintPx,
            animationSpec = tween(durationMillis = 2200, easing = FastOutSlowInEasing)
        )
        scrollState.animateScrollTo(
            value = 0,
            animationSpec = tween(durationMillis = 2200, easing = FastOutSlowInEasing)
        )
    }

    StarryBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Image Marie
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 1.0f)
            ) {
                // Lueur blanche douce en arrière-plan général
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color.White.copy(alpha = 0.15f), Color.Transparent),
                                radius = Float.POSITIVE_INFINITY
                            )
                        )
                )

                // Halos dorés derrière Marie (bas uniquement)
                Canvas(modifier = Modifier.fillMaxSize().alpha(marieAlpha.value)) {
                    val centerX = size.width / 2f

                    // Halo principal (bas) — resserré
                    val centerY1 = size.height * 0.65f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFD060).copy(alpha = 0.17f),
                                Color.Transparent
                            ),
                            center = androidx.compose.ui.geometry.Offset(centerX, centerY1),
                            radius = size.width * 0.52f
                        ),
                        radius = size.width * 0.52f,
                        center = androidx.compose.ui.geometry.Offset(centerX, centerY1)
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFFF99).copy(alpha = 0.28f),
                                Color(0xFFFFD060).copy(alpha = 0.12f),
                                Color.Transparent
                            ),
                            center = androidx.compose.ui.geometry.Offset(centerX, centerY1),
                            radius = size.width * 0.28f
                        ),
                        radius = size.width * 0.28f,
                        center = androidx.compose.ui.geometry.Offset(centerX, centerY1)
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.22f),
                                Color(0xFFFFFF99).copy(alpha = 0.07f),
                                Color.Transparent
                            ),
                            center = androidx.compose.ui.geometry.Offset(centerX, centerY1),
                            radius = size.width * 0.12f
                        ),
                        radius = size.width * 0.12f,
                        center = androidx.compose.ui.geometry.Offset(centerX, centerY1)
                    )
                }

                // Halo de tête — devant la nébuleuse violette, derrière Marie
                Canvas(modifier = Modifier.fillMaxSize().alpha(marieAlpha.value)) {
                    val centerX = size.width / 2f
                    val centerY2 = size.height * 0.19f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFFF99).copy(alpha = 0.35f),
                                Color(0xFFFFD060).copy(alpha = 0.15f),
                                Color.Transparent
                            ),
                            center = androidx.compose.ui.geometry.Offset(centerX, centerY2),
                            radius = size.width * 0.32f
                        ),
                        radius = size.width * 0.32f,
                        center = androidx.compose.ui.geometry.Offset(centerX, centerY2)
                    )
                }

                Image(
                    painter = painterResource(R.drawable.portal_marie),
                    contentDescription = stringResource(R.string.marie_image_desc),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = marieAlpha.value
                            scaleX = 1.15f
                            scaleY = 1.15f
                        }
                )

                Column(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.oracle_name),
                        style = MaterialTheme.typography.headlineLarge,
                        color = SacredGold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.portal_tagline),
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftWhite.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(56.dp))

            // Tuiles
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tirage à une carte
                PortalTile(
                    title = stringResource(R.string.portal_tile_draw1),
                    subtitle = stringResource(R.string.portal_tile_draw1_sub),
                    onClick = { onNavigate("home") }
                ) {
                    val resId = remember {
                        context.resources.getIdentifier("card_05", "drawable", context.packageName)
                    }
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = resId,
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(width = 48.dp, height = 72.dp)
                                .rotate(-8f)
                                .clip(RoundedCornerShape(6.dp))
                                .border(0.5.dp, SacredGold.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        )
                    }
                }

                // Tirage 3 cartes
                PortalTile(
                    title = stringResource(R.string.portal_tile_draw3),
                    subtitle = stringResource(R.string.portal_tile_draw3_sub),
                    onClick = { onNavigate("trois_cartes") }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        listOf(-14f, 0f, 14f).forEachIndexed { i, angle ->
                            Image(
                                painter = painterResource(R.drawable.card_back),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(width = 36.dp, height = 54.dp)
                                    .offset(x = ((i - 1) * 20).dp)
                                    .rotate(angle)
                                    .clip(RoundedCornerShape(4.dp))
                                    .border(0.5.dp, SacredGold.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            )
                        }
                    }
                }

                // Galerie
                PortalTile(
                    title = stringResource(R.string.portal_tile_gallery),
                    subtitle = stringResource(R.string.portal_tile_gallery_sub),
                    onClick = { onNavigate("gallery") }
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(R.drawable.tile_galerie),
                            contentDescription = null,
                            modifier = Modifier.size(210.dp)
                        )
                    }
                }

                // Paroles de Marie
                PortalTile(
                    title = stringResource(R.string.portal_tile_paroles),
                    subtitle = stringResource(R.string.portal_tile_paroles_sub),
                    onClick = { onNavigate("paroles") }
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(R.drawable.tile_livre),
                            contentDescription = null,
                            modifier = Modifier.size(170.dp)
                        )
                    }
                }

                // Prières
                PortalTile(
                    title = stringResource(R.string.portal_tile_prayers),
                    subtitle = stringResource(R.string.portal_tile_prayers_sub),
                    onClick = { onNavigate("prayers") }
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(R.drawable.tile_priere),
                            contentDescription = null,
                            modifier = Modifier.size(170.dp)
                        )
                    }
                }

                // Journal
                PortalTile(
                    title = stringResource(R.string.portal_tile_journal),
                    subtitle = stringResource(R.string.portal_tile_journal_sub),
                    onClick = { onNavigate("journal") }
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(R.drawable.tile_journal),
                            contentDescription = null,
                            modifier = Modifier.size(170.dp)
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "✦  À propos & Soutenir",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftWhite.copy(alpha = 0.65f),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable { onNavigate("settings") }
                )

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun PortalTile(
    title: String,
    subtitle: String,
    isPrimary: Boolean = false,
    onClick: () -> Unit,
    rightContent: @Composable BoxScope.() -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isPrimary) 90.dp else 78.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(
                width = if (isPrimary) 1.dp else 0.5.dp,
                color = SacredGold.copy(alpha = if (isPrimary) 0.75f else 0.30f),
                shape = RoundedCornerShape(18.dp)
            )
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        MarianBlue.copy(alpha = if (isPrimary) 0.55f else 0.25f),
                        MarianBlue.copy(alpha = if (isPrimary) 0.25f else 0.10f),
                    )
                )
            )
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Étoile à gauche
        Text(
            text = "✦",
            style = MaterialTheme.typography.bodyLarge,
            color = SacredGold.copy(alpha = if (isPrimary) 0.9f else 0.55f),
            modifier = Modifier.padding(start = 16.dp)
        )

        // Texte
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = if (isPrimary) MaterialTheme.typography.titleLarge
                        else MaterialTheme.typography.titleMedium,
                color = if (isPrimary) SacredGold else SoftWhite
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = SoftWhite.copy(alpha = 0.45f)
            )
        }

        // Illustration à droite
        Box(
            modifier = Modifier
                .width(90.dp)
                .fillMaxHeight(),
            content = rightContent
        )
    }
}
