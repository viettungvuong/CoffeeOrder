package com.tung.coffeeorder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView



class HistoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.history_fragment, container, false)
        val ordersRecycler = view.findViewById<RecyclerView>(R.id.historyRecycler)
        ordersRecycler.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        AppController.historyAdapter = OrderAdapter(requireActivity(), AppController.ongoingOrders, this)
        ordersRecycler.adapter=AppController.historyAdapter

        return view
    }

}
