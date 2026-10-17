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
            aliasError = if (alias.isBlank()) "Lütfen bir takma ad gir" else null,
            edadError = when {
                edad == null -> "Lütfen geçerli bir yaş gir"
                edad !in 1..10 -> "Bu uygulama 1–10 yaş arası çocuklar içindir"
                else -> null
            },
            edad = edad
        )
    }
}
