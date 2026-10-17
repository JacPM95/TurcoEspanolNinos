package com.example.turcoespanolninos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.turcoespanolninos.data.ContenidoAprendizaje
import com.example.turcoespanolninos.data.ProgresoGuardado
import com.example.turcoespanolninos.data.UserPreferences
import com.example.turcoespanolninos.model.AprendizajeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AprendizajeViewModel(application: Application) : AndroidViewModel(application) {
    private val preferencias = UserPreferences(application)
    private val _estado = MutableStateFlow(AprendizajeUiState())
    val estado: StateFlow<AprendizajeUiState> = _estado

    init {
        viewModelScope.launch {
            preferencias.progresoGuardado.collect { progreso ->
                _estado.value = AprendizajeUiState(
                    nivelActual = progreso.nivelDesbloqueado.coerceIn(0, ContenidoAprendizaje.niveles.lastIndex),
                    estrellas = progreso.estrellas,
                    palabrasCompletadas = progreso.palabrasCompletadas,
                    medallaObtenida = progreso.nivelDesbloqueado >= ContenidoAprendizaje.niveles.size,
                    cargando = false
                )
            }
        }
    }

    fun marcarPalabraAprendida(id: String) {
        if (id in _estado.value.palabrasCompletadas) return
        _estado.update {
            it.copy(
                palabrasCompletadas = it.palabrasCompletadas + id,
                estrellas = it.estrellas + 1
            )
        }
        persistir()
    }

    fun avanzarNivel() {
        val estadoActual = _estado.value
        val nivel = ContenidoAprendizaje.niveles[estadoActual.nivelActual]
        val nivelCompleto = nivel.palabras.all { it.id in estadoActual.palabrasCompletadas }
        if (!nivelCompleto) return

        val esUltimo = estadoActual.nivelActual == ContenidoAprendizaje.niveles.lastIndex
        _estado.update {
            it.copy(
                nivelActual = if (esUltimo) it.nivelActual else it.nivelActual + 1,
                estrellas = it.estrellas + 5,
                medallaObtenida = esUltimo
            )
        }
        persistir(nivelFinalizado = if (esUltimo) ContenidoAprendizaje.niveles.size else null)
    }

    private fun persistir(nivelFinalizado: Int? = null) {
        val actual = _estado.value
        viewModelScope.launch {
            preferencias.guardarProgreso(
                ProgresoGuardado(
                    nivelDesbloqueado = nivelFinalizado ?: actual.nivelActual,
                    estrellas = actual.estrellas,
                    palabrasCompletadas = actual.palabrasCompletadas
                )
            )
        }
    }
}
