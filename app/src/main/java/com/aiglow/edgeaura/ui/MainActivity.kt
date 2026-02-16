package com.aiglow.edgeaura.ui

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.aiglow.edgeaura.R
import com.aiglow.edgeaura.service.EdgeAuraService
import com.aiglow.edgeaura.utils.PreferencesManager

class MainActivity : AppCompatActivity() {
    
    private lateinit var prefs: PreferencesManager
    
    private lateinit var switchMain: Switch
    private lateinit var switchAnimate: Switch
    private lateinit var switchMusic: Switch
    private lateinit var sliderWidth: SeekBar
    private lateinit var sliderIntensity: SeekBar
    private lateinit var btnColor1: Button
    private lateinit var btnColor2: Button
    private lateinit var btnColor3: Button
    private lateinit var btnColor4: Button
    private lateinit var tvStatus: TextView
    private lateinit var layoutPermission: LinearLayout
    private lateinit var btnGrantPermission: Button
    
    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (Settings.canDrawOverlays(this)) {
            startGlowService()
        }
    }
    
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* Handle result */ }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        prefs = PreferencesManager(this)
        
        initViews()
        setupUI()
        checkPermissions()
        updateUI()
    }
    
    private fun initViews() {
        switchMain = findViewById(R.id.switchMain)
        switchAnimate = findViewById(R.id.switchAnimate)
        switchMusic = findViewById(R.id.switchMusic)
        sliderWidth = findViewById(R.id.sliderWidth)
        sliderIntensity = findViewById(R.id.sliderIntensity)
        btnColor1 = findViewById(R.id.btnColor1)
        btnColor2 = findViewById(R.id.btnColor2)
        btnColor3 = findViewById(R.id.btnColor3)
        btnColor4 = findViewById(R.id.btnColor4)
        tvStatus = findViewById(R.id.tvStatus)
        layoutPermission = findViewById(R.id.layoutPermission)
        btnGrantPermission = findViewById(R.id.btnGrantPermission)
    }
    
    override fun onResume() {
        super.onResume()
        updateUI()
    }
    
    private fun setupUI() {
        // Main toggle
        switchMain.setOnCheckedChangeListener { _, isChecked ->
            prefs.setEnabled(isChecked)
            if (isChecked) {
                if (Settings.canDrawOverlays(this)) {
                    startGlowService()
                } else {
                    requestOverlayPermission()
                }
            } else {
                stopGlowService()
            }
        }
        
        // Settings button
        findViewById<Button>(R.id.btnSettings)?.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        
        // Sliders
        sliderWidth.max = 100
        sliderWidth.progress = prefs.getEdgeWidth().toInt()
        sliderWidth.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                prefs.setEdgeWidth(progress.toFloat())
                updateService()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        sliderIntensity.max = 255
        sliderIntensity.progress = prefs.getIntensity()
        sliderIntensity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                prefs.setIntensity(progress)
                updateService()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        // Animation toggle
        switchAnimate.setOnCheckedChangeListener { _, isChecked ->
            prefs.setAnimating(isChecked)
            updateService()
        }
        
        // Music reactive toggle
        switchMusic.setOnCheckedChangeListener { _, isChecked ->
            prefs.setMusicReactive(isChecked)
        }
        
        // Color buttons
        setupColorPickers()
        
        // Preset chips
        setupQuickColors()
    }
    
    private fun setupColorPickers() {
        val colors = prefs.getColors()
        if (colors.size >= 4) {
            btnColor1.setBackgroundColor(colors[0])
            btnColor2.setBackgroundColor(colors[1])
            btnColor3.setBackgroundColor(colors[2])
            btnColor4.setBackgroundColor(colors[3])
        }
        
        val colorOptions = listOf(
            Color.CYAN, Color.GREEN, Color.MAGENTA, Color.BLUE,
            Color.RED, Color.YELLOW, Color.WHITE, Color.parseColor("#FF00FF"),
            Color.parseColor("#00FFFF"), Color.parseColor("#FF0080"), Color.parseColor("#80FF00"), Color.parseColor("#FF8000")
        )
        
        btnColor1.setOnClickListener { showColorPicker(0, colorOptions) }
        btnColor2.setOnClickListener { showColorPicker(1, colorOptions) }
        btnColor3.setOnClickListener { showColorPicker(2, colorOptions) }
        btnColor4.setOnClickListener { showColorPicker(3, colorOptions) }
    }
    
    private fun showColorPicker(index: Int, colors: List<Int>) {
        val currentColors = prefs.getColors().toMutableList()
        if (index < currentColors.size) {
            val currentIndex = colors.indexOf(currentColors[index])
            val nextIndex = (currentIndex + 1) % colors.size
            currentColors[index] = colors[nextIndex]
            prefs.setColors(currentColors)
            
            val buttons = listOf(btnColor1, btnColor2, btnColor3, btnColor4)
            buttons[index].setBackgroundColor(colors[nextIndex])
            updateService()
        }
    }
    
    private fun setupQuickColors() {
        val presets = mapOf(
            "Neon" to listOf(0xFF00FFFF.toInt(), 0xFF00FF00.toInt(), 0xFFFF00FF.toInt(), 0xFF0080FF.toInt()),
            "Fire" to listOf(0xFFFF0000.toInt(), 0xFFFF6600.toInt(), 0xFFFFCC00.toInt(), 0xFFFF3300.toInt()),
            "Purple" to listOf(0xFF9900FF.toInt(), 0xFFCC00FF.toInt(), 0xFF6600FF.toInt(), 0xFF3300FF.toInt()),
            "Rainbow" to listOf(0xFFFF0000.toInt(), 0xFFFF7F00.toInt(), 0xFFFFFF00.toInt(), 0xFF00FF00.toInt())
        )
        
        findViewById<Button>(R.id.chipNeon)?.setOnClickListener { applyPreset(presets["Neon"]!!) }
        findViewById<Button>(R.id.chipFire)?.setOnClickListener { applyPreset(presets["Fire"]!!) }
        findViewById<Button>(R.id.chipPurple)?.setOnClickListener { applyPreset(presets["Purple"]!!) }
        findViewById<Button>(R.id.chipRainbow)?.setOnClickListener { applyPreset(presets["Rainbow"]!!) }
    }
    
    private fun applyPreset(colors: List<Int>) {
        prefs.setColors(colors)
        val buttons = listOf(btnColor1, btnColor2, btnColor3, btnColor4)
        colors.forEachIndexed { index, color ->
            if (index < buttons.size) {
                buttons[index].setBackgroundColor(color)
            }
        }
        updateService()
    }
    
    private fun checkPermissions() {
        if (!Settings.canDrawOverlays(this)) {
            layoutPermission.visibility = View.VISIBLE
            btnGrantPermission.setOnClickListener { requestOverlayPermission() }
        } else {
            layoutPermission.visibility = View.GONE
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (!notificationManager.areNotificationsEnabled()) {
                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    
    private fun requestOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        overlayPermissionLauncher.launch(intent)
    }
    
    private fun startGlowService() {
        if (Settings.canDrawOverlays(this)) {
            EdgeAuraService.start(this)
        }
    }
    
    private fun stopGlowService() {
        EdgeAuraService.stop(this)
    }
    
    private fun updateService() {
        if (prefs.isEnabled() && Settings.canDrawOverlays(this)) {
            stopGlowService()
            startGlowService()
        }
    }
    fun updateUI() {
        switchMain.isChecked = prefs.isEnabled()
        switchAnimate.isChecked = prefs.isAnimating()
        switchMusic.isChecked = prefs.isMusicReactive()
        sliderWidth.progress = prefs.getEdgeWidth().toInt()
        sliderIntensity.progress = prefs.getIntensity()
        
        val colors = prefs.getColors()
        val buttons = listOf(btnColor1, btnColor2, btnColor3, btnColor4)
        colors.forEachIndexed { index, color ->
            if (index < buttons.size) {
                buttons[index].setBackgroundColor(color)
            }
        }
        
        tvStatus.text = if (prefs.isEnabled()) "Active" else "Inactive"
    }
}
