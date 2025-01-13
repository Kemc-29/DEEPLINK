package com.example.deeplink.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.deeplink.data.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.gson.Gson

class MoviesViewModel : ViewModel() {

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> get() = _movies

    fun fetchMoviesFromDeeplink(data: String) {
        try {
            if (data.isNotEmpty()) {
                val moviesList = Gson().fromJson(data, Array<Movie>::class.java).toList()
                _movies.value = moviesList
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _movies.value = emptyList()
        }
    }
}