package com.example.turcoespanolninos.model

data class PalabraAprendizaje(
    val id: String,
    val espanol: String,
    val turco: String,
    val emoji: String
)

data class NivelAprendizaje(
    val numero: Int,
    val tituloEspanol: String,
    val tituloTurco: String,
    val palabras: List<PalabraAprendizaje>
)

data class AprendizajeUiState(
    val nivelActual: Int = 0,
    val nivelDesbloqueado: Int = 0,
    val estrellas: Int = 0,
    val palabrasCompletadas: Set<String> = emptySet(),
    val medallaObtenida: Boolean = false,
    val cargando: Boolean = true
) {
    val puedeRetroceder: Boolean get() = nivelActual > 0
    val puedeAvanzar: Boolean get() = nivelActual < nivelDesbloqueado
}
