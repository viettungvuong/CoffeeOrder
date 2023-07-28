package com.tung.coffeeorder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.tung.coffeeorder.AppController.Companion.checkInCart
import com.tung.coffeeorder.AppController.Companion.currentCart
import com.tung.coffeeorder.AppController.Companion.increaseCarts
import com.tung.coffeeorder.AppController.Companion.reformatNumber

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
        priceText.text = reformatNumber(coffee.getSinglePrice()) + " VNĐ"

        val imageView = findViewById<ImageView>(R.id.coffee_image)
        imageView.setImageResource(AppController.imageFromCoffee(this, coffee))

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

        val singleDoubleLayout = findViewById<LinearLayout>(R.id.shotPicker)
        singleDoubleLayout.removeAllViews()
        val singleDoublePicker=SingleDoublePicker(this,inflater,coffeeInCart)
        singleDoubleLayout.addView(singleDoublePicker)


        if (coffeeInCart.getName()!="Cold brew"){ //cold brew thì không hiện phần chọn Nóng hay lạnh
            val icePickerLayout = findViewById<LinearLayout>(R.id.icePicker)
            icePickerLayout.removeAllViews()
            val icePicker = IcePicker(this, inflater, coffeeInCart)
            icePickerLayout.addView(icePicker)
        }
        else{
            coffeeInCart.changeHotOrCold(true) //cold brew sẽ luôn luôn là cold
            findViewById<LinearLayout>(R.id.coldHotSection).visibility=View.GONE //mất luôn phần nóng lạnh nếu là cold brew
        }


        val purchaseBtn=findViewById<MaterialButton>(R.id.purchaseBtn)
        purchaseBtn.setOnClickListener(
            View.OnClickListener {
                val checkInCart = checkInCart(coffeeInCart) //kiểm tra loại cà phê hiện tại đã có trong giỏ hàng hay chưa
                if (checkInCart!=-1){ //nếu có trong giỏ hàng
                    currentCart!!.cartList[checkInCart].changeQuantity(coffeeInCart.getquantity()) //thay đổi số lượng nếu đã có trong giỏ hàng
                    updateCart(currentCart!!,this) //update cart trên hệ thống
                }
                else{
                    val temp = CoffeeInCart(coffeeInCart) //tạo copy để đề phòng người dùng muốn thêm ly khác size của cùng một loại cà phê
                    if (currentCart!!.cartList.isEmpty()){
                        increaseCarts() //thêm số cart
                        AppDatabase.getSingleton(this).cartDao().insertCart(currentCart)
                    }
                    addToCart( currentCart!!,this,temp) //thêm ly cà phê hiện tại vào giỏ hàng

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

    interface Picker{
        fun buttonClick(index: Int, view: View) //bấm nút
    }

    inner class SingleDoublePicker(context: Context, inflater: LayoutInflater, private var coffeeInCart: CoffeeInCart): Picker, LinearLayout(context){
        private var buttons = ArrayList<MaterialButton>()
        init{
            inflater.inflate(R.layout.single_double_picker,this,true)
            buttons.add(findViewById(R.id.singleBtn))
            buttons.add(findViewById(R.id.doubleBtn))

            for (i in 0 until buttons.size){
                buttons[i].setOnClickListener {
                        view -> buttonClick(i,view)
                }
            }
        }

        override fun buttonClick(index: Int, view: View) {
            Log.d("clicked",index.toString())
            view.alpha=1f
            for (i in 0 until buttons.size){
                if (i!=index){
                    buttons[i].alpha=0.5f
                }
            }
            when (index){
                0->coffeeInCart.changeShot(Shot.Single)
                1->coffeeInCart.changeShot(Shot.Double)
            }
        }
    }

    inner class SizePicker(context: Context, inflater: LayoutInflater, private var coffeeInCart: CoffeeInCart, private var priceText: TextView): Picker,LinearLayout(context){

        private var buttons= ArrayList<ImageButton>()
        init {
            inflater.inflate(R.layout.pick_size,this,true)

            buttons.add(findViewById(R.id.buttonSmall))
            buttons.add(findViewById(R.id.buttonMedium))
            buttons.add(findViewById(R.id.buttonLarge))


            for (i in 0 until buttons.size){
                buttons[i].setOnClickListener {
                    view -> buttonClick(i,view)
                }
            }
        }

        private fun updatePrice(){
            priceText.text= reformatNumber(coffeeInCart.calculatePrice())+" VNĐ"
        }
        override fun buttonClick(index: Int, view: View) {
            view.alpha=1f
            for (i in 0 until index){
                if (i!=index){
                    buttons[i].alpha=0.5f
                }
            }
            when (index){
                0->coffeeInCart.changeSize(Size.Small)
                1->coffeeInCart.changeSize(Size.Medium)
                2->coffeeInCart.changeSize(Size.Large)
            }
            updatePrice()
        }
    }

    inner class IcePicker(context: Context, inflater: LayoutInflater, private var coffeeInCart: CoffeeInCart): Picker,LinearLayout(context){

        private var buttons= ArrayList<ImageButton>()
        init {
            inflater.inflate(R.layout.pick_ice,this,true)

            buttons.add(findViewById(R.id.hot))
            buttons.add(findViewById(R.id.cold))


            for (i in 0 until buttons.size){
                buttons[i].setOnClickListener {
                        view -> buttonClick(i,view)
                }
            }
        }

        override fun buttonClick(index: Int, view: View) {
            view.alpha=1f
            for (i in 0 until buttons.size){
                if (i!=index){
                    buttons[i].alpha=0.5f
                }
            }
            when (index){
                0->coffeeInCart.changeHotOrCold(HotCold.Hot)
                1->coffeeInCart.changeHotOrCold(HotCold.Cold)
            }
        }
    }

    inner class NumberPicker(context: Context, inflater: LayoutInflater, private var coffeeInCart: CoffeeInCart, private var priceText: TextView): LinearLayout(context){
        var plusButton: ImageButton
        var minusButton: ImageButton
        private var numberEditText: TextInputEditText
        init {
            inflater.inflate(R.layout.number_picker,this,true)

            plusButton=findViewById(R.id.plusButton)
            minusButton=findViewById(R.id.minusButton)


            numberEditText=findViewById(R.id.currentNumber)
            numberEditText.setText("1")
            numberEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    //sau khi chữ trong numberEditText thay đổi
                    //ta đặt lại số lượng cho coffeeInCart
                    coffeeInCart.changeQuantity(Integer.parseInt(s.toString())) //đổi số lượng
                    priceText.text= reformatNumber(coffeeInCart.calculatePrice())+" VNĐ"
                }
            })

            plusButton.setOnClickListener(plusButtonClick())
            minusButton.setOnClickListener(minusButtonClick())
        }

        fun plusButtonClick(): OnClickListener{
            return OnClickListener {
                coffeeInCart.changeQuantity(coffeeInCart.getquantity()+1) //tăng quantity
                numberEditText.setText(coffeeInCart.getquantity().toString())

                priceText.text= reformatNumber(coffeeInCart.calculatePrice())+" VNĐ"
            }
        }

        fun minusButtonClick(): OnClickListener{
            return OnClickListener {
                if (coffeeInCart.getquantity()<=1){
                    Toast.makeText(
                        context,
                        "Không thể giảm số lượng thêm nữa",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                else {
                    coffeeInCart.changeQuantity(coffeeInCart.getquantity()-1) //giảm quantity
                    Log.d("Coffee in cart quantity",coffeeInCart.getquantity().toString())
                    numberEditText.setText(coffeeInCart.getquantity().toString())

                    priceText.text=reformatNumber(coffeeInCart.calculatePrice())+" VNĐ"
                }
            }
        }
    }
}

