package com.company.planet.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.company.planet.R

@Composable
fun TppLogo(
    modifier: Modifier = Modifier,
    height: Dp = 64.dp,
    maxWidth: Dp = 220.dp
) {
    Image(
        painter = painterResource(R.drawable.logo_tpp),
        contentDescription = "The Phone's Planet",
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = maxWidth)
            .height(height),
        contentScale = ContentScale.Fit
    )
}
