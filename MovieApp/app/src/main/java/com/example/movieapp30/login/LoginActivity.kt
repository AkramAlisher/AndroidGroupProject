package com.example.movieapp30.login

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

class LoginActivity : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var requestToken: String

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

    fun getRequestToken(){
        RetrofitService.getPostApi()
            .createRequestToken(CurrentUser.api_key)
            .enqueue(object :
                Callback<GetRequestTokenResponse> {
                override fun onResponse(
                    call: Call<GetRequestTokenResponse>,
                    response: Response<GetRequestTokenResponse>
                ) {

                    Log.d(
                        "My_create_guest_session_response",
                        response.body().toString()
                    )

                    if (response.isSuccessful) {
                        requestToken = response.body()!!.request_token
                        CreateSessionWithLogin()
                    }
                }

                override fun onFailure(call: Call<GetRequestTokenResponse>, t: Throwable) {
                    Log.d(
                        "CreateGuestSession",
                        t.message
                    )
                    Toast.makeText(
                        this@LoginActivity,
                        "Retry once more!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    fun CreateSessionWithLogin(){
        val body = JsonObject().apply {
            addProperty("username", CurrentUser.username)
            addProperty("password", CurrentUser.password)
            addProperty("request_token", requestToken)
        }
        RetrofitService.getPostApi()
            .CreateSessionWithLogin(CurrentUser.api_key, body)
            .enqueue(object :
                Callback<GetRequestTokenResponse> {
                override fun onResponse(
                    call: Call<GetRequestTokenResponse>,
                    response: Response<GetRequestTokenResponse>
                ) {

                    Log.d(
                        "My_create_guest_session_response",
                        response.body().toString()
                    )

                    if (response.isSuccessful) {
                        CreateSession()
                    }
                }

                override fun onFailure(call: Call<GetRequestTokenResponse>, t: Throwable) {
                    Log.d(
                        "CreateGuestSession",
                        t.message
                    )
                    Toast.makeText(
                        this@LoginActivity,
                        "Retry once more!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    fun CreateSession(){
        val body = JsonObject().apply {
            addProperty("request_token", requestToken)
        }
        RetrofitService.getPostApi()
            .CreateSession(CurrentUser.api_key, body)
            .enqueue(object :
                Callback<CreateSessionResponse> {
                override fun onResponse(
                    call: Call<CreateSessionResponse>,
                    response: Response<CreateSessionResponse>
                ) {

                    Log.d(
                        "My_create_guest_session_response",
                        response.body().toString()
                    )

                    if (response.isSuccessful) {
                        CurrentUser.session_id = response.body()!!.session_id
                        GetAccountDetails()
                    }
                }

                override fun onFailure(call: Call<CreateSessionResponse>, t: Throwable) {
                    Log.d(
                        "CreateGuestSession",
                        t.message
                    )
                    Toast.makeText(
                        this@LoginActivity,
                        "Retry once more!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    fun GetAccountDetails(){
        RetrofitService.getPostApi()
            .getAccountDetails(CurrentUser.api_key, CurrentUser.session_id)
            .enqueue(object :
                Callback<AccountDetailsResponse> {
                override fun onResponse(
                    call: Call<AccountDetailsResponse>,
                    response: Response<AccountDetailsResponse>
                ) {

                    Log.d(
                        "My_create_guest_session_response",
                        response.body().toString()
                    )

                    if (response.isSuccessful) {
                        CurrentUser.account_id = response.body()!!.id
                        finish()
                    }
                }

                override fun onFailure(call: Call<AccountDetailsResponse>, t: Throwable) {
                    Log.d(
                        "CreateGuestSession",
                        t.message
                    )
                    Toast.makeText(
                        this@LoginActivity,
                        "Retry once more!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}


