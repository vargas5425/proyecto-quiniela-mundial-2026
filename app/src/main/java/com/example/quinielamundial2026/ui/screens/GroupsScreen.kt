package com.example.quinielamundial2026.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quinielamundial2026.QuinielaApplication
import com.example.quinielamundial2026.ui.components.GroupCard
import com.example.quinielamundial2026.ui.components.LoadingSpinner
import com.example.quinielamundial2026.ui.viewmodels.GroupsViewModel
import com.example.quinielamundial2026.ui.viewmodels.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    onNavigateToGroupDetail: (Int) -> Unit
) {
    val factory = ViewModelFactory(QuinielaApplication.instance.container)
    val viewModel: GroupsViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Grupos") },
                actions = {
                    IconButton(onClick = { viewModel.showJoinDialog(true) }) {
                        Icon(Icons.Default.QrCode, contentDescription = "Unirse")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showCreateDialog(true) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Crear Grupo")
            }
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> LoadingSpinner()
            uiState.groups.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No tienes grupos",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "Crea uno nuevo o únete con un código",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.groups) { group ->
                        GroupCard(
                            group = group,
                            onClick = { onNavigateToGroupDetail(group.id) }
                        )
                    }
                }
            }
        }
    }

    if (uiState.showCreateDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showCreateDialog(false) },
            title = { Text("Crear Grupo") },
            text = {
                OutlinedTextField(
                    value = uiState.newGroupName,
                    onValueChange = { viewModel.updateNewGroupName(it) },
                    label = { Text("Nombre del grupo") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.createGroup() },
                    enabled = uiState.newGroupName.isNotBlank() && !uiState.isLoading
                ) {
                    Text("Crear")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showCreateDialog(false) }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (uiState.showJoinDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showJoinDialog(false) },
            title = { Text("Unirse a Grupo") },
            text = {
                OutlinedTextField(
                    value = uiState.inviteCode,
                    onValueChange = { viewModel.updateInviteCode(it.uppercase()) },
                    label = { Text("Código de invitación") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ej: ABCD1234") }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.joinGroup() },
                    enabled = uiState.inviteCode.length == 8 && !uiState.isLoading
                ) {
                    Text("Unirse")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showJoinDialog(false) }) {
                    Text("Cancelar")
                }
            }
        )
    }
}