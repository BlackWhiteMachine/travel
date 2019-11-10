package com.example.travel.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.util.*

class ToolWindowFragmentsAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val fragmentsList: MutableList<Fragment>

    init {
        fragmentsList = ArrayList()
    }

    fun addFragment(fragment: Fragment) {
        fragmentsList.add(fragment)
    }

    override fun getItem(position: Int): Fragment {
        return fragmentsList[position]
    }

    override fun getCount(): Int {
        return fragmentsList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return "Title $position"
    }
}
