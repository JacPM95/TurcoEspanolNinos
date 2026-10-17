package com.example.turcoespanolninos.ui.theme.screen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.turcoespanolninos.data.ContenidoAprendizaje
import com.example.turcoespanolninos.model.PalabraAprendizaje
import com.example.turcoespanolninos.model.PronunciacionValidator
import com.example.turcoespanolninos.viewmodel.AprendizajeViewModel
import java.util.Locale

private enum class ResultadoPractica { PENDIENTE, ESCUCHANDO, CORRECTO, REINTENTAR }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularioScreen(alias: String, viewModel: AprendizajeViewModel) {
    val estado by viewModel.estado.collectAsState()
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var ttsDisponible by remember { mutableStateOf(false) }
    var reconocedor by remember { mutableStateOf<SpeechRecognizer?>(null) }
    var palabraPractica by remember { mutableStateOf<PalabraAprendizaje?>(null) }
    var resultadoPractica by remember { mutableStateOf(ResultadoPractica.PENDIENTE) }
    var mensajePractica by remember { mutableStateOf("") }
    val palabraActualizada by rememberUpdatedState(palabraPractica)

    fun mensajeErrorReconocimiento(error: Int): String = when (error) {
        SpeechRecognizer.ERROR_NO_MATCH,
        SpeechRecognizer.ERROR_SPEECH_TIMEOUT ->
            "No alcancé a escuchar la palabra. ¡Intentémoslo otra vez!"
        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->
            "Necesitamos permiso para usar el micrófono."
        SpeechRecognizer.ERROR_NETWORK,
        SpeechRecognizer.ERROR_NETWORK_TIMEOUT ->
            "La voz no está disponible ahora. Revisa la conexión e inténtalo nuevamente."
        else -> "No pude escuchar bien. ¡Probemos una vez más!"
    }

    DisposableEffect(Unit) {
        val motor = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsDisponible = tts?.setLanguage(Locale.forLanguageTag("es-ES")) !=
                    TextToSpeech.LANG_MISSING_DATA
            }
        }
        tts = motor

        val motorVoz = if (SpeechRecognizer.isRecognitionAvailable(context)) {
            SpeechRecognizer.createSpeechRecognizer(context)
        } else null
        reconocedor = motorVoz
        motorVoz?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                resultadoPractica = ResultadoPractica.ESCUCHANDO
                mensajePractica = "Te escucho… di la palabra con calma."
            }
            override fun onBeginningOfSpeech() = Unit
            override fun onRmsChanged(rmsdB: Float) = Unit
            override fun onBufferReceived(buffer: ByteArray?) = Unit
            override fun onEndOfSpeech() {
                mensajePractica = "¡Muy bien! Estoy comprobando tu respuesta…"
            }
            override fun onError(error: Int) {
                resultadoPractica = ResultadoPractica.REINTENTAR
                mensajePractica = mensajeErrorReconocimiento(error)
            }
            override fun onResults(results: Bundle?) {
                val alternativas = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    .orEmpty()
                val palabra = palabraActualizada ?: return
                if (PronunciacionValidator.coincide(palabra.espanol, alternativas)) {
                    viewModel.marcarPalabraAprendida(palabra.id)
                    resultadoPractica = ResultadoPractica.CORRECTO
                    mensajePractica = "¡Excelente! Dijiste ${palabra.espanol}. Ganaste una estrella."
                } else {
                    resultadoPractica = ResultadoPractica.REINTENTAR
                    mensajePractica =
                        "¡Casi lo tienes! Escucha ${palabra.espanol} e inténtalo nuevamente."
                }
            }
            override fun onPartialResults(partialResults: Bundle?) = Unit
            override fun onEvent(eventType: Int, params: Bundle?) = Unit
        })

        onDispose {
            motor.stop()
            motor.shutdown()
            motorVoz?.cancel()
            motorVoz?.destroy()
        }
    }

    fun iniciarEscucha() {
        val palabra = palabraPractica ?: return
        if (reconocedor == null) {
            resultadoPractica = ResultadoPractica.REINTENTAR
            mensajePractica = "Este dispositivo no tiene reconocimiento de voz disponible."
            return
        }
        resultadoPractica = ResultadoPractica.ESCUCHANDO
        mensajePractica = "Preparando el micrófono…"
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "es-ES")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Di: ${palabra.espanol}")
        }
        runCatching { reconocedor?.startListening(intent) }
            .onFailure {
                resultadoPractica = ResultadoPractica.REINTENTAR
                mensajePractica = "No pude iniciar el micrófono. ¡Intentemos nuevamente!"
            }
    }

    val permisoMicrofono = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) iniciarEscucha()
        else {
            resultadoPractica = ResultadoPractica.REINTENTAR
            mensajePractica = "Necesitamos permiso para escucharte practicar."
        }
    }

    fun solicitarEscucha() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_GRANTED
        ) iniciarEscucha()
        else permisoMicrofono.launch(Manifest.permission.RECORD_AUDIO)
    }

    if (estado.cargando) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (estado.medallaObtenida) {
        PantallaFelicitaciones(alias, estado.estrellas, viewModel::reiniciarRuta)
        return
    }

    val nivel = ContenidoAprendizaje.niveles[estado.nivelActual]
    val completadas = nivel.palabras.count { it.id in estado.palabrasCompletadas }
    val progreso = completadas.toFloat() / nivel.palabras.size

    Scaffold(topBar = { TopAppBar(title = { Text("¡Hola, $alias! / Merhaba!") }) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Nivel ${nivel.numero}: ${nivel.tituloEspanol}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${nivel.tituloTurco} · Aprende español",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Progreso: $completadas/${nivel.palabras.size}")
                    Text("⭐ ${estado.estrellas}")
                }
                LinearProgressIndicator(
                    progress = { progreso },
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                )
                Text(
                    "Escucha cada palabra y practica su pronunciación para ganar una estrella.",
                    modifier = Modifier.padding(vertical = 12.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            items(nivel.palabras, key = { it.id }) { palabra ->
                TarjetaPalabra(
                    palabra = palabra,
                    completada = palabra.id in estado.palabrasCompletadas,
                    audioDisponible = ttsDisponible,
                    onEscuchar = {
                        tts?.speak(palabra.espanol, TextToSpeech.QUEUE_FLUSH, null, palabra.id)
                    },
                    onPracticar = {
                        palabraPractica = palabra
                        resultadoPractica = ResultadoPractica.PENDIENTE
                        mensajePractica = "Ahora repite conmigo: ${palabra.espanol}."
                        tts?.speak(
                            "Ahora repite conmigo: ${palabra.espanol}",
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            "practica_${palabra.id}"
                        )
                    }
                )
            }

            item {
                AnimatedVisibility(
                    visible = completadas == nivel.palabras.size,
                    enter = fadeIn() + scaleIn()
                ) {
                    Button(
                        onClick = viewModel::avanzarNivel,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                    ) {
                        Text(
                            if (nivel.numero == ContenidoAprendizaje.niveles.size)
                                "Finalizar ruta / Rotayı bitir"
                            else "Siguiente nivel / Sonraki seviye"
                        )
                    }
                }
            }
        }
    }

    palabraPractica?.let { palabra ->
        AlertDialog(
            onDismissRequest = {
                if (resultadoPractica != ResultadoPractica.ESCUCHANDO) palabraPractica = null
            },
            icon = { Text(if (resultadoPractica == ResultadoPractica.CORRECTO) "⭐" else "🎤") },
            title = { Text("Practiquemos: ${palabra.espanol}") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(mensajePractica, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "La app no guarda tu grabación; el servicio de voz del dispositivo la procesa.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                when (resultadoPractica) {
                    ResultadoPractica.CORRECTO -> TextButton(onClick = { palabraPractica = null }) {
                        Text("Continuar")
                    }
                    ResultadoPractica.ESCUCHANDO -> TextButton(onClick = {}) { Text("Escuchando…") }
                    else -> Button(onClick = { solicitarEscucha() }) {
                        Text(if (resultadoPractica == ResultadoPractica.REINTENTAR) "Intentar otra vez" else "🎤 Repetir")
                    }
                }
            },
            dismissButton = {
                if (resultadoPractica != ResultadoPractica.ESCUCHANDO &&
                    resultadoPractica != ResultadoPractica.CORRECTO
                ) {
                    TextButton(onClick = {
                        tts?.speak(
                            palabra.espanol,
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            "repetir_${palabra.id}"
                        )
                    }) { Text("🔊 Escuchar") }
                }
            }
        )
    }
}

@Composable
private fun TarjetaPalabra(
    palabra: PalabraAprendizaje,
    completada: Boolean,
    audioDisponible: Boolean,
    onEscuchar: () -> Unit,
    onPracticar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (completada) MaterialTheme.colorScheme.secondaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(palabra.emoji, style = MaterialTheme.typography.displaySmall)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        palabra.espanol,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Türkçe: ${palabra.turco}", style = MaterialTheme.typography.bodyLarge)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onEscuchar,
                    enabled = audioDisponible,
                    modifier = Modifier.weight(1f)
                ) { Text("🔊 Español") }
                Button(
                    onClick = onPracticar,
                    enabled = !completada,
                    modifier = Modifier.weight(1f)
                ) { Text(if (completada) "✓ Pronunciada" else "Ya la aprendí") }
            }
        }
    }
}

@Composable
private fun PantallaFelicitaciones(alias: String, estrellas: Int, onRepasar: () -> Unit) {
    AnimatedVisibility(visible = true, enter = fadeIn() + scaleIn()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("🏅", style = MaterialTheme.typography.displayLarge)
            Spacer(modifier = Modifier.size(20.dp))
            Text(
                "¡FELICIDADES, $alias!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                "Tebrikler! Dört seviyeyi tamamladın.",
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
            Text(
                "Has superado los cuatro niveles. ¡Sigue practicando español!",
                modifier = Modifier.padding(top = 16.dp),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Text(
                "⭐ $estrellas estrellas acumuladas",
                modifier = Modifier.padding(top = 24.dp),
                style = MaterialTheme.typography.titleLarge
            )
            Button(
                onClick = onRepasar,
                modifier = Modifier.fillMaxWidth().padding(top = 28.dp)
            ) { Text("Repasar nuevamente / Yeniden çalış") }
            Text(
                "Conservarás tus estrellas y podrás acumular más.",
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}
