package com.example.turcoespanolninos.ui.theme.screen

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.util.Locale

data class TarjetaVocabulario(
    val id: Int,
    val palabraTurco: String,
    val palabraEspanol: String,
    val emoji: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularioScreen(alias: String) {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(Unit) {
        val speechListener = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("tr", "TR")
            }
        }
        tts = speechListener
        onDispose {
            speechListener.stop()
            speechListener.shutdown()
        }
    }

    val listaVocabulario = remember {
        listOf(
            TarjetaVocabulario(1, "Kedi", "Gato", "🐱"),
            TarjetaVocabulario(2, "Köpek", "Perro", "🐶"),
            TarjetaVocabulario(3, "Kuş", "Pájaro", "🐦"),
            TarjetaVocabulario(4, "Elma", "Manzana", "🍎"),
            TarjetaVocabulario(5, "Su", "Agua", "💧")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("¡Hola, $alias! / Merhaba!") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(listaVocabulario) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = item.emoji, style = MaterialTheme.typography.headlineMedium)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = item.palabraTurco,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = item.palabraEspanol,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Button(
                            onClick = {
                                tts?.speak(item.palabraTurco, TextToSpeech.QUEUE_FLUSH, null, null)
                            }
                        ) {
                            Text("🔊 Escuchar")
                        }
                    }
                }
            }
        }
    }
}