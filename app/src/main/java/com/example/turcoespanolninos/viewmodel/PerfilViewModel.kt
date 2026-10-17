package com.example.turcoespanolninos.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.turcoespanolninos.data.UserPreferences
import com.example.turcoespanolninos.model.PerfilErrores
import com.example.turcoespanolninos.model.PerfilUsuarioUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)
    private val _estado = MutableStateFlow(PerfilUsuarioUiState())
    val estado: StateFlow<PerfilUsuarioUiState> = _estado

    init {
        viewModelScope.launch {
            userPreferences.guardarAlias.collect { aliasGuardado ->
                if (aliasGuardado.isNotEmpty()) {
                    _estado.update { it.copy(aliasFicticio = aliasGuardado) }
                }
            }
        }
    }

    fun onAliasChange(nuevoAlias: String) {
        _estado.update {
            it.copy(
                aliasFicticio = nuevoAlias,
                errores = it.errores.copy(aliasFicticio = null)
            )
        }
    }

    fun onEdadChange(nuevaEdad: String) {
        _estado.update {
            it.copy(
                edadSimulada = nuevaEdad,
                errores = it.errores.copy(edadSimulada = null)
            )
        }
    }

    fun actualizarImagenPerfil(uri: Uri) {
        _estado.update { it.copy(imagenPerfilUri = uri) }
    }

    fun validarFormulario(): Boolean {
        val actual = _estado.value
        val edadNum = actual.edadSimulada.toIntOrNull()

        val errAlias = if (actual.aliasFicticio.isBlank()) "Ingresa un apodo ficticio" else null
        val errEdad = when {
            edadNum == null -> "Ingresa una edad válida"
            edadNum <= 0 || edadNum >= 10 -> "La app es solo para menores de 10 años"
            else -> null
        }

        val hayErrores = errAlias != null || errEdad != null

        _estado.update {
            it.copy(
                errores = PerfilErrores(
                    aliasFicticio = errAlias,
                    edadSimulada = errEdad
                )
            )
        }

        if (!hayErrores) {
            viewModelScope.launch {
                userPreferences.salvarAlias(actual.aliasFicticio)
            }
        }

        return !hayErrores
    }
}
