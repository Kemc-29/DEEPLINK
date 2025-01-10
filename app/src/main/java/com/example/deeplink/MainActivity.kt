package com.example.deeplink

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MoviesActivityScreen(intent = intent)
            }
        }
    }
}

@Composable
fun MoviesActivityScreen(intent: android.content.Intent) {
    val peliculasJson = intent?.data?.getQueryParameter("data")
    var listaDePeliculas by remember { mutableStateOf<List<Movie>>(emptyList()) }

    LaunchedEffect(peliculasJson) {
        if (peliculasJson != null) {
            listaDePeliculas = Gson().fromJson(peliculasJson, Array<Movie>::class.java).toList()
        }
    }

    if (listaDePeliculas.isEmpty()) {
        Text(
            text = "No se recibieron películas",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
    } else {
        MoviesListScreen(listaDePeliculas)
    }
}

@Composable
fun MoviesListScreen(movies: List<Movie>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(movies) { movie ->
            MovieItem(movie = movie)
        }
    }
}

@Composable
fun MovieItem(movie: Movie) {
    val imageUrl = "https://image.tmdb.org/t/p/w500${movie.posterPath}"

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(imageUrl) {
        bitmap = downloadBitmap(imageUrl)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .clip(MaterialTheme.shapes.medium),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier
                .height(200.dp)
                .background(Color.Gray)
        ) {

            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Poster de ${movie.title}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 50f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.height(60.dp)
                )
            }
        }
    }
}

suspend fun downloadBitmap(imageUrl: String): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL(imageUrl)
            val connection = url.openConnection()
            connection.connect()
            val inputStream = connection.getInputStream()
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    @SerializedName("poster_path") val posterPath: String
)
