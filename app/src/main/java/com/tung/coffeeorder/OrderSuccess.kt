package com.tung.coffeeorder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.button.MaterialButton

class OrderSuccess : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.order_success)

        val trackOrderBtn = findViewById<MaterialButton>(R.id.trackBtn)
        trackOrderBtn.setOnClickListener(
            View.OnClickListener {
                val intent = Intent(this,MainActivity::class.java)
                intent.putExtra("CallingActivity","OrderSuccess") ///thông báo để MainActivity mở đúng Orders fragment
                startActivity(intent)
                finish()
            }
        )

    }
}