package com.example.turcoespanolninos.model

import android.net.Uri

data class PerfilUsuarioUiState(
    val aliasFicticio: String = "",
    val edadSimulada: String = "",
    val moduloSeleccionado: String = "Animales",
    val imagenPerfilUri: Uri? = null,
    val errores: PerfilErrores = PerfilErrores()
)

data class PerfilErrores(
    val aliasFicticio: String? = null,
    val edadSimulada: String? = null
)
