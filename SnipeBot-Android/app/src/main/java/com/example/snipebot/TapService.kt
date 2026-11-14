package com.example.snipebot

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent

class TapService : AccessibilityService() {
    companion object {
        @Volatile var running = false
        @Volatile var instance: TapService? = null
        fun tap(x: Float, y: Float) {
            val s = instance ?: return
            if (!running) return
            val p = Path().apply { moveTo(x, y) }
            val g = GestureDescription.Builder().addStroke(GestureDescription.StrokeDescription(p, 0, 45)).build()
            s.dispatchGesture(g, null, null)
        }
    }
    override fun onServiceConnected() { instance = this }
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() { running = false }
    override fun onDestroy() { running = false; instance = null; super.onDestroy() }
}
