package com.example.turcoespanolninos

import com.example.turcoespanolninos.model.PronunciacionValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PronunciacionValidatorTest {
    @Test
    fun aceptaCoincidenciaExacta() {
        assertTrue(PronunciacionValidator.coincide("perro", listOf("perro")))
    }

    @Test
    fun ignoraMayusculasAcentosYPuntuacion() {
        assertTrue(PronunciacionValidator.coincide("Pájaro", listOf("¡PAJARO!")))
    }

    @Test
    fun encuentraLaPalabraDentroDeUnaFrase() {
        assertTrue(PronunciacionValidator.coincide("caballo", listOf("yo dije caballo")))
    }

    @Test
    fun toleraUnaDiferenciaPequenaEnPalabrasLargas() {
        assertTrue(PronunciacionValidator.coincide("manzana", listOf("mansana")))
    }

    @Test
    fun rechazaUnaPalabraDistinta() {
        assertFalse(PronunciacionValidator.coincide("perro", listOf("gato")))
    }
}
