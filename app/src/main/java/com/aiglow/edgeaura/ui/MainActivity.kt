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
import com.aiglow.edgeaura.databinding.ActivityMainBinding
import com.aiglow.edgeaura.service.EdgeAuraService
import com.aiglow.edgeaura.utils.PreferencesManager

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var prefs: PreferencesManager
    
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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        prefs = PreferencesManager(this)
        
        setupUI()
        checkPermissions()
        updateUI()
    }
    
    override fun onResume() {
        super.onResume()
        updateUI()
    }
    
    private fun setupUI() {
        // Main toggle
        binding.switchMain.setOnCheckedChangeListener { _, isChecked ->
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
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        
        // Color pickers
        setupColorPickers()
        
        // Sliders
        binding.sliderWidth.value = prefs.getEdgeWidth() / 100f
        binding.sliderWidth.addOnChangeListener { _, value, _ ->
            prefs.setEdgeWidth(value * 100f)
            updateService()
        }
        
        binding.sliderIntensity.value = prefs.getIntensity() / 255f
        binding.sliderIntensity.addOnChangeListener { _, value, _ ->
            prefs.setIntensity((value * 255).toInt())
            updateService()
        }
        
        // Animation toggle
        binding.switchAnimate.setOnCheckedChangeListener { _, isChecked ->
            prefs.setAnimating(isChecked)
            updateService()
        }
        
        // Music reactive toggle
        binding.switchMusic.setOnCheckedChangeListener { _, isChecked ->
            prefs.setMusicReactive(isChecked)
        }
        
        // Quick color buttons
        setupQuickColors()
    }
    
    private fun setupColorPickers() {
        val colors = prefs.getColors()
        if (colors.size >= 4) {
            binding.btnColor1.setBackgroundColor(colors[0])
            binding.btnColor2.setBackgroundColor(colors[1])
            binding.btnColor3.setBackgroundColor(colors[2])
            binding.btnColor4.setBackgroundColor(colors[3])
        }
        
        // Click listeners for color picker (simplified - uses preset colors)
        val colorOptions = listOf(
            Color.CYAN, Color.GREEN, Color.MAGENTA, Color.BLUE,
            Color.RED, Color.YELLOW, Color.WHITE, Color.parseColor("#FF00FF"),
            Color.parseColor("#00FFFF"), Color.parseColor("#FF0080"), Color.parseColor("#80FF00"), Color.parseColor("#FF8000")
        )
        
        binding.btnColor1.setOnClickListener { showColorPicker(0, colorOptions) }
        binding.btnColor2.setOnClickListener { showColorPicker(1, colorOptions) }
        binding.btnColor3.setOnClickListener { showColorPicker(2, colorOptions) }
        binding.btnColor4.setOnClickListener { showColorPicker(3, colorOptions) }
    }
    
    private fun showColorPicker(index: Int, colors: List<Int>) {
        // Simplified: just cycle through colors for now
        val currentColors = prefs.getColors().toMutableList()
        if (index < currentColors.size) {
            val currentIndex = colors.indexOf(currentColors[index])
            val nextIndex = (currentIndex + 1) % colors.size
            currentColors[index] = colors[nextIndex]
            prefs.setColors(currentColors)
            
            // Update button
            val buttons = listOf(binding.btnColor1, binding.btnColor2, binding.btnColor3, binding.btnColor4)
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
        
        binding.chipNeon.setOnClickListener { applyPreset(presets["Neon"]!!) }
        binding.chipFire.setOnClickListener { applyPreset(presets["Fire"]!!) }
        binding.chipPurple.setOnClickListener { applyPreset(presets["Purple"]!!) }
        binding.chipRainbow.setOnClickListener { applyPreset(presets["Rainbow"]!!) }
    }
    
    private fun applyPreset(colors: List<Int>) {
        prefs.setColors(colors)
        val buttons = listOf(binding.btnColor1, binding.btnColor2, binding.btnColor3, binding.btnColor4)
        colors.forEachIndexed { index, color ->
            buttons[index].setBackgroundColor(color)
        }
        updateService()
    }
    
    private fun checkPermissions() {
        // Overlay permission
        if (!Settings.canDrawOverlays(this)) {
            binding.layoutPermission.visibility = View.VISIBLE
            binding.btnGrantPermission.setOnClickListener { requestOverlayPermission() }
        } else {
            binding.layoutPermission.visibility = View.GONE
        }
        
        // Notification permission (Android 13+)
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
        // Restart service to apply new settings
        if (prefs.isEnabled() && Settings.canDrawOverlays(this)) {
            stopGlowService()
            startGlowService()
        }
    }
    
    private fun updateUI() {
        binding.switchMain.isChecked = prefs.isEnabled()
        binding.switchAnimate.isChecked = prefs.isAnimating()
        binding.switchMusic.isChecked = prefs.isMusicReactive()
        binding.sliderWidth.value = prefs.getEdgeWidth() / 100f
        binding.sliderIntensity.value = prefs.getIntensity() / 255f
        
        val colors = prefs.getColors()
        val buttons = listOf(binding.btnColor1, binding.btnColor2, binding.btnColor3, binding.btnColor4)
        colors.forEachIndexed { index, color ->
            if (index < buttons.size) {
                buttons[index].setBackgroundColor(color)
            }
        }
        
        // Update status
        binding.tvStatus.text = if (prefs.isEnabled()) "Active" else "Inactive"
    }
}
