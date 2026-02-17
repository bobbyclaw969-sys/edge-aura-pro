package com.aiglow.edgeaura

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.Toast

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        
        val title = TextView(this)
        title.text = "✨ Edge Aura"
        title.textSize = 32f
        title.setPadding(50, 100, 50, 50)
        
        val status = TextView(this)
        status.text = "Status: Ready"
        status.textSize = 24f
        status.setPadding(50, 20, 50, 20)
        
        val btnToggle = Button(this)
        btnToggle.text = "Toggle Edge Glow"
        btnToggle.textSize = 20f
        btnToggle.setPadding(50, 40, 50, 40)
        
        val btnColors = Button(this)
        btnColors.text = "Change Colors"
        btnColors.textSize = 20f
        btnColors.setPadding(50, 40, 50, 40)
        
        val btnAnimate = Button(this)
        btnAnimate.text = "Toggle Animation"
        btnAnimate.textSize = 20f
        btnAnimate.setPadding(50, 40, 50, 40)
        
        var glowOn = false
        var colorIndex = 0
        var animating = false
        
        val colorNames = arrayOf("Cyan", "Green", "Magenta", "Blue", "Red", "Yellow")
        
        btnToggle.setOnClickListener {
            glowOn = !glowOn
            val msg = if (glowOn) "✨ Glow is ON!" else "Glow is OFF"
            status.text = msg
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
        
        btnColors.setOnClickListener {
            colorIndex = (colorIndex + 1) % colorNames.size
            val msg = "Color: ${colorNames[colorIndex]}"
            status.text = msg
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
        
        btnAnimate.setOnClickListener {
            animating = !animating
            val msg = if (animating) "Animation ON 🔄" else "Animation OFF"
            status.text = msg
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
        
        layout.addView(title)
        layout.addView(status)
        layout.addView(btnToggle)
        layout.addView(btnColors)
        layout.addView(btnAnimate)
        
        setContentView(layout)
    }
}
