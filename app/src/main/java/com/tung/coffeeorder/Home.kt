package com.tung.coffeeorder

import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tung.coffeeorder.AppController.Companion.listCoffee
import java.time.LocalDate
import java.time.LocalTime

class Home: Fragment() {
    lateinit var view: View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.home_fragment, container, false)

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

    override fun onResume() {
        super.onResume()
        var timeOfDate = "" //sáng trưa chiều tối string
        val timeOfDateIcon = view.findViewById<ImageView>(R.id.morningOrNight)

        if (LocalTime.now().hour in 5..11){
            timeOfDate="sáng"
           timeOfDateIcon.setImageResource(R.drawable.morning)
        }
        else if (LocalTime.now().hour in 12..14){
            timeOfDate="trưa"
            timeOfDateIcon.setImageResource(R.drawable.morning)
        }
        else if (LocalTime.now().hour in 15..18){
            timeOfDate="chiều"
            timeOfDateIcon.setImageResource(R.drawable.night)
        }
        else{
            timeOfDate="tối"
            timeOfDateIcon.setImageResource(R.drawable.night)
        }

        view.findViewById<TextView>(R.id.hello).text="Chào buổi $timeOfDate, ${User.singleton.getname()}"
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