package com.tung.coffeeorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.fragment.app.Fragment
import com.tung.coffeeorder.Functions.Companion.listCoffee

class Home: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.home_fragment, container, false)
        val grid = view.findViewById<GridLayout>(R.id.grid)

        loadCoffeeToGrid(grid) //thêm các cà phê vào danh sách trong grid

        return view
    }

    fun loadCoffeeToGrid(grid: GridLayout){
        //duyệt từng cà phê
        for (coffee in listCoffee){
            grid.addView(CoffeePreview(requireContext(),coffee)) //thêm vào grid từng cà phê
        }
    }
}