package com.example.movieapp30.view_model

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movieapp30.model.*
import com.example.movieapp30.view.RetrofitService
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class AuthorizationViewModel(
    private val context: Context
): ViewModel(), CoroutineScope {

    private val job = Job()
    val liveData = MutableLiveData<State>()
    private var requestToken: String? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    fun initCurrentUser() {
        if(CurrentUser.needToInit) {
            var pref: SharedPreferences = context.getSharedPreferences("MyPref", 0)
            val sessionId = pref.getString("session_id", null)
            val accountId = pref.getInt("account_id", 0)

            CurrentUser.sessionId = sessionId.toString()
            CurrentUser.accountId = accountId

            CurrentUser.needToInit = false
        }
    }

    fun saveCurrentUser() {
        var pref: SharedPreferences = context.getSharedPreferences("MyPref", 0)
        val editor = pref.edit()
        editor.putString("session_id", CurrentUser.sessionId)
        editor.putInt("account_id", CurrentUser.accountId)
        editor.commit()
    }

    fun deleteSession() {
        launch {
            try {
                val body = JsonObject().apply {
                    addProperty("session_id", CurrentUser.sessionId)
                }
                val response: Response<JsonObject> =
                    RetrofitService.getPostApi().deleteSession(CurrentUser.apiKey, body)
                if (response.isSuccessful){
                    CurrentUser.sessionId = ""
                    Toast.makeText(context, "You was successful log out!", Toast.LENGTH_LONG).show()
                    liveData.value = State.SessionWasDeleted
                }
            } catch (e: Exception) {
                Toast.makeText(this@AuthorizationViewModel.context, "We have problems with the internet!", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun getRequestToken() {
        launch {
            try {
                val response: Response<GetRequestTokenResponse> =
                    RetrofitService.getPostApi().createRequestToken(CurrentUser.apiKey)
                if (response.isSuccessful) {
                    requestToken = (response.body()?.requestToken ?: String) as String
                    createSessionWithLogin()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AuthorizationViewModel.context, "Please, check your internet connection and try again!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun createSessionWithLogin() {
        launch {
            try {
                val body = JsonObject().apply {
                    addProperty("username", CurrentUser.username)
                    addProperty("password", CurrentUser.password)
                    addProperty("request_token", requestToken)
                }
                val response: Response<GetRequestTokenResponse> =
                    RetrofitService.getPostApi().createSessionWithLogin(CurrentUser.apiKey, body)
                if (response.isSuccessful) {
                    createSession()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AuthorizationViewModel.context, "Please, check your internet connection and try again!", Toast.LENGTH_LONG).show() }
        }
    }

    private fun createSession() {
        launch {
            try {
                val body = JsonObject().apply {
                    addProperty("request_token", requestToken)
                }
                val response: Response<CreateSessionResponse> =
                    RetrofitService.getPostApi().createSession(CurrentUser.apiKey, body)
                if (response.isSuccessful) {
                    CurrentUser.sessionId = (response.body()?.sessionId ?: String) as String
                    getAccountDetails()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AuthorizationViewModel.context, "Please, check your internet connection and try again!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getAccountDetails() {
        launch {
            try {
                val response: Response<AccountDetailsResponse> =
                    RetrofitService.getPostApi().getAccountDetails(CurrentUser.apiKey, CurrentUser.sessionId)
                if (response.isSuccessful) {
                    CurrentUser.accountId = (response.body()?.id ?: Int) as Int
                    liveData.value = State.UserWasLoggedIn
                }
            } catch (e: Exception) {
                Toast.makeText(this@AuthorizationViewModel.context, "Please, check your internet connection and try again!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun userLoggedIn(): Boolean {
        return !CurrentUser.sessionId.isEmpty()
    }

    sealed class State {
        object SessionWasDeleted: State()
        object UserWasLoggedIn: State()
    }
}