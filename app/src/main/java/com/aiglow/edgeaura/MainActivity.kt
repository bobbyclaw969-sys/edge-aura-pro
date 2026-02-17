package com.aiglow.edgeaura

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val tv = TextView(this)
        tv.text = "Edge Aura Works!"
        tv.textSize = 24f
        tv.setPadding(50, 200, 50, 50)
        
        val btn = Button(this)
        btn.text = "Click Me"
        btn.setOnClickListener {
            tv.text = "Button Clicked!"
        }
        
        val layout = android.widget.LinearLayout(this)
        layout.orientation = android.widget.LinearLayout.VERTICAL
        layout.addView(tv)
        layout.addView(btn)
        
        setContentView(layout)
    }
}
