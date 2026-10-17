package com.example.turcoespanolninos.ui.theme.screen

import android.speech.tts.TextToSpeech
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
import com.example.turcoespanolninos.data.ContenidoAprendizaje
import com.example.turcoespanolninos.model.PalabraAprendizaje
import com.example.turcoespanolninos.viewmodel.AprendizajeViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularioScreen(alias: String, viewModel: AprendizajeViewModel) {
    val estado by viewModel.estado.collectAsState()
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var ttsDisponible by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val motor = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsDisponible = tts?.setLanguage(Locale.forLanguageTag("es-ES")) != TextToSpeech.LANG_MISSING_DATA
            }
        }
        tts = motor
        onDispose {
            motor.stop()
            motor.shutdown()
        }
    }

    if (estado.cargando) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (estado.medallaObtenida) {
        PantallaFelicitaciones(alias = alias, estrellas = estado.estrellas)
        return
    }

    val nivel = ContenidoAprendizaje.niveles[estado.nivelActual]
    val completadas = nivel.palabras.count { it.id in estado.palabrasCompletadas }
    val progreso = completadas.toFloat() / nivel.palabras.size

    Scaffold(topBar = { TopAppBar(title = { Text("¡Hola, $alias! / Merhaba!") }) }) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Nivel ${nivel.numero}: ${nivel.tituloEspanol}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${nivel.tituloTurco} · Aprende español",
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
                    text = "Dinle, tekrarla ve öğrendiğinde işaretle. / Escucha, repite y marca cuando la aprendas.",
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
                    onCompletar = { viewModel.marcarPalabraAprendida(palabra.id) }
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
                            if (nivel.numero == ContenidoAprendizaje.niveles.size) {
                                "Finalizar ruta / Rotayı bitir"
                            } else {
                                "Siguiente nivel / Sonraki seviye"
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaPalabra(
    palabra: PalabraAprendizaje,
    completada: Boolean,
    audioDisponible: Boolean,
    onEscuchar: () -> Unit,
    onCompletar: () -> Unit
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
                Text(text = palabra.emoji, style = MaterialTheme.typography.displaySmall)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = palabra.espanol,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "Türkçe: ${palabra.turco}", style = MaterialTheme.typography.bodyLarge)
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
                    onClick = onCompletar,
                    enabled = !completada,
                    modifier = Modifier.weight(1f)
                ) { Text(if (completada) "✓ Aprendida" else "Ya la aprendí") }
            }
        }
    }
}

@Composable
private fun PantallaFelicitaciones(alias: String, estrellas: Int) {
    AnimatedVisibility(visible = true, enter = fadeIn() + scaleIn()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("🏅", style = MaterialTheme.typography.displayLarge)
            Spacer(modifier = Modifier.size(20.dp))
            Text(
                text = "¡FELICIDADES, $alias!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Tebrikler! Dört seviyeyi tamamladın.",
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Has superado los cuatro niveles. ¡Sigue aprendiendo español!",
                modifier = Modifier.padding(top = 16.dp),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = "⭐ $estrellas estrellas acumuladas",
                modifier = Modifier.padding(top = 24.dp),
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
