package com.tung.coffeeorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class Orders: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.orders_fragment, container, false)

        val viewPager=view.findViewById<ViewPager2>(R.id.viewPager) //viewPager
        val tabLayout=view.findViewById<TabLayout>(R.id.tabLayout) //tabLayout
        viewPager.adapter=TabPageAdapter(requireActivity()) //adapter tab cho viewPager

        //hiển thị tab
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Đang giao"
                1 -> tab.text = "Lịch sử"
            }
        }.attach()

        return view
    }
}