package com.example.movieapp30.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.movieapp30.R
import com.example.movieapp30.view_model.AuthorizationViewModel
import com.example.movieapp30.view_model.ViewModelProviderFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var authorizationViewModel: AuthorizationViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModelProviderFactory = ViewModelProviderFactory(this)
        authorizationViewModel = ViewModelProvider(this, viewModelProviderFactory).get(
            AuthorizationViewModel::class.java)

        initFragmentPagerAdapter()
        initBottomNavigation()
    }

    private fun initFragmentPagerAdapter() {
        viewPager = findViewById(R.id.viewPager)
        val pagerAdapter =
            com.example.movieapp30.view.fragments.PagerAdapter(supportFragmentManager)
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
        authorizationViewModel.saveCurrentUser()
    }

    override fun onStart() {
        super.onStart()
        Log.i("MainActivity", "onStart")
        authorizationViewModel.initCurrentUser()
    }
}
