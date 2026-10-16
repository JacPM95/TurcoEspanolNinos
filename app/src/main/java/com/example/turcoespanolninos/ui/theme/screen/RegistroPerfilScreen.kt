package com.example.turcoespanolninos.ui.screen

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.turcoespanolninos.viewmodel.PerfilViewModel

@Composable
fun RegistroPerfilScreen(
    viewModel: PerfilViewModel,
    onRegistroExitoso: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()

    // Animación declarativa de entrada para la pantalla
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + expandVertically()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¡Bienvenido! / Hoş geldiniz!",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo Alias Ficticio
            OutlinedTextField(
                value = estado.aliasFicticio,
                onValueChange = viewModel::onAliasChange,
                label = { Text("Apodo/Alias Ficticio") },
                isError = estado.errores.aliasFicticio != null,
                supportingText = {
                    // Animación visual para la aparición del mensaje de error
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

            // Campo Edad
            OutlinedTextField(
                value = estado.edadSimulada,
                onValueChange = viewModel::onEdadChange,
                label = { Text("Edad (Menor a 10 años)") },
                isError = estado.errores.edadSimulada != null,
                supportingText = {
                    // Animación visual para el error de edad
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