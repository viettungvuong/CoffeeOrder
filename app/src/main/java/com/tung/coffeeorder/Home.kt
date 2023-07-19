package com.tung.coffeeorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.tung.coffeeorder.Functions.Companion.listCoffee

class Home: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.home_fragment, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.coffeeRecyclerView)

        recyclerView.adapter=CoffeeAdapter(requireContext(), listCoffee)

        return view
    }
}