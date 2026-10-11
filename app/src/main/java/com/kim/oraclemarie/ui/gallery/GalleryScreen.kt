package com.kim.oraclemarie.ui.gallery

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.ui.geometry.Rect
import kotlinx.coroutines.delay
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.kim.oraclemarie.R
import com.kim.oraclemarie.data.model.Card
import com.kim.oraclemarie.data.model.localeName
import com.kim.oraclemarie.data.model.localeKeyword
import com.kim.oraclemarie.ui.components.StarryBackground
import com.kim.oraclemarie.ui.theme.MarianBlue
import com.kim.oraclemarie.ui.theme.MysticPurple
import com.kim.oraclemarie.ui.theme.SacredGold
import com.kim.oraclemarie.ui.theme.SoftWhite
import com.kim.oraclemarie.ui.theme.WarmAmber
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToCardDetail: (Int) -> Unit = {},
    viewModel: GalleryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedCard by remember { mutableStateOf<Card?>(null) }
    var selectedCardBounds by remember { mutableStateOf(Rect.Zero) }
    var isNavigating by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    // Quand on revient de CardDetailScreen, fermer l'overlay proprement
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            if (isNavigating) {
                selectedCard = null
                isNavigating = false
            }
        }
    }

    BackHandler(enabled = selectedCard != null) {
        isNavigating = false
        selectedCard = null
    }

    Box(modifier = Modifier.fillMaxSize()) {
        StarryBackground {
            Column(modifier = Modifier.fillMaxSize()) {
                TopAppBar(
                    title = { Text(stringResource(R.string.portal_tile_gallery), color = SacredGold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back),
                                tint = SacredGold
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )

                if (uiState.isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = SacredGold)
                    }
                } else {
                    val part1 = uiState.cards.filter { it.partNumber == 1 }
                    val part2 = uiState.cards.filter { it.partNumber == 2 }
                    val part3 = uiState.cards.filter { it.partNumber == 3 }

                    val gridAlpha = remember { Animatable(0f) }
                    LaunchedEffect(Unit) {
                        gridAlpha.animateTo(1f, tween(durationMillis = 2400))
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize().graphicsLayer { alpha = gridAlpha.value },
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            PartHeader(title = "Partie I", color = MarianBlue)
                        }
                        items(part1) { card ->
                            GalleryCardItem(
                                card = card,
                                isDiscovered = card.id in uiState.discoveredIds,
                                onClick = { bounds ->
                                    selectedCardBounds = bounds
                                    selectedCard = card
                                }
                            )
                        }
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            PartHeader(title = "Partie II", color = MysticPurple)
                        }
                        items(part2) { card ->
                            GalleryCardItem(
                                card = card,
                                isDiscovered = card.id in uiState.discoveredIds,
                                onClick = { bounds ->
                                    selectedCardBounds = bounds
                                    selectedCard = card
                                }
                            )
                        }
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            PartHeader(title = "Partie III", color = WarmAmber)
                        }
                        items(part3) { card ->
                            GalleryCardItem(
                                card = card,
                                isDiscovered = card.id in uiState.discoveredIds,
                                onClick = { bounds ->
                                    selectedCardBounds = bounds
                                    selectedCard = card
                                }
                            )
                        }
                    }
                }
            }
        }

        // Overlay plein écran sur fond étoilé
        AnimatedVisibility(
            visible = selectedCard != null,
            enter = fadeIn(tween(400)),
            exit = fadeOut(tween(300))
        ) {
            selectedCard?.let { card ->
                StarryBackground {
                    CardPreviewOverlay(
                        card = card,
                        isDiscovered = card.id in uiState.discoveredIds,
                        startBounds = selectedCardBounds,
                        onClose = {
                            selectedCard = null
                        },
                        onNavigateToDetail = {
                            // On ne ferme PAS l'overlay — il reste visible pendant
                            // que CardDetailScreen s'ouvre par-dessus (pas de flash galerie)
                            // Fermeture déclenchée au retour via LaunchedEffect lifecycle
                            isNavigating = true
                            onNavigateToCardDetail(card.id)
                        }
                    )
                }
            }
        }
    }
}

// ─── Composables liste ────────────────────────────────────────────────────────

@Composable
private fun PartHeader(title: String, color: Color) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = color,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp)
    )
}

@Composable
private fun GalleryCardItem(
    card: Card,
    isDiscovered: Boolean,
    onClick: (Rect) -> Unit
) {
    val context = LocalContext.current
    var cardBounds by remember { mutableStateOf(Rect.Zero) }
    Box(
        modifier = Modifier
            .aspectRatio(2f / 3f)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = SacredGold.copy(alpha = if (isDiscovered) 0.6f else 0.25f),
                shape = RoundedCornerShape(12.dp)
            )
            .onGloballyPositioned { cardBounds = it.boundsInRoot() }
            .clickable { onClick(cardBounds) }
    ) {
        if (isDiscovered) {
            val imageResId = remember(card.imageName) {
                context.resources.getIdentifier(card.imageName, "drawable", context.packageName)
            }
            AsyncImage(
                model = imageResId,
                contentDescription = card.localeName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                        )
                    )
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = card.localeName,
                    style = MaterialTheme.typography.labelSmall,
                    color = SoftWhite,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            }
        } else {
            Image(
                painter = painterResource(R.drawable.card_back),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = "✦",
                style = MaterialTheme.typography.titleLarge,
                color = SacredGold.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

// ─── Overlay plein écran ──────────────────────────────────────────────────────

@Composable
private fun CardPreviewOverlay(
    card: Card,
    isDiscovered: Boolean,
    startBounds: Rect,
    onClose: () -> Unit,
    onNavigateToDetail: () -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val imageResId = remember(card.imageName) {
        context.resources.getIdentifier(card.imageName, "drawable", context.packageName)
    }

    // Un seul Animatable 0→1 pilote scale ET translation en parfaite synchronisation
    val progress = remember { Animatable(0f) }
    var translationTarget by remember { mutableFloatStateOf(0f) }
    var isAnimating by remember { mutableStateOf(false) }
    var imageCenterYPx by remember { mutableFloatStateOf(0f) }
    var overlayCenterX by remember { mutableFloatStateOf(0f) }
    var overlayCardWidth by remember { mutableFloatStateOf(0f) }
    val entryProgress = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        // Attendre que le layout soit mesuré
        while (overlayCardWidth == 0f) { delay(8L) }
        entryProgress.animateTo(1f, tween(1100, easing = FastOutSlowInEasing))
    }

    // Position cible de l'image dans CardDetailScreen :
    // TopAppBar M3 = 64dp, status bar mesurée via WindowInsets, padding header = 12dp
    val targetScale = 0.55f
    val statusBarHeightPx = WindowInsets.statusBars.getTop(density)
    val targetCenterYPx = with(density) {
        val screenWidthPx = configuration.screenWidthDp.dp.toPx()
        val targetImageWidthPx = (screenWidthPx - 40.dp.toPx()) * targetScale
        val targetImageHeightPx = targetImageWidthPx * 1.5f          // ratio 2:3
        val topPx = statusBarHeightPx + 64.dp.toPx() + 12.dp.toPx() // statusBar + TopAppBar M3 + padding
        topPx + targetImageHeightPx / 2f
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onClose),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            // Wrapper : mesure le centre réel, applique l'animation graphicsLayer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
                    .onGloballyPositioned { coords ->
                        val b = coords.boundsInRoot()
                        imageCenterYPx = b.center.y
                        overlayCenterX = b.center.x
                        overlayCardWidth = b.width
                    }
                    .graphicsLayer {
                        val ep = entryProgress.value
                        when {
                            overlayCardWidth == 0f -> alpha = 0f
                            ep < 1f -> {
                                val rawScale = if (startBounds.width > 0f) startBounds.width / overlayCardWidth else 0.25f
                                val startScale = rawScale.coerceIn(0.1f, 0.8f)
                                val startTx = if (startBounds.width > 0f) startBounds.center.x - overlayCenterX else 0f
                                val startTy = if (startBounds.width > 0f) startBounds.center.y - imageCenterYPx else 0f
                                val s = startScale + (1f - startScale) * ep
                                scaleX = s
                                scaleY = s
                                translationX = startTx * (1f - ep)
                                translationY = startTy * (1f - ep)
                            }
                            else -> {
                                val t = progress.value
                                val s = 1f + (targetScale - 1f) * t
                                scaleX = s
                                scaleY = s
                                translationY = translationTarget * t
                            }
                        }
                    }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.5.dp, SacredGold.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
                        .clickable {
                            if (!isAnimating && imageCenterYPx > 0f) {
                                isAnimating = true
                                translationTarget = targetCenterYPx - imageCenterYPx
                                scope.launch {
                                    progress.animateTo(1f, tween(500))
                                    onNavigateToDetail()
                                }
                            }
                        }
                ) {
                    if (isDiscovered) {
                        AsyncImage(
                            model = imageResId,
                            contentDescription = card.localeName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.card_back),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer { alpha = entryProgress.value }
            ) {
                if (isDiscovered) {
                    Text(
                        text = card.localeName,
                        style = MaterialTheme.typography.titleMedium,
                        color = SoftWhite,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = card.localeKeyword,
                        style = MaterialTheme.typography.labelLarge,
                        color = SacredGold
                    )
                    Spacer(Modifier.height(20.dp))
                    Text(
                        text = stringResource(R.string.gallery_discover_hint),
                        style = MaterialTheme.typography.labelSmall,
                        color = SoftWhite.copy(alpha = 0.4f)
                    )
                } else {
                    Text(
                        text = stringResource(R.string.gallery_undiscovered),
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftWhite.copy(alpha = 0.5f)
                    )
                    Spacer(Modifier.height(20.dp))
                    Text(
                        text = stringResource(R.string.gallery_discover_hint),
                        style = MaterialTheme.typography.labelSmall,
                        color = SoftWhite.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}