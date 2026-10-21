package com.example.turcoespanolninos.model

import android.graphics.Bitmap

data class PerfilUsuarioUiState(
    val aliasFicticio: String = "",
    val edadSimulada: String = "",
    val moduloSeleccionado: String = "Animales",
    val imagenPerfil: Bitmap? = null,
    val errores: PerfilErrores = PerfilErrores(),
    val perfilRegistrado: Boolean = false,
    val cargando: Boolean = true
)

data class PerfilErrores(
    val aliasFicticio: String? = null,
    val edadSimulada: String? = null
)
