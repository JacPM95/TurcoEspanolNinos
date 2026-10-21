package com.example.turcoespanolninos.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.turcoespanolninos.data.UserPreferences
import com.example.turcoespanolninos.model.PerfilErrores
import com.example.turcoespanolninos.model.PerfilUsuarioUiState
import com.example.turcoespanolninos.model.PerfilValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)
    private val _estado = MutableStateFlow(PerfilUsuarioUiState())
    val estado: StateFlow<PerfilUsuarioUiState> = _estado

    init {
        viewModelScope.launch {
            combine(
                userPreferences.aliasGuardado,
                userPreferences.edadGuardada,
                userPreferences.perfilVisible
            ) { alias, edad, perfilVisible ->
                Triple(alias, edad, perfilVisible)
            }.collect { (aliasGuardado, edadGuardada, perfilVisible) ->
                _estado.update {
                    val perfilRegistrado = aliasGuardado.isNotBlank() && edadGuardada in 1..10
                    it.copy(
                        aliasFicticio = aliasGuardado,
                        edadSimulada = edadGuardada.takeIf { edad -> edad in 1..10 }?.toString().orEmpty(),
                        perfilRegistrado = perfilRegistrado,
                        perfilVisible = !perfilRegistrado || perfilVisible,
                        cargando = false
                    )
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
        val edadNormalizada = nuevaEdad.filter(Char::isDigit).take(2)
        _estado.update {
            it.copy(
                edadSimulada = edadNormalizada,
                errores = it.errores.copy(edadSimulada = null)
            )
        }
    }

    fun actualizarImagenPerfil(bitmap: Bitmap) {
        _estado.update { it.copy(imagenPerfil = bitmap) }
    }

    fun validarFormulario(): Boolean {
        val actual = _estado.value
        val resultado = PerfilValidator.validar(actual.aliasFicticio, actual.edadSimulada)

        _estado.update {
            it.copy(
                errores = PerfilErrores(
                    aliasFicticio = resultado.aliasError,
                    edadSimulada = resultado.edadError
                )
            )
        }

        if (resultado.esValido) {
            _estado.update { it.copy(perfilRegistrado = true, perfilVisible = false) }
            viewModelScope.launch {
                userPreferences.guardarPerfil(actual.aliasFicticio.trim(), resultado.edad!!)
            }
        }

        return resultado.esValido
    }

    fun abrirPerfil() {
        _estado.update { it.copy(perfilVisible = true) }
        viewModelScope.launch { userPreferences.guardarPerfilVisible(true) }
    }

    fun volverAlAprendizaje() {
        if (!_estado.value.perfilRegistrado) return
        _estado.update { it.copy(perfilVisible = false) }
        viewModelScope.launch { userPreferences.guardarPerfilVisible(false) }
    }
}
