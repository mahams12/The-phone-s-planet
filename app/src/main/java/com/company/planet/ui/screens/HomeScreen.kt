package com.company.planet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.planet.data.Phone
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import com.company.planet.ui.components.TppLogo
import com.company.planet.ui.components.tppCard3D
import com.company.planet.ui.components.tppDropIn
import com.company.planet.ui.components.TppTabBar
import com.company.planet.ui.components.TppToast
import com.company.planet.ui.theme.Bg
import com.company.planet.ui.theme.BgPanel
import com.company.planet.ui.theme.Danger
import com.company.planet.ui.theme.Line
import com.company.planet.ui.theme.TextPrimary
import com.company.planet.ui.theme.TextSecondary
import com.company.planet.ui.util.rememberResponsive
import com.company.planet.viewmodel.AppScreen
import com.company.planet.viewmodel.PhoneViewModel

@Composable
fun HomeScreen(
    viewModel: PhoneViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val responsive = rememberResponsive()
    var deleteTarget by remember { mutableStateOf<Phone?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0x0F5EEAD4), Color.Transparent),
                    radius = 900f,
                    center = androidx.compose.ui.geometry.Offset(0.15f, 0f)
                )
            )
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0x0DA78BFA), Color.Transparent),
                    radius = 900f,
                    center = androidx.compose.ui.geometry.Offset(1f, 0f)
                )
            )
            .background(Bg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = responsive.horizontalPadding)
                .widthIn(max = 1280.dp)
                .align(Alignment.TopCenter)
        ) {
            TopBar(
                currentScreen = state.currentScreen,
                onScreenSelected = { screen ->
                    val keepForm = screen == AppScreen.ADD && state.editingPhone != null
                    viewModel.navigateTo(screen, keepForm = keepForm)
                },
                compact = responsive.compactHeader
            )

            Spacer(Modifier.height(18.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                AnimatedContent(
                    targetState = state.currentScreen,
                    transitionSpec = {
                        (fadeIn(tween(280)) + slideInVertically(tween(280)) { it / 8 })
                            .togetherWith(fadeOut(tween(180)) + slideOutVertically(tween(180)) { -it / 10 })
                    },
                    label = "screenTransition"
                ) { screen ->
                    when (screen) {
                        AppScreen.ADD -> AddPhoneScreen(
                            editingPhone = state.editingPhone,
                            viewModel = viewModel,
                            modifier = Modifier
                                .fillMaxSize()
                                .fillMaxWidth()
                        )
                        else -> Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                        ) {
                            when (screen) {
                                AppScreen.DASHBOARD -> DashboardScreen(
                                    phones = state.phones,
                                    totals = state.totals,
                                    viewModel = viewModel
                                )
                                AppScreen.INVENTORY -> InventoryScreen(
                                    state = state,
                                    viewModel = viewModel,
                                    onDeleteRequest = { deleteTarget = it }
                                )
                                else -> Unit
                            }
                            Spacer(Modifier.height(80.dp))
                        }
                    }
                }
            }
        }

        state.previewPhone?.let { phone ->
            PhonePreviewSheet(
                phone = phone,
                onDismiss = viewModel::closePreview,
                onEdit = { viewModel.openEditForm(phone) },
                onDelete = { deleteTarget = phone },
                fullScreen = responsive.useFullScreenSheet
            )
        }

        deleteTarget?.let { phone ->
            AlertDialog(
                onDismissRequest = { deleteTarget = null },
                title = { Text("Delete phone?", color = TextPrimary) },
                text = { Text("Delete \"${phone.model.ifBlank { "this phone" }}\"? This cannot be undone.", color = TextSecondary) },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deletePhone(phone)
                        deleteTarget = null
                    }) { Text("Delete", color = Danger) }
                },
                dismissButton = {
                    TextButton(onClick = { deleteTarget = null }) { Text("Cancel") }
                },
                containerColor = BgPanel
            )
        }

        state.errorMessage?.let { message ->
            AlertDialog(
                onDismissRequest = viewModel::clearError,
                title = { Text("Error", color = TextPrimary) },
                text = { Text(message, color = TextSecondary) },
                confirmButton = { TextButton(onClick = viewModel::clearError) { Text("OK") } },
                containerColor = BgPanel
            )
        }

        TppToast(
            message = state.toastMessage,
            onDismiss = viewModel::clearToast,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun TopBar(
    currentScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit,
    compact: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .tppDropIn()
            .tppCard3D(shape = RoundedCornerShape(16.dp), elevation = 8.dp, lift = -2f)
            .background(BgPanel, RoundedCornerShape(16.dp))
            .border(1.dp, Line, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BrandBlock(compact = compact)
        TppTabBar(
            currentScreen = currentScreen,
            onScreenSelected = onScreenSelected,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun BrandBlock(compact: Boolean) {
    val responsive = rememberResponsive()

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        TppLogo(
            height = responsive.logoHeight,
            maxWidth = responsive.logoMaxWidth
        )
    }
}
