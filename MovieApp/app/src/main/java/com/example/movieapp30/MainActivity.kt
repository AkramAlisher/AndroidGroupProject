package com.example.movieapp30

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.movieapp30.login.CurrentUser
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFragmentPagerAdapter()
        initBottomNavigation()
    }

    private fun initCurrentUser() {
        if(CurrentUser.needToInit) {
            Log.d("MainActivity", "initCurrentUser")
            var pref: SharedPreferences = getSharedPreferences("MyPref", 0)
            val sessionId = pref.getString("session_id", null)
            val accountId = pref.getInt("account_id", 0)

            CurrentUser.sessionId = sessionId.toString()
            CurrentUser.accountId = accountId

            CurrentUser.needToInit = false
        }
    }

    private fun saveCurrentUser() {
            var pref: SharedPreferences = getSharedPreferences("MyPref", 0)
            val editor = pref.edit()
            editor.putString("session_id", CurrentUser.sessionId)
        editor.putInt("account_id", CurrentUser.accountId)
        editor.commit()
    }

    private fun initFragmentPagerAdapter() {
        viewPager = findViewById(R.id.viewPager)
        val pagerAdapter =
            com.example.movieapp30.fragments.PagerAdapter(supportFragmentManager)
        viewPager.adapter = pagerAdapter
        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {}

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> bottomNavigationView.menu.findItem(R.id.nav_all).isChecked = true
                    1 -> bottomNavigationView.menu.findItem(R.id.nav_favourite).isChecked = true
                    2 -> bottomNavigationView.menu.findItem(R.id.nav_profile).isChecked = true
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun initBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        viewPager.currentItem = 0

        bottomNavigationView.setOnNavigationItemSelectedListener {item ->
            when (item.itemId) {
                R.id.nav_all -> viewPager.currentItem = 0
                R.id.nav_favourite -> viewPager.currentItem = 1
                R.id.nav_profile -> viewPager.currentItem = 2
            }
            return@setOnNavigationItemSelectedListener true
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "onStop")
        saveCurrentUser()
    }

    override fun onStart() {
        super.onStart()
        Log.i("MainActivity", "onStart")
        initCurrentUser()
    }
}
