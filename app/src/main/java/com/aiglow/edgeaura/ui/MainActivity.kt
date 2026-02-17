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
        
        // Color buttons - simple click cycles through colors
        val colors = listOf(
            -0x1, -0xff0100, -0xffff01, -0xff0001, -0xff00ff01, -0x100, -0xfe0100
        ) // white, red, yellow, green, cyan, blue, magenta
        
        btnColor1.setOnClickListener { cycleColor(0, colors) }
        btnColor2.setOnClickListener { cycleColor(1, colors) }
        btnColor3.setOnClickListener { cycleColor(2, colors) }
        btnColor4.setOnClickListener { cycleColor(3, colors) }
        
        // Presets
        findViewById<Button>(R.id.chipNeon)?.setOnClickListener {
            prefs.setColors(listOf(-0x1, -0xffff01, -0xff00ff, -0xff7f80))
            updateColors()
        }
        findViewById<Button>(R.id.chipFire)?.setOnClickListener {
            prefs.setColors(listOf(-0xff0001, -0xfe0100, -0xfea100, -0xffcc01))
            updateColors()
        }
    }
    
    private fun cycleColor(index: Int, colors: List<Int>) {
        val current = prefs.getColors().toMutableList()
        if (index < current.size) {
            val currentIndex = colors.indexOf(current[index])
            val nextIndex = (currentIndex + 1) % colors.size
            current[index] = colors[nextIndex]
            prefs.setColors(current)
            updateColors()
        }
    }
    
    private fun updateColors() {
        val colors = prefs.getColors()
        btnColor1.setBackgroundColor(colors.getOrElse(0) { -0x1 })
        btnColor2.setBackgroundColor(colors.getOrElse(1) { -0x1 })
        btnColor3.setBackgroundColor(colors.getOrElse(2) { -0x1 })
        btnColor4.setBackgroundColor(colors.getOrElse(3) { -0x1 })
    }
    
    private fun updateStatus() {
        tvStatus.text = if (prefs.isEnabled()) "Active" else "Inactive"
        tvStatus.setTextColor(if (prefs.isEnabled()) 0xFF00FF00.toInt() else 0xFF888888.toInt())
    }
    
    private fun updateUI() {
        switchMain.isChecked = prefs.isEnabled()
        switchAnimate.isChecked = prefs.isAnimating()
        sliderWidth.progress = prefs.getEdgeWidth().toInt()
        sliderIntensity.progress = prefs.getIntensity()
        updateColors()
        updateStatus()
    }
}
