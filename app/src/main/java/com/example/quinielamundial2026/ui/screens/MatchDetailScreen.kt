package com.example.quinielamundial2026.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.quinielamundial2026.QuinielaApplication
import com.example.quinielamundial2026.ui.components.LoadingSpinner
import com.example.quinielamundial2026.ui.components.ScoreInput
import com.example.quinielamundial2026.ui.viewmodels.MatchDetailViewModel
import com.example.quinielamundial2026.ui.viewmodels.ViewModelFactory
import com.example.quinielamundial2026.utils.DateUtils
import com.example.quinielamundial2026.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(
    navController: NavController,
    matchId: Int
) {
    val factory = ViewModelFactory(QuinielaApplication.instance.container)
    val viewModel: MatchDetailViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsState()

    val myPrediction = uiState.myPrediction

    var showSuccessMessage by remember { mutableStateOf(false) }
    LaunchedEffect(uiState.isPredictionSuccess) {
        if (uiState.isPredictionSuccess) {
            showSuccessMessage = true
            kotlinx.coroutines.delay(3000)
            showSuccessMessage = false
            viewModel.resetPredictionState()
        }
    }

    LaunchedEffect(matchId) {
        viewModel.loadMatchDetail(matchId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle del Partido",
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                LoadingSpinner()
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${uiState.error}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadMatchDetail(matchId) }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            uiState.matchDetail != null -> {
                val match = uiState.matchDetail!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // ============ TARJETA DEL PARTIDO ============
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = match.phase.replace("_", " ").uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color = when (match.status) {
                                        Constants.STATUS_SCHEDULED -> MaterialTheme.colorScheme.secondaryContainer
                                        Constants.STATUS_LIVE -> MaterialTheme.colorScheme.errorContainer
                                        Constants.STATUS_FINISHED -> MaterialTheme.colorScheme.primaryContainer
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    }
                                ) {
                                    Text(
                                        text = when (match.status) {
                                            Constants.STATUS_SCHEDULED -> "Programado"
                                            Constants.STATUS_LIVE -> "En vivo"
                                            Constants.STATUS_FINISHED -> "Finalizado"
                                            else -> match.status
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        color = when (match.status) {
                                            Constants.STATUS_SCHEDULED -> MaterialTheme.colorScheme.onSecondaryContainer
                                            Constants.STATUS_LIVE -> MaterialTheme.colorScheme.onErrorContainer
                                            Constants.STATUS_FINISHED -> MaterialTheme.colorScheme.onPrimaryContainer
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = match.homeTeam,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    if (match.status == Constants.STATUS_FINISHED) {
                                        Text(
                                            text = "${match.homeScore ?: 0} - ${match.awayScore ?: 0}",
                                            style = MaterialTheme.typography.headlineMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    } else {
                                        Text(
                                            text = "VS",
                                            style = MaterialTheme.typography.headlineMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = DateUtils.formatDate(match.matchDate),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = match.awayTeam,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            match.stadium?.let { stadium ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Column {
                                            Text(
                                                text = "${stadium.name}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = "${stadium.city}, ${stadium.country}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = "Capacidad: ${stadium.capacity} espectadores",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ============ PRONÓSTICO ============
                    if (match.status == Constants.STATUS_SCHEDULED) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Tu Pronóstico",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                // ============ MOSTRAR PRONÓSTICO GUARDADO ============
                                if (myPrediction != null) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp)
                                        ) {
                                            Text(
                                                text = "Pronóstico guardado: ${myPrediction.homeScore} - ${myPrediction.awayScore}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Puedes modificarlo antes del inicio del partido",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                ScoreInput(
                                    homeTeam = match.homeTeam,
                                    awayTeam = match.awayTeam,
                                    homeScore = uiState.homeScore,
                                    awayScore = uiState.awayScore,
                                    onHomeScoreChange = { viewModel.updateHomeScore(it) },
                                    onAwayScoreChange = { viewModel.updateAwayScore(it) },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                if (uiState.error != null) {
                                    Text(
                                        text = uiState.error ?: "",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }

                                Button(
                                    onClick = { viewModel.submitPrediction() },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !uiState.isLoading
                                ) {
                                    if (uiState.isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    } else {
                                        Text(if (myPrediction != null) "Actualizar Pronóstico" else "Guardar Pronóstico")
                                    }
                                }
                            }
                        }
                    } else if (match.status == Constants.STATUS_FINISHED) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = "Partido Finalizado",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "El pronóstico ya no está disponible",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                if (myPrediction != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Tu pronóstico fue: ${myPrediction.homeScore} - ${myPrediction.awayScore}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    // ============ MOSTRAR ESTADO DEL PRONÓSTICO ============
                                    val statusMessage = when (myPrediction.status) {
                                        Constants.PREDICTION_CORRECT_SCORE -> "¡Acertaste el marcador exacto! (+3 pts)"
                                        Constants.PREDICTION_CORRECT_WINNER -> "Acertaste el ganador (+1 pt)"
                                        Constants.PREDICTION_INCORRECT -> "No acertaste"
                                        Constants.PREDICTION_PENDING -> "Pendiente de revisión"
                                        else -> ""
                                    }
                                    if (statusMessage.isNotEmpty()) {
                                        Text(
                                            text = statusMessage,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = when (myPrediction.status) {
                                                Constants.PREDICTION_CORRECT_SCORE -> Color(0xFF4CAF50)
                                                Constants.PREDICTION_CORRECT_WINNER -> Color(0xFFFF9800)
                                                Constants.PREDICTION_INCORRECT -> Color(0xFFF44336)
                                                Constants.PREDICTION_PENDING -> Color(0xFF2196F3)
                                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                                            },
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    if (myPrediction.pointsEarned != null) {
                                        Text(
                                            text = "Puntos obtenidos: ${myPrediction.pointsEarned}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (showSuccessMessage && uiState.predictionMessage != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${uiState.predictionMessage ?: "Pronóstico guardado"}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}