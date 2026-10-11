package com.kim.oraclemarie.ui.journal

import android.content.Intent
import com.kim.oraclemarie.ui.utils.cardImageUri
import com.kim.oraclemarie.ui.utils.themeLabel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import androidx.compose.ui.res.stringResource
import com.kim.oraclemarie.R
import com.kim.oraclemarie.data.model.Card
import com.kim.oraclemarie.data.model.localeName
import com.kim.oraclemarie.data.model.localeKeyword
import com.kim.oraclemarie.data.model.localeSituationForTheme
import com.kim.oraclemarie.data.model.localeConseilForTheme
import com.kim.oraclemarie.ui.components.StarryBackground
import com.kim.oraclemarie.ui.theme.MarianBlue
import com.kim.oraclemarie.ui.theme.SacredGold
import com.kim.oraclemarie.ui.theme.SoftWhite
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dateFormatter get() = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.getDefault())

private fun Card.interpretationFor(theme: String): Pair<String, String> =
    localeSituationForTheme(theme) to localeConseilForTheme(theme)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: JournalViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var expandedKey by remember { mutableStateOf<Long?>(null) }
    val screenAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        screenAlpha.animateTo(1f, tween(durationMillis = 2400))
    }

    StarryBackground {
        Column(modifier = Modifier.fillMaxSize().graphicsLayer { alpha = screenAlpha.value }) {
            TopAppBar(
                title = {},
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

            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = SacredGold)
                    }
                }
                uiState.entries.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("✦", style = MaterialTheme.typography.headlineLarge, color = SacredGold.copy(alpha = 0.4f))
                            Spacer(Modifier.height(12.dp))
                            Text(
                                stringResource(R.string.journal_empty),
                                style = MaterialTheme.typography.bodyMedium,
                                color = SoftWhite.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        item {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                Spacer(Modifier.height(32.dp))
                                Text("Mon Journal", style = MaterialTheme.typography.headlineSmall, color = SacredGold, textAlign = TextAlign.Center)
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                        items(uiState.entries) { entry ->
                            when (entry) {
                                is JournalEntry.Single -> SingleEntryCard(
                                    entry = entry,
                                    isExpanded = expandedKey == entry.draw.timestamp,
                                    onClick = {
                                        expandedKey = if (expandedKey == entry.draw.timestamp) null else entry.draw.timestamp
                                    }
                                )
                                is JournalEntry.Three -> ThreeEntryCard(
                                    entry = entry,
                                    isExpanded = expandedKey == entry.draw.timestamp,
                                    onClick = {
                                        expandedKey = if (expandedKey == entry.draw.timestamp) null else entry.draw.timestamp
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── Tirage 1 carte ──────────────────────────────────────────────────────────

@Composable
private fun SingleEntryCard(
    entry: JournalEntry.Single,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val date = remember(entry.draw.timestamp) {
        Instant.ofEpochMilli(entry.draw.timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .format(dateFormatter)
    }
    val imageResId = remember(entry.card?.imageName) {
        entry.card?.imageName?.let {
            context.resources.getIdentifier(it, "drawable", context.packageName)
        } ?: 0
    }
    val interpretation = remember(entry.card, entry.draw.selectedTheme) {
        entry.card?.interpretationFor(entry.draw.selectedTheme)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MarianBlue.copy(alpha = 0.18f), RoundedCornerShape(16.dp))
            .border(
                1.dp,
                SacredGold.copy(alpha = if (isExpanded) 0.5f else 0.25f),
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // En-tête compact : toujours visible
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CardThumbnail(imageResId = imageResId, widthDp = 48)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(text = date, style = MaterialTheme.typography.labelMedium, color = SacredGold.copy(alpha = 0.7f))
                    Text(text = entry.card?.localeName ?: stringResource(R.string.journal_unknown_card), style = MaterialTheme.typography.titleSmall, color = SoftWhite)
                    Text(text = entry.card?.localeKeyword ?: "", style = MaterialTheme.typography.bodySmall, color = SoftWhite.copy(alpha = 0.55f))
                }
                ThemeBadge(theme = entry.draw.selectedTheme)
            }

            // Contenu détaillé : déplie au tap
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(900)) + fadeIn(animationSpec = tween(700, delayMillis = 200)),
                exit = shrinkVertically(animationSpec = tween(650)) + fadeOut(animationSpec = tween(400))
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        IconButton(onClick = {
                            val shareText = buildShareTextSingle(
                                date = date,
                                cardName = entry.card?.localeName ?: "",
                                keyword = entry.card?.localeKeyword ?: "",
                                theme = entry.draw.selectedTheme,
                                interpretation = interpretation,
                                note = entry.draw.userNote
                            )
                            val imageUri = cardImageUri(context, imageResId, entry.card?.imageName ?: "card")
                            val intent = if (imageUri != null) {
                                Intent(Intent.ACTION_SEND).apply {
                                    type = "image/*"
                                    putExtra(Intent.EXTRA_STREAM, imageUri)
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                            } else {
                                Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                }
                            }
                            context.startActivity(Intent.createChooser(intent, context.getString(R.string.journal_share_chooser)))
                        }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = stringResource(R.string.share),
                                tint = SacredGold.copy(alpha = 0.8f)
                            )
                        }
                    }

                    if (interpretation != null) {
                        HorizontalDivider(color = SacredGold.copy(alpha = 0.15f))
                        InterpretationBlock(situation = interpretation.first, conseil = interpretation.second)
                    }

                    if (entry.draw.userNote.isNotBlank()) {
                        UserNote(text = entry.draw.userNote)
                    }
                }
            }
        }
    }
}

private fun buildShareTextSingle(
    date: String,
    cardName: String,
    keyword: String,
    theme: String,
    interpretation: Pair<String, String>?,
    note: String
): String = buildString {
    appendLine("✦ Oracle de Marie — $date")
    appendLine("Carte : $cardName — $keyword")
    appendLine("Thème : ${themeLabel(theme)}")
    if (interpretation != null) {
        appendLine()
        appendLine(interpretation.first)
        appendLine(interpretation.second)
    }
    if (note.isNotBlank()) {
        appendLine()
        append("« $note »")
    }
}

// ─── Tirage 3 cartes ─────────────────────────────────────────────────────────

private fun threeCardPositions(): List<String> {
    val isFr = Locale.getDefault().language == "fr"
    return if (isFr) listOf("Corps", "Âme", "Esprit") else listOf("Body", "Soul", "Spirit")
}

@Composable
private fun ThreeEntryCard(
    entry: JournalEntry.Three,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val date = remember(entry.draw.timestamp) {
        Instant.ofEpochMilli(entry.draw.timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .format(dateFormatter)
    }
    val cards = listOf(entry.card1, entry.card2, entry.card3)
    val imageResIds = cards.map { card ->
        remember(card?.imageName) {
            card?.imageName?.let {
                context.resources.getIdentifier(it, "drawable", context.packageName)
            } ?: 0
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MarianBlue.copy(alpha = 0.18f), RoundedCornerShape(16.dp))
            .border(
                1.dp,
                SacredGold.copy(alpha = if (isExpanded) 0.5f else 0.25f),
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // En-tête compact : toujours visible
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    imageResIds.forEach { resId ->
                        CardThumbnail(imageResId = resId, widthDp = 36)
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(text = date, style = MaterialTheme.typography.labelMedium, color = SacredGold.copy(alpha = 0.7f))
                    Text(
                        text = stringResource(R.string.journal_three_label),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Normal,
                        color = SoftWhite
                    )
                }
                ThemeBadge(theme = entry.draw.selectedTheme)
            }

            // Contenu détaillé : déplie au tap
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(900)) + fadeIn(animationSpec = tween(700, delayMillis = 200)),
                exit = shrinkVertically(animationSpec = tween(650)) + fadeOut(animationSpec = tween(400))
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        IconButton(onClick = {
                            val shareText = buildShareTextThree(
                                date = date,
                                cards = cards,
                                theme = entry.draw.selectedTheme,
                                note = entry.draw.userNote
                            )
                            val uris = cards.mapIndexedNotNull { i, card ->
                                card?.imageName?.let { name ->
                                    val resId = context.resources.getIdentifier(name, "drawable", context.packageName)
                                    cardImageUri(context, resId, "${name}_$i")
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
                            context.startActivity(Intent.createChooser(intent, context.getString(R.string.journal_share_chooser)))
                        }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = stringResource(R.string.share),
                                tint = SacredGold.copy(alpha = 0.8f)
                            )
                        }
                    }

                    HorizontalDivider(color = SacredGold.copy(alpha = 0.15f))
                    cards.zip(threeCardPositions()).forEachIndexed { index, (card, label) ->
                        if (card != null) {
                            val interpretation = card.interpretationFor(entry.draw.selectedTheme)
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "✦ $label — ${card.localeName}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = SacredGold
                                )
                                InterpretationBlock(situation = interpretation.first, conseil = interpretation.second)
                            }
                            if (index < 2) HorizontalDivider(color = SacredGold.copy(alpha = 0.08f))
                        }
                    }

                    if (entry.draw.userNote.isNotBlank()) {
                        UserNote(text = entry.draw.userNote)
                    }
                }
            }
        }
    }
}

private fun buildShareTextThree(
    date: String,
    cards: List<Card?>,
    theme: String,
    note: String
): String = buildString {
    appendLine("✦ Oracle de Marie — Tirage 3 cartes — $date")
    appendLine("Thème : ${themeLabel(theme)}")
    cards.zip(threeCardPositions()).forEach { (card, label) ->
        if (card != null) {
            val (situation, conseil) = card.interpretationFor(theme)
            appendLine()
            appendLine("✦ $label — ${card.localeName}")
            appendLine(situation)
            appendLine(conseil)
        }
    }
    if (note.isNotBlank()) {
        appendLine()
        append("« $note »")
    }
}

// ─── Composables partagés ────────────────────────────────────────────────────

@Composable
private fun CardThumbnail(imageResId: Int, widthDp: Int) {
    Box(
        modifier = Modifier
            .width(widthDp.dp)
            .aspectRatio(2f / 3f)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, SacredGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
    ) {
        if (imageResId != 0) {
            AsyncImage(
                model = imageResId,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MarianBlue),
                contentAlignment = Alignment.Center
            ) {
                Text("✦", color = SacredGold.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
private fun InterpretationBlock(situation: String, conseil: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = situation,
            style = MaterialTheme.typography.bodySmall,
            color = SoftWhite.copy(alpha = 0.85f)
        )
        Text(
            text = conseil,
            style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
            color = SoftWhite.copy(alpha = 0.65f)
        )
    }
}

@Composable
private fun UserNote(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MarianBlue.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = "« $text »",
            style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
            color = SoftWhite.copy(alpha = 0.85f)
        )
    }
}

@Composable
private fun ThemeBadge(theme: String) {
    Box(
        modifier = Modifier
            .background(MarianBlue.copy(alpha = 0.7f), RoundedCornerShape(50))
            .border(1.dp, SacredGold.copy(alpha = 0.4f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Text(text = themeLabel(theme), style = MaterialTheme.typography.labelSmall, color = SacredGold)
    }
}
