package com.internshala.foodering.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RelativeLayout
import com.internshala.foodering.R

class OrderPlaced : AppCompatActivity() {

    lateinit var btnOrderPlaced:Button
    lateinit var orderLayout:RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_placed)

        orderLayout=findViewById(R.id.orderPlaced)
        btnOrderPlaced=findViewById(R.id.btnOrderPlaced)
        btnOrderPlaced.setOnClickListener {
            val intent= Intent(this@OrderPlaced,MainActivity2::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }
}