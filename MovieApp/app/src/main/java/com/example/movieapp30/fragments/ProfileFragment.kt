package com.example.movieapp30.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.JsonReader
import android.util.Log
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
import com.google.gson.reflect.TypeToken
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.util.*
import kotlin.coroutines.CoroutineContext


class ProfileFragment : Fragment(), CoroutineScope {

    private lateinit var login_logout_button: Button
    private lateinit var profile_username: TextView
    private lateinit var profile_image: CircleImageView

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    val LOG_TAG = "ProfileFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        login_logout_button = root.findViewById(R.id.login_logout_button)
        profile_username = root.findViewById(R.id.profile_username)
        profile_image = root.findViewById(R.id.profile_image)

        checkStatus()

        login_logout_button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if(!CurrentUser.userLoggedIn()){
                    val intent = Intent(v?.context, LoginActivity::class.java)
                    startActivity(intent)
                }else{
                    deleteSession()
                }
            }
        })

        return root
    }

    fun checkStatus(){
        if(CurrentUser.userLoggedIn()) {
            login_logout_button.setText("Log out")
            profile_username.setText(CurrentUser.username)
            profile_image.visibility = CircleImageView.VISIBLE
        }
        else{
            login_logout_button.setText("Log In")
            profile_username.setText("My Movie App")
            profile_image.visibility = CircleImageView.INVISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        checkStatus()
    }

    fun deleteSession(){
        launch {
            try {
                val body = JsonObject().apply {
                    addProperty("session_id", CurrentUser.session_id)
                }
                val response: Response<JsonObject> =
                    RetrofitService.getPostApi().deleteSession(CurrentUser.api_key, body)
                if (response.isSuccessful){
                    CurrentUser.session_id = ""
                    checkStatus()
                    Toast.makeText(
                        context,
                        "You was successful log out!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception){
                //Toast.makeText( this@ProfileFragment.context, "We have problems with the internet!", Toast.LENGTH_LONG).show()
                Toast.makeText( this@ProfileFragment.context, e.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }
}
