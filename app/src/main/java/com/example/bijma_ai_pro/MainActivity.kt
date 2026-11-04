package com.example.bijma_ai_pro

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import android.app.AlertDialog
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.media3.ui.AspectRatioFrameLayout
import java.net.URL
import java.util.Locale

class MainActivity : Activity() {

    private val bg = Color.rgb(10, 13, 28)
    private val card = Color.rgb(29, 35, 59)
    private val purple = Color.rgb(105, 55, 230)
    private val white = Color.WHITE

    private var player: ExoPlayer? = null
    private var webView: WebView? = null
    private var tts: TextToSpeech? = null
    private var zoomed = false

    private val channels = mutableListOf<Pair<String, String>>()
    private var channelList: LinearLayout? = null

    companion object {
        private const val VOICE_REQUEST = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tts = TextToSpeech(this) {
            if (it == TextToSpeech.SUCCESS) {
                tts?.language = Locale("hi", "IN")
            }
        }

        showHome()
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    private fun newLayout(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
            setPadding(dp(16), dp(15), dp(16), dp(15))
        }
    }

    private fun addHeading(parent: LinearLayout, text: String) {
        val heading = TextView(this).apply {
            this.text = text
            textSize = 25f
            setTextColor(white)
            gravity = Gravity.CENTER
            setPadding(0, dp(16), 0, dp(16))
        }
        parent.addView(heading)
    }

    private fun addButton(
        parent: LinearLayout,
        name: String,
        action: () -> Unit
    ) {
        val button = Button(this).apply {
            text = name
            textSize = 17f
            setTextColor(white)
            isAllCaps = false
            setBackgroundColor(card)
            setOnClickListener { action() }
        }

        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(58)
        )
        params.setMargins(0, dp(6), 0, dp(6))
        parent.addView(button, params)
    }

    private fun showHome() {
        player?.release()
        player = null
        webView = null

        val layout = newLayout()

        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        addHeading(content, "BIJMA AI PRO")

        val subtitle = TextView(this).apply {
            text = "आपका स्मार्ट डिजिटल असिस्टेंट"
            textSize = 17f
            setTextColor(white)
            gravity = Gravity.CENTER
            setPadding(0, dp(8), 0, dp(20))
        }
        content.addView(subtitle)

        val welcome = TextView(this).apply {
            text = "नमस्ते! मैं आपकी क्या मदद कर सकता हूँ?"
            textSize = 20f
            gravity = Gravity.CENTER
            setTextColor(white)
            setBackgroundColor(purple)
            setPadding(dp(15), dp(25), dp(15), dp(25))
        }
        content.addView(welcome)

        addButton(content, "🎙 Voice Assistant") {
            startVoiceSearch()
        }

        addButton(content, "🔎 Google Search") {
            showBrowser("https://www.google.com")
        }

        addButton(content, "▶ YouTube") {
            showBrowser("https://m.youtube.com")
        }

        addButton(content, "📺 Live TV") {
            showM3uScreen()
        }

        addButton(content, "📡 IPTV / M3U Playlist") {
            showM3uScreen()
        }

        addButton(content, "🌐 Smart Browser") {
            showBrowser("https://www.google.com")
        }

        addButton(content, "🛡 VPN Settings") {
            showVpnInfo()
        }

        val footer = TextView(this).apply {
            text = "BIJMA AI PRO • Smart Experience"
            gravity = Gravity.CENTER
            textSize = 13f
            setTextColor(Color.LTGRAY)
            setPadding(0, dp(25), 0, dp(20))
        }
        content.addView(footer)

        scroll.addView(content)
        layout.addView(
            scroll,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        setContentView(layout)
    }

    // SMART BROWSER

    private fun showBrowser(initialUrl: String) {
        player?.release()
        player = null

        val layout = newLayout()

        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val back = Button(this).apply {
            text = "←"
            setOnClickListener { showHome() }
        }
        top.addView(back)

        val address = EditText(this).apply {
            hint = "Website या Search लिखें"
            setSingleLine(true)
            setTextColor(white)
            setHintTextColor(Color.LTGRAY)
            setBackgroundColor(card)
        }

        top.addView(
            address,
            LinearLayout.LayoutParams(0, dp(50), 1f)
        )

        val go = Button(this).apply {
            text = "Go"
            setOnClickListener {
                val input = address.text.toString().trim()
                if (input.isNotEmpty()) {
                    val url = if (
                        input.startsWith("http://") ||
                        input.startsWith("https://")
                    ) {
                        input
                    } else if (input.contains(".") && !input.contains(" ")) {
                        "https://$input"
                    } else {
                        "https://www.google.com/search?q=" +
                                Uri.encode(input)
                    }
                    webView?.loadUrl(url)
                }
            }
        }
        top.addView(go)
        layout.addView(top)

        val browser = WebView(this)
        webView = browser

        browser.settings.javaScriptEnabled = true
        browser.settings.domStorageEnabled = true
        browser.settings.useWideViewPort = true
        browser.settings.loadWithOverviewMode = true

        browser.webViewClient = WebViewClient()
        browser.webChromeClient = WebChromeClient()

        browser.loadUrl(initialUrl)

        layout.addView(
            browser,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        setContentView(layout)
    }

    // M3U PLAYLIST

    private fun showM3uScreen() {
        player?.release()
        player = null

        val layout = newLayout()

        val back = Button(this).apply {
            text = "← वापस"
            setOnClickListener { showHome() }
        }
        layout.addView(back)

        addHeading(layout, "IPTV / M3U PLAYER")

        val urlInput = EditText(this).apply {
            hint = "अपनी M3U Playlist URL डालें"
            setTextColor(white)
            setHintTextColor(Color.LTGRAY)
            setSingleLine(true)
            setBackgroundColor(card)
        }
        layout.addView(urlInput)

        addButton(layout, "Load Playlist") {
            val url = urlInput.text.toString().trim()
            if (url.isNotEmpty()) {
                loadM3u(url)
            } else {
                Toast.makeText(
                    this,
                    "पहले M3U URL डालें",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val scroll = ScrollView(this)
        channelList = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        scroll.addView(channelList)

        layout.addView(
            scroll,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        renderChannels()
        setContentView(layout)
    }

    private fun loadM3u(url: String) {
        Toast.makeText(
            this,
            "Playlist लोड हो रही है...",
            Toast.LENGTH_SHORT
        ).show()

        Thread {
            try {
                val connection = URL(url).openConnection()
                connection.connectTimeout = 15000
                connection.readTimeout = 20000

                val text = connection.getInputStream()
                    .bufferedReader()
                    .use { it.readText() }

                val result = mutableListOf<Pair<String, String>>()
                var name = "TV Channel"

                for (line in text.lineSequence()) {
                    val value = line.trim()

                    if (value.startsWith("#EXTINF")) {
                        name = value.substringAfter(",", "TV Channel").trim()
                    } else if (
                        value.isNotEmpty() &&
                        !value.startsWith("#")
                    ) {
                        result.add(Pair(name, value))
                        name = "TV Channel"
                    }
                }

                runOnUiThread {
                    channels.clear()
                    channels.addAll(result)
                    renderChannels()

                    Toast.makeText(
                        this,
                        "${channels.size} चैनल मिले",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Playlist लोड नहीं हुई: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }.start()
    }

    private fun renderChannels() {
        val list = channelList ?: return
        list.removeAllViews()

        for ((name, url) in channels) {
            addButton(list, "▶ $name") {
                playChannel(name, url)
            }
        }
    }

    // VIDEO PLAYER WITH ZOOM

    private fun playChannel(name: String, url: String) {
        player?.release()

        val layout = newLayout()

        val back = Button(this).apply {
            text = "← वापस"
            setOnClickListener { showM3uScreen() }
        }
        layout.addView(back)

        addHeading(layout, name)

        val frame = FrameLayout(this)

        val videoView = PlayerView(this).apply {
            useController = true
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        }

        player = ExoPlayer.Builder(this).build()
        videoView.player = player

        frame.addView(
            videoView,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(250)
            )
        )

        val zoomButton = Button(this).apply {
            text = "ZOOM"
            setTextColor(white)
            setBackgroundColor(purple)
            setOnClickListener {
                zoomed = !zoomed
                videoView.resizeMode = if (zoomed) {
                    AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                } else {
                    AspectRatioFrameLayout.RESIZE_MODE_FIT
                }
                text = if (zoomed) "FIT" else "ZOOM"
            }
        }

        val zoomParams = FrameLayout.LayoutParams(
            dp(90),
            dp(45),
            Gravity.TOP or Gravity.END
        )
        frame.addView(zoomButton, zoomParams)

        layout.addView(
            frame,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(250)
            )
        )

        setContentView(layout)

        try {
            player?.setMediaItem(MediaItem.fromUri(url))
            player?.prepare()
            player?.playWhenReady = true
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "वीडियो चलाने में समस्या",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // VOICE SEARCH

    private fun startVoiceSearch() {
        if (!android.speech.SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(
                this,
                "Voice Recognition उपलब्ध नहीं है",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        try {
            val intent = Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH
            ).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE,
                    "hi-IN"
                )
                putExtra(
                    RecognizerIntent.EXTRA_PROMPT,
                    "बोलिए, आपको क्या खोजना है?"
                )
            }

            startActivityForResult(intent, VOICE_REQUEST)

        } catch (e: Exception) {
            Toast.makeText(
                this,
                "माइक शुरू नहीं हो पाया",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == VOICE_REQUEST &&
            resultCode == RESULT_OK
        ) {
            val results = data?.getStringArrayListExtra(
                RecognizerIntent.EXTRA_RESULTS
            )

            val query = results?.firstOrNull() ?: return

            tts?.speak(
                "आपने कहा $query अब मैं इसे खोज रहा हूँ",
                TextToSpeech.QUEUE_FLUSH,
                null,
                "voice_search"
            )

            showBrowser(
                "https://www.google.com/search?q=" +
                        Uri.encode(query)
            )
        }
    }

    // VPN INFORMATION

    private fun showVpnInfo() {
        AlertDialog.Builder(this)
            .setTitle("BIJMA AI PRO VPN")
            .setMessage(
                "VPN सुविधा के लिए वास्तविक VPN सर्वर, " +
                "VPN कॉन्फ़िगरेशन और Android VPN Service जरूरी हैं।\n\n" +
                "अभी VPN कनेक्शन सक्रिय नहीं है।"
            )
            .setPositiveButton("ठीक है", null)
            .show()
    }

    @Deprecated("Deprecated Android back callback")
    override fun onBackPressed() {
        if (webView?.canGoBack() == true) {
            webView?.goBack()
        } else {
            showHome()
        }
    }

    override fun onDestroy() {
        player?.release()
        player = null

        webView?.destroy()
        webView = null

        tts?.shutdown()
        tts = null

        super.onDestroy()
    }
}

