package com.example.snipebot

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var status: TextView
    private val captureRequest = 701

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 32, 28, 32)
        }
        val title = TextView(this).apply { text = "SnipeBot"; textSize = 30f; gravity = Gravity.CENTER }
        status = TextView(this).apply { text = "Prototype ready. Enable Accessibility, then grant screen capture."; textSize = 16f; gravity = Gravity.CENTER; setPadding(0, 24, 0, 24) }
        val access = Button(this).apply { text = "1. Enable Accessibility"; setOnClickListener { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) } }
        val permission = Button(this).apply { text = "2. Grant screen capture"; setOnClickListener {
            val mgr = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            startActivityForResult(mgr.createScreenCaptureIntent(), captureRequest)
        } }
        val start = Button(this).apply { text = "START"; setOnClickListener {
            if (TapService.instance == null) { status.text = "Enable SnipeBot in Accessibility settings first."; return@setOnClickListener }
            TapService.running = true
            status.text = "RUNNING — experimental visual detector; no throws until calibration is validated."
        } }
        val stop = Button(this).apply { text = "STOP"; setOnClickListener { TapService.running = false; stopService(Intent(this@MainActivity, CaptureService::class.java)); status.text = "Stopped. You have control." } }
        layout.addView(title); layout.addView(status); layout.addView(access); layout.addView(permission); layout.addView(start); layout.addView(stop)
        setContentView(layout)
    }

    @Deprecated("Deprecated by Android, retained for broad device compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == captureRequest && resultCode == RESULT_OK && data != null) {
            val i = Intent(this, CaptureService::class.java).putExtra("resultCode", resultCode).putExtra("data", data)
            startForegroundService(i)
            status.text = "Screen capture permission granted. Detector calibration is still required."
        }
    }
}
