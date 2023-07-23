package com.tung.coffeeorder

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tung.coffeeorder.AppController.Companion.listCoffee

class Home: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.home_fragment, container, false)

        requireActivity().supportFragmentManager.beginTransaction().replace(R.id.rewards_section,RewardSection()).commit()

        val coffeeRecyclerView: RecyclerView = view.findViewById(R.id.coffeeRecyclerView)
        val spanCount = 2
        val spacing = 30
        val layoutManager = GridLayoutManager(requireContext(), spanCount)
        coffeeRecyclerView.layoutManager = layoutManager

        //đặt margin cho các item trong recycler view
        coffeeRecyclerView.addItemDecoration(GridSpacingItemDecoration(spanCount, spacing))

        coffeeRecyclerView.adapter = CoffeeAdapter(requireActivity(), listCoffee)

        val cartBtn = view.findViewById<ImageButton>(R.id.cartBtn)
        cartBtn.setOnClickListener(
            View.OnClickListener {
                val intent= Intent(requireActivity(),CartActivity::class.java)
                startActivity(intent) //mở cart lên
            }
        )

        val userBtn = view.findViewById<ImageButton>(R.id.userBtn)
        userBtn.setOnClickListener(
            View.OnClickListener {
                val intent= Intent(requireActivity(),UserEdit::class.java)
                startActivity(intent) //mở cart lên
            }
        )

        return view
    }


    //decorate grid cho recyccler view
    inner class GridSpacingItemDecoration(private val spanCount: Int, private val spacing: Int) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = parent.getChildAdapterPosition(view)
            val column = position % spanCount

            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount

            if (position >= spanCount) {
                outRect.top = spacing
            }
        }
    }
}