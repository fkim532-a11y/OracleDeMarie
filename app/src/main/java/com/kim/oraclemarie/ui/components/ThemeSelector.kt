package com.kim.oraclemarie.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kim.oraclemarie.R
import com.kim.oraclemarie.ui.theme.MarianBlue
import com.kim.oraclemarie.ui.theme.SacredGold
import com.kim.oraclemarie.ui.theme.SoftWhite

data class ThemeOption(val key: String, val iconRes: Int, val labelRes: Int)

val themeOptions = listOf(
    ThemeOption("spirituel", R.drawable.theme_spirituel, R.string.theme_spirituel),
    ThemeOption("amour",     R.drawable.theme_amour,     R.string.theme_amour),
    ThemeOption("travail",   R.drawable.theme_travail,   R.string.theme_travail),
    ThemeOption("sante",     R.drawable.theme_sante,     R.string.theme_sante),
    ThemeOption("famille",   R.drawable.theme_famille,   R.string.theme_famille)
)

@Composable
fun ThemeSelector(onThemeSelected: (String) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.choose_theme),
            style = MaterialTheme.typography.titleLarge,
            color = SoftWhite,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemeButton(modifier = Modifier.weight(1f), theme = themeOptions[0], onClick = { onThemeSelected(themeOptions[0].key) })
            ThemeButton(modifier = Modifier.weight(1f), theme = themeOptions[1], onClick = { onThemeSelected(themeOptions[1].key) })
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemeButton(modifier = Modifier.weight(1f), theme = themeOptions[2], onClick = { onThemeSelected(themeOptions[2].key) })
            ThemeButton(modifier = Modifier.weight(1f), theme = themeOptions[3], onClick = { onThemeSelected(themeOptions[3].key) })
        }
        ThemeButton(
            modifier = Modifier.fillMaxWidth(0.5f),
            theme = themeOptions[4],
            onClick = { onThemeSelected(themeOptions[4].key) }
        )
    }
}

@Composable
fun ThemeButton(modifier: Modifier = Modifier, theme: ThemeOption, onClick: () -> Unit) {
    val buttonHeight = 42.dp
    val iconSize = 82.dp
    Box(
        modifier = modifier.height(iconSize),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(buttonHeight)
                .clip(RoundedCornerShape(50))
                .background(MarianBlue.copy(alpha = 0.85f))
                .border(1.5.dp, SacredGold.copy(alpha = 0.8f), RoundedCornerShape(50))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(theme.labelRes),
                style = MaterialTheme.typography.bodyLarge,
                color = SoftWhite,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 58.dp, end = 8.dp)
            )
        }
        Image(
            painter = painterResource(theme.iconRes),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(iconSize)
                .align(Alignment.CenterStart)
        )
    }
}
