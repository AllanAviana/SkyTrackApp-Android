package com.example.skytrackapp_android.data.model

data class SearchUiState(
    val weathers: List<Map<String, String>> = emptyList(),
    val success: Boolean = false,
    val error: Boolean = false,
    var city: String = "",
)
