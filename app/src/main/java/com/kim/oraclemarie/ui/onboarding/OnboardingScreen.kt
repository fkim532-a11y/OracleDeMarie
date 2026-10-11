package com.kim.oraclemarie.ui.onboarding

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kim.oraclemarie.R
import com.kim.oraclemarie.ui.components.StarryBackground
import com.kim.oraclemarie.ui.theme.MarianBlue
import com.kim.oraclemarie.ui.theme.SacredGold
import com.kim.oraclemarie.ui.theme.SoftWhite

private const val KOFI_URL = "https://ko-fi.com/franckim"

@Composable
fun OnboardingScreen(onDone: () -> Unit) {
    var page by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    StarryBackground {
        Image(
            painter = painterResource(R.drawable.portal_marie),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = 0.28f; scaleX = 0.88f; scaleY = 0.88f }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Indicateurs de page
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(2) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (page == index) 24.dp else 8.dp, 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (page == index) SacredGold else SacredGold.copy(alpha = 0.3f)
                            )
                    )
                }
            }

            // Contenu animé
            AnimatedContent(
                targetState = page,
                transitionSpec = {
                    fadeIn(tween(600)) togetherWith fadeOut(tween(400))
                },
                label = "onboarding_page"
            ) { currentPage ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (currentPage == 0) {
                        SlideOne()
                    } else {
                        SlideTwo(
                            onKofi = {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(KOFI_URL)))
                            }
                        )
                    }
                }
            }

            // Bouton bas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .background(SacredGold)
                    .clickable {
                        if (page == 0) page = 1 else onDone()
                    }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (page == 0) stringResource(R.string.onboarding_next)
                           else stringResource(R.string.onboarding_start),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF0A0C1E)
                )
            }
        }
    }
}

@Composable
private fun SlideOne() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "✦",
            style = MaterialTheme.typography.displayLarge,
            color = SacredGold
        )
        Text(
            text = stringResource(R.string.onboarding_slide1_title),
            style = MaterialTheme.typography.headlineLarge,
            color = SacredGold,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.onboarding_slide1_desc),
            style = MaterialTheme.typography.bodyLarge,
            color = SoftWhite.copy(alpha = 0.85f),
            textAlign = TextAlign.Center,
            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.4f
        )
    }
}

@Composable
private fun SlideTwo(onKofi: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = stringResource(R.string.onboarding_slide2_title),
            style = MaterialTheme.typography.headlineMedium,
            color = SacredGold,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.onboarding_slide2_desc),
            style = MaterialTheme.typography.bodyLarge,
            color = SoftWhite.copy(alpha = 0.85f),
            textAlign = TextAlign.Center,
            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.4f
        )
        Spacer(Modifier.height(8.dp))
        DonButton(label = stringResource(R.string.onboarding_support_kofi), onClick = onKofi)
    }
}

@Composable
private fun DonButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(MarianBlue.copy(alpha = 0.6f))
            .border(1.dp, SacredGold.copy(alpha = 0.6f), RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = SacredGold
        )
    }
}
