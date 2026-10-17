package com.example.turcoespanolninos.model

import java.text.Normalizer

object PronunciacionValidator {
    fun coincide(objetivo: String, alternativas: List<String>): Boolean {
        val palabraObjetivo = normalizar(objetivo)
        if (palabraObjetivo.isBlank()) return false

        return alternativas.any { alternativa ->
            val texto = normalizar(alternativa)
            texto == palabraObjetivo ||
                texto.split(" ").any { palabra -> palabrasSimilares(palabraObjetivo, palabra) }
        }
    }

    private fun normalizar(texto: String): String = Normalizer
        .normalize(texto.lowercase().trim(), Normalizer.Form.NFD)
        .replace("\\p{M}+".toRegex(), "")
        .replace("[^a-z0-9ñ ]".toRegex(), " ")
        .replace("\\s+".toRegex(), " ")
        .trim()

    private fun palabrasSimilares(objetivo: String, resultado: String): Boolean {
        if (objetivo == resultado) return true
        val tolerancia = when {
            objetivo.length >= 8 -> 2
            objetivo.length >= 4 -> 1
            else -> 0
        }
        return distanciaEdicion(objetivo, resultado) <= tolerancia
    }

    private fun distanciaEdicion(origen: String, destino: String): Int {
        var anterior = IntArray(destino.length + 1) { it }
        for (i in origen.indices) {
            val actual = IntArray(destino.length + 1)
            actual[0] = i + 1
            for (j in destino.indices) {
                val sustitucion = if (origen[i] == destino[j]) 0 else 1
                actual[j + 1] = minOf(
                    actual[j] + 1,
                    anterior[j + 1] + 1,
                    anterior[j] + sustitucion
                )
            }
            anterior = actual
        }
        return anterior[destino.length]
    }
}
