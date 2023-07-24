package com.tung.coffeeorder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.tung.coffeeorder.AppController.Companion.checkInCart
import com.tung.coffeeorder.Functions.Companion.increaseCarts
import com.tung.coffeeorder.Functions.Companion.reformatNumber

class CoffeeView() : AppCompatActivity() {
    lateinit var coffee: Coffee
    lateinit var coffeeInCart: CoffeeInCart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.coffee_view)

        coffee = intent.getSerializableExtra("Coffee") as Coffee
        coffeeInCart=CoffeeInCart(coffee) //tạo object coffeeInCart để chỉnh sửa

        val coffeeText = findViewById<TextView>(R.id.coffee_title)
        coffeeText.text = coffee.getName()

        val priceText = findViewById<TextView>(R.id.coffee_price)
        priceText.text = reformatNumber(coffee.getPrice()) + " VNĐ"

        val imageView = findViewById<ImageView>(R.id.coffee_image)
        imageView.setImageResource(Functions.imageFromCoffee(this, coffee))

        val inflater=LayoutInflater.from(this)

        //ta sẽ thế vào vị trí numberPicker là custom view NumberPicker của chúng ta
        val numberPickerLayout = findViewById<LinearLayout>(R.id.numberPicker)
        numberPickerLayout.removeAllViews() //xoá hết mọi view trong linearLayout này
        val numberPicker = NumberPicker(this, inflater, coffeeInCart, priceText)
        numberPickerLayout.addView(numberPicker)
        //rồi bây giờ ta thêm customView vào vị trí linearLayout

        //tương tự với sizePicker
        val sizePickerLayout = findViewById<LinearLayout>(R.id.sizePicker)
        sizePickerLayout.removeAllViews()
        val sizePicker = SizePicker(this, inflater, coffeeInCart,priceText)
        sizePickerLayout.addView(sizePicker)

        val purchaseBtn=findViewById<MaterialButton>(R.id.purchaseBtn)
        purchaseBtn.setOnClickListener(
            View.OnClickListener {
                val checkInCart = checkInCart(coffeeInCart) //kiểm tra loại cà phê hiện tại đã có trong giỏ hàng hay chưa
                if (checkInCart!=-1){
                    Cart.singleton.getList()[checkInCart].changeQuantity(coffeeInCart.getquantity()) //thay đổi số lượng nếu đã có trong giỏ hàng
                    Cart.singleton.update(this) //update cart trên hệ thống
                }
                else{
                    val temp = CoffeeInCart(coffeeInCart) //tạo copy để đề phòng người dùng muốn thêm ly khác size của cùng một loại cà phê
                    Log.d("Coffee in cart quantity",coffeeInCart.getquantity().toString())
                    if (Cart.singleton.getList().isEmpty()){
                        increaseCarts() //thêm số cart
                    }
                    Cart.singleton.addToCart(this,temp) //thêm ly cà phê hiện tại vào giỏ hàng

                }

                val intent= Intent(this,CartActivity::class.java)
                startActivity(intent) //mở cart lên
            }
        )

        val backBtn = findViewById<ImageButton>(R.id.back_button)
        backBtn.setOnClickListener(
            View.OnClickListener {
                finish() //quay về activity trước
            }
        )

        val cartBtn=findViewById<ImageButton>(R.id.cartButton)
        cartBtn.setOnClickListener(
            View.OnClickListener {
                val intent= Intent(this,CartActivity::class.java)
                startActivity(intent) //mở cart lên

            }
        )
    }
}
