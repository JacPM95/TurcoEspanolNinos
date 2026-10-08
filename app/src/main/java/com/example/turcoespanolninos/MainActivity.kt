package com.example.turcoespanolninos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.turcoespanolninos.ui.theme.screen.RegistroPerfilScreen
import com.example.turcoespanolninos.ui.theme.screen.VocabularioScreen
import com.example.turcoespanolninos.viewmodel.PerfilViewModel

class MainActivity : ComponentActivity() {
    private val perfilViewModel: PerfilViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    val estado by perfilViewModel.estado.collectAsState()
                    var mostrarVocabulario by remember { mutableStateOf(false) }

                    if (mostrarVocabulario) {
                        VocabularioScreen(alias = estado.aliasFicticio)
                    } else {
                        RegistroPerfilScreen(
                            viewModel = perfilViewModel,
                            onRegistroExitoso = {
                                mostrarVocabulario = true
                            }
                        )
                    }
                }
            }
        }
    }
}