package com.aiglow.edgeaura.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PreferencesManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREFS_NAME = "edge_glow_prefs"
        
        // Keys
        private const val KEY_ENABLED = "enabled"
        private const val KEY_EDGE_WIDTH = "edge_width"
        private const val KEY_INTENSITY = "intensity"
        private const val KEY_COLORS = "colors"
        private const val KEY_ANIMATE = "animate"
        private const val KEY_MUSIC_REACTIVE = "music_reactive"
        private const val KEY_CALL_NOTIFY = "call_notify"
        private const val KEY_NOTIFY_COLOR = "notify_color"
        private const val KEY_WIDGET_ENABLED = "widget_enabled"
        
        // Defaults
        private val DEFAULT_COLORS = listOf(
            0xFF00FFFF.toInt(), // Cyan
            0xFF00FF00.toInt(), // Green
            0xFFFF00FF.toInt(), // Magenta
            0xFF0080FF.toInt()  // Blue
        )
    }
    
    fun isEnabled(): Boolean = prefs.getBoolean(KEY_ENABLED, false)
    fun setEnabled(enabled: Boolean) = prefs.edit { putBoolean(KEY_ENABLED, enabled) }
    
    fun getEdgeWidth(): Float = prefs.getFloat(KEY_EDGE_WIDTH, 30f)
    fun setEdgeWidth(width: Float) = prefs.edit { putFloat(KEY_EDGE_WIDTH, width) }
    
    fun getIntensity(): Int = prefs.getInt(KEY_INTENSITY, 200)
    fun setIntensity(intensity: Int) = prefs.edit { putInt(KEY_INTENSITY, intensity.coerceIn(50, 255)) }
    
    fun getColors(): List<Int> {
        val colorsString = prefs.getString(KEY_COLORS, null)
        return if (colorsString != null) {
            colorsString.split(",").map { it.toInt() }
        } else DEFAULT_COLORS
    }
    fun setColors(colors: List<Int>) = prefs.edit { putString(KEY_COLORS, colors.joinToString(",")) }
    
    fun isAnimating(): Boolean = prefs.getBoolean(KEY_ANIMATE, true)
    fun setAnimating(animate: Boolean) = prefs.edit { putBoolean(KEY_ANIMATE, animate) }
    
    fun isMusicReactive(): Boolean = prefs.getBoolean(KEY_MUSIC_REACTIVE, false)
    fun setMusicReactive(reactive: Boolean) = prefs.edit { putBoolean(KEY_MUSIC_REACTIVE, reactive) }
    
    fun isCallNotifyEnabled(): Boolean = prefs.getBoolean(KEY_CALL_NOTIFY, true)
    fun setCallNotifyEnabled(enabled: Boolean) = prefs.edit { putBoolean(KEY_CALL_NOTIFY, enabled) }
    
    fun getNotifyColor(): Int = prefs.getInt(KEY_NOTIFY_COLOR, 0xFFFF0000.toInt())
    fun setNotifyColor(color: Int) = prefs.edit { putInt(KEY_NOTIFY_COLOR, color) }
    
    fun isWidgetEnabled(): Boolean = prefs.getBoolean(KEY_WIDGET_ENABLED, true)
    fun setWidgetEnabled(enabled: Boolean) = prefs.edit { putBoolean(KEY_WIDGET_ENABLED, enabled) }
    
    // Quick toggle without edit transaction
    fun toggleEnabled(): Boolean {
        val newValue = !isEnabled()
        prefs.edit { putBoolean(KEY_ENABLED, newValue) }
        return newValue
    }
}
