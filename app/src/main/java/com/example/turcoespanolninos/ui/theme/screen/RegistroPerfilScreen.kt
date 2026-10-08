package com.example.turcoespanolninos.ui.theme.screen

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

        OutlinedTextField(
            value = estado.aliasFicticio,
            onValueChange = viewModel::onAliasChange,
            label = { Text("Apodo/Alias Ficticio") },
            isError = estado.errores.aliasFicticio != null,
            supportingText = {
                estado.errores.aliasFicticio?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = estado.edadSimulada,
            onValueChange = viewModel::onEdadChange,
            label = { Text("Edad (Menor a 10 años)") },
            isError = estado.errores.edadSimulada != null,
            supportingText = {
                estado.errores.edadSimulada?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
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