package com.aiglow.edgeaura.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.aiglow.edgeaura.service.EdgeAuraService
import com.aiglow.edgeaura.utils.PreferencesManager

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            
            val prefs = PreferencesManager(context)
            if (prefs.isEnabled()) {
                EdgeAuraService.start(context)
            }
        }
    }
}
