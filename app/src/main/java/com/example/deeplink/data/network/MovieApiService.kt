package com.example.deeplink.data.network

import com.example.deeplink.data.model.Movie
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApiService{
    @GET("movies")
    suspend fun getMovies(@Query("data") data: String): List<Movie>
}