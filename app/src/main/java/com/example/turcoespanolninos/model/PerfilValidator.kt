package com.example.turcoespanolninos.model

data class ResultadoValidacionPerfil(
    val aliasError: String? = null,
    val edadError: String? = null,
    val edad: Int? = null
) {
    val esValido: Boolean get() = aliasError == null && edadError == null
}

object PerfilValidator {
    fun validar(alias: String, edadTexto: String): ResultadoValidacionPerfil {
        val edad = edadTexto.trim().toIntOrNull()
        return ResultadoValidacionPerfil(
            aliasError = if (alias.isBlank()) "Ingresa un apodo ficticio" else null,
            edadError = when {
                edad == null -> "Ingresa una edad válida"
                edad !in 1..10 -> "La aplicación es para niños de 10 años o menos"
                else -> null
            },
            edad = edad
        )
    }
}
