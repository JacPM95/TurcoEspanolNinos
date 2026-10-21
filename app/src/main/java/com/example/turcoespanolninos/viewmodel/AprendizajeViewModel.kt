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
                val nivelDesbloqueado = progreso.nivelDesbloqueado
                    .coerceIn(0, ContenidoAprendizaje.niveles.size)
                val paginaActual = progreso.paginaActual.coerceIn(0, nivelDesbloqueado)
                _estado.value = AprendizajeUiState(
                    nivelActual = paginaActual,
                    nivelDesbloqueado = nivelDesbloqueado,
                    estrellas = progreso.estrellas,
                    palabrasCompletadas = progreso.palabrasCompletadas,
                    medallaObtenida = paginaActual == ContenidoAprendizaje.niveles.size,
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
        if (estadoActual.nivelActual >= ContenidoAprendizaje.niveles.size) return
        val nivel = ContenidoAprendizaje.niveles[estadoActual.nivelActual]
        val nivelCompleto = nivel.palabras.all { it.id in estadoActual.palabrasCompletadas }
        if (!nivelCompleto) return

        val yaEstabaDesbloqueado = estadoActual.nivelActual < estadoActual.nivelDesbloqueado
        val siguientePagina = estadoActual.nivelActual + 1
        _estado.update {
            it.copy(
                nivelActual = siguientePagina,
                nivelDesbloqueado = maxOf(it.nivelDesbloqueado, siguientePagina),
                estrellas = if (yaEstabaDesbloqueado) it.estrellas else it.estrellas + 5,
                medallaObtenida = siguientePagina == ContenidoAprendizaje.niveles.size
            )
        }
        persistir()
    }

    fun reiniciarRuta() {
        _estado.update {
            it.copy(
                nivelActual = 0,
                nivelDesbloqueado = 0,
                palabrasCompletadas = emptySet(),
                medallaObtenida = false,
                cargando = false
            )
        }
        persistir()
    }

    fun irAnterior() {
        if (!_estado.value.puedeRetroceder) return
        _estado.update {
            val pagina = it.nivelActual - 1
            it.copy(nivelActual = pagina, medallaObtenida = false)
        }
        persistir()
    }

    fun irSiguiente() {
        if (!_estado.value.puedeAvanzar) return
        _estado.update {
            val pagina = it.nivelActual + 1
            it.copy(
                nivelActual = pagina,
                medallaObtenida = pagina == ContenidoAprendizaje.niveles.size
            )
        }
        persistir()
    }

    private fun persistir() {
        val actual = _estado.value
        viewModelScope.launch {
            preferencias.guardarProgreso(
                ProgresoGuardado(
                    nivelDesbloqueado = actual.nivelDesbloqueado,
                    paginaActual = actual.nivelActual,
                    estrellas = actual.estrellas,
                    palabrasCompletadas = actual.palabrasCompletadas
                )
            )
        }
    }
}
