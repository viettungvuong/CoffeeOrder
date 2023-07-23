package com.tung.coffeeorder

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.tung.coffeeorder.AppController.Companion.carts
import com.tung.coffeeorder.Functions.Companion.increaseCart
import com.tung.coffeeorder.Functions.Companion.reformatNumber
import java.time.LocalDateTime
import java.util.*
import kotlin.math.roundToInt

class CartActivity: AppCompatActivity() {
    var totalPrice=0L
    lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.cart_activity)
        val cartRecyclerView = findViewById<RecyclerView>(R.id.cartRecyclerView)
        cartRecyclerView.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false) //đặt recyclerView là chiều ngang
        cartAdapter=CartAdapter(this,Cart.singleton.getList())
        cartRecyclerView.adapter=cartAdapter

        //xử lý vuốt thì sẽ xoá
        val swipeHandlerCallback =
            SwipeRecyclerHandler(cartAdapter, this) //cái này là callback của itemTouchHelper
        val itemTouchHelper = ItemTouchHelper(swipeHandlerCallback)
        itemTouchHelper.attachToRecyclerView((cartRecyclerView))

        val backBtn = findViewById<ImageButton>(R.id.back_button)
        backBtn.setOnClickListener(
            View.OnClickListener {
                finish() //quay về activity trước
            }
        )

        val totalPriceText = findViewById<TextView>(R.id.totalPrice)
        totalPriceText.text=reformatNumber(totalPrice)+" VNĐ"

        val checkoutBtn=findViewById<MaterialButton>(R.id.checkoutBtn)
        checkoutBtn.setOnClickListener(
            View.OnClickListener {
                //thêm vào một order
                //checkout
                checkOut()
                val intent = Intent(this,OrderSuccess::class.java)
                startActivity(intent) //mở order success
            }
        )



    }

    //mỗi lần mở đi mở lại cái activity này thì sẽ cập nhật giá
    override fun onResume() {
        super.onResume()
        updateCartPrice(findViewById(R.id.totalPrice))
    }

    fun updateCartPrice(totalPriceText: TextView){
        for (coffeeInCart in Cart.singleton.getList()){
            totalPrice+=coffeeInCart.calculatePrice()
        }
        totalPriceText.text=reformatNumber(totalPrice)+" VNĐ"
    }

    fun removeFromCartPrice(index: Int, totalPriceText: TextView){
        totalPrice-=Cart.singleton.getList()[index].calculatePrice()
        totalPriceText.text=reformatNumber(totalPrice)+" VNĐ"
    }

    inner class SwipeRecyclerHandler(adapter: CartAdapter, context: Context): ItemTouchHelper.Callback() {
        lateinit var adapter: CartAdapter
        lateinit var context: Context
        init {
            this.adapter=adapter
            this.context=context
        }
        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            return makeMovementFlags(0, swipeFlags)
        }
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false //ta kh kich hoat tinh nang keo tha
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            //vi tri adapter position cua mot item trong recycler view

            //xoá khỏi cart ở vị trí
            removeFromCartPrice(position, findViewById(R.id.totalPrice)) //giảm giá tổng của cart
            Cart.singleton.removeFromCart(position)
            adapter.notifyItemRemoved(position)
            adapter.notifyItemRangeChanged(position,Cart.singleton.getList().size-position)
            viewHolder.itemView.visibility= View.GONE
            Toast.makeText(
                context,
                "Đã xoá khỏi giỏ hàng",
                Toast.LENGTH_SHORT,
            ).show()
            //thông báo với adapter là đã xoá

        }

        //để khi vuốt thì nó sẽ vẽ một phần màu đỏ để bấm xoá
        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            //nếu ta swipe
            if (actionState== ItemTouchHelper.ACTION_STATE_SWIPE){
                val itemView = viewHolder.itemView
                val height = itemView.height
                val width = itemView.width

                val ratio = (dX/3).roundToInt()

                val background= ColorDrawable()
                background.color= Color.RED
                background.setBounds(itemView.right + ratio, itemView.top, itemView.right, itemView.bottom)
                background.draw(c)

                val icon = AppCompatResources.getDrawable(context,R.drawable.remove)

                val intrinsicWidth = icon!!.getIntrinsicWidth();
                val intrinsicHeight = icon!!.getIntrinsicHeight();
                val deleteIconTop: Int = itemView.top + (height - intrinsicHeight) / 2
                val deleteIconMargin: Int = (height - intrinsicHeight) / 2
                val deleteIconLeft: Int = itemView.right - deleteIconMargin - intrinsicWidth
                val deleteIconRight = itemView.right - deleteIconMargin
                val deleteIconBottom: Int = deleteIconTop + intrinsicHeight

                icon.setBounds(deleteIconLeft,deleteIconTop,deleteIconRight,deleteIconBottom)
                icon.setTint(Color.WHITE)
                icon.draw(c)
            }

            //dX/5 nghĩa là ta vuốt 1/5 chiều dài là khoảng nhỏ nhất để nhận sự kiện
            super.onChildDraw(c, recyclerView, viewHolder, dX/5, dY, actionState, isCurrentlyActive)
        }
    }

    //rảnh thì đổi Cart qua Map (key là tên cà phê cùng với size) để tối ưu vụ updateCartPrice

    fun checkOut(){
        val temp= ArrayList(Cart.singleton.getList()) //copy constructor
        val order = Order(temp, LocalDateTime.now(), User.singleton.getaddress())
        addToOngoing(order) //thêm vào orders

        //xoá hết giỏ hàng khi đã checkout
        Cart.singleton.getList().clear()

    }

    fun addToOngoing(order: Order){
        order.update() //lúc này chưa increaseCart
        AppController.ongoingOrders.add(order) //thêm vào orders

        increaseCart() //tăng số cart lên
    }
}

