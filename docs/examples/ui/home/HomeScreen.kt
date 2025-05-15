package com.zenlauncher.ui.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenlauncher.ui.theme.ZenLauncherTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.minutes

/**
 * Tela inicial do ZenLauncher
 */
@Composable
fun HomeScreen(
    onNavigateToAppList: () -> Unit,
    onNavigateToFocusMode: () -> Unit,
    onNavigateToStats: () -> Unit
) {
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }
    
    // Atualiza o tempo a cada segundo
    androidx.compose.runtime.LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1.minutes.inWholeMilliseconds / 60)
            currentTime = System.currentTimeMillis()
        }
    }
    
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val dateFormat = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())
    
    ZenLauncherTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Status Bar Placeholder
                Spacer(modifier = Modifier.height(24.dp))
                
                // Central Content
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Relógio digital grande
                        Text(
                            text = timeFormat.format(Date(currentTime)),
                            style = TextStyle(
                                fontSize = 72.sp,
                                fontWeight = FontWeight.Light
                            )
                        )
                        
                        // Data por extenso
                        Text(
                            text = dateFormat.format(Date(currentTime)).uppercase(),
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                        )
                        
                        // Espaço entre o relógio e os botões
                        Spacer(modifier = Modifier.height(48.dp))
                        
                        // Botões principais (Deep Focus e Apps)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Deep Focus Mode
                            HomeButton(
                                icon = Icons.Outlined.Timer,
                                label = "Foco",
                                onClick = onNavigateToFocusMode
                            )
                            
                            // App Drawer
                            HomeButton(
                                icon = Icons.Default.Apps,
                                label = "Apps",
                                onClick = onNavigateToAppList
                            )
                        }
                    }
                }
                
                    // Bottom Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Configurações
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Configurações",
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { /* Implementar tela de configurações */ }
                    )
                    
                    // Estatísticas
                    Icon(
                        imageVector = Icons.Default.Insights,
                        contentDescription = "Estatísticas",
                        modifier = Modifier
                            .size(28.dp)
                            .clickable(onClick = onNavigateToStats)
                    )
                    
                    // Notificações
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notificações",
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { /* Implementar painel de notificações */ }
                    )
                }
            }
        }
    }
}

/**
 * Botão da tela inicial com ícone e texto
 */
@Composable
fun HomeButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(28.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = label,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        )
    }
}
