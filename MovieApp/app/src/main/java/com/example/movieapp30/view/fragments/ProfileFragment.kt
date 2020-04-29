package com.example.movieapp30.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.movieapp30.R
import com.example.movieapp30.model.CurrentUser
import com.example.movieapp30.view.LoginActivity
import com.example.movieapp30.view_model.AuthorizationViewModel
import com.example.movieapp30.view_model.ViewModelProviderFactory
import de.hdodenhof.circleimageview.CircleImageView

class ProfileFragment : Fragment() {

    private lateinit var loginLogoutButton: Button
    private lateinit var profileUsername: TextView
    private lateinit var profileImage: CircleImageView
    private lateinit var authorizationViewModel: AuthorizationViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bindViews(view)
    }

    private fun bindViews(view: View) = with(view) {
        loginLogoutButton = view.findViewById(R.id.login_logout_button)
        profileUsername = view.findViewById(R.id.profile_username)
        profileImage = view.findViewById(R.id.profile_image)
        val viewModelProviderFactory = ViewModelProviderFactory(this@ProfileFragment.context)
        authorizationViewModel = ViewModelProvider(this@ProfileFragment, viewModelProviderFactory).get(
            AuthorizationViewModel::class.java)

        loginLogoutButton.setOnClickListener { v ->
            if(!authorizationViewModel.userLoggedIn()){
                val intent = Intent(v?.context, LoginActivity::class.java)
                startActivity(intent)
            } else {
                authorizationViewModel.deleteSession()
                authorizationViewModel.liveData.observe(viewLifecycleOwner, Observer { result ->
                    if(result == AuthorizationViewModel.State.SessionWasDeleted)
                        checkStatus()
                })
            }
        }
    }

    private fun checkStatus() {
        if(authorizationViewModel.userLoggedIn()) {
            loginLogoutButton.setText("Log out")
            profileUsername.setText(CurrentUser.username)
            profileImage.visibility = CircleImageView.VISIBLE
        } else {
            loginLogoutButton.setText("Log In")
            profileUsername.setText("My Movie App")
            profileImage.visibility = CircleImageView.INVISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        checkStatus()
    }
}
