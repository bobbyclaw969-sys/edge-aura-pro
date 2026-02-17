package com.aiglow.edgeaura.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.aiglow.edgeaura.R
import com.aiglow.edgeaura.utils.PreferencesManager

class MainActivity : AppCompatActivity() {
    
    private lateinit var prefs: PreferencesManager
    private lateinit var switchMain: Switch
    private lateinit var switchAnimate: Switch
    private lateinit var sliderWidth: SeekBar
    private lateinit var sliderIntensity: SeekBar
    private lateinit var btnColor1: Button
    private lateinit var btnColor2: Button
    private lateinit var btnColor3: Button
    private lateinit var btnColor4: Button
    private lateinit var tvStatus: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        prefs = PreferencesManager(this)
        
        initViews()
        setupUI()
        updateUI()
    }
    
    private fun initViews() {
        switchMain = findViewById(R.id.switchMain)
        switchAnimate = findViewById(R.id.switchAnimate)
        sliderWidth = findViewById(R.id.sliderWidth)
        sliderIntensity = findViewById(R.id.sliderIntensity)
        btnColor1 = findViewById(R.id.btnColor1)
        btnColor2 = findViewById(R.id.btnColor2)
        btnColor3 = findViewById(R.id.btnColor3)
        btnColor4 = findViewById(R.id.btnColor4)
        tvStatus = findViewById(R.id.tvStatus)
    }
    
    private fun setupUI() {
        switchMain.setOnCheckedChangeListener { _, isChecked ->
            prefs.setEnabled(isChecked)
            updateStatus()
        }
        
        switchAnimate.setOnCheckedChangeListener { _, isChecked ->
            prefs.setAnimating(isChecked)
        }
        
        sliderWidth.max = 100
        sliderWidth.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                prefs.setEdgeWidth(progress.toFloat())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        sliderIntensity.max = 255
        sliderIntensity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                prefs.setIntensity(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        // Color buttons - simple preset
        btnColor1.setOnClickListener {
            val colors = listOf(-0x1, 0xFF00FFFF.toInt(), 0xFF00FF00.toInt(), 0xFFFF00FF.toInt())
            val current = prefs.getColors().toMutableList()
            if (current.isNotEmpty()) {
                val idx = colors.indexOf(current[0])
                current[0] = colors[(idx + 1) % colors.size]
                prefs.setColors(current)
                updateColors()
            }
        }
        
        btnColor2.setOnClickListener {
            val colors = listOf(-0x1, 0xFF00FFFF.toInt(), 0xFF00FF00.toInt(), 0xFFFF00FF.toInt())
            val current = prefs.getColors().toMutableList()
            if (current.size > 1) {
                val idx = colors.indexOf(current[1])
                current[1] = colors[(idx + 1) % colors.size]
                prefs.setColors(current)
                updateColors()
            }
        }
        
        btnColor3.setOnClickListener {
            val colors = listOf(-0x1, 0xFF00FFFF.toInt(), 0xFF00FF00.toInt(), 0xFFFF00FF.toInt())
            val current = prefs.getColors().toMutableList()
            if (current.size > 2) {
                val idx = colors.indexOf(current[2])
                current[2] = colors[(idx + 1) % colors.size]
                prefs.setColors(current)
                updateColors()
            }
        }
        
        btnColor4.setOnClickListener {
            val colors = listOf(-0x1, 0xFF00FFFF.toInt(), 0xFF00FF00.toInt(), 0xFFFF00FF.toInt())
            val current = prefs.getColors().toMutableList()
            if (current.size > 3) {
                val idx = colors.indexOf(current[3])
                current[3] = colors[(idx + 1) % colors.size]
                prefs.setColors(current)
                updateColors()
            }
        }
        
        // Presets
        try {
            findViewById<Button>(R.id.chipNeon)?.setOnClickListener {
                prefs.setColors(listOf(0xFF00FFFF.toInt(), 0xFF00FF00.toInt(), 0xFFFF00FF.toInt(), 0xFF0080FF.toInt()))
                updateColors()
            }
            findViewById<Button>(R.id.chipFire)?.setOnClickListener {
                prefs.setColors(listOf(0xFFFF0000.toInt(), 0xFFFF6600.toInt(), 0xFFFFCC00.toInt(), 0xFFFF3300.toInt()))
                updateColors()
            }
        } catch (e: Exception) {
            // Ignore missing buttons
        }
    }
    
    private fun updateColors() {
        try {
            val colors = prefs.getColors()
            btnColor1.setBackgroundColor(colors.getOrElse(0) { 0xFF00FFFF.toInt() })
            btnColor2.setBackgroundColor(colors.getOrElse(1) { 0xFF00FF00.toInt() })
            btnColor3.setBackgroundColor(colors.getOrElse(2) { 0xFFFF00FF.toInt() })
            btnColor4.setBackgroundColor(colors.getOrElse(3) { 0xFF0080FF.toInt() })
        } catch (e: Exception) {
            // Ignore
        }
    }
    
    private fun updateStatus() {
        try {
            tvStatus.text = if (prefs.isEnabled()) "Active" else "Inactive"
            tvStatus.setTextColor(if (prefs.isEnabled()) 0xFF00FF00.toInt() else 0xFF888888.toInt())
        } catch (e: Exception) {
            // Ignore
        }
    }
    
    private fun updateUI() {
        try {
            switchMain.isChecked = prefs.isEnabled()
            switchAnimate.isChecked = prefs.isAnimating()
            sliderWidth.progress = prefs.getEdgeWidth().toInt()
            sliderIntensity.progress = prefs.getIntensity()
            updateColors()
            updateStatus()
        } catch (e: Exception) {
            // Ignore
        }
    }
}
