package com.kim.oraclemarie.ui.settings

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kim.oraclemarie.R
import com.kim.oraclemarie.ui.components.StarryBackground
import com.kim.oraclemarie.ui.theme.DarkNavy
import com.kim.oraclemarie.ui.theme.MarianBlue
import com.kim.oraclemarie.ui.theme.SacredGold
import com.kim.oraclemarie.ui.theme.SoftWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onNavigateBack: () -> Unit) {
    val viewModel: SettingsViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity

    val hasNotifPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    } else true

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) viewModel.setNotificationsEnabled(true) }

    StarryBackground {
        Image(
            painter = painterResource(R.drawable.portal_marie),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().graphicsLayer { alpha = 0.28f; scaleX = 0.88f; scaleY = 0.88f }
        )
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back), tint = SacredGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 40.dp)
            ) {
                item {
                    Text(
                        text = stringResource(R.string.settings_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = SacredGold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 28.dp)
                    )
                }

                // ── Notifications ──
                item { SectionHeader(stringResource(R.string.settings_notif_section)) }
                item {
                    NotifCard(
                        enabled = uiState.notificationsEnabled,
                        onToggle = { enabled ->
                            if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotifPermission) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                viewModel.setNotificationsEnabled(enabled)
                            }
                        }
                    )
                }
                item {
                    AnimatedVisibility(
                        visible = uiState.notificationsEnabled,
                        enter = expandVertically(tween(300)) + fadeIn(tween(300)),
                        exit = shrinkVertically(tween(200)) + fadeOut(tween(200))
                    ) {
                        Column {
                            Spacer(Modifier.height(8.dp))
                            TimePickerCard(
                                hour = uiState.notificationHour,
                                minute = uiState.notificationMinute,
                                onTimeChanged = { h, m -> viewModel.setNotificationTime(h, m) }
                            )
                        }
                    }
                }
                item { Spacer(Modifier.height(28.dp)) }

                // ── Langue ──
                item { SectionHeader(stringResource(R.string.settings_lang_section)) }
                item {
                    LanguageCard(
                        selected = uiState.language,
                        onSelect = { lang ->
                            viewModel.setLanguage(lang)
                            activity?.recreate()
                        }
                    )
                }
                item { Spacer(Modifier.height(28.dp)) }

                // ── Soutenir ──
                item { SectionHeader(stringResource(R.string.settings_support_section)) }
                item { SupportCard() }
                item { Spacer(Modifier.height(28.dp)) }

                // ── À propos ──
                item { SectionHeader(stringResource(R.string.settings_about_section)) }
                item { AboutCard() }
            }
        }
    }
}

// ---------------------------------------------------------------------------

@Composable
private fun SectionHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = SacredGold.copy(alpha = 0.35f))
        Text(
            text = "  ✦ $title ✦  ",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.5.sp),
            color = SacredGold
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = SacredGold.copy(alpha = 0.35f))
    }
    Spacer(Modifier.height(10.dp))
}

@Composable
private fun SettingsCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .background(MarianBlue.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            .border(1.dp, SacredGold.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        content()
    }
}

@Composable
private fun NotifCard(enabled: Boolean, onToggle: (Boolean) -> Unit) {
    SettingsCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                Text(
                    text = stringResource(R.string.settings_notif_enable),
                    color = SoftWhite,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = stringResource(R.string.settings_notif_subtitle),
                    color = SoftWhite.copy(alpha = 0.55f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Switch(
                checked = enabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = SacredGold,
                    checkedTrackColor = MarianBlue,
                    uncheckedThumbColor = SoftWhite.copy(alpha = 0.5f),
                    uncheckedTrackColor = SoftWhite.copy(alpha = 0.15f)
                )
            )
        }
    }
}

@Composable
private fun TimePickerCard(hour: Int, minute: Int, onTimeChanged: (Int, Int) -> Unit) {
    SettingsCard {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.settings_notif_time),
                color = SoftWhite.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodySmall
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TimeUnitPicker(
                    value = hour,
                    range = 0..23,
                    onChanged = { onTimeChanged(it, minute) }
                )
                Text(
                    text = ":",
                    style = MaterialTheme.typography.headlineLarge,
                    color = SacredGold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                TimeUnitPicker(
                    value = minute,
                    range = 0..59,
                    onChanged = { onTimeChanged(hour, it) }
                )
            }
        }
    }
}

@Composable
private fun TimeUnitPicker(value: Int, range: IntRange, onChanged: (Int) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        IconButton(onClick = { onChanged(if (value >= range.last) range.first else value + 1) }) {
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, tint = SacredGold)
        }
        Box(
            modifier = Modifier
                .background(DarkNavy.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .border(1.dp, SacredGold.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value.toString().padStart(2, '0'),
                style = MaterialTheme.typography.headlineMedium,
                color = SoftWhite
            )
        }
        IconButton(onClick = { onChanged(if (value <= range.first) range.last else value - 1) }) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = SacredGold)
        }
    }
}

@Composable
private fun LanguageCard(selected: String, onSelect: (String) -> Unit) {
    SettingsCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LanguageButton(
                label = stringResource(R.string.settings_lang_fr),
                selected = selected == "fr",
                modifier = Modifier.weight(1f),
                onClick = { onSelect("fr") }
            )
            LanguageButton(
                label = stringResource(R.string.settings_lang_en),
                selected = selected == "en",
                modifier = Modifier.weight(1f),
                onClick = { onSelect("en") }
            )
        }
    }
}

@Composable
private fun LanguageButton(label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) MarianBlue else Color.Transparent)
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) SacredGold else SacredGold.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (selected) SacredGold else SoftWhite.copy(alpha = 0.5f),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun SupportCard() {
    val context = LocalContext.current
    SettingsCard {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.settings_support_desc),
                color = SoftWhite.copy(alpha = 0.65f),
                style = MaterialTheme.typography.bodySmall,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            SupportButton(stringResource(R.string.settings_kofi)) {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://ko-fi.com/franckim")))
            }
        }
    }
}

@Composable
private fun SupportButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(MarianBlue.copy(alpha = 0.5f))
            .border(1.dp, SacredGold.copy(alpha = 0.6f), RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = SacredGold)
    }
}

@Composable
private fun AboutCard() {
    val context = LocalContext.current
    SettingsCard {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = stringResource(R.string.oracle_name),
                style = MaterialTheme.typography.titleMedium,
                color = SacredGold,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.settings_version),
                style = MaterialTheme.typography.bodySmall,
                color = SoftWhite.copy(alpha = 0.4f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.settings_about_desc),
                style = MaterialTheme.typography.bodySmall,
                color = SoftWhite.copy(alpha = 0.65f),
                textAlign = TextAlign.Center,
                lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.5f
            )
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(0.4f),
                color = SacredGold.copy(alpha = 0.2f)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.settings_about_heart),
                style = MaterialTheme.typography.bodySmall,
                color = SoftWhite.copy(alpha = 0.65f),
                textAlign = TextAlign.Center,
                lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.5f
            )
            Spacer(Modifier.height(12.dp))
            SupportButton(stringResource(R.string.settings_privacy)) {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://fkim532-a11y.github.io/tarots-legal/marie/privacy.html")))
            }
        }
    }
}
