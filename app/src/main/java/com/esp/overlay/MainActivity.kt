package com.esp.overlay

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.provider.Settings
import android.net.Uri
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(35, 60, 35, 35)
            setBackgroundColor(Color.rgb(18, 18, 18))
        }

        val title = TextView(this).apply {
            text = "ESP OVERLAY"
            textSize = 28f
            setTextColor(Color.WHITE)
        }

        val start = Button(this).apply {
            text = "Разрешить Overlay / Запустить"
            setOnClickListener {
                if (!Settings.canDrawOverlays(this@MainActivity)) {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                    startActivity(intent)
                } else {
                    startService(
                        Intent(this@MainActivity, OverlayService::class.java)
                    )
                }
            }
        }

        val stop = Button(this).apply {
            text = "Остановить Overlay"
            setOnClickListener {
                stopService(
                    Intent(this@MainActivity, OverlayService::class.java)
                )
            }
        }

        layout.addView(title)
        layout.addView(start)
        layout.addView(stop)

        setContentView(layout)
    }
}
