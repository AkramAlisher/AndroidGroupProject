package com.example.movieapp30.view.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

private const val NUM_PAGES = 3

class PagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    override fun getCount(): Int = NUM_PAGES

    override fun getItem(position: Int): Fragment {
            if(position == 0) {
                return AllFilmsFragment()
            }else if(position == 1){
                return FavouriteFragment()
            }else if(position == 2){
                return ProfileFragment()
            }else {
                return ProfileFragment()
            }
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_UNCHANGED;
    }
}