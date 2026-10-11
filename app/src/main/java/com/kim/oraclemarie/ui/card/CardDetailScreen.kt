package com.kim.oraclemarie.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
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
import com.kim.oraclemarie.ui.theme.MysticPurple
import com.kim.oraclemarie.ui.theme.SacredGold
import com.kim.oraclemarie.ui.theme.SoftWhite
import com.kim.oraclemarie.ui.theme.WarmAmber

private data class TabItem(val labelRes: Int)

private val tabs = listOf(
    TabItem(R.string.tab_spirituel),
    TabItem(R.string.tab_amour),
    TabItem(R.string.tab_travail),
    TabItem(R.string.tab_sante),
    TabItem(R.string.tab_famille)
)

private val tabThemeKeys = listOf("spirituel", "amour", "travail", "sante", "famille")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailScreen(
    cardId: Int,
    initialTab: Int = 0,
    onNavigateUp: () -> Unit,
    viewModel: CardDetailViewModel = viewModel(factory = CardDetailViewModel.factory(cardId, initialTab))
) {
    val uiState by viewModel.uiState.collectAsState()

    StarryBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Carte de l'Oracle", color = SacredGold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
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
                uiState.card != null -> {
                    CardDetailContent(
                        card = uiState.card!!,
                        selectedTab = uiState.selectedTab,
                        onTabSelected = viewModel::selectTab
                    )
                }
            }
        }
    }
}

@Composable
private fun CardDetailContent(
    card: Card,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Image + en-tête (non scrollable)
        CardHeader(card = card)

        // Onglets
        PrimaryScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MarianBlue,
            contentColor = SacredGold,
            edgePadding = 8.dp
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { onTabSelected(index) },
                    text = {
                        Text(
                            stringResource(tab.labelRes),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    selectedContentColor = SacredGold,
                    unselectedContentColor = SoftWhite.copy(alpha = 0.55f)
                )
            }
        }

        // Contenu scrollable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val theme = tabThemeKeys.getOrElse(selectedTab) { "spirituel" }
            val situation = card.localeSituationForTheme(theme)
            val conseil = card.localeConseilForTheme(theme)
            Text(
                text = situation,
                style = MaterialTheme.typography.bodyLarge,
                color = SoftWhite,
                textAlign = TextAlign.Justify
            )
            HorizontalDivider(color = SacredGold.copy(alpha = 0.25f))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MarianBlue.copy(alpha = 0.3f)
            ) {
                Text(
                    text = conseil,
                    style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                    color = SoftWhite,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CardHeader(card: Card) {
    val context = LocalContext.current
    val imageResId = remember(card.imageName) {
        context.resources.getIdentifier(card.imageName, "drawable", context.packageName)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.55f)
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, SacredGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
        ) {
            AsyncImage(
                model = imageResId,
                contentDescription = card.localeName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = card.localeName,
                    style = MaterialTheme.typography.headlineMedium,
                    color = SacredGold
                )
                Text(
                    text = card.localeKeyword,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftWhite.copy(alpha = 0.7f)
                )
            }
            PartBadge(part = card.partNumber)
        }
    }
}

@Composable
private fun PartBadge(part: Int) {
    val (color, labelRes) = when (part) {
        1 -> MarianBlue to R.string.part_1
        2 -> MysticPurple to R.string.part_2
        else -> WarmAmber to R.string.part_3
    }
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
            .border(1.dp, SacredGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = stringResource(labelRes), style = MaterialTheme.typography.labelSmall, color = SoftWhite)
    }
}

