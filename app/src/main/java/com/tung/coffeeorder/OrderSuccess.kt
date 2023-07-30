package com.tung.coffeeorder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.google.android.material.button.MaterialButton

class OrderSuccess : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.order_success)

        val fromRedeem = intent.getBooleanExtra("Redeem",false) //kiểm tra có phải mở từ trang redeem không
        if (fromRedeem){
            findViewById<TextView>(R.id.successText).text="Đã đổi nước thành công"
            findViewById<TextView>(R.id.successText2).text="Bạn đã đổi thưởng thành công! Hãy đến mục Đơn hàng của tôi để theo dõi"
        }

        val trackOrderBtn = findViewById<MaterialButton>(R.id.trackBtn)
        trackOrderBtn.setOnClickListener(
            View.OnClickListener {
                val intent = Intent(this,MainActivity::class.java)
                intent.putExtra(callingActivityExtra,"OrderSuccess") ///thông báo để MainActivity mở đúng Orders fragment
                startActivity(intent)
                finish()
            }
        )

    }
}