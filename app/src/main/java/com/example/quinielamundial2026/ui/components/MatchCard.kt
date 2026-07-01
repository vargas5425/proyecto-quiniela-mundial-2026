package com.example.quinielamundial2026.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quinielamundial2026.data.models.response.MatchResponse
import com.example.quinielamundial2026.utils.DateUtils

@Composable
fun MatchCard(
    match: MatchResponse,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = DateUtils.formatDate(match.matchDate),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = match.phase.replace("_", " ").uppercase(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = match.homeTeam,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Text(
                    text = if (match.status == "finished") {
                        "${match.homeScore ?: 0} - ${match.awayScore ?: 0}"
                    } else {
                        "VS"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = if (match.status == "finished") {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = match.awayTeam,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = when (match.status) {
                        "scheduled" -> "Programado"
                        "live" -> "En vivo"
                        "finished" -> "Finalizado"
                        else -> match.status
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = when (match.status) {
                        "scheduled" -> MaterialTheme.colorScheme.onSurfaceVariant
                        "live" -> MaterialTheme.colorScheme.error
                        "finished" -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            match.stadium?.let {
                Text(
                    text = "${it.name}, ${it.city}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}