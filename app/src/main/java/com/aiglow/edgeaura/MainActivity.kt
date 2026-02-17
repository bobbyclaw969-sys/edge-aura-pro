package com.aiglow.edgeaura

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.LinearLayout

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
        status.textSize = 18f
        status.setPadding(50, 20, 50, 20)
        
        val btnToggle = Button(this)
        btnToggle.text = "Toggle Edge Glow"
        btnToggle.setPadding(50, 30, 50, 30)
        
        val btnColors = Button(this)
        btnColors.text = "Change Colors"
        btnColors.setPadding(50, 30, 50, 30)
        
        val btnAnimate = Button(this)
        btnAnimate.text = "Toggle Animation"
        btnAnimate.setPadding(50, 30, 50, 30)
        
        var glowOn = false
        var colorIndex = 0
        var animating = false
        
        val colors = arrayOf("Cyan", "Green", "Magenta", "Blue", "Red", "Yellow")
        
        btnToggle.setOnClickListener {
            glowOn = !glowOn
            status.text = if (glowOn) "Status: Glow ON ✨" else "Status: Glow OFF"
        }
        
        btnColors.setOnClickListener {
            colorIndex = (colorIndex + 1) % colors.size
            status.text = "Color: ${colors[colorIndex]}"
        }
        
        btnAnimate.setOnClickListener {
            animating = !animating
            status.text = if (animating) "Animation: ON" else "Animation: OFF"
        }
        
        layout.addView(title)
        layout.addView(status)
        layout.addView(btnToggle)
        layout.addView(btnColors)
        layout.addView(btnAnimate)
        
        setContentView(layout)
    }
}
