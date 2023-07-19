package com.tung.coffeeorder

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tung.coffeeorder.Functions.Companion.db
import com.tung.coffeeorder.Functions.Companion.initCoffeeList
import com.tung.coffeeorder.Functions.Companion.listCoffee
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Home: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.home_fragment, container, false)


        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
        val recyclerView = view.findViewById<RecyclerView>(R.id.coffeeRecyclerView)
        recyclerView.layoutManager = layoutManager

        initCoffeeList(listCoffee,db) {
            recyclerView.adapter = CoffeeAdapter(requireContext(), listCoffee)
        }

        return view
    }
}