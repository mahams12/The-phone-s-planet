package com.company.planet.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.company.planet.R

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private val interFont = GoogleFont("Inter")
private val spaceGroteskFont = GoogleFont("Space Grotesk")
private val jetBrainsMonoFont = GoogleFont("JetBrains Mono")

val InterFamily = FontFamily(
    Font(interFont, provider, FontWeight.Normal),
    Font(interFont, provider, FontWeight.Medium),
    Font(interFont, provider, FontWeight.SemiBold)
)

// Space Grotesk can fail to load on some devices and render text as near-black.
// SansSerif keeps headings readable while matching the overall UI weight.
val SpaceGroteskFamily = FontFamily.SansSerif

val JetBrainsMonoFamily = FontFamily(
    Font(jetBrainsMonoFont, provider, FontWeight.Normal),
    Font(jetBrainsMonoFont, provider, FontWeight.Medium),
    Font(jetBrainsMonoFont, provider, FontWeight.SemiBold)
)

val TppTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = TextPrimary
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = TextPrimary
    ),
    bodySmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        color = TextSecondary
    ),
    titleLarge = TextStyle(
        fontFamily = SpaceGroteskFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        color = TextPrimary
    ),
    titleMedium = TextStyle(
        fontFamily = SpaceGroteskFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        color = TextPrimary
    ),
    labelSmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        letterSpacing = 0.6.sp,
        color = Muted
    )
)
