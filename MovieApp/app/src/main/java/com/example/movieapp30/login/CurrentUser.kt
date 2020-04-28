package com.example.movieapp30.login

class CurrentUser {
    companion object{
        val api_key = "70cc73850767d158d99274cc128b6632"
        val username: String = "AkramAlisher"
        val password: String = "ILoveAndroid"
        var session_id: String = ""
        var account_id: Int = 0

        var needToInit = true

        fun userLoggedIn(): Boolean{
            return !session_id.isEmpty()
        }
    }
}