package com.aiglow.edgeaura.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.os.IBinder
import android.view.*
import android.window.WindowManager
import com.aiglow.edgeaura.EdgeAuraApp
import com.aiglow.edgeaura.R
import com.aiglow.edgeaura.ui.MainActivity
import com.aiglow.edgeaura.utils.PreferencesManager

class EdgeAuraService : Service() {
    
    private var windowManager: WindowManager? = null
    private var edgeView: EdgeAuraView? = null
    private lateinit var prefs: PreferencesManager
    
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, EdgeAuraService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
        
        fun stop(context: Context) {
            context.stopService(Intent(context, EdgeAuraService::class.java))
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        prefs = PreferencesManager(this)
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification())
        
        if (prefs.isEnabled()) {
            showEdgeAura()
        } else {
            hideEdgeAura()
        }
        
        return START_STICKY
    }
    
    private fun createNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, EdgeAuraApp.CHANNEL_ID)
            .setContentTitle("Edge Glow Active")
            .setContentText("Your screen edges are glowing")
            .setSmallIcon(R.drawable.ic_glow)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
    
    fun showEdgeAura() {
        if (edgeView != null) return
        
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        
        edgeView = EdgeAuraView(this, prefs)
        windowManager?.addView(edgeView, params)
    }
    
    fun hideEdgeAura() {
        edgeView?.let {
            windowManager?.removeView(it)
            edgeView = null
        }
    }
    
    fun updateSettings() {
        edgeView?.invalidate()
    }
    
    override fun onDestroy() {
        hideEdgeAura()
        super.onDestroy()
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
}

// Custom View for Edge Glow Effect
class EdgeAuraView(context: Context, private val prefs: PreferencesManager) : View(context) {
    
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var animationPhase = 0f
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private val animator = object : Runnable {
        override fun run() {
            animationPhase = (animationPhase + 1) % 360
            invalidate()
            handler.postDelayed(this, 50) // 20fps
        }
    }
    
    init {
        setZOrderOnTop(true)
        handler.post(animator)
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        if (!prefs.isEnabled()) return
        
        val colors = prefs.getColors()
        val width = width.toFloat()
        val height = height.toFloat()
        val edgeWidth = prefs.getEdgeWidth()
        
        // Animate colors if enabled
        val animatedColors = if (prefs.isAnimating()) {
            colors.mapIndexed { index, color ->
                val hsl = FloatArray(3)
                Color.colorToHSL(color, hsl)
                hsl[0] = (hsl[0] + animationPhase) % 360
                Color.HSLToColor(hsl)
            }
        } else colors
        
        // Draw edges
        val rects = listOf(
            // Top edge
            RectF(0f, 0f, width, edgeWidth),
            // Bottom edge  
            RectF(0f, height - edgeWidth, width, height),
            // Left edge
            RectF(0f, 0f, edgeWidth, height),
            // Right edge
            RectF(width - edgeWidth, 0f, width, height)
        )
        
        for ((index, rect) in rects.withIndex()) {
            if (index < animatedColors.size) {
                val gradient = RadialGradient(
                    rect.centerX(), rect.centerY(),
                    edgeWidth * 2,
                    animatedColors[index],
                    Color.TRANSPARENT,
                    Shader.TileMode.CLAMP
                )
                glowPaint.shader = gradient
                glowPaint.alpha = prefs.getIntensity()
                canvas.drawRect(rect, glowPaint)
            }
        }
        
        // Draw corner glows
        drawCornerGlow(canvas, 0f, 0f, animatedColors, edgeWidth) // Top-left
        drawCornerGlow(canvas, width, 0f, animatedColors, edgeWidth) // Top-right
        drawCornerGlow(canvas, 0f, height, animatedColors, edgeWidth) // Bottom-left
        drawCornerGlow(canvas, width, height, animatedColors, edgeWidth) // Bottom-right
    }
    
    private fun drawCornerGlow(canvas: Canvas, x: Float, y: Float, colors: List<Int>, size: Float) {
        if (colors.isEmpty()) return
        val gradient = RadialGradient(
            x, y, size * 3,
            colors.first(),
            Color.TRANSPARENT,
            Shader.TileMode.CLAMP
        )
        glowPaint.shader = gradient
        glowPaint.alpha = prefs.getIntensity()
        canvas.drawCircle(x, y, size * 3, glowPaint)
    }
    
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handler.removeCallbacks(animator)
    }
}
