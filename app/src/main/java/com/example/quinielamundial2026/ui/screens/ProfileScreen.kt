package com.example.quinielamundial2026.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quinielamundial2026.QuinielaApplication
import com.example.quinielamundial2026.ui.components.LoadingSpinner
import com.example.quinielamundial2026.ui.viewmodels.ProfileViewModel
import com.example.quinielamundial2026.ui.viewmodels.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit
) {
    val factory = ViewModelFactory(QuinielaApplication.instance.container)
    val viewModel: ProfileViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsState()

    val container = QuinielaApplication.instance.container
    val userName = container.authRepository.getUserName() ?: "Usuario"
    val userEmail = container.authRepository.getUserEmail() ?: "email@ejemplo.com"

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mi Perfil") })
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            LoadingSpinner()
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = userName.take(1).uppercase(),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Text(
                text = userName,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = userEmail,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Estadísticas",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Puntaje total: ${uiState.totalScore} pts",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "• Grupos: ${uiState.groupsCount}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "• Pronósticos: ${uiState.predictionsCount}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (uiState.error != null) {
                Text(
                    text = uiState.error ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {
                    viewModel.logout()
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                if (uiState.isLoggingOut) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onError
                    )
                } else {
                    Text("Cerrar Sesión")
                }
            }
        }
    }
}