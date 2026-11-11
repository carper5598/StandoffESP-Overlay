package com.esp.overlay

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView

class OverlayService : Service() {

    private lateinit var wm: WindowManager
    private var floatingButton: View? = null
    private var menu: View? = null

    private lateinit var buttonParams: WindowManager.LayoutParams

    override fun onCreate() {
        super.onCreate()

        wm = getSystemService(WINDOW_SERVICE) as WindowManager

        createFloatingButton()
    }

    private fun overlayType(): Int {
        return if (android.os.Build.VERSION.SDK_INT >= 26) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }
    }

    private fun createFloatingButton() {

        val button = TextView(this).apply {
            text = "ESP"
            textSize = 17f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(190, 20, 35))
            setPadding(25, 15, 25, 15)
        }

        buttonParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            overlayType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 30
            y = 250
        }

        var startX = 0
        var startY = 0
        var touchX = 0f
        var touchY = 0f
        var moved = false

        button.setOnTouchListener { _, event ->

            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    startX = buttonParams.x
                    startY = buttonParams.y
                    touchX = event.rawX
                    touchY = event.rawY
                    moved = false
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - touchX).toInt()
                    val dy = (event.rawY - touchY).toInt()

                    if (kotlin.math.abs(dx) > 5 ||
                        kotlin.math.abs(dy) > 5) {
                        moved = true
                    }

                    buttonParams.x = startX + dx
                    buttonParams.y = startY + dy

                    wm.updateViewLayout(button, buttonParams)
                    true
                }

                MotionEvent.ACTION_UP -> {
                    if (!moved) {
                        toggleMenu()
                    }
                    true
                }

                else -> true
            }
        }

        floatingButton = button
        wm.addView(button, buttonParams)
    }

    private fun toggleMenu() {

        if (menu != null) {
            wm.removeView(menu)
            menu = null
            return
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(25, 20, 25, 20)
            setBackgroundColor(Color.rgb(25, 25, 25))
        }

        val title = TextView(this).apply {
            text = "ESP CONTROL PANEL"
            textSize = 20f
            setTextColor(Color.RED)
        }

        layout.addView(title)

        val options = listOf(
            "Box ESP",
            "Name ESP",
            "Health ESP",
            "Distance ESP"
        )

        options.forEach { option ->

            val toggle = Switch(this).apply {
                text = option
                textSize = 15f
                setTextColor(Color.WHITE)
                isChecked = false
            }

            layout.addView(toggle)
        }

        val demo = TextView(this).apply {
            text = "DEMO MODE: активен"
            setTextColor(Color.GREEN)
            textSize = 13f
        }

        layout.addView(demo)

        val close = Button(this).apply {
            text = "CLOSE"
            setOnClickListener {
                toggleMenu()
            }
        }

        layout.addView(close)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            overlayType(),
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }

        menu = layout
        wm.addView(layout, params)
    }

    override fun onDestroy() {
        floatingButton?.let {
            wm.removeView(it)
        }

        menu?.let {
            wm.removeView(it)
        }

        floatingButton = null
        menu = null

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
