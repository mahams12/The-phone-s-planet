package com.company.planet.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.planet.ui.theme.Accent
import com.company.planet.ui.theme.AccentOn
import com.company.planet.ui.theme.BgElevated
import com.company.planet.ui.theme.Danger
import com.company.planet.ui.theme.DangerDim
import com.company.planet.ui.theme.InterFamily
import com.company.planet.ui.theme.Line
import com.company.planet.ui.theme.Muted
import com.company.planet.ui.theme.TextPrimary

@Composable
fun TppPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(42.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Accent,
            contentColor = AccentOn
        ),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 11.dp)
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
        }
        Text(text, fontFamily = InterFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}

@Composable
fun TppGhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(42.dp),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Line),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = BgElevated,
            contentColor = TextPrimary
        ),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 11.dp)
    ) {
        Text(text, fontFamily = InterFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}

@Composable
fun TppDangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(42.dp),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Danger.copy(alpha = 0.3f)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = DangerDim,
            contentColor = Danger
        ),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 11.dp)
    ) {
        Text(text, fontFamily = InterFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}

@Composable
fun TppIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    danger: Boolean = false
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(28.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (danger) Danger else Muted,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
fun AddPhoneButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    TppPrimaryButton(
        text = "Add Phone",
        onClick = onClick,
        modifier = modifier,
        icon = Icons.Default.Add
    )
}
