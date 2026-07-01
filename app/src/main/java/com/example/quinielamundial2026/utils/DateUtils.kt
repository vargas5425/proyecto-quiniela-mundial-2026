package com.example.quinielamundial2026.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())

    private val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    private val shortFormat = SimpleDateFormat("dd/MM", Locale.getDefault())

    fun formatDate(dateString: String): String {
        return try {
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            dateString
        }
    }

    fun formatDateShort(dateString: String): String {
        return try {
            val date = inputFormat.parse(dateString)
            shortFormat.format(date ?: Date())
        } catch (e: Exception) {
            dateString
        }
    }
}