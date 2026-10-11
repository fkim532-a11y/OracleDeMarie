package com.kim.oraclemarie.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.content.Intent
import androidx.compose.ui.res.stringResource
import com.kim.oraclemarie.R
import com.kim.oraclemarie.data.model.Card
import com.kim.oraclemarie.data.model.localeName
import com.kim.oraclemarie.data.model.localeKeyword
import com.kim.oraclemarie.data.model.localeSituationForTheme
import com.kim.oraclemarie.data.model.localeConseilForTheme
import com.kim.oraclemarie.ui.components.ShuffleAnimation
import com.kim.oraclemarie.ui.components.StarryBackground
import com.kim.oraclemarie.ui.components.ThemeSelector
import com.kim.oraclemarie.ui.theme.MarianBlue
import com.kim.oraclemarie.ui.theme.SacredGold
import com.kim.oraclemarie.ui.theme.SoftWhite
import com.kim.oraclemarie.ui.utils.cardImageUri


@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToCardDetail: (Int, Int) -> Unit = { _, _ -> },
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var isCardFullscreen by remember { mutableStateOf(false) }
    var fullscreenWasShown by remember { mutableStateOf(false) }
    var cardFlipCenter by remember { mutableStateOf(Offset.Zero) }
    var cardFlipWidthPx by remember { mutableStateOf(0f) }

    LaunchedEffect(uiState.isFlipped) {
        if (uiState.isFlipped) {
            isCardFullscreen = true
            fullscreenWasShown = true
        } else {
            isCardFullscreen = false
            fullscreenWasShown = false
        }
    }

    StarryBackground {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = SacredGold
                )
            }
            uiState.card != null -> {
                HomeContent(
                    uiState = uiState,
                    isCardFullscreen = isCardFullscreen,
                    fullscreenWasShown = fullscreenWasShown,
                    onThemeSelected = viewModel::selectTheme,
                    onSaveNote = viewModel::saveNote,
                    onCardPositioned = { center, widthPx ->
                        cardFlipCenter = center
                        cardFlipWidthPx = widthPx
                    }
                )
            }
            else -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = SacredGold)
                    Spacer(Modifier.height(16.dp))
                    Text("Chargement des cartes...", color = SoftWhite)
                }
            }
        }
        if (isCardFullscreen && uiState.card != null) {
            CardFullscreenOverlay(
                card = uiState.card!!,
                cardFlipCenter = cardFlipCenter,
                cardFlipWidthPx = cardFlipWidthPx,
                onDismiss = { isCardFullscreen = false }
            )
        }
        IconButton(
            onClick = {
                when {
                    isCardFullscreen -> isCardFullscreen = false
                    else -> onNavigateBack()
                }
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back_to_portal),
                tint = SoftWhite.copy(alpha = 0.5f)
            )
        }
    }
}

private fun themeToTab(theme: String?): Int = when (theme) {
    "spirituel" -> 0
    "amour"     -> 1
    "travail"   -> 2
    "sante"     -> 3
    "famille"   -> 4
    else        -> 0
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
private fun HomeContent(
    uiState: HomeUiState,
    isCardFullscreen: Boolean,
    fullscreenWasShown: Boolean = false,
    onThemeSelected: (String) -> Unit,
    onSaveNote: (String) -> Unit = {},
    onCardPositioned: (Offset, Float) -> Unit = { _, _ -> }
) {
    val card = uiState.card!!
    var shuffleDone by remember(uiState.shuffleKey) { mutableStateOf(uiState.isFlipped) }
    val imeVisible = WindowInsets.isImeVisible
    val cardHeight by animateDpAsState(
        targetValue = if (imeVisible) 0.dp else 410.dp,
        animationSpec = tween(300),
        label = "cardHeight"
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.oracle_name),
            style = MaterialTheme.typography.headlineMedium,
            color = SacredGold,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.home_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = SoftWhite.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(6.dp))

        // Carte hors du scroll → pleine largeur, pas de clip
        // Se rétracte à 0dp quand le clavier est visible
        Box(
            modifier = Modifier.fillMaxWidth().height(cardHeight),
            contentAlignment = Alignment.Center
        ) {
            if (!shuffleDone && !uiState.isFlipped) {
                ShuffleAnimation(onDone = { shuffleDone = true })
            } else {
                Box(
                    modifier = Modifier
                        .alpha(if (isCardFullscreen) 0f else 1f)
                        .onGloballyPositioned { coords ->
                            val pos = coords.positionInWindow()
                            onCardPositioned(
                                Offset(pos.x + coords.size.width / 2f, pos.y + coords.size.height / 2f),
                                coords.size.width.toFloat()
                            )
                        }
                ) {
                    CardFlip(
                        card = card,
                        isFlipped = uiState.isFlipped
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = !uiState.isFlipped && shuffleDone,
            enter = fadeIn(animationSpec = tween(durationMillis = 1400)),
            exit = fadeOut(animationSpec = tween(durationMillis = 400))
        ) {
            ThemeSelector(onThemeSelected = onThemeSelected)
        }

        // Contenu scrollable en dessous
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))

            AnimatedVisibility(
                visible = uiState.isFlipped && uiState.selectedTheme != null && !isCardFullscreen && fullscreenWasShown,
                enter = fadeIn(animationSpec = tween(durationMillis = 800)),
                exit = fadeOut(animationSpec = tween(durationMillis = 200))
            ) {
                Column {
                    CardMessage(card = card, theme = uiState.selectedTheme ?: "spirituel")
                    Spacer(Modifier.height(20.dp))
                    NoteField(note = uiState.note, onSave = onSaveNote)
                    Spacer(Modifier.height(16.dp))
                    ShareButton(card = card, theme = uiState.selectedTheme ?: "spirituel")
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun CardFlip(card: Card, isFlipped: Boolean) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "cardFlip"
    )
    Box(
        modifier = Modifier
            .width(240.dp)
            .height(360.dp)
            .clip(RoundedCornerShape(16.dp))
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
    ) {
        if (rotation <= 90f) {
            CardBack()
        } else {
            Box(modifier = Modifier.graphicsLayer { rotationY = 180f }) {
                CardFront(card)
            }
        }
    }
}

@Composable
private fun CardBack() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, SacredGold.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
    ) {
        Image(
            painter = painterResource(R.drawable.card_back),
            contentDescription = stringResource(R.string.card_back_desc),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@SuppressLint("DiscouragedApi")
@Composable
private fun CardFront(card: Card) {
    val context = LocalContext.current
    val imageResId = remember(card.imageName) {
        context.resources.getIdentifier(card.imageName, "drawable", context.packageName)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, SacredGold.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
    ) {
        AsyncImage(
            model = imageResId,
            contentDescription = card.localeName,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}


@Composable
private fun CardMessage(card: Card, theme: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = card.localeName,
                style = MaterialTheme.typography.headlineMedium,
                color = SacredGold,
                textAlign = TextAlign.Center
            )
            Text(
                text = card.localeKeyword,
                style = MaterialTheme.typography.bodyMedium,
                color = SoftWhite.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }

        HorizontalDivider(color = SacredGold.copy(alpha = 0.3f))

        Text(
            text = card.localeSituationForTheme(theme),
            style = MaterialTheme.typography.bodyLarge,
            color = SoftWhite,
            textAlign = TextAlign.Justify
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MarianBlue.copy(alpha = 0.3f)
        ) {
            Text(
                text = card.localeConseilForTheme(theme),
                style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                color = SoftWhite,
                textAlign = TextAlign.Justify,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NoteField(note: String, onSave: (String) -> Unit) {
    var text by remember(note) { mutableStateOf(note) }
    var saved by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                saved = false
            },
            placeholder = {
                Text(
                    stringResource(R.string.home_note_placeholder),
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

@SuppressLint("DiscouragedApi")
@Composable
private fun ShareButton(card: Card, theme: String) {
    val context = LocalContext.current
    val imageResId = remember(card.imageName) {
        context.resources.getIdentifier(card.imageName, "drawable", context.packageName)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(MarianBlue.copy(alpha = 0.5f))
            .border(1.dp, SacredGold.copy(alpha = 0.6f), RoundedCornerShape(50))
            .clickable {
                val text = buildString {
                    appendLine("✦ Oracle de Marie — ${card.localeName}")
                    appendLine(card.localeKeyword)
                    appendLine()
                    appendLine(card.localeSituationForTheme(theme))
                    appendLine()
                    append("« ${card.localeConseilForTheme(theme)} »")
                }
                val imageUri = cardImageUri(context, imageResId, card.imageName)
                val intent = if (imageUri != null) {
                    Intent(Intent.ACTION_SEND).apply {
                        type = "image/*"
                        putExtra(Intent.EXTRA_STREAM, imageUri)
                        putExtra(Intent.EXTRA_TEXT, text)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                } else {
                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, text)
                    }
                }
                context.startActivity(Intent.createChooser(intent, null))
            }
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.home_share),
            style = MaterialTheme.typography.bodyLarge,
            color = SacredGold
        )
    }
}

@SuppressLint("DiscouragedApi")
@Composable
private fun CardFullscreenOverlay(
    card: Card,
    cardFlipCenter: Offset,
    cardFlipWidthPx: Float,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val imageResId = remember(card.imageName) {
        context.resources.getIdentifier(card.imageName, "drawable", context.packageName)
    }
    val configuration = LocalConfiguration.current
    val localDensity = LocalDensity.current
    val screenWidthDp = configuration.screenWidthDp
    val screenWidthPx = with(localDensity) { screenWidthDp.dp.toPx() }
    val screenHeightPx = with(localDensity) { configuration.screenHeightDp.dp.toPx() }

    // La carte overlay fait fillMaxWidth(0.9f) → pour démarrer à 240dp, le scale est 240/(screenWidth*0.9)
    val startScale = 240f / (screenWidthDp * 0.9f)

    // Offset initial : la carte réelle n'est pas centrée à l'écran (il y a des titres au-dessus)
    val initialTx = if (cardFlipCenter != Offset.Zero) cardFlipCenter.x - screenWidthPx / 2f else 0f
    val initialTy = if (cardFlipCenter != Offset.Zero) cardFlipCenter.y - screenHeightPx / 2f else 0f

    val progress = remember { Animatable(0f) }
    val bgAlpha = remember { Animatable(0f) }
    val entryTranslationX = remember { Animatable(initialTx) }
    val entryTranslationY = remember { Animatable(initialTy) }
    var isDismissing by remember { mutableStateOf(false) }
    val dismissScale = remember { Animatable(1f) }
    val dismissTranslationX = remember { Animatable(0f) }
    val dismissTranslationY = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch { bgAlpha.animateTo(1f, tween(1000, easing = FastOutSlowInEasing)) }
        launch { entryTranslationX.animateTo(0f, tween(800, easing = FastOutSlowInEasing)) }
        launch { entryTranslationY.animateTo(0f, tween(800, easing = FastOutSlowInEasing)) }
        delay(1600)
        progress.animateTo(1f, tween(2800, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(isDismissing) {
        if (isDismissing) {
            val overlayCardWidthPx = screenWidthPx * 0.9f
            val targetScale = if (cardFlipWidthPx > 0f) cardFlipWidthPx / overlayCardWidthPx else startScale
            val targetTx = if (cardFlipWidthPx > 0f) cardFlipCenter.x - screenWidthPx / 2f else 0f
            val targetTy = if (cardFlipWidthPx > 0f) cardFlipCenter.y - screenHeightPx / 2f else 0f

            launch { bgAlpha.animateTo(0f, tween(450, easing = FastOutSlowInEasing)) }
            launch { dismissTranslationX.animateTo(targetTx, tween(450, easing = FastOutSlowInEasing)) }
            launch { dismissTranslationY.animateTo(targetTy, tween(450, easing = FastOutSlowInEasing)) }
            dismissScale.animateTo(targetScale, tween(800, easing = FastOutSlowInEasing))
            onDismiss()
        }
    }

    val showFront = progress.value > 0.5f
    val animDone = progress.value >= 1f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(if (animDone && !isDismissing) Modifier.clickable(onClick = { isDismissing = true }) else Modifier)
    ) {
        StarryBackground(modifier = Modifier.fillMaxSize().alpha(bgAlpha.value)) {}
        // Texte au-dessus de la carte — élément séparé, sans impact sur le layout de la carte
        val textAlpha = if (isDismissing) 0f else ((progress.value - 0.55f) * 3f).coerceIn(0f, 1f)
        val cardHeightDp = screenWidthDp * 0.9f * 1.5f
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = (configuration.screenHeightDp / 2f + cardHeightDp / 2f + 16f).dp)
                .alpha(textAlpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = card.localeName,
                style = MaterialTheme.typography.titleMedium,
                color = SacredGold,
                textAlign = TextAlign.Center
            )
            Text(
                text = card.localeKeyword,
                style = MaterialTheme.typography.bodyMedium,
                color = SoftWhite.copy(alpha = 0.70f),
                textAlign = TextAlign.Center
            )
        }

        // Outer Box : translation (entrée depuis la position réelle, sortie vers la position réelle)
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(2f / 3f)
                .align(Alignment.Center)
                .graphicsLayer {
                    translationX = if (isDismissing) dismissTranslationX.value else entryTranslationX.value
                    translationY = if (isDismissing) dismissTranslationY.value else entryTranslationY.value
                }
        ) {
            // Inner Box : scale + flip + clip arrondi
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = if (isDismissing) dismissScale.value else startScale + (1f - startScale) * progress.value
                        scaleY = if (isDismissing) dismissScale.value else startScale + (1f - startScale) * progress.value
                        rotationY = progress.value * 180f
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
                        AsyncImage(
                            model = imageResId,
                            contentDescription = card.localeName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
        if (animDone && !isDismissing) {
            Text(
                text = stringResource(R.string.home_reveal),
                style = MaterialTheme.typography.bodySmall,
                color = SoftWhite.copy(alpha = 0.45f),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp)
            )
        }
    }
}

