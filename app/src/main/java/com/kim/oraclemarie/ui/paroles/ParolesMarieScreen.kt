package com.kim.oraclemarie.ui.paroles

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.kim.oraclemarie.R
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kim.oraclemarie.data.model.MarieQuote
import com.kim.oraclemarie.data.model.localeText
import com.kim.oraclemarie.ui.components.StarryBackground
import com.kim.oraclemarie.ui.theme.DarkNavy
import com.kim.oraclemarie.ui.theme.MarianBlue
import com.kim.oraclemarie.ui.theme.SacredGold
import com.kim.oraclemarie.ui.theme.SoftWhite
import kotlinx.coroutines.delay

@Composable
private fun categoryLabel(category: String): String = when (category) {
    "amour"     -> stringResource(R.string.category_amour)
    "paix"      -> stringResource(R.string.category_paix)
    "espoir"    -> stringResource(R.string.category_espoir)
    "force"     -> stringResource(R.string.category_force)
    "douceur"   -> stringResource(R.string.category_douceur)
    "gratitude" -> stringResource(R.string.category_gratitude)
    else        -> category.replaceFirstChar { it.uppercase() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParolesMarieScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: ParolesMarieViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var entryStep by remember { mutableIntStateOf(0) }
    var animationStarted by remember { mutableStateOf(false) }

    val buttonAlpha by animateFloatAsState(
        targetValue = if (entryStep >= 2) 1f else 0f,
        animationSpec = tween(1000),
        label = "buttonAlpha"
    )

    // Séquence d'entrée : une seule fois à l'arrivée sur l'écran
    LaunchedEffect(uiState.quote) {
        if (uiState.quote != null && !animationStarted) {
            animationStarted = true
            delay(300)
            entryStep = 1  // étoile + séparateurs
            delay(1300)
            entryStep = 2  // bouton
            delay(1400)
            entryStep = 3  // citation surgit
        }
    }

    StarryBackground {
        Image(
            painter = painterResource(R.drawable.portal_marie),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().graphicsLayer { alpha = 0.28f; scaleX = 0.88f; scaleY = 0.88f }
        )
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { },
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
                Box(modifier = Modifier.fillMaxSize()) {
                    AnimatedContent(
                        targetState = uiState.quote,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(5000)) togetherWith
                                    fadeOut(animationSpec = tween(800))
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 28.dp),
                        label = "quoteTransition"
                    ) { quote ->
                        if (quote != null) {
                            QuoteContent(quote = quote, entryStep = entryStep)
                        }
                    }

                    NewMessageButton(
                        onClick = viewModel::loadRandom,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(horizontal = 40.dp, vertical = 36.dp)
                            .alpha(buttonAlpha)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuoteContent(quote: MarieQuote, entryStep: Int) {

    val starAlpha by animateFloatAsState(
        targetValue = if (entryStep >= 1) 1f else 0f,
        animationSpec = tween(1200),
        label = "starAlpha"
    )

    // Le texte surgit : scale 0→1 avec rebond + alpha rapide
    val textScale by animateFloatAsState(
        targetValue = if (entryStep >= 3) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = 80f
        ),
        label = "textScale"
    )
    val textAlpha by animateFloatAsState(
        targetValue = if (entryStep >= 3) 1f else 0f,
        animationSpec = tween(1500),
        label = "textAlpha"
    )

    // Badge catégorie apparaît juste après le texte
    val categoryAlpha by animateFloatAsState(
        targetValue = if (entryStep >= 3) 1f else 0f,
        animationSpec = tween(400, delayMillis = 250),
        label = "catAlpha"
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(bottom = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "✦",
            style = MaterialTheme.typography.headlineLarge,
            color = SacredGold,
            modifier = Modifier.alpha(starAlpha)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.paroles_title),
            style = MaterialTheme.typography.titleMedium,
            color = SacredGold,
            modifier = Modifier.alpha(starAlpha)
        )

        Spacer(Modifier.height(20.dp))

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .alpha(starAlpha),
            color = SacredGold.copy(alpha = 0.4f)
        )

        Spacer(Modifier.height(32.dp))

        Text(
            text = "« ${quote.localeText} »",
            style = MaterialTheme.typography.titleLarge.copy(fontStyle = FontStyle.Italic),
            color = SoftWhite,
            textAlign = TextAlign.Center,
            lineHeight = MaterialTheme.typography.titleLarge.lineHeight * 1.3f,
            modifier = Modifier.graphicsLayer {
                scaleX = textScale
                scaleY = textScale
                alpha = textAlpha
            }
        )

        Spacer(Modifier.height(32.dp))

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .alpha(textAlpha),
            color = SacredGold.copy(alpha = 0.4f)
        )

        Spacer(Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .alpha(categoryAlpha)
                .background(MarianBlue.copy(alpha = 0.6f), RoundedCornerShape(50))
                .border(1.dp, SacredGold.copy(alpha = 0.5f), RoundedCornerShape(50))
                .padding(horizontal = 16.dp, vertical = 5.dp)
        ) {
            Text(
                text = categoryLabel(quote.category),
                style = MaterialTheme.typography.labelMedium,
                color = SacredGold
            )
        }
    }
}

@Composable
private fun NewMessageButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(DarkNavy.copy(alpha = 0.7f))
            .border(1.5.dp, SacredGold.copy(alpha = 0.8f), RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.paroles_new_message),
            style = MaterialTheme.typography.bodyLarge,
            color = SacredGold
        )
    }
}