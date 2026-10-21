package com.example.turcoespanolninos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.turcoespanolninos.ui.theme.screen.RegistroPerfilScreen
import com.example.turcoespanolninos.ui.theme.screen.VocabularioScreen
import com.example.turcoespanolninos.viewmodel.AprendizajeViewModel
import com.example.turcoespanolninos.viewmodel.PerfilViewModel

class MainActivity : ComponentActivity() {
    private val perfilViewModel: PerfilViewModel by viewModels()
    private val aprendizajeViewModel: AprendizajeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    val estado by perfilViewModel.estado.collectAsState()
                    if (estado.cargando) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else if (estado.perfilRegistrado && !estado.perfilVisible) {
                        VocabularioScreen(
                            alias = estado.aliasFicticio,
                            viewModel = aprendizajeViewModel,
                            onVolverPerfil = perfilViewModel::abrirPerfil
                        )
                    } else {
                        RegistroPerfilScreen(
                            viewModel = perfilViewModel,
                            perfilExistente = estado.perfilRegistrado,
                            onRegistroExitoso = perfilViewModel::volverAlAprendizaje,
                            onVolverAprendizaje = perfilViewModel::volverAlAprendizaje
                        )
                    }
                }
            }
        }
    }
}
