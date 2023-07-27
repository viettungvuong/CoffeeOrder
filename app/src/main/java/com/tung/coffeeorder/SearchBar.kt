package com.tung.coffeeorder

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.tung.coffeeorder.AppController.Companion.listCoffee
import com.tung.coffeeorder.R.id.suggestions
import java.util.*


class SearchBar(private var coffeeRecyclerView: RecyclerView): Fragment() {

    private lateinit var searchBar: EditText

    private lateinit var searchView: ListView

    private lateinit var autoCompleteSearchBar: AutoCompleteSearchBar

    var currentTextSearchBar=""
    //biến lưu string hiện tại trong searchBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.search_bar, container, false)

        searchBar=view.findViewById(R.id.searchBar)
        searchView = view.findViewById(suggestions)

        autoCompleteSearchBar= AutoCompleteSearchBar(requireContext(), listCoffee)

        searchView.adapter=autoCompleteSearchBar.getSuggestionsAdapter()

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                autoCompleteSearchBar.searchKeyword(s.toString())
                if (s != null) {
                    if (s.isNotEmpty()) {
                        //hiện suggestion
                        searchView.setVisibility(ListView.VISIBLE);
                    } else {
                        //ẩn suggestion nếu không có gõ gì hết
                        coffeeRecyclerView.adapter = CoffeeAdapter(requireActivity(), listCoffee) //hiện lại tất cả cà phê
                        searchView.setVisibility(ListView.GONE);
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        
        searchView.setOnItemClickListener { parent, view, position, id ->
            val suggestion = (view as TextView).text

            val temp = ArrayList(listCoffee.filter { coffee ->
                coffee.getName().contains(suggestion, ignoreCase = true)
            })
            searchBar.text=Editable.Factory.getInstance().newEditable(suggestion)
            coffeeRecyclerView.adapter = CoffeeAdapter(requireActivity(),temp)

            val imm: InputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0) //ẩn bàn phím
        }

        view.findViewById<ImageButton>(R.id.cancel_button).setOnClickListener {
            //xoá hết, hiện danh sách đầy đủ
            searchBar.text=Editable.Factory.getInstance().newEditable("")
            coffeeRecyclerView.adapter = CoffeeAdapter(requireActivity(), listCoffee)

            val imm: InputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0) //ẩn bàn phím
        }

        return view
    }



}

class AutoCompleteSearchBar(private val context: Context, private val listCoffee: ArrayList<Coffee>) {
    //tạo list để nối vào listView
    private var suggestionsAdapter: ArrayAdapter<String>? = null

    init{
        val suggestions=ArrayList<String>()

        for (coffee in listCoffee){
            suggestions.add(coffee.getName()) //lấy danh sách chứa tên của từng coffee lưu vào mảng
        }

        suggestionsAdapter=ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,suggestions)

    }

    fun searchKeyword(keyword: String){
        suggestionsAdapter!!.filter.filter(keyword)
    }

    fun getSuggestionsAdapter(): ArrayAdapter<String>{
        return this.suggestionsAdapter!!
    }
}