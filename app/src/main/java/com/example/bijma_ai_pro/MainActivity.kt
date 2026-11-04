
package com.example.bijma_ai_pro

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private val backgroundColor = Color.rgb(7, 9, 24)
    private val purpleColor = Color.rgb(111, 38, 255)
    private val cardColor = Color.rgb(27, 31, 56)

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = backgroundColor
        window.navigationBarColor = backgroundColor

        val scroll = ScrollView(this)
        scroll.setBackgroundColor(backgroundColor)

        val main = LinearLayout(this)
        main.orientation = LinearLayout.VERTICAL
        main.setPadding(dp(18), dp(22), dp(18), dp(24))

        scroll.addView(main)
        setContentView(scroll)

        val title = TextView(this)
        title.text = "✦ BIJMA AI  👑 PRO"
        title.textSize = 30f
        title.setTextColor(Color.rgb(75, 177, 255))
        title.gravity = Gravity.CENTER
        title.setPadding(0, dp(15), 0, dp(12))
        main.addView(title)

        val subtitle = TextView(this)
        subtitle.text = "आपका अपना स्मार्ट मनोरंजन ब्राउज़र"
        subtitle.textSize = 18f
        subtitle.setTextColor(Color.WHITE)
        subtitle.gravity = Gravity.CENTER
        subtitle.setPadding(0, dp(8), 0, dp(25))
        main.addView(subtitle)

        val greeting = TextView(this)
        greeting.text = "👩🏻‍💻\\nHello Mahendra ji!\\nमैं आपकी क्या मदद कर सकती हूँ?"
        greeting.textSize = 22f
        greeting.gravity = Gravity.CENTER
        greeting.setTextColor(Color.WHITE)
        greeting.setBackgroundColor(Color.rgb(53, 25, 91))
        greeting.setPadding(dp(15), dp(25), dp(15), dp(25))
        main.addView(greeting)

        addFeature(main, "🎙️ मुझसे बात करें")
        addFeature(main, "🔎 Google Search")
        
        addSection(main, "✦ मुख्य सुविधाएँ")

        addFeature(main, "▶ YouTube")
        addFeature(main, "📺 Live TV")
        addFeature(main, "▶ IPTV / M3U")
        addFeature(main, "🌐 Browser")
        addFeature(main, "🛡️ VPN")
        addFeature(main, "🔍 Video Zoom")

        addSection(main, "🎬 मनोरंजन")

        addFeature(main, "🎞️ Movies")
        addFeature(main, "📺 Web Series")
        addFeature(main, "📡 TV Shows")
        addFeature(main, "♫ Music")
        addFeature(main, "⭐ Favorites")
        addFeature(main, "⬇ Downloads")

        val footer = TextView(this)
        footer.text = "BIJMA AI • आपका अपना स्मार्ट ब्राउज़र"
        footer.textSize = 14f
        footer.gravity = Gravity.CENTER
        footer.setTextColor(Color.LTGRAY)
        footer.setPadding(0, dp(30), 0, dp(15))
        main.addView(footer)
    }

    private fun addSection(
        parent: LinearLayout,
        text: String
    ) {
        val heading = TextView(this)
        heading.text = text
        heading.textSize = 23f
        heading.setTextColor(Color.WHITE)
        heading.setPadding(0, dp(25), 0, dp(12))
        parent.addView(heading)
    }

    private fun addFeature(
        parent: LinearLayout,
        name: String
    ) {
        val button = Button(this)
        button.text = name
        button.textSize = 18f
        button.isAllCaps = false
        button.setTextColor(Color.WHITE)
        button.setBackgroundColor(
            if (name.contains("Search") ||
                name.contains("बात करें")) {
                purpleColor
            } else {
                cardColor
            }
        )

        button.setOnClickListener {
            Toast.makeText(
                this,
                "$name की सुविधा तैयार की जाएगी",
                Toast.LENGTH_SHORT
            ).show()
        }

        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(62)
        )
        params.setMargins(0, dp(6), 0, dp(6))

        parent.addView(button, params)
    }
}
