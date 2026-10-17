package com.example.turcoespanolninos

import com.example.turcoespanolninos.model.PerfilValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PerfilValidatorTest {
    @Test
    fun edadesEntreUnoYDiezSonValidas() {
        (1..10).forEach { edad ->
            assertTrue("La edad $edad debería ser válida", PerfilValidator.validar("Pepe", "$edad").esValido)
        }
    }

    @Test
    fun edadMayorADiezEsRechazada() {
        assertFalse(PerfilValidator.validar("Pepe", "11").esValido)
    }

    @Test
    fun edadVaciaEsRechazada() {
        assertFalse(PerfilValidator.validar("Pepe", "").esValido)
    }

    @Test
    fun aliasVacioEsRechazado() {
        assertFalse(PerfilValidator.validar(" ", "9").esValido)
    }
}
