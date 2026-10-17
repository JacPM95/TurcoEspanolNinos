package com.example.turcoespanolninos.ui.theme.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.turcoespanolninos.ui.theme.components.ImagenPerfil
import com.example.turcoespanolninos.viewmodel.PerfilViewModel
import java.io.File

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

private fun crearUriImagen(context: Context): Uri {
    val directorio = File(context.cacheDir, "images").apply { mkdirs() }
    val archivo = File.createTempFile("perfil_", ".jpg", directorio)
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        archivo
    )
}

@Composable
fun RegistroPerfilScreen(
    viewModel: PerfilViewModel,
    onRegistroExitoso: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var uriTemporal by remember { mutableStateOf<Uri?>(null) }
    var errorCamara by remember { mutableStateOf<String?>(null) }

    val contratoCamara = remember { ActivityResultContracts.TakePicture() }
    val launcherCamara = rememberLauncherForActivityResult(
        contract = contratoCamara
    ) { capturaExitosa ->
        if (capturaExitosa) {
            uriTemporal?.let(viewModel::actualizarImagenPerfil)
            errorCamara = null
        }
        uriTemporal = null
    }

    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let(viewModel::actualizarImagenPerfil)
    }

    val abrirCamara = {
        runCatching {
            val uri = crearUriImagen(context)
            check(contratoCamara.createIntent(context, uri).resolveActivity(context.packageManager) != null) {
                "No hay una aplicación de cámara disponible"
            }
            uri
        }
            .onSuccess { uri ->
                uriTemporal = uri
                runCatching { launcherCamara.launch(uri) }
                    .onFailure { errorCamara = "No se pudo abrir la cámara" }
            }
            .onFailure { error ->
                errorCamara = error.message ?: "No se pudo abrir la cámara"
            }
    }

    val launcherPermisoCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { permisoConcedido ->
        if (permisoConcedido) {
            abrirCamara()
        } else {
            errorCamara = "Se necesita permiso de cámara para tomar la fotografía"
        }
    }

    // 1. ANIMACIÓN DE ENTRADA COMPLETA DE LA PANTALLA
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + expandVertically()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(all = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "İspanyolca öğrenelim! / ¡Aprendamos español!",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            ImagenPerfil(imagenUri = estado.imagenPerfilUri)

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    errorCamara = null
                    if (
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        abrirCamara()
                    } else {
                        launcherPermisoCamara.launch(Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (estado.imagenPerfilUri == null) {
                        "Cámara (solo imagen de prueba)"
                    } else {
                        "Cambiar fotografía"
                    }
                )
            }

            OutlinedButton(
                onClick = { launcherGaleria.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Elegir avatar o imagen autorizada")
            }

            Text(
                text = "Gizlilik: Gerçek çocuk fotoğrafları kullanmayın. / Privacidad: no uses fotos reales de menores.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            errorCamara?.let { mensaje ->
                Text(
                    text = mensaje,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Campo Alias / Apodo
            OutlinedTextField(
                value = estado.aliasFicticio,
                onValueChange = viewModel::onAliasChange,
                label = { Text("Takma ad / Alias ficticio") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
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

            // Campo Edad (1 a 10 años, inclusive)
            OutlinedTextField(
                value = estado.edadSimulada,
                onValueChange = viewModel::onEdadChange,
                label = { Text("Yaş / Edad (1 a 10 años)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
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
                        focusManager.clearFocus()
                        onRegistroExitoso()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Comenzar / Başla")
            }
        }
    }
}
