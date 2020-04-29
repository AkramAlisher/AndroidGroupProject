package com.example.movieapp30.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.movieapp30.R
import com.example.movieapp30.api.RetrofitService
import com.example.movieapp30.login.CurrentUser
import com.example.movieapp30.login.LoginActivity
import com.google.gson.JsonObject
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response
import kotlin.coroutines.CoroutineContext


class ProfileFragment : Fragment(), CoroutineScope {

    private lateinit var login_logout_button: Button
    private lateinit var profile_username: TextView
    private lateinit var profile_image: CircleImageView

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        login_logout_button = view.findViewById(R.id.login_logout_button)
        profile_username = view.findViewById(R.id.profile_username)
        profile_image = view.findViewById(R.id.profile_image)

        checkStatus()

        login_logout_button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if(!CurrentUser.userLoggedIn()){
                    val intent = Intent(v?.context, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    deleteSession()
                }
            }
        })
    }

    fun checkStatus() {
        if(CurrentUser.userLoggedIn()) {
            login_logout_button.setText("Log out")
            profile_username.setText(CurrentUser.username)
            profile_image.visibility = CircleImageView.VISIBLE
        } else {
            login_logout_button.setText("Log In")
            profile_username.setText("My Movie App")
            profile_image.visibility = CircleImageView.INVISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        checkStatus()
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
                    checkStatus()
                    Toast.makeText(context, "You was successful log out!", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ProfileFragment.context, "We have problems with the internet!", Toast.LENGTH_LONG).show()
            }
        }
    }
}
