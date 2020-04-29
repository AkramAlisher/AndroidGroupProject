package com.example.movieapp30.login

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.movieapp30.R
import com.example.movieapp30.api.RetrofitService
import com.example.movieapp30.model.AccountDetailsResponse
import com.example.movieapp30.model.CreateSessionResponse
import com.example.movieapp30.model.GetRequestTokenResponse
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class LoginActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var requestToken: String

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        username = findViewById(R.id.login_username)
        password = findViewById(R.id.login_password)
        loginButton = findViewById(R.id.login_button)

        loginButton.setOnClickListener {
            if (!username.text.isEmpty() && !password.text.isEmpty()) {
                if(username.text.toString().equals(CurrentUser.username) && password.text.toString().equals(CurrentUser.password)) {
                    loginButton.isEnabled = false
                    getRequestToken()
                }else
                    Toast.makeText(this@LoginActivity, "Username or password are incorrect!", Toast.LENGTH_LONG).show()
            }else
                Toast.makeText(this@LoginActivity, "Fill all the forms!", Toast.LENGTH_LONG).show()
        }
    }

    private fun getRequestToken() {
        launch {
            try {
                val response: Response<GetRequestTokenResponse> =
                    RetrofitService.getPostApi().createRequestToken(CurrentUser.apiKey)
                if (response.isSuccessful) {
                    requestToken = (response.body()?.requestToken ?: String) as String
                    createSessionWithLogin()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Please, check your internet connection and try again!", Toast.LENGTH_LONG).show()
                loginButton.isEnabled = true
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
                Toast.makeText(this@LoginActivity, "Please, check your internet connection and try again!", Toast.LENGTH_LONG).show()
                loginButton.isEnabled = true
            }
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
                Toast.makeText(this@LoginActivity, "Please, check your internet connection and try again!", Toast.LENGTH_LONG).show()
                loginButton.isEnabled = true
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
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Please, check your internet connection and try again!", Toast.LENGTH_LONG).show()
                loginButton.isEnabled = true
            }
        }
    }
}


