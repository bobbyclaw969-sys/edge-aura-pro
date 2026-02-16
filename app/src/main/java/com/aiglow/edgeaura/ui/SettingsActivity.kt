package com.aiglow.edgeaura.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.aiglow.edgeaura.R
import com.aiglow.edgeaura.utils.PreferencesManager

class SettingsActivity : AppCompatActivity() {
    
    private lateinit var prefs: PreferencesManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        prefs = PreferencesManager(this)
        
        setupUI()
    }
    
    private fun setupUI() {
        // Call notifications
        val switchCall = findViewById<Switch>(R.id.switchCallNotify)
        switchCall.isChecked = prefs.isCallNotifyEnabled()
        switchCall.setOnCheckedChangeListener { _, isChecked ->
            prefs.setCallNotifyEnabled(isChecked)
        }
        
        // Widget toggle
        val switchWidget = findViewById<Switch>(R.id.switchWidget)
        switchWidget.isChecked = prefs.isWidgetEnabled()
        switchWidget.setOnCheckedChangeListener { _, isChecked ->
            prefs.setWidgetEnabled(isChecked)
        }
        
        // Call notification color
        val btnNotifyColor = findViewById<Button>(R.id.btnNotifyColor)
        btnNotifyColor.setBackgroundColor(prefs.getNotifyColor())
        btnNotifyColor.setOnClickListener {
            // Simple color cycle
            val colors = listOf(0xFFFF0000.toInt(), 0xFF00FF00.toInt(), 0xFF0000FF.toInt(), 0xFFFFFF00.toInt())
            val currentIndex = colors.indexOf(prefs.getNotifyColor())
            val nextColor = colors[(currentIndex + 1) % colors.size]
            prefs.setNotifyColor(nextColor)
            btnNotifyColor.setBackgroundColor(nextColor)
        }
        
        // Reset button
        findViewById<Button>(R.id.btnReset).setOnClickListener {
            prefs.setEnabled(false)
            prefs.setColors(listOf(0xFF00FFFF.toInt(), 0xFF00FF00.toInt(), 0xFFFF00FF.toInt(), 0xFF0080FF.toInt()))
            prefs.setEdgeWidth(30f)
            prefs.setIntensity(200)
            prefs.setAnimating(true)
            prefs.setMusicReactive(false)
            prefs.setCallNotifyEnabled(true)
            Toast.makeText(this, "Reset to defaults", Toast.LENGTH_SHORT).show()
            finish()
        }
        
        // About section
        findViewById<TextView>(R.id.tvVersion).text = "Edge Glow Pro v1.0.0 Beta"
    }
}
