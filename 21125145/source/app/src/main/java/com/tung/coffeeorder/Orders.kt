package com.tung.coffeeorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
        tabLayout.tabMode = TabLayout.MODE_FIXED

        //hiển thị tab
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val customView = layoutInflater.inflate(R.layout.custom_tab, null) //đặt custom layout cho tab item
            customView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val tabTextView = customView.findViewById<TextView>(R.id.tab_text) //khi đặt custom layout thì ta phải set text thủ công như thế này
            val tabImage = customView.findViewById<ImageView>(R.id.tab_icon)
            when (position) {
                0 -> {
                    tabImage.setImageResource(R.drawable.ongoing)
                    tabTextView.text = "Đang giao"
                }
                1 -> {
                    tabImage.setImageResource(R.drawable.history)
                    tabTextView.text = "Lịch sử"
                }
            }
            tab.customView=customView //đặt customView cho tab
        }.attach()

        return view
    }
}