package com.aiglow.edgeaura.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.aiglow.edgeaura.R
import com.aiglow.edgeaura.service.EdgeAuraService
import com.aiglow.edgeaura.ui.MainActivity
import com.aiglow.edgeaura.utils.PreferencesManager

class EdgeAuraWidget : AppWidgetProvider() {
    
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
    
    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val prefs = PreferencesManager(context)
            
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val toggleIntent = Intent(context, WidgetToggleReceiver::class.java)
            val togglePendingIntent = PendingIntent.getBroadcast(
                context, 0, toggleIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val views = RemoteViews(context.packageName, R.layout.widget_edge_glow)
            views.setOnClickPendingIntent(R.id.widgetContainer, pendingIntent)
            views.setOnClickPendingIntent(R.id.btnToggle, togglePendingIntent)
            
            val statusText = if (prefs.isEnabled()) "ON" else "OFF"
            views.setTextViewText(R.id.tvStatus, statusText)
            
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
    
    override fun onEnabled(context: Context) {}
    override fun onDisabled(context: Context) {}
}

class WidgetToggleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val prefs = PreferencesManager(context)
        val newState = prefs.toggleEnabled()
        
        if (newState) {
            EdgeAuraService.start(context)
        } else {
            EdgeAuraService.stop(context)
        }
        
        // Update widget
        val appWidgetManager = AppWidgetManager.getInstance(context)
        EdgeAuraWidget.updateAppWidget(context, appWidgetManager, 0)
    }
}
