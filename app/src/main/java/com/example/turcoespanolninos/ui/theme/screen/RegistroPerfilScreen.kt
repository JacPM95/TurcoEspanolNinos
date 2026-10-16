package com.example.turcoespanolninos.ui.theme.screen

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.turcoespanolninos.viewmodel.PerfilViewModel

// Función auxiliar para activar la respuesta háptica por vibración (Recurso Nativo)
private fun ejecutarVibracion(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(100)
    }
}

@Composable
fun RegistroPerfilScreen(
    viewModel: PerfilViewModel,
    onRegistroExitoso: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    val context = LocalContext.current

    // 1. ANIMACIÓN DE ENTRADA COMPLETA DE LA PANTALLA
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + expandVertically()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¡Bienvenido! / Hoş geldiniz!",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo Alias / Apodo
            OutlinedTextField(
                value = estado.aliasFicticio,
                onValueChange = viewModel::onAliasChange,
                label = { Text("Apodo/Alias Ficticio") },
                isError = estado.errores.aliasFicticio != null,
                trailingIcon = {
                    if (estado.errores.aliasFicticio != null) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error de validación",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                supportingText = {
                    // 2. ANIMACIÓN FLUIDA PARA LA APARICIÓN DEL MENSAJE DE ERROR
                    AnimatedVisibility(
                        visible = estado.errores.aliasFicticio != null,
                        enter = fadeIn() + slideInVertically()
                    ) {
                        estado.errores.aliasFicticio?.let { mensaje ->
                            Text(text = mensaje, color = MaterialTheme.colorScheme.error)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo Edad (< 10 años)
            OutlinedTextField(
                value = estado.edadSimulada,
                onValueChange = viewModel::onEdadChange,
                label = { Text("Edad (Menor a 10 años)") },
                isError = estado.errores.edadSimulada != null,
                trailingIcon = {
                    if (estado.errores.edadSimulada != null) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error de validación",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                supportingText = {
                    // ANIMACIÓN FLUIDA PARA EL ERROR DE EDAD
                    AnimatedVisibility(
                        visible = estado.errores.edadSimulada != null,
                        enter = fadeIn() + slideInVertically()
                    ) {
                        estado.errores.edadSimulada?.let { mensaje ->
                            Text(text = mensaje, color = MaterialTheme.colorScheme.error)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // Ejecuta la respuesta háptica (vibración) al pulsar el botón
                    ejecutarVibracion(context)

                    if (viewModel.validarFormulario()) {
                        onRegistroExitoso()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Comenzar Aprendizaje")
            }
        }
    }
}