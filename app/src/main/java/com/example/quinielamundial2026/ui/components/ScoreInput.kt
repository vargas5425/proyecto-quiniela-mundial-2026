package com.example.quinielamundial2026.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme

@Composable
fun ScoreInput(
    homeTeam: String,
    awayTeam: String,
    homeScore: String,
    awayScore: String,
    onHomeScoreChange: (String) -> Unit,
    onAwayScoreChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = homeTeam,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )

        OutlinedTextField(
            value = homeScore,
            onValueChange = onHomeScoreChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .width(70.dp)
                .padding(horizontal = 8.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "-",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        OutlinedTextField(
            value = awayScore,
            onValueChange = onAwayScoreChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .width(70.dp)
                .padding(horizontal = 8.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.titleMedium
        )

        Text(
            text = awayTeam,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}