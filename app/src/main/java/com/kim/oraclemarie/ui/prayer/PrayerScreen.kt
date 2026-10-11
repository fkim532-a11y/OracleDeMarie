package com.kim.oraclemarie.ui.prayer

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.kim.oraclemarie.R
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kim.oraclemarie.data.model.Prayer
import com.kim.oraclemarie.data.model.localeTitle
import com.kim.oraclemarie.data.model.localeText
import com.kim.oraclemarie.ui.components.StarryBackground
import com.kim.oraclemarie.ui.theme.DarkNavy
import com.kim.oraclemarie.ui.theme.SacredGold
import com.kim.oraclemarie.ui.theme.SoftWhite

private fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%02d:%02d".format(m, s)
}

@Composable
fun PrayerScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: PrayerViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val screenAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        screenAlpha.animateTo(1f, tween(durationMillis = 2400))
    }

    StarryBackground {
        Image(
            painter = painterResource(R.drawable.portal_marie),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().graphicsLayer { alpha = 0.28f; scaleX = 0.88f; scaleY = 0.88f }
        )
        AnimatedContent(
            targetState = uiState.mode,
            transitionSpec = {
                fadeIn(tween(400)) togetherWith fadeOut(tween(300))
            },
            modifier = Modifier.fillMaxSize().graphicsLayer { alpha = screenAlpha.value },
            label = "prayerMode"
        ) { mode ->
            when (mode) {
                PrayerMode.LIST -> PrayerListContent(
                    uiState = uiState,
                    onNavigateBack = onNavigateBack,
                    onSelectPrayer = viewModel::selectPrayer
                )
                PrayerMode.DETAIL -> PrayerDetailContent(
                    prayer = uiState.selectedPrayer,
                    isPlayingPrayer = uiState.isPlayingPrayer,
                    onBack = viewModel::backToList,
                    onReadPrayer = viewModel::openChoice,
                    onStopAudio = viewModel::stopPrayerAudio
                )
                PrayerMode.CHOICE -> PrayerChoiceContent(
                    uiState = uiState,
                    onBack = viewModel::backFromChoice,
                    onJustListen = viewModel::justListen,
                    onSetAmbiance = viewModel::setAmbiance,
                    onSetMinutes = viewModel::setChoiceMinutes,
                    onLaunch = viewModel::launchMeditation
                )
                PrayerMode.TIMER -> PrayerTimerContent(
                    uiState = uiState,
                    onBack = viewModel::backToDetail
                )
            }
        }
    }
}

// ─── Liste ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrayerListContent(
    uiState: PrayerUiState,
    onNavigateBack: () -> Unit,
    onSelectPrayer: (Prayer) -> Unit
) {
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

        Spacer(Modifier.height(100.dp))

        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.loading), color = SoftWhite.copy(alpha = 0.5f))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(bottom = 28.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("✦", style = MaterialTheme.typography.titleSmall, color = SacredGold.copy(alpha = 0.6f))
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.prayers_title),
                            style = MaterialTheme.typography.headlineSmall,
                            color = SacredGold
                        )
                    }
                }
                itemsIndexed(uiState.prayers) { index, prayer ->
                    if (index == 0) HorizontalDivider(color = SacredGold.copy(alpha = 0.2f))
                    PrayerCard(index = index, prayer = prayer, onClick = { onSelectPrayer(prayer) })
                    HorizontalDivider(color = SacredGold.copy(alpha = 0.2f))
                }
            }
        }
    }
}

@Composable
private fun PrayerCard(index: Int, prayer: Prayer, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 26.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = (index + 1).toString().padStart(2, '0'),
            style = MaterialTheme.typography.labelLarge,
            color = SacredGold.copy(alpha = 0.6f)
        )
        Text(
            text = prayer.localeTitle,
            style = MaterialTheme.typography.bodyLarge,
            color = SoftWhite,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "›",
            style = MaterialTheme.typography.titleMedium,
            color = SacredGold.copy(alpha = 0.5f)
        )
    }
}

// ─── Détail ──────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrayerDetailContent(
    prayer: Prayer?,
    isPlayingPrayer: Boolean,
    onBack: () -> Unit,
    onReadPrayer: () -> Unit,
    onStopAudio: () -> Unit
) {
    if (prayer == null) return

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        text = prayer.localeTitle,
                        color = SacredGold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = SacredGold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp)
                    .padding(bottom = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "✦",
                    style = MaterialTheme.typography.headlineMedium,
                    color = SacredGold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = prayer.theme,
                    style = MaterialTheme.typography.labelLarge,
                    color = SacredGold.copy(alpha = 0.8f)
                )

                Spacer(Modifier.height(24.dp))

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(0.4f),
                    color = SacredGold.copy(alpha = 0.3f)
                )

                Spacer(Modifier.height(28.dp))

                Text(
                    text = prayer.localeText,
                    style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                    color = SoftWhite,
                    textAlign = TextAlign.Center,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.6f
                )

                Spacer(Modifier.height(16.dp))
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 40.dp, vertical = 24.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(50))
                .background(DarkNavy.copy(alpha = 0.85f))
                .border(1.dp, SacredGold.copy(alpha = 0.45f), RoundedCornerShape(50))
                .clickable(onClick = if (isPlayingPrayer) onStopAudio else onReadPrayer)
                .padding(vertical = 13.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isPlayingPrayer)
                    stringResource(R.string.prayer_stop_listen)
                else
                    stringResource(R.string.prayer_listen),
                style = MaterialTheme.typography.bodyMedium,
                color = SacredGold.copy(alpha = 0.85f)
            )
        }
    }
}

// ─── Choix ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrayerChoiceContent(
    uiState: PrayerUiState,
    onBack: () -> Unit,
    onJustListen: () -> Unit,
    onSetAmbiance: (Ambiance) -> Unit,
    onSetMinutes: (Int) -> Unit,
    onLaunch: () -> Unit
) {
    val prayer = uiState.selectedPrayer ?: return

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = prayer.localeTitle,
                    color = SacredGold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = SacredGold
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 36.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))

            Text("✦", style = MaterialTheme.typography.titleSmall, color = SacredGold.copy(alpha = 0.6f))
            Spacer(Modifier.height(4.dp))
            HorizontalDivider(modifier = Modifier.fillMaxWidth(0.3f), color = SacredGold.copy(alpha = 0.25f))

            Spacer(Modifier.height(28.dp))

            // Option 1 — Juste écouter
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .background(DarkNavy.copy(alpha = 0.85f))
                    .border(1.dp, SacredGold.copy(alpha = 0.45f), RoundedCornerShape(50))
                    .clickable(onClick = onJustListen)
                    .padding(vertical = 13.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.prayer_just_listen),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SacredGold.copy(alpha = 0.85f)
                )
            }

            Spacer(Modifier.height(24.dp))

            // Divider "ou"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = SacredGold.copy(alpha = 0.2f))
                Text(
                    text = stringResource(R.string.prayer_or),
                    style = MaterialTheme.typography.labelMedium,
                    color = SacredGold.copy(alpha = 0.4f)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = SacredGold.copy(alpha = 0.2f))
            }

            Spacer(Modifier.height(24.dp))

            // Option 2 — Écouter + Méditer
            Text(
                text = stringResource(R.string.prayer_listen_meditate),
                style = MaterialTheme.typography.titleMedium,
                color = SacredGold
            )

            Spacer(Modifier.height(20.dp))

            // Ambiance label
            Text(
                text = stringResource(R.string.prayer_ambiance_label),
                style = MaterialTheme.typography.labelLarge,
                color = SacredGold.copy(alpha = 0.6f)
            )

            Spacer(Modifier.height(10.dp))

            // Ambiance chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                AmbianceChip(
                    label = stringResource(R.string.prayer_ambiance_eau),
                    selected = uiState.selectedAmbiance == Ambiance.EAU,
                    onClick = { onSetAmbiance(Ambiance.EAU) }
                )
                AmbianceChip(
                    label = stringResource(R.string.prayer_ambiance_oiseaux),
                    selected = uiState.selectedAmbiance == Ambiance.OISEAUX,
                    onClick = { onSetAmbiance(Ambiance.OISEAUX) }
                )
                AmbianceChip(
                    label = stringResource(R.string.prayer_ambiance_silence),
                    selected = uiState.selectedAmbiance == Ambiance.SILENCE,
                    onClick = { onSetAmbiance(Ambiance.SILENCE) }
                )
            }

            Spacer(Modifier.height(24.dp))

            // Duration selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .background(DarkNavy.copy(alpha = 0.85f))
                    .border(1.dp, SacredGold.copy(alpha = 0.45f), RoundedCornerShape(50)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSetMinutes(uiState.choiceMinutes - 1) }
                        .padding(vertical = 11.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("−", style = MaterialTheme.typography.bodyMedium, color = SacredGold)
                }
                Text(
                    text = stringResource(R.string.prayer_duration_min, uiState.choiceMinutes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftWhite,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSetMinutes(uiState.choiceMinutes + 1) }
                        .padding(vertical = 11.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", style = MaterialTheme.typography.bodyMedium, color = SacredGold)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Lancer button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .background(DarkNavy.copy(alpha = 0.9f))
                    .border(1.5.dp, SacredGold.copy(alpha = 0.8f), RoundedCornerShape(50))
                    .clickable(onClick = onLaunch)
                    .padding(vertical = 13.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.prayer_launch),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SacredGold
                )
            }
        }
    }
}

@Composable
private fun AmbianceChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) SacredGold.copy(alpha = 0.18f) else Color.Transparent)
            .border(
                1.dp,
                if (selected) SacredGold else SacredGold.copy(alpha = 0.3f),
                RoundedCornerShape(50)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (selected) SacredGold else SoftWhite.copy(alpha = 0.6f)
        )
    }
}

// ─── Minuteur ────────────────────────────────────────────────────────────────

@Composable
private fun PrayerTimerContent(
    uiState: PrayerUiState,
    onBack: () -> Unit
) {
    val prayer = uiState.selectedPrayer ?: return

    val animatedProgress by animateFloatAsState(
        targetValue = uiState.timerProgress,
        animationSpec = tween(950),
        label = "timerProgress"
    )

    if (uiState.timerFinished) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            TimerFinishedContent(onBack = onBack)
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.meditation_stop),
                tint = SacredGold.copy(alpha = 0.7f)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp)
                .padding(bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("✦", style = MaterialTheme.typography.titleSmall, color = SacredGold.copy(alpha = 0.6f))
            Spacer(Modifier.height(4.dp))
            Text(
                text = prayer.theme,
                style = MaterialTheme.typography.labelLarge,
                color = SacredGold.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(0.4f),
                color = SacredGold.copy(alpha = 0.3f)
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = prayer.localeText,
                style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                color = SoftWhite,
                textAlign = TextAlign.Center,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.6f
            )
            Spacer(Modifier.height(16.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HorizontalDivider(color = SacredGold.copy(alpha = 0.2f))
            Spacer(Modifier.height(4.dp))

            if (uiState.isPlayingPrayer) {
                // Prayer audio is playing — timer not started yet
                Column(
                    modifier = Modifier.height(120.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🔊",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.prayer_playing),
                        style = MaterialTheme.typography.bodyMedium,
                        color = SacredGold.copy(alpha = 0.7f)
                    )
                }
            } else {
                // Timer countdown
                Box(contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(120.dp)) {
                        drawArc(
                            color = SacredGold.copy(alpha = 0.12f),
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = SacredGold,
                            startAngle = -90f,
                            sweepAngle = 360f * animatedProgress,
                            useCenter = false,
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    Text(
                        text = formatTime(uiState.remainingSeconds),
                        style = MaterialTheme.typography.headlineMedium,
                        color = SacredGold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .border(1.dp, SoftWhite.copy(alpha = 0.25f), RoundedCornerShape(50))
                    .clickable(onClick = onBack)
                    .padding(horizontal = 32.dp, vertical = 10.dp)
            ) {
                Text(
                    text = stringResource(R.string.meditation_stop),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftWhite.copy(alpha = 0.45f)
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun TimerFinishedContent(onBack: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "✦",
            style = MaterialTheme.typography.displayLarge,
            color = SacredGold
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.meditation_amen),
            style = MaterialTheme.typography.headlineLarge,
            color = SoftWhite
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.meditation_done),
            style = MaterialTheme.typography.bodyMedium,
            color = SoftWhite.copy(alpha = 0.55f)
        )

        Spacer(Modifier.height(48.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(DarkNavy.copy(alpha = 0.9f))
                .border(1.dp, SacredGold.copy(alpha = 0.6f), RoundedCornerShape(50))
                .clickable(onClick = onBack)
                .padding(horizontal = 32.dp, vertical = 12.dp)
        ) {
            Text(
                text = stringResource(R.string.meditation_back),
                style = MaterialTheme.typography.bodyMedium,
                color = SacredGold
            )
        }
    }
}
