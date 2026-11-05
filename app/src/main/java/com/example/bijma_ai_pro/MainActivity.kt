
package com.example.bijma_ai_pro

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

class MainActivity : Activity() {

    private val bgColor = Color.rgb(10, 13, 24)
    private val panelColor = Color.rgb(25, 30, 48)
    private val accentColor = Color.rgb(125, 85, 255)
    private val blueColor = Color.rgb(30, 145, 255)
    private val whiteColor = Color.WHITE
    private val mutedColor = Color.rgb(170, 177, 195)

    private var rootLayout: LinearLayout? = null
    private var webView: WebView? = null
    private var player: ExoPlayer? = null
    private var playerView: PlayerView? = null
    private var channelBox: LinearLayout? = null
    private var tts: TextToSpeech? = null

    private val channelList =
        mutableListOf<Pair<String, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = bgColor
        window.navigationBarColor = Color.BLACK

        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )

        showHome()
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    private fun rounded(
        color: Int,
        radius: Int = 18,
        strokeColor: Int = Color.TRANSPARENT
    ): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
            if (strokeColor != Color.TRANSPARENT) {
                setStroke(dp(1), strokeColor)
            }
        }
    }

    private fun makeText(
        value: String,
        size: Float = 15f,
        color: Int = whiteColor,
        bold: Boolean = false
    ): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            gravity = Gravity.CENTER_VERTICAL

            if (bold) {
                setTypeface(null, Typeface.BOLD)
            }
        }
    }

    private fun makeButton(
        value: String,
        action: () -> Unit
    ): Button {
        return Button(this).apply {
            text = value
            textSize = 14f
            isAllCaps = false
            setTextColor(whiteColor)
            background = rounded(panelColor, 14, accentColor)
            setPadding(dp(12), dp(8), dp(12), dp(8))
            setOnClickListener { action() }
        }
    }

    private fun newPage(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bgColor)
            setPadding(dp(16), dp(12), dp(16), dp(16))
        }
    }

    private fun setPage(page: LinearLayout) {
        rootLayout = page
        setContentView(page)
    }

    private fun addSpace(
        parent: LinearLayout,
        height: Int
    ) {
        parent.addView(
            View(this),
            LinearLayout.LayoutParams(
                1,
                dp(height)
            )
        )
    }

    private fun showMessage(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
    
    private fun showHome() {
        val page = newPage()
        setPage(page)

        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        scroll.addView(content)
        page.addView(
            scroll,
            LinearLayout.LayoutParams(
                -1, 0, 1f
            )
        )

        // Logo and title
        val header = LinearLayout(this).apply {
            gravity = Gravity.CENTER_VERTICAL
            orientation = LinearLayout.HORIZONTAL
        }

        val robot = makeText("🤖", 36f)
        header.addView(robot)

        val titleBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(10), 0, 0, 0)
        }

        titleBox.addView(
            makeText("BIJMA AI", 23f, whiteColor, true)
        )
        titleBox.addView(
            makeText("PRO", 13f, accentColor, true)
        )

        header.addView(
            titleBox,
            LinearLayout.LayoutParams(0, -2, 1f)
        )

        header.addView(
            makeButton("⚙") {
                showSettings()
            }
        )

        content.addView(header)
        addSpace(content, 25)

        // Welcome
        content.addView(
            makeText("Hello! Welcome back", 22f, whiteColor, true)
        )
        content.addView(
            makeText(
                "Your smart digital world",
                14f,
                mutedColor
            )
        )

        addSpace(content, 22)

        // Search bar
        val searchBox = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = rounded(panelColor, 25)
            setPadding(dp(15), dp(5), dp(8), dp(5))
        }

        val searchInput = EditText(this).apply {
            hint = "Search Google or enter website"
            setHintTextColor(mutedColor)
            setTextColor(whiteColor)
            textSize = 14f
            singleLine = true
            setBackgroundColor(Color.TRANSPARENT)
        }

        searchBox.addView(
            searchInput,
            LinearLayout.LayoutParams(0, dp(52), 1f)
        )

        searchBox.addView(
            makeButton("GO") {
                val query = searchInput.text.toString().trim()
                if (query.isNotEmpty()) {
                    openWebsite(query)
                }
            }
        )

        content.addView(searchBox)
        addSpace(content, 25)

        content.addView(
            makeText("Quick Shortcuts", 18f, whiteColor, true)
        )

        addSpace(content, 12)

        val shortcuts = listOf(
            "Google" to "https://www.google.com",
            "YouTube" to "https://m.youtube.com",
            "Facebook" to "https://www.facebook.com",
            "Wikipedia" to "https://www.wikipedia.org"
        )

        val shortcutRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        shortcuts.forEach { item ->
            val button = makeButton(item.first) {
                openWebsite(item.second)
            }

            shortcutRow.addView(
                button,
                LinearLayout.LayoutParams(0, dp(55), 1f).apply {
                    marginEnd = dp(5)
                }
            )
        }

        content.addView(shortcutRow)
        addSpace(content, 25)

        content.addView(
            makeText("All Features", 20f, whiteColor, true)
        )

        addSpace(content, 14)

        val features = listOf(
            "🎙 AI Voice" to { showVoiceAssistant() },
            "▶ YouTube" to { openWebsite("https://m.youtube.com") },
            "📺 Live TV" to { showLiveTV() },
            "🎬 IPTV Player" to { showIPTV() },
            "🌐 Smart Browser" to { showBrowser() },
            "🔎 Google Search" to { openWebsite("https://www.google.com") },
            "🛡 VPN Settings" to { showSettings() },
            "📁 File Manager" to { showMessage("File Manager") },
            "📋 M3U Playlist" to { showIPTV() },
            "⬇ Downloads" to { showMessage("Download Manager") }
        )

        features.chunked(2).forEach { rowItems ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
            }

            rowItems.forEach { feature ->
                val button = makeButton(feature.first) {
                    feature.second()
                }

                row.addView(
                    button,
                    LinearLayout.LayoutParams(0, dp(65), 1f).apply {
                        marginEnd = dp(8)
                        bottomMargin = dp(8)
                    }
                )
            }

            if (rowItems.size == 1) {
                row.addView(
                    View(this),
                    LinearLayout.LayoutParams(0, dp(65), 1f)
                )
            }

            content.addView(row)
        }

        addSpace(content, 20)

        content.addView(
            makeText(
                "BIJMA AI PRO • Smart Browser Experience",
                12f,
                mutedColor
            ).apply {
                gravity = Gravity.CENTER
            }
        )
    }
    
    private fun openWebsite(value: String) {
        val input = value.trim()
        if (input.isEmpty()) {
            showMessage("कृपया वेबसाइट या सर्च लिखें")
            return
        }

        val url = when {
            input.startsWith("http://") || input.startsWith("https://") -> input
            input.contains(".") && !input.contains(" ") ->
                "https://$input"
            else ->
                "https://www.google.com/search?q=${Uri.encode(input)}"
        }

        showBrowser()
        webView?.loadUrl(url)
    }

    private fun showBrowser() {
        val page = newPage()

        val address = EditText(this).apply {
            hint = "वेबसाइट या Google Search लिखें"
            setSingleLine(true)
            setTextColor(whiteColor)
            setHintTextColor(mutedColor)
            setPadding(dp(14), dp(12), dp(14), dp(12))
            background = rounded(panelColor)
        }

        val openButton = makeButton("खोलें") {
            openWebsite(address.text.toString())
        }

        page.addView(address)
        page.addView(openButton)

        val browser = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadsImagesAutomatically = true
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
        }

        webView = browser
        page.addView(
            browser,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )
        setPage(page)
    }

    private fun showSettings() {
        val page = newPage()
        page.addView(makeText("सेटिंग्स", 25, whiteColor, true))
        page.addView(makeText("VPN सेटिंग्स", 19, whiteColor, true))
        page.addView(
            makeText(
                "यहाँ VPN सर्वर और कनेक्शन की सेटिंग रखी जाएगी। " +
                    "अभी सर्वर की जानकारी उपलब्ध नहीं है, इसलिए VPN कनेक्ट नहीं होगा।",
                15,
                mutedColor
            )
        )
        page.addView(makeButton("होम पर वापस जाएँ") {
            showHome()
        })
        setPage(page)
    }

    private fun showVoiceAssistant() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
                putExtra(RecognizerIntent.EXTRA_PROMPT, "बोलिए, मैं सुन रहा हूँ")
            }
            startActivityForResult(intent, VOICE_REQUEST_CODE)
        } catch (e: Exception) {
            showMessage("इस मोबाइल में Voice Recognition उपलब्ध नहीं है")
        }
    }

    private fun showLiveTV() {
        showIPTV()
    }

    private fun showIPTV() {
        val page = newPage()
        page.addView(makeText("Smart IPTV Player", 24, whiteColor, true))
        page.addView(
            makeText(
                "अपनी वैध M3U Playlist का URL डालें।",
                15,
                mutedColor
            )
        )

        val playlistInput = EditText(this).apply {
            hint = "M3U Playlist URL"
            setSingleLine(true)
            setTextColor(whiteColor)
            setHintTextColor(mutedColor)
            setPadding(dp(14), dp(12), dp(14), dp(12))
            background = rounded(panelColor)
        }

        page.addView(playlistInput)
        page.addView(makeButton("Playlist लोड करें") {
            val playlistUrl = playlistInput.text.toString().trim()
            if (playlistUrl.isEmpty()) {
                showMessage("पहले M3U URL डालें")
            } else {
                loadM3U(playlistUrl)
            }
        })

        channelBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        page.addView(channelBox)
        setPage(page)
    }

    private fun loadM3U(playlistUrl: String) {
        showMessage("Playlist लोड हो रही है...")
        Thread {
            try {
                val connection = URL(playlistUrl).openConnection() as HttpURLConnection
                connection.connectTimeout = 15000
                connection.readTimeout = 15000
                connection.requestMethod = "GET"

                val lines = connection.inputStream.bufferedReader().use {
                    it.readLines()
                }
                connection.disconnect()

                val loadedChannels = mutableListOf<Pair<String, String>>()
                var channelName = "चैनल"

                for (line in lines) {
                    val item = line.trim()
                    if (item.startsWith("#EXTINF", ignoreCase = true)) {
                        channelName = item.substringAfterLast(",").trim()
                        if (channelName.isEmpty()) channelName = "चैनल"
                    } else if (
                        item.isNotEmpty() &&
                        !item.startsWith("#") &&
                        (item.startsWith("http://") || item.startsWith("https://"))
                    ) {
                        loadedChannels.add(channelName to item)
                    }
                }

                runOnUiThread {
                    channelList.clear()
                    channelList.addAll(loadedChannels)
                    channelBox?.removeAllViews()

                    if (loadedChannels.isEmpty()) {
                        showMessage("कोई चैनल नहीं मिला। M3U URL जाँचें।")
                    } else {
                        loadedChannels.forEach { channel ->
                            channelBox?.addView(
                                makeButton(channel.first) {
                                    playChannel(channel.first, channel.second)
                                }
                            )
                        }
                        showMessage("${loadedChannels.size} चैनल मिले")
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    showMessage("Playlist लोड नहीं हुई: ${e.message ?: "त्रुटि"}")
                }
            }
        }.start()
    }

    private fun playChannel(name: String, streamUrl: String) {
        try {
            player?.release()

            player = ExoPlayer.Builder(this).build()
            playerView = PlayerView(this).apply {
                player = this@MainActivity.player
                useController = true
            }

            val page = newPage()
            page.addView(makeText(name, 21, whiteColor, true))
            page.addView(
                playerView!!,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp(230)
                )
            )

            page.addView(makeButton("चैनल सूची") {
                showIPTV()
            })

            setPage(page)

            player?.setMediaItem(MediaItem.fromUri(streamUrl))
            player?.prepare()
            player?.play()
        } catch (e: Exception) {
            showMessage("चैनल चलाने में समस्या: ${e.message ?: "त्रुटि"}")
        }
    }
    
    companion object {
        private const val VOICE_REQUEST_CODE = 101
    }

    @Deprecated("Deprecated in Android API")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (
            requestCode == VOICE_REQUEST_CODE &&
            resultCode == RESULT_OK
        ) {
            val spokenText = data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()

            if (!spokenText.isNullOrBlank()) {
                val command = spokenText.trim()

                when {
                    command.contains("यूट्यूब", ignoreCase = true) ||
                    command.contains("youtube", ignoreCase = true) -> {
                        openWebsite("https://www.youtube.com")
                    }

                    command.contains("ब्राउज़र", ignoreCase = true) ||
                    command.contains("browser", ignoreCase = true) -> {
                        showBrowser()
                    }

                    else -> {
                        openWebsite(command)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        player?.release()
        player = null

        webView?.apply {
            stopLoading()
            destroy()
        }
        webView = null

        tts?.stop()
        tts?.shutdown()
        tts = null

        super.onDestroy()
    }
}
