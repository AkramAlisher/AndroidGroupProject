package com.example.movieapp30.login

import android.os.Bundle
import android.view.View
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

        username = findViewById<EditText>(R.id.login_username)
        password = findViewById<EditText>(R.id.login_password)
        loginButton = findViewById<Button>(R.id.login_button)

        loginButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (!username.text.isEmpty() && !password.text.isEmpty()) {
                    if(username.text.toString().equals(CurrentUser.username) && password.text.toString().equals(CurrentUser.password)) {
                        loginButton.isEnabled = false
                        getRequestToken()
                    }else
                        Toast.makeText(this@LoginActivity, "Username or password are incorrect!", Toast.LENGTH_LONG).show()
                }else
                    Toast.makeText(this@LoginActivity, "Fill all the forms!", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getRequestToken(){
        launch {
            try {
                val response: Response<GetRequestTokenResponse> =
                    RetrofitService.getPostApi().createRequestToken(CurrentUser.api_key)
                if (response.isSuccessful){
                    requestToken = response.body()!!.request_token
                    createSessionWithLogin()
                }
            } catch (e: Exception){
                Toast.makeText( this@LoginActivity, "We have problems with the internet!", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun createSessionWithLogin(){
        launch {
            try {
                val body = JsonObject().apply {
                    addProperty("username", CurrentUser.username)
                    addProperty("password", CurrentUser.password)
                    addProperty("request_token", requestToken)
                }
                val response: Response<GetRequestTokenResponse> =
                    RetrofitService.getPostApi().createSessionWithLogin(CurrentUser.api_key, body)
                if (response.isSuccessful){
                    createSession()
                }
            } catch (e: Exception){
                Toast.makeText( this@LoginActivity, "We have problems with the internet!", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun createSession(){
        launch {
            try {
                val body = JsonObject().apply {
                    addProperty("request_token", requestToken)
                }
                val response: Response<CreateSessionResponse> =
                    RetrofitService.getPostApi().createSession(CurrentUser.api_key, body)
                if (response.isSuccessful){
                    CurrentUser.session_id = response.body()!!.session_id
                    getAccountDetails()
                }
            } catch (e: Exception){
                Toast.makeText( this@LoginActivity, "We have problems with the internet!", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun getAccountDetails(){
        launch {
            try {
                val response: Response<AccountDetailsResponse> =
                    RetrofitService.getPostApi().getAccountDetails(CurrentUser.api_key, CurrentUser.session_id)
                if (response.isSuccessful){
                    CurrentUser.account_id = response.body()!!.id
                    finish()
                }
            } catch (e: Exception){
                Toast.makeText( this@LoginActivity, "We have problems with the internet!", Toast.LENGTH_LONG).show()
            }
        }
    }
}


