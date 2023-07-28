package com.tung.coffeeorder.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tung.coffeeorder.HistoryFragment
import com.tung.coffeeorder.OngoingFragment

//adapter cho tabLayout
class TabPageAdapter(activity: FragmentActivity): FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 2 //có 2 tab
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OngoingFragment()
            1 -> HistoryFragment()
            else -> throw IllegalArgumentException("Vị trí tab không hợp lệ")
        }
    }
}