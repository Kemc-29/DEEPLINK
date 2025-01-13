package com.example.deeplink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deeplink.ui.view.MoviesListScreen
import com.example.deeplink.ui.viewmodel.MoviesViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.Text


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val data = intent?.data?.getQueryParameter("data") ?: ""
            val viewModel: MoviesViewModel = viewModel()

            // Cargar películas basadas en el Deeplink
            viewModel.fetchMoviesFromDeeplink(data)

            // Observar los datos de las películas
            val movies by viewModel.movies.collectAsState()

            if (movies.isNotEmpty()) {
                MoviesListScreen(movies)
            } else {
                Text("No se recibieron películas")
            }
        }
    }
}
