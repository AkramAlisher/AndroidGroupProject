package com.example.movieapp30.login

class CurrentUser {
    companion object{
        val apiKey = "70cc73850767d158d99274cc128b6632"
        val username: String = "AkramAlisher"
        val password: String = "ILoveAndroid"
        var sessionId: String = ""
        var accountId: Int = 0

        var needToInit = true

        fun userLoggedIn(): Boolean{
            return !sessionId.isEmpty()
        }
    }
}