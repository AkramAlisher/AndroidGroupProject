package com.example.movieapp30.view

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.movieapp30.R
import com.example.movieapp30.model.CurrentUser
import com.example.movieapp30.view_model.AuthorizationViewModel
import com.example.movieapp30.view_model.ViewModelProviderFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var requestToken: String
    private lateinit var authorizationViewModel: AuthorizationViewModel
    private lateinit var viewModelProviderFactory: ViewModelProviderFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        username = findViewById(R.id.login_username)
        password = findViewById(R.id.login_password)
        loginButton = findViewById(R.id.login_button)
        viewModelProviderFactory = ViewModelProviderFactory(this)
        authorizationViewModel = ViewModelProvider(this, viewModelProviderFactory).get(
            AuthorizationViewModel::class.java)

        loginButton.setOnClickListener {
            if (!username.text.isEmpty() && !password.text.isEmpty()) {
                if(username.text.toString().equals(CurrentUser.username) && password.text.toString().equals(
                        CurrentUser.password)) {
                    loginButton.isEnabled = false
                    authorizationViewModel.getRequestToken()
                    authorizationViewModel.liveData.observe( this, Observer { result ->
                        if(result == AuthorizationViewModel.State.UserWasLoggedIn)
                            finish()
                        loginButton.isEnabled = true
                    })
                }else
                    Toast.makeText(this@LoginActivity, "Username or password are incorrect!", Toast.LENGTH_LONG).show()
            }else
                Toast.makeText(this@LoginActivity, "Fill all the forms!", Toast.LENGTH_LONG).show()
        }
    }
}


