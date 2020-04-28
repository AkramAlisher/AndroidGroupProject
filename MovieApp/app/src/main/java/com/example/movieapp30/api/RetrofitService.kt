package com.example.movieapp30.api

import android.util.Log
import com.example.movieapp30.model.*
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit


object RetrofitService{

    var gson = GsonBuilder()
        .setLenient()
        .create()

    const val BASE_URL = "https://api.themoviedb.org/3/"

    fun getPostApi(): PostApi{
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

interface PostApi{
    @GET("authentication/token/new")
    fun createRequestToken(
        @Query("api_key") apiKey: String
    ): Call<GetRequestTokenResponse>

    @POST("authentication/token/validate_with_login")
    fun CreateSessionWithLogin(
        @Query("api_key") apiKey: String?,
        @Body body: JsonObject
    ): Call<GetRequestTokenResponse>

    @POST("authentication/session/new")
    fun CreateSession(
        @Query("api_key") apiKey: String?,
        @Body body: JsonObject
    ): Call<CreateSessionResponse>

    @GET("movie/popular")
    fun getPopularMoviesList(
        @Query("api_key") apiKey: String?,
        @Query("page") page: Int,
        @Query("language") lang: String?
    ): Call<MovieResponse>

    @GET("account")
    @Headers(
        "Content-Type: application/json;charset=utf-8"
    )
    fun getAccountDetails(
        @Query("api_key") apiKey: String?,
        @Query("session_id") session_id: String?
    ): Call<AccountDetailsResponse>

    @POST("account/{account_id}/favorite")
    fun markAsFavourite(
        @Path(value = "account_id", encoded = true) account_id: Int,
        @Query("api_key") apiKey: String?,
        @Query("session_id") session_id: String?,
        @Body body: JsonObject
    ): Call<JsonObject>

    @GET("movie/{movie_id}")
    fun getMovie(
        @Path(value = "movie_id", encoded = true) movie_id: Int,
        @Query("api_key") apiKey: String?,
        @Query("language") lang: String?
    ): Call<Movie>

    @GET("account/{account_id}/favorite/movies")
    fun getFavouritesMoviesList(
        @Path(value = "account_id", encoded = true) account_id: Int,
        @Query("api_key") apiKey: String,
        @Query("session_id") session_id: String,
        @Query("page") page: Int,
        @Query("language") lang: String?
    ): Call<MovieResponse>

    @HTTP(method = "DELETE", path = "authentication/session", hasBody = true)
    fun deleteSession(
        @Query("api_key") apiKey: String,
        @Body body: JsonObject
    ): Call<JsonObject>

}