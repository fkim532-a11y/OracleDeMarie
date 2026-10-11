package com.kim.oraclemarie.ui.troiscartes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import android.annotation.SuppressLint
import android.content.Intent
import java.util.Locale
import com.kim.oraclemarie.ui.utils.cardImageUri
import com.kim.oraclemarie.ui.utils.themeLabel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.kim.oraclemarie.R
import com.kim.oraclemarie.data.model.Card
import com.kim.oraclemarie.data.model.localeName
import com.kim.oraclemarie.data.model.localeKeyword
import com.kim.oraclemarie.data.model.localeSituationForTheme
import com.kim.oraclemarie.data.model.localeConseilForTheme
import com.kim.oraclemarie.ui.components.ShuffleAnimation
import com.kim.oraclemarie.ui.components.StarryBackground
import com.kim.oraclemarie.ui.components.ThemeSelector
import com.kim.oraclemarie.ui.components.themeOptions
import com.kim.oraclemarie.ui.theme.MarianBlue
import com.kim.oraclemarie.ui.theme.SacredGold
import com.kim.oraclemarie.ui.theme.SoftWhite

@SuppressLint("DiscouragedApi")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun TroisCartesScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: TroisCartesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val positions = listOf(
        stringResource(R.string.position_corps),
        stringResource(R.string.position_ame),
        stringResource(R.string.position_esprit)
    )
    val overlayMessages = listOf(
        stringResource(R.string.troiscartes_hint_next),
        stringResource(R.string.troiscartes_hint_next),
        stringResource(R.string.troiscartes_hint_reveal)
    )
    var shuffleDone by remember(uiState.shuffleKey) { mutableStateOf(false) }
    var overlayDone by remember(uiState.shuffleKey) { mutableStateOf(false) }
    // revealPhase dérivé synchroniquement — overlay démarre immédiatement au clic
    val revealPhase = when {
        overlayDone -> 4
        uiState.selectedTheme != null -> 1
        else -> 0
    }
    var shuffleCenter by remember(uiState.shuffleKey) { mutableStateOf(Offset.Zero) }
    val imeVisible = WindowInsets.isImeVisible

    // Phase 4 — carte zoomée au tap
    var zoomedCard by remember { mutableStateOf<Card?>(null) }
    var zoomedCardBounds by remember { mutableStateOf(Rect.Zero) }
    val cardBoundsMap = remember { mutableStateMapOf<Int, Rect>() }  // clé = card.id

    // Réinitialise le zoom sur nouveau tirage
    LaunchedEffect(uiState.shuffleKey) { zoomedCard = null }

    StarryBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            stringResource(R.string.troiscartes_title),
                            color = SoftWhite,
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (uiState.selectedTheme != null) {
                            Text(
                                "✦ ${stringResource(themeOptions.first { it.key == uiState.selectedTheme }.labelRes)} ✦",
                                color = SacredGold,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back), tint = SacredGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = SacredGold)
                }
                return@Column
            }

            // Phase 0 : animation de mélange — fondu 400ms avant que l'overlay arrive (à 450ms)
            AnimatedVisibility(
                visible = revealPhase == 0,
                enter = fadeIn(),
                exit = fadeOut(animationSpec = tween(400))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(410.dp)
                        .onGloballyPositioned { coords ->
                            val bounds = coords.boundsInWindow()
                            shuffleCenter = Offset(bounds.center.x, bounds.center.y)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    ShuffleAnimation(onDone = { shuffleDone = true })
                }
            }

            // Phase 4 : 3 cartes face visible, fadeIn lent, cliquables pour zoom
            // Se masque quand le clavier est visible pour libérer l'espace
            AnimatedVisibility(
                visible = revealPhase == 4 && !imeVisible,
                enter = expandVertically(animationSpec = tween(1200)) + fadeIn(animationSpec = tween(1000, delayMillis = 200)),
                exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(animationSpec = tween(200))
            ) {
                val cardsAlpha = remember { Animatable(0f) }
                LaunchedEffect(Unit) { cardsAlpha.animateTo(1f, tween(1800)) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 48.dp, bottom = 8.dp)
                        .alpha(cardsAlpha.value),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf(uiState.card1, uiState.card2, uiState.card3)
                        .zip(positions)
                        .forEach { (card, label) ->
                            CardSlotRevealed(
                                modifier = Modifier.weight(1f),
                                card = card,
                                position = label,
                                onBoundsChanged = { bounds -> card?.let { cardBoundsMap[it.id] = bounds } },
                                onClick = {
                                    zoomedCardBounds = card?.let { cardBoundsMap[it.id] } ?: Rect.Zero
                                    zoomedCard = card
                                }
                            )
                        }
                }
            }

            AnimatedVisibility(
                visible = shuffleDone && uiState.selectedTheme == null,
                enter = fadeIn(animationSpec = tween(durationMillis = 1400)),
                exit = fadeOut(animationSpec = tween(durationMillis = 800))
            ) {
                ThemeSelector(onThemeSelected = { viewModel.selectTheme(it) })
            }

            // Zone scrollable
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (revealPhase) {
                    4 -> {
                        Spacer(Modifier.height(16.dp))
                        listOf(
                            uiState.card1 to positions[0],
                            uiState.card2 to positions[1],
                            uiState.card3 to positions[2]
                        ).forEach { (card, posLabel) ->
                            AnimatedCardMessage(
                                card = card,
                                posLabel = posLabel,
                                theme = uiState.selectedTheme ?: "spirituel"
                            )
                        }
                        NoteFieldThree(note = uiState.note, onSave = viewModel::saveNote)
                        Spacer(Modifier.height(4.dp))
                        val context = LocalContext.current
                        val cards3 = listOf(uiState.card1, uiState.card2, uiState.card3)
                        val positions3 = if (Locale.getDefault().language == "fr")
                            listOf("Corps", "Âme", "Esprit") else listOf("Body", "Soul", "Spirit")
                        val resIds3 = remember(uiState.card1, uiState.card2, uiState.card3) {
                            cards3.map { card ->
                                card?.imageName?.let { name ->
                                    context.resources.getIdentifier(name, "drawable", context.packageName)
                                } ?: 0
                            }
                        }
                        OutlinedButton(
                            onClick = {
                                val theme = uiState.selectedTheme ?: "spirituel"
                                val note = uiState.note
                                val shareText = buildString {
                                    appendLine("✦ Oracle de Marie — Tirage 3 cartes")
                                    appendLine("Thème : ${themeLabel(theme)}")
                                    cards3.zip(positions3).forEach { (card, label) ->
                                        if (card != null) {
                                            appendLine()
                                            appendLine("✦ $label — ${card.localeName}")
                                            appendLine(card.localeSituationForTheme(theme))
                                            append(card.localeConseilForTheme(theme))
                                        }
                                    }
                                    if (note.isNotBlank()) {
                                        appendLine()
                                        appendLine()
                                        append("« $note »")
                                    }
                                }
                                val uris = cards3.mapIndexedNotNull { i, card ->
                                    card?.imageName?.let { name ->
                                        cardImageUri(context, resIds3[i], "${name}_share")
                                    }
                                }
                                val intent = if (uris.isNotEmpty()) {
                                    Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                                        type = "image/*"
                                        putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                } else {
                                    Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                    }
                                }
                                context.startActivity(Intent.createChooser(intent, null))
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SacredGold),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SacredGold.copy(alpha = 0.5f))
                        ) {
                            Text(stringResource(R.string.home_share))
                        }
                        OutlinedButton(
                            onClick = { viewModel.drawNewCards() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SoftWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SacredGold.copy(alpha = 0.5f))
                        ) {
                            Text(stringResource(R.string.troiscartes_new))
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }

        // Overlay séquence 3 cartes
        if (revealPhase == 1
            && uiState.card1 != null
            && uiState.card2 != null
            && uiState.card3 != null
        ) {
            TroisCartesCardOverlay(
                card1 = uiState.card1!!,
                card2 = uiState.card2!!,
                card3 = uiState.card3!!,
                shuffleCenter = shuffleCenter,
                overlayMessages = overlayMessages,
                onDone = { overlayDone = true }
            )
        }

        // Overlay zoom sur une carte de la phase 4
        if (zoomedCard != null) {
            CardZoomOverlay(
                card = zoomedCard!!,
                cardBounds = zoomedCardBounds,
                onDismiss = { zoomedCard = null }
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Overlay zoom : la carte s'agrandit depuis sa position dans la grille
// ---------------------------------------------------------------------------
@SuppressLint("DiscouragedApi")
@Composable
private fun CardZoomOverlay(
    card: Card,
    cardBounds: Rect,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val localDensity = LocalDensity.current
    val configuration = LocalConfiguration.current

    val imageResId = remember(card.imageName) {
        context.resources.getIdentifier(card.imageName, "drawable", context.packageName)
    }

    val screenWidthPx = with(localDensity) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(localDensity) { configuration.screenHeightDp.dp.toPx() }

    // Calcul de l'état initial : la carte part de sa position dans la ligne
    val startScale = if (cardBounds.width > 0f)
        (cardBounds.width / (screenWidthPx * 0.9f)).coerceIn(0.1f, 0.8f)
    else 0.3f
    val initialTx = cardBounds.center.x - screenWidthPx / 2f
    val initialTy = cardBounds.center.y - screenHeightPx / 2f

    val scale = remember { Animatable(startScale) }
    val entryTx = remember { Animatable(initialTx) }
    val entryTy = remember { Animatable(initialTy) }
    val bgAlpha = remember { Animatable(0f) }
    var isDismissing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        launch { bgAlpha.animateTo(1f, tween(700, easing = FastOutSlowInEasing)) }
        launch { entryTx.animateTo(0f, tween(1200, easing = FastOutSlowInEasing)) }
        launch { entryTy.animateTo(0f, tween(1200, easing = FastOutSlowInEasing)) }
        scale.animateTo(1f, tween(1200, easing = FastOutSlowInEasing))
    }

    LaunchedEffect(isDismissing) {
        if (isDismissing) {
            launch { bgAlpha.animateTo(0f, tween(1200, easing = FastOutSlowInEasing)) }
            launch { entryTx.animateTo(initialTx, tween(1200, easing = FastOutSlowInEasing)) }
            launch { entryTy.animateTo(initialTy, tween(1200, easing = FastOutSlowInEasing)) }
            scale.animateTo(startScale, tween(1200, easing = FastOutSlowInEasing))
            onDismiss()
        }
    }

    val animDone = scale.value >= 1f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(bgAlpha.value)
            .then(if (animDone && !isDismissing) Modifier.clickable { isDismissing = true } else Modifier)
    ) {
        StarryBackground(modifier = Modifier.fillMaxSize()) {}

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(2f / 3f)
                .align(Alignment.Center)
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                    translationX = entryTx.value
                    translationY = entryTy.value
                    shape = RoundedCornerShape(20.dp)
                    clip = true
                }
        ) {
            if (imageResId != 0) {
                AsyncImage(
                    model = imageResId,
                    contentDescription = card.localeName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        if (animDone && !isDismissing) {
            Text(
                text = card.localeName,
                style = MaterialTheme.typography.titleSmall,
                color = SacredGold,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 56.dp)
            )
            Text(
                text = stringResource(R.string.troiscartes_dismiss),
                style = MaterialTheme.typography.bodySmall,
                color = SoftWhite.copy(alpha = 0.35f),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 36.dp)
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Overlay séquence 3 cartes : entrée, transition sans dos, dismiss
// ---------------------------------------------------------------------------
@SuppressLint("DiscouragedApi")
@Composable
private fun TroisCartesCardOverlay(
    card1: Card,
    card2: Card,
    card3: Card,
    shuffleCenter: Offset,
    overlayMessages: List<String>,
    onDone: () -> Unit
) {
    val context = LocalContext.current
    val localDensity = LocalDensity.current
    val configuration = LocalConfiguration.current
    val scope = rememberCoroutineScope()

    val cards = remember { listOf(card1, card2, card3) }

    var cardIndex by remember { mutableIntStateOf(0) }
    var displayedCardIndex by remember { mutableIntStateOf(0) }

    val screenWidthPx = with(localDensity) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(localDensity) { configuration.screenHeightDp.dp.toPx() }
    val startScale = (with(localDensity) { 240.dp.toPx() } / (screenWidthPx * 0.9f)).coerceIn(0.3f, 0.9f)
    val initialTy = if (shuffleCenter == Offset.Zero) 0f else shuffleCenter.y - screenHeightPx / 2f

    val progress = remember { Animatable(0f) }
    val entryAlpha = remember { Animatable(1f) }  // dos visible immédiatement
    val bgAlpha = remember { Animatable(0f) }     // fond monte progressivement → ThemeSelector visible dessous
    val entryTranslationY = remember { Animatable(initialTy) }
    val transScaleX = remember { Animatable(1f) }
    var isDismissing by remember { mutableStateOf(false) }
    val dismissAlpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        launch { bgAlpha.animateTo(1f, tween(800, easing = FastOutSlowInEasing)) }
        launch { entryTranslationY.animateTo(0f, tween(2400, easing = FastOutSlowInEasing)) }
        progress.animateTo(1f, tween(2400, easing = FastOutSlowInEasing))
    }

    LaunchedEffect(isDismissing) {
        if (isDismissing) {
            dismissAlpha.animateTo(0f, tween(1200, easing = FastOutSlowInEasing))
            onDone()
        }
    }

    val showFront = progress.value > 0.5f
    val animDone = progress.value >= 1f

    val displayedCard = cards[displayedCardIndex]
    val imageResId = remember(displayedCard.imageName) {
        context.resources.getIdentifier(displayedCard.imageName, "drawable", context.packageName)
    }

    val handleTap: () -> Unit = {
        if (animDone && !isDismissing && transScaleX.value > 0.9f) {
            if (cardIndex < 2) {
                scope.launch {
                    transScaleX.animateTo(0f, tween(700, easing = FastOutSlowInEasing))
                    cardIndex++
                    displayedCardIndex = cardIndex
                    transScaleX.animateTo(1f, tween(700, easing = FastOutSlowInEasing))
                }
            } else {
                isDismissing = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(if (animDone && !isDismissing) Modifier.clickable { handleTap() } else Modifier)
    ) {
        StarryBackground(modifier = Modifier
            .fillMaxSize()
            .alpha(if (isDismissing) dismissAlpha.value else bgAlpha.value)
        ) {}

        // Carte et texte en fondu progressif
        Box(modifier = Modifier
            .fillMaxSize()
            .alpha(if (isDismissing) dismissAlpha.value else entryAlpha.value)
        ) {

        // Texte au-dessus de la carte — élément séparé, sans impact sur le layout de la carte
        val textAlpha = ((progress.value - 0.55f) * 3f).coerceIn(0f, 1f) * transScaleX.value
        val cardHeightDp = configuration.screenWidthDp * 0.9f * 1.5f
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = (configuration.screenHeightDp / 2f + cardHeightDp / 2f + 16f).dp)
                .alpha(textAlpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = displayedCard.localeName,
                style = MaterialTheme.typography.titleMedium,
                color = SacredGold,
                textAlign = TextAlign.Center
            )
            Text(
                text = displayedCard.localeKeyword,
                style = MaterialTheme.typography.bodyMedium,
                color = SoftWhite.copy(alpha = 0.70f),
                textAlign = TextAlign.Center
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(2f / 3f)
                .align(Alignment.Center)
                .graphicsLayer {
                    val entryScale = startScale + (1f - startScale) * progress.value
                    scaleX = entryScale * transScaleX.value
                    scaleY = entryScale
                    rotationY = progress.value * 180f
                    translationY = if (isDismissing) 0f else entryTranslationY.value
                    cameraDistance = 12f * density
                    shape = RoundedCornerShape(20.dp)
                    clip = true
                }
        ) {
            if (!showFront) {
                Image(
                    painter = painterResource(R.drawable.card_back),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(modifier = Modifier.graphicsLayer { rotationY = 180f }) {
                    if (imageResId != 0) {
                        AsyncImage(
                            model = imageResId,
                            contentDescription = displayedCard.localeName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        if (animDone && !isDismissing) {
            Text(
                text = overlayMessages[cardIndex],
                style = MaterialTheme.typography.bodySmall,
                color = SoftWhite.copy(alpha = 0.45f),
                modifier = Modifier
                    .alpha(transScaleX.value)
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp)
            )
        }
        } // ferme Box(inner) carte + texte en fondu
    }
}

// ---------------------------------------------------------------------------
// Phase 4 — Carte face visible sans animation de flip
// ---------------------------------------------------------------------------
@SuppressLint("DiscouragedApi")
@Composable
private fun CardSlotRevealed(
    modifier: Modifier = Modifier,
    card: Card?,
    position: String,
    onBoundsChanged: (Rect) -> Unit = {},
    onClick: () -> Unit = {}
) {
    if (card == null) return
    val context = LocalContext.current
    val imageResId = remember(card.imageName) {
        context.resources.getIdentifier(card.imageName, "drawable", context.packageName)
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(text = position, style = MaterialTheme.typography.labelMedium, color = SacredGold.copy(alpha = 0.8f))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, SacredGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .onGloballyPositioned { coords -> onBoundsChanged(coords.boundsInWindow()) }
                .clickable { onClick() }
        ) {
            if (imageResId != 0) {
                AsyncImage(
                    model = imageResId,
                    contentDescription = card.localeName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Text(text = card.localeName, style = MaterialTheme.typography.labelSmall, color = SoftWhite, textAlign = TextAlign.Center, maxLines = 2)
    }
}

@Composable
private fun AnimatedCardMessage(card: Card?, posLabel: String, theme: String) {
    if (card == null) return
    val alpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(1800L)
        alpha.animateTo(1f, tween(1200))
    }
    Box(modifier = Modifier.alpha(alpha.value)) {
        CardMessage(card = card, theme = theme, positionLabel = posLabel)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NoteFieldThree(note: String, onSave: (String) -> Unit) {
    var text by remember(note) { mutableStateOf(note) }
    var saved by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it; saved = false },
            placeholder = {
                Text(
                    stringResource(R.string.troiscartes_note_placeholder),
                    color = SoftWhite.copy(alpha = 0.35f),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            supportingText = {
                Text(
                    stringResource(R.string.note_journal_hint),
                    color = SoftWhite.copy(alpha = 0.4f),
                    style = MaterialTheme.typography.labelSmall
                )
            },
            minLines = 2,
            maxLines = 5,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SacredGold.copy(alpha = 0.7f),
                unfocusedBorderColor = SacredGold.copy(alpha = 0.3f),
                focusedTextColor = SoftWhite,
                unfocusedTextColor = SoftWhite,
                cursorColor = SacredGold,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50))
                .background(MarianBlue.copy(alpha = 0.6f))
                .border(1.dp, SacredGold.copy(alpha = 0.5f), RoundedCornerShape(50))
                .clickable {
                    onSave(text)
                    saved = true
                    focusManager.clearFocus()
                }
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (saved) stringResource(R.string.home_note_saved) else stringResource(R.string.home_note_save),
                style = MaterialTheme.typography.bodyMedium,
                color = SacredGold
            )
        }
    }
}

@Composable
private fun CardMessage(card: Card, theme: String, positionLabel: String) {
    val situation = card.localeSituationForTheme(theme)
    val conseil = card.localeConseilForTheme(theme)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MarianBlue.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            .border(1.dp, SacredGold.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "✦ $positionLabel — ${card.localeName}", style = MaterialTheme.typography.labelLarge, color = SacredGold)
            Text(text = situation, style = MaterialTheme.typography.bodySmall, color = SoftWhite.copy(alpha = 0.9f))
            Text(text = conseil, style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic), color = SoftWhite.copy(alpha = 0.7f))
        }
    }
}
