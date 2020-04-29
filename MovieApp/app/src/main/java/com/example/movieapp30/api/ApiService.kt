package com.example.movieapp30.api

import android.util.Log
import com.example.movieapp30.model.*
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit


object RetrofitService {

    var gson = GsonBuilder()
        .setLenient()
        .create()

    const val BASE_URL = "https://api.themoviedb.org/3/"

    fun getPostApi(): PostApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(getOkHttp())
            .build()
        return retrofit.create(PostApi::class.java)
    }

    private fun getOkHttp(): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(getLoggingInterceptor())
        return okHttpClient.build()
    }

    private fun getLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor(logger = object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Log.d("OkHttp", message)
            }
        }).apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
}

interface PostApi {

    @GET("movie/popular")
    suspend fun getPopularMoviesList (
        @Query("api_key") apiKey: String?,
        @Query("page") page: Int,
        @Query("language") lang: String?
    ): Response<MovieResponse>


    @GET("account/{account_id}/favorite/movies")
    suspend fun getFavouritesMoviesList (
        @Path(value = "account_id", encoded = true) accountId: Int,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String,
        @Query("page") page: Int,
        @Query("language") lang: String?
    ): Response<MovieResponse>

    @GET("authentication/token/new")
    suspend fun createRequestToken (
        @Query("api_key") apiKey: String
    ): Response<GetRequestTokenResponse>

    @POST("authentication/token/validate_with_login")
    suspend fun createSessionWithLogin (
        @Query("api_key") apiKey: String?,
        @Body body: JsonObject
    ): Response<GetRequestTokenResponse>

    @POST("authentication/session/new")
    suspend fun createSession (
        @Query("api_key") apiKey: String?,
        @Body body: JsonObject
    ): Response<CreateSessionResponse>

    @GET("account")
    @Headers(
        "Content-Type: application/json;charset=utf-8"
    )
    suspend fun getAccountDetails (
        @Query("api_key") apiKey: String?,
        @Query("session_id") sessionId: String?
    ): Response<AccountDetailsResponse>

    @POST("account/{account_id}/favorite")
    suspend fun markAsFavourite (
        @Path(value = "account_id", encoded = true) accountId: Int,
        @Query("api_key") apiKey: String?,
        @Query("session_id") sessionId: String?,
        @Body body: JsonObject
    ): Response<JsonObject>

    @GET("movie/{movie_id}")
    suspend fun getMovie (
        @Path(value = "movie_id", encoded = true) movieId: Int,
        @Query("api_key") apiKey: String?,
        @Query("language") lang: String?
    ): Response<Movie>

    @HTTP(method = "DELETE", path = "authentication/session", hasBody = true)
    suspend fun deleteSession (
        @Query("api_key") apiKey: String,
        @Body body: JsonObject
    ): Response<JsonObject>
}