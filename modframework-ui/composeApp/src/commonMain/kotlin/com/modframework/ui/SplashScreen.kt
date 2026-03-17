package com.modframework.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var visible by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(800)
    )

    val bounce by animateFloatAsState(
        targetValue = if (visible) 0f else 40f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    val claudeBounce by animateFloatAsState(
        targetValue = if (visible) 0f else 60f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessVeryLow
        )
    )

    LaunchedEffect(Unit) {
        delay(100)
        visible = true
        delay(2500)
        visible = false
        delay(800)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFB300))
            .alpha(alpha),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "🤖",
                fontSize = 64.sp,
                modifier = Modifier.offset(y = bounce.dp)
            )
            Text(
                text = "UI/UX by",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.offset(y = bounce.dp)
            )
            Text(
                text = "Claude",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.offset(y = claudeBounce.dp)
            )
            Text(
                text = "Anthropic",
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.offset(y = bounce.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "🥭 MangoLoader",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.offset(y = bounce.dp)
            )
        }
    }
}