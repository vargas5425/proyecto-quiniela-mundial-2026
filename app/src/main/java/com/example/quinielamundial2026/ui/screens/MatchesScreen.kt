package com.example.quinielamundial2026.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quinielamundial2026.QuinielaApplication
import com.example.quinielamundial2026.ui.components.LoadingSpinner
import com.example.quinielamundial2026.ui.components.MatchCard
import com.example.quinielamundial2026.ui.viewmodels.MatchesViewModel
import com.example.quinielamundial2026.ui.viewmodels.ViewModelFactory
import com.example.quinielamundial2026.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchesScreen(
    onNavigateToMatchDetail: (Int) -> Unit
) {
    val factory = ViewModelFactory(QuinielaApplication.instance.container)
    val viewModel: MatchesViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    var showFilterMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Partidos") },
                actions = {
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filtros")
                    }
                    IconButton(onClick = { viewModel.loadNextMatches() }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Próximos partidos")
                    }
                    IconButton(onClick = { viewModel.loadMatches() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
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
                        Button(onClick = { viewModel.loadMatches() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            uiState.matches.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No hay partidos",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "Próximamente se publicarán los partidos",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            else -> {
                if (uiState.selectedPhase != null || uiState.selectedStatus != null || uiState.selectedDate != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (uiState.selectedPhase != null) {
                                    AssistChip(
                                        onClick = { },
                                        label = { Text(uiState.selectedPhase!!.replace("_", " ")) },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Circle,
                                                contentDescription = null,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    )
                                }
                                if (uiState.selectedStatus != null) {
                                    AssistChip(
                                        onClick = { },
                                        label = { Text(uiState.selectedStatus!!) }
                                    )
                                }
                            }
                            TextButton(onClick = { viewModel.clearFilters() }) {
                                Text("Limpiar filtros")
                            }
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.matches) { match ->
                        MatchCard(
                            match = match,
                            onClick = { onNavigateToMatchDetail(match.id) }
                        )
                    }
                }
            }
        }
    }

    // ============ MODAL BOTTOM PARA FILTROS ============
    if (showFilterMenu) {
        ModalBottomSheet(
            onDismissRequest = { showFilterMenu = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Filtrar partidos",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // ============ FILTRO POR FASE ============
                Text(
                    text = "Fase del torneo",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        onClick = {
                            viewModel.loadMatches()
                            showFilterMenu = false
                        },
                        label = { Text("Todas") },
                        selected = uiState.selectedPhase == null
                    )
                    FilterChip(
                        onClick = {
                            viewModel.filterByPhase(Constants.PHASE_GROUP)
                            showFilterMenu = false
                        },
                        label = { Text("Grupos") },
                        selected = uiState.selectedPhase == Constants.PHASE_GROUP
                    )
                    FilterChip(
                        onClick = {
                            viewModel.filterByPhase(Constants.PHASE_ROUND_32)
                            showFilterMenu = false
                        },
                        label = { Text("Dieciseisavos") },
                        selected = uiState.selectedPhase == Constants.PHASE_ROUND_32
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        onClick = {
                            viewModel.filterByPhase(Constants.PHASE_ROUND_16)
                            showFilterMenu = false
                        },
                        label = { Text("Octavos") },
                        selected = uiState.selectedPhase == Constants.PHASE_ROUND_16
                    )
                    FilterChip(
                        onClick = {
                            viewModel.filterByPhase(Constants.PHASE_QUARTER)
                            showFilterMenu = false
                        },
                        label = { Text("Cuartos") },
                        selected = uiState.selectedPhase == Constants.PHASE_QUARTER
                    )
                    FilterChip(
                        onClick = {
                            viewModel.filterByPhase(Constants.PHASE_SEMI)
                            showFilterMenu = false
                        },
                        label = { Text("Semifinal") },
                        selected = uiState.selectedPhase == Constants.PHASE_SEMI
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        onClick = {
                            viewModel.filterByPhase(Constants.PHASE_FINAL)
                            showFilterMenu = false
                        },
                        label = { Text("Final") },
                        selected = uiState.selectedPhase == Constants.PHASE_FINAL
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 16.dp))

                // ============ FILTRO POR ESTADO ============
                Text(
                    text = "Estado del partido",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        onClick = {
                            viewModel.loadMatches();
                            showFilterMenu = false
                        },
                        label = { Text("Todos") },
                        selected = uiState.selectedStatus == null
                    )
                    FilterChip(
                        onClick = {
                            viewModel.filterByStatus(Constants.STATUS_SCHEDULED)
                            showFilterMenu = false
                        },
                        label = { Text("Programado") },
                        selected = uiState.selectedStatus == Constants.STATUS_SCHEDULED
                    )
                    FilterChip(
                        onClick = {
                            viewModel.filterByStatus(Constants.STATUS_LIVE)
                            showFilterMenu = false
                        },
                        label = { Text("En vivo") },
                        selected = uiState.selectedStatus == Constants.STATUS_LIVE
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        onClick = {
                            viewModel.filterByStatus(Constants.STATUS_FINISHED)
                            showFilterMenu = false
                        },
                        label = { Text("Finalizado") },
                        selected = uiState.selectedStatus == Constants.STATUS_FINISHED
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.clearFilters()
                        showFilterMenu = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Limpiar Filtros")
                }
            }
        }
    }
}