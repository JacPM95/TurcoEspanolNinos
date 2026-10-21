package com.example.turcoespanolninos.ui.theme.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.turcoespanolninos.ui.theme.components.ImagenPerfil
import com.example.turcoespanolninos.viewmodel.PerfilViewModel

private fun ejecutarVibracion(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator.vibrate(
            VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
        )
    } else {
        @Suppress("DEPRECATION")
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(100)
    }
}

private fun cargarImagenSoloEnMemoria(context: Context, uri: Uri): Bitmap? {
    val opcionesTamano = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, opcionesTamano)
    }

    var escala = 1
    while (opcionesTamano.outWidth / escala > 1024 || opcionesTamano.outHeight / escala > 1024) {
        escala *= 2
    }

    val opcionesCarga = BitmapFactory.Options().apply { inSampleSize = escala }
    return context.contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, opcionesCarga)
    }
}

@Composable
fun RegistroPerfilScreen(
    viewModel: PerfilViewModel,
    perfilExistente: Boolean,
    onRegistroExitoso: () -> Unit,
    onVolverAprendizaje: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var errorCamara by remember { mutableStateOf<String?>(null) }

    BackHandler(enabled = perfilExistente, onBack = onVolverAprendizaje)

    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmapTemporal ->
        if (bitmapTemporal != null) {
            viewModel.actualizarImagenPerfil(bitmapTemporal)
            errorCamara = null
        } else {
            errorCamara = "Fotoğraf çekimi iptal edildi"
        }
    }

    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            runCatching { cargarImagenSoloEnMemoria(context, it) }
                .onSuccess { bitmap ->
                    if (bitmap != null) viewModel.actualizarImagenPerfil(bitmap)
                    else errorCamara = "Seçilen görsel okunamadı"
                }
                .onFailure { errorCamara = "Seçilen görsel okunamadı" }
        }
    }

    val abrirCamara = {
        runCatching { launcherCamara.launch(null) }
            .onFailure { error ->
                errorCamara = error.message ?: "Kamera açılamadı"
            }
    }

    val launcherPermisoCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { permisoConcedido ->
        if (permisoConcedido) abrirCamara()
        else errorCamara = "Fotoğraf çekmek için kamera izni gerekiyor"
    }

    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + expandVertically()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(all = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("İspanyolca öğrenelim!", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(20.dp))
                ImagenPerfil(imagen = estado.imagenPerfil)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        errorCamara = null
                        if (
                            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_GRANTED
                        ) abrirCamara()
                        else launcherPermisoCamara.launch(Manifest.permission.CAMERA)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (estado.imagenPerfil == null) "Geçici fotoğraf çek (kaydedilmez)"
                        else "Fotoğrafı değiştir"
                    )
                }

                OutlinedButton(
                    onClick = { launcherGaleria.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Avatar veya izinli bir görsel seç") }

                Text(
                    "Gizlilik: Görsel yalnızca bellekte tutulur ve kaydedilmez.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                errorCamara?.let { mensaje ->
                    Text(mensaje, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = estado.aliasFicticio,
                    onValueChange = viewModel::onAliasChange,
                    label = { Text("Takma ad") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    isError = estado.errores.aliasFicticio != null,
                    trailingIcon = {
                        if (estado.errores.aliasFicticio != null) {
                            Icon(Icons.Default.Warning, "Doğrulama hatası", tint = MaterialTheme.colorScheme.error)
                        }
                    },
                    supportingText = {
                        AnimatedVisibility(
                            visible = estado.errores.aliasFicticio != null,
                            enter = fadeIn() + slideInVertically()
                        ) {
                            estado.errores.aliasFicticio?.let { mensaje ->
                                Text(mensaje, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = estado.edadSimulada,
                    onValueChange = viewModel::onEdadChange,
                    label = { Text("Yaş (1–10)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }),
                    isError = estado.errores.edadSimulada != null,
                    trailingIcon = {
                        if (estado.errores.edadSimulada != null) {
                            Icon(Icons.Default.Warning, "Doğrulama hatası", tint = MaterialTheme.colorScheme.error)
                        }
                    },
                    supportingText = {
                        AnimatedVisibility(
                            visible = estado.errores.edadSimulada != null,
                            enter = fadeIn() + slideInVertically()
                        ) {
                            estado.errores.edadSimulada?.let { mensaje ->
                                Text(mensaje, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        ejecutarVibracion(context)
                        if (viewModel.validarFormulario()) {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            onRegistroExitoso()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Başla") }
            }

            if (perfilExistente) {
                IconButton(
                    onClick = onVolverAprendizaje,
                    modifier = Modifier.align(Alignment.TopEnd).statusBarsPadding().padding(8.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, "Öğrenmeye dön")
                }
            }
        }
    }
}
