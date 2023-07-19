package com.tung.coffeeorder

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.LinkedList

class CoffeeAdapter(context: Context, coffeeList: LinkedList<Coffee>) :
    RecyclerView.Adapter<CoffeeAdapter.CoffeeViewHolder>() {

    private var context: Context
    private var coffeeList: LinkedList<Coffee>

    init {
        this.context=context
        this.coffeeList=coffeeList
    }

    //class hiển thị sản phẩm trong cart
    //kế thừa viewHolder của recyclerView
    inner class CoffeeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var coffeeImage: ImageView
        private var coffeeText: TextView
        private var coffePriceText: TextView

        init {
            coffeeImage=view.findViewById(R.id.coffeeImage)
            coffeeText=view.findViewById(R.id.coffeeName)
            coffePriceText=view.findViewById(R.id.coffeePrice)
        }

        fun setText(coffee: Coffee){
            coffeeText.text=coffee.getName()
        }

        fun setImage(coffee: Coffee){
            CoroutineScope(Dispatchers.Main).launch { //để có thể mở trong suspend fun
                try{
                    Glide.with(context)
                        .load(Functions.getDownloadUrl(coffee.getImageFilename()))
                        .into(coffeeImage) //đặt hình ảnh vào imageView
                }
                catch (exception: Exception){
                    print("Lỗi khi tải hình ảnh")
                }
            }
        }

        fun setPrice(coffee: Coffee){
            coffePriceText.text= Functions.reformatNumber(coffee.getPrice())
        }

        fun bind(coffee: Coffee){
            setImage(coffee)
            setText(coffee)
            setPrice(coffee)
        }
    }

    //tạo view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoffeeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.coffee_view, //lấy coffee_view làm view cho adapter
            parent,false
        )
        return CoffeeViewHolder(view) //trả về cart view holder ứng với layout
    }

    override fun onBindViewHolder(holder: CoffeeViewHolder, position: Int) {
        val currentItem = coffeeList[position] //lấy vật ở vị trí thứ position trong list
        holder.bind(currentItem) //gán vào coffeeViewHolder

        //thêm xử lý khi click
        holder.itemView.setOnClickListener(
            View.OnClickListener {
                val intent=Intent(context,CoffeeView::class.java)
                context.startService(intent)
            }
        )
    }

    override fun getItemCount(): Int {
        return coffeeList.size
    }
}