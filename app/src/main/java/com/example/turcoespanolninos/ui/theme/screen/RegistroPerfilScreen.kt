package com.example.turcoespanolninos.ui.theme.screen

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.turcoespanolninos.viewmodel.PerfilViewModel
import kotlinx.coroutines.delay

private fun ejecutarVibracion(context: Context) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            vibrator?.vibrate(100)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@Composable
fun RegistroPerfilScreen(
    viewModel: PerfilViewModel,
    onRegistroExitoso: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Control para forzar el inicio de la animación de entrada
    var visibilidadFormulario by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100) // Breve pausa para asegurar el renderizado
        visibilidadFormulario = true
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // ANIMACIÓN DE ENTRADA VISIBLE (Desvanecimiento y Desplazamiento desde abajo)
        AnimatedVisibility(
            visible = visibilidadFormulario,
            enter = fadeIn(animationSpec = tween(durationMillis = 800)) +
                    slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(durationMillis = 800)
                    )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(24.dp),
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
                            Text(text = "⚠️", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    supportingText = {
                        estado.errores.aliasFicticio?.let { mensaje ->
                            Text(text = mensaje, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Edad
                OutlinedTextField(
                    value = estado.edadSimulada,
                    onValueChange = viewModel::onEdadChange,
                    label = { Text("Edad (Menor a 10 años)") },
                    isError = estado.errores.edadSimulada != null,
                    trailingIcon = {
                        if (estado.errores.edadSimulada != null) {
                            Text(text = "⚠️", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    supportingText = {
                        estado.errores.edadSimulada?.let { mensaje ->
                            Text(text = mensaje, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
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
}