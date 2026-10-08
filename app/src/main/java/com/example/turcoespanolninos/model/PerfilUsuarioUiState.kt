package com.example.turcoespanolninos.model

data class PerfilUsuarioUiState(
    val aliasFicticio: String = "",
    val edadSimulada: String = "",
    val moduloSeleccionado: String = "Animales",
    val errores: PerfilErrores = PerfilErrores()
)

data class PerfilErrores(
    val aliasFicticio: String? = null,
    val edadSimulada: String? = null
)