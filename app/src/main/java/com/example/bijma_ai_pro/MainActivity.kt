package com.example.bijma_ai_pro

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Typeface
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
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

class MainActivity : Activity() {

    private val bg = Color.rgb(9, 14, 29)
    private val card = Color.rgb(24, 36, 59)
    private val accent = Color.rgb(65, 205, 225)
    private val purple = Color.rgb(105, 75, 235)
    private val white = Color.WHITE

    private var player: ExoPlayer? = null
    private var webView: WebView? = null
    private var browserLayout: LinearLayout? = null
    private var customVideoView: View? = null
    private var customVideoCallback: WebChromeClient.CustomViewCallback? = null

    private var tts: TextToSpeech? = null
    private var zoomed = false
    private var iptvFullScreen = false

    private var iptvRoot: LinearLayout? = null
    private var iptvVideoFrame: FrameLayout? = null
    private var iptvPlayerView: PlayerView? = null
    private var iptvTopControls: LinearLayout? = null
    private var iptvChannelTitle: TextView? = null
    private var iptvSearchRow: LinearLayout? = null
    private var iptvChannelScroll: ScrollView? = null

    private var channelList: LinearLayout? = null
    private var searchInput: EditText? = null

    private val channels = mutableListOf<Pair<String, String>>()

    companion object {
        private const val VOICE_REQUEST = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
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
            setPadding(dp(16), dp(14), dp(16), dp(14))
        }
    }

    private fun makeText(
        value: String,
        size: Float,
        color: Int = white,
        bold: Boolean = false
    ): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) {
                setTypeface(null, Typeface.BOLD)
            }
        }
    }

    private fun addHeading(parent: LinearLayout, value: String) {
        val heading = makeText(value, 24f, white, true).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(12), 0, dp(16))
        }
        parent.addView(heading)
    }

    private fun addButton(
        parent: LinearLayout,
        value: String,
        action: () -> Unit
    ) {
        val button = Button(this).apply {
            text = value
            textSize = 15f
            isAllCaps = false
            setTextColor(white)
            setBackgroundColor(card)
            setOnClickListener { action() }
        }

        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(52)
        )
        params.setMargins(0, dp(5), 0, dp(5))
        parent.addView(button, params)
    }

    private fun showHome() {
        releasePlayer()
        webView?.stopLoading()
        webView?.destroy()
        webView = null
        browserLayout = null

        iptvFullScreen = false
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE

        val root = newLayout()
        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val header = makeText("BIJMA AI PRO", 30f, white, true).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(12), 0, dp(3))
        }
        content.addView(header)

        val subtitle = makeText(
            "YOUR SMART DIGITAL WORLD",
            11f,
            accent,
            true
        ).apply {
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, dp(22))
        }
        content.addView(subtitle)

        val welcome = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = android.graphics.drawable.GradientDrawable(
                android.graphics.drawable.GradientDrawable.Orientation.TL_BR,
                intArrayOf(
                    Color.rgb(93, 55, 205),
                    Color.rgb(20, 130, 170)
                )
            ).apply {
                cornerRadius = dp(22).toFloat()
            }
            setPadding(dp(20), dp(20), dp(20), dp(20))
        }

        welcome.addView(makeText("नमस्ते! 👋", 23f, white, true))
        welcome.addView(
            makeText("मैं आपकी क्या मदद कर सकता हूँ?", 15f).apply {
                setPadding(0, dp(7), 0, 0)
            }
        )

        val welcomeParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        welcomeParams.bottomMargin = dp(22)
        content.addView(welcome, welcomeParams)

        content.addView(
            makeText("आपकी स्मार्ट सुविधाएँ", 19f, white, true).apply {
                setPadding(dp(3), dp(4), 0, dp(12))
            }
        )

        val grid = android.widget.GridLayout(this).apply {
            columnCount = 2
            useDefaultMargins = false
            alignmentMode = android.widget.GridLayout.ALIGN_BOUNDS
        }

        fun addFeature(
            icon: String,
            title: String,
            description: String,
            action: () -> Unit
        ) {
            val item = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(14), dp(12), dp(8), dp(12))
                background = android.graphics.drawable.GradientDrawable().apply {
                    setColor(Color.rgb(24, 37, 60))
                    cornerRadius = dp(19).toFloat()
                    setStroke(dp(1), Color.rgb(48, 68, 95))
                }
                isClickable = true
                isFocusable = true
                setOnClickListener { action() }
            }

            item.addView(makeText(icon, 28f))
            item.addView(
                makeText(title, 15f, white, true).apply {
                    setPadding(0, dp(6), 0, 0)
                }
            )
            item.addView(
                makeText(description, 11f, Color.LTGRAY).apply {
                    setPadding(0, dp(4), 0, 0)
                }
            )

            val params = android.widget.GridLayout.LayoutParams().apply {
                width = 0
                height = dp(130)
                columnSpec = android.widget.GridLayout.spec(
                    android.widget.GridLayout.UNDEFINED,
                    1f
                )
                setMargins(dp(4), dp(5), dp(4), dp(5))
            }

            grid.addView(item, params)
        }

        addFeature("🎙️", "Voice Assistant", "आवाज़ से बात करें") {
            startVoiceSearch()
        }

        addFeature("▶️", "YouTube", "वीडियो देखें") {
            showBrowser("https://m.youtube.com")
        }

        addFeature("📺", "Live TV", "लाइव चैनल देखें") {
            showM3uScreen()
        }

        addFeature("📡", "IPTV Player", "M3U Playlist चलाएँ") {
            showM3uScreen()
        }

        addFeature("🌐", "Smart Browser", "वेबसाइट खोलें") {
            showBrowser("https://www.google.com")
        }

        addFeature("🔎", "Google Search", "इंटरनेट पर खोजें") {
            showBrowser("https://www.google.com")
        }

        addFeature("🛡️", "VPN Settings", "VPN सेटिंग्स देखें") {
            showVpnInfo()
        }

        content.addView(grid)

        content.addView(
            makeText(
                "BIJMA AI PRO  •  SMART EXPERIENCE",
                11f,
                Color.LTGRAY
            ).apply {
                gravity = Gravity.CENTER
                setPadding(0, dp(24), 0, dp(16))
            }
        )

        scroll.addView(content)
        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        setContentView(root)
    }

    // SMART BROWSER

    private fun showBrowser(initialUrl: String) {
        releasePlayer()
        iptvFullScreen = false

        val root = newLayout()
        browserLayout = root

        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
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
            LinearLayout.LayoutParams(0, dp(48), 1f)
        )

        val go = Button(this).apply {
            text = "Go"
            setOnClickListener {
                val input = address.text.toString().trim()
                if (input.isNotEmpty()) {
                    val url = when {
                        input.startsWith("http://") ||
                            input.startsWith("https://") -> input

                        input.contains(".") && !input.contains(" ") ->
                            "https://$input"

                        else ->
                            "https://www.google.com/search?q=" +
                                Uri.encode(input)
                    }
                    webView?.loadUrl(url)
                }
            }
        }
        top.addView(go)
        root.addView(top)

        val browser = WebView(this)
        webView = browser

        browser.settings.javaScriptEnabled = true
        browser.settings.domStorageEnabled = true
        browser.settings.useWideViewPort = true
        browser.settings.loadWithOverviewMode = true
        browser.settings.mediaPlaybackRequiresUserGesture = false

        browser.webViewClient = WebViewClient()

        browser.webChromeClient = object : WebChromeClient() {

            override fun onShowCustomView(
                view: View?,
                callback: CustomViewCallback?
            ) {
                if (view == null) return

                if (customVideoView != null) {
                    callback?.onCustomViewHidden()
                    return
                }

                customVideoView = view
                customVideoCallback = callback

                (view.parent as? ViewGroup)?.removeView(view)

                requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

                setContentView(view)
            }

            override fun onHideCustomView() {
                hideBrowserVideo()
            }
        }

        browser.loadUrl(initialUrl)

        root.addView(
            browser,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        setContentView(root)
    }

    private fun hideBrowserVideo() {
        if (customVideoView == null) return

        customVideoView = null
        customVideoCallback?.onCustomViewHidden()
        customVideoCallback = null

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE

        browserLayout?.let { setContentView(it) }
    }
    
    // IPTV / LIVE TV SCREEN

    private fun showM3uScreen() {
        iptvFullScreen = false
        zoomed = false

        if (player == null) {
            player = ExoPlayer.Builder(this).build()
        }

        val root = newLayout()
        iptvRoot = root

        // TOP CONTROLS
        val topControls = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        iptvTopControls = topControls

        val topRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val back = Button(this).apply {
            text = "← वापस"
            setOnClickListener { showHome() }
        }
        topRow.addView(back)

        topRow.addView(
            makeText("  LIVE TV", 22f, white, true)
        )
        topControls.addView(topRow)

        val urlInput = EditText(this).apply {
            hint = "अपनी M3U Playlist URL डालें"
            setTextColor(white)
            setHintTextColor(Color.LTGRAY)
            setSingleLine(true)
            setBackgroundColor(card)
        }

        topControls.addView(
            urlInput,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(48)
            )
        )

        addButton(topControls, "📡 Playlist Load करें") {
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

        root.addView(topControls)

        // VIDEO PLAYER FRAME
        val videoFrame = FrameLayout(this).apply {
            setBackgroundColor(Color.BLACK)
        }
        iptvVideoFrame = videoFrame

        val videoView = PlayerView(this).apply {
            player = this@MainActivity.player
            useController = true
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        }
        iptvPlayerView = videoView

        videoFrame.addView(
            videoView,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        // FULL SCREEN BUTTON
        val fullButton = Button(this).apply {
            text = "⛶"
            textSize = 22f
            setTextColor(white)
            setBackgroundColor(Color.DKGRAY)
            setOnClickListener {
                toggleIptvFullScreen()
            }
        }

        videoFrame.addView(
            fullButton,
            FrameLayout.LayoutParams(
                dp(55),
                dp(48),
                Gravity.TOP or Gravity.END
            )
        )

        // ZOOM BUTTON
        val zoomButton = Button(this).apply {
            text = "ZOOM"
            textSize = 12f
            setTextColor(white)
            setBackgroundColor(Color.DKGRAY)

            setOnClickListener {
                zoomed = !zoomed

                videoView.resizeMode =
                    if (zoomed) {
                        AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    } else {
                        AspectRatioFrameLayout.RESIZE_MODE_FIT
                    }

                text = if (zoomed) "FIT" else "ZOOM"
            }
        }

        videoFrame.addView(
            zoomButton,
            FrameLayout.LayoutParams(
                dp(75),
                dp(42),
                Gravity.BOTTOM or Gravity.END
            )
        )

        root.addView(
            videoFrame,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(220)
            )
        )

        // CURRENT CHANNEL
        val title = makeText("📺 चैनल चुनें", 16f, white, true).apply {
            setPadding(dp(8), dp(10), dp(8), dp(10))
        }
        iptvChannelTitle = title
        root.addView(title)

        // SEARCH
        val searchRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        iptvSearchRow = searchRow

        val search = EditText(this).apply {
            hint = "चैनल खोजें..."
            setTextColor(white)
            setHintTextColor(Color.LTGRAY)
            setSingleLine(true)
            setBackgroundColor(card)
        }
        searchInput = search

        searchRow.addView(
            search,
            LinearLayout.LayoutParams(0, dp(48), 1f)
        )

        val searchButton = Button(this).apply {
            text = "खोजें"
            setOnClickListener {
                renderChannels(search.text.toString())
            }
        }
        searchRow.addView(searchButton)
        root.addView(searchRow)

        // CHANNEL LIST
        val channelScroll = ScrollView(this)
        iptvChannelScroll = channelScroll

        channelList = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        channelScroll.addView(channelList)

        root.addView(
            channelScroll,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        setContentView(root)
        renderChannels()
    }

    // LOAD M3U PLAYLIST

    private fun loadM3u(url: String) {
        Toast.makeText(
            this,
            "Playlist लोड हो रही है...",
            Toast.LENGTH_SHORT
        ).show()

        Thread {
            var connection: HttpURLConnection? = null

            try {
                connection = URL(url).openConnection() as HttpURLConnection
                connection.connectTimeout = 15000
                connection.readTimeout = 20000
                connection.instanceFollowRedirects = true
                connection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 BIJMA-AI-PRO"
                )

                val text = connection.inputStream
                    .bufferedReader()
                    .use { it.readText() }

                val result = mutableListOf<Pair<String, String>>()
                var name = "TV Channel"

                for (line in text.lineSequence()) {
                    val value = line.trim()

                    if (value.startsWith("#EXTINF", ignoreCase = true)) {
                        name = value.substringAfter(",", "TV Channel").trim()
                        if (name.isEmpty()) name = "TV Channel"
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
                    renderChannels(searchInput?.text?.toString() ?: "")

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
            } finally {
                connection?.disconnect()
            }
        }.start()
    }

    // CHANNEL CARDS AND SEARCH

    private fun renderChannels(search: String = "") {
        val list = channelList ?: return
        list.removeAllViews()

        val filtered = channels.filter {
            it.first.contains(search.trim(), ignoreCase = true)
        }

        for ((name, url) in filtered) {
            val item = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(12), dp(10), dp(12), dp(10))

                background =
                    android.graphics.drawable.GradientDrawable().apply {
                        setColor(Color.rgb(24, 37, 60))
                        cornerRadius = dp(14).toFloat()
                        setStroke(dp(1), Color.rgb(48, 68, 95))
                    }

                isClickable = true
                isFocusable = true

                setOnClickListener {
                    playChannel(name, url)
                }
            }

            item.addView(
                makeText("▶", 20f, accent, true)
            )

            item.addView(
                makeText("  $name", 15f, white, true),
                LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                )
            )

            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(58)
            )
            params.setMargins(0, dp(4), 0, dp(4))
            list.addView(item, params)
        }

        if (filtered.isEmpty()) {
            val message = if (channels.isEmpty()) {
                "पहले M3U Playlist Load करें"
            } else {
                "कोई चैनल नहीं मिला"
            }

            list.addView(
                makeText(message, 15f, Color.LTGRAY).apply {
                    gravity = Gravity.CENTER
                    setPadding(dp(12), dp(25), dp(12), dp(25))
                }
            )
        }
    }

    // PLAY CHANNEL IN TOP VIDEO PLAYER

    private fun playChannel(name: String, url: String) {
        try {
            val activePlayer = player ?: return

            iptvChannelTitle?.text = "▶ $name"

            activePlayer.setMediaItem(
                MediaItem.fromUri(url)
            )
            activePlayer.prepare()
            activePlayer.playWhenReady = true

        } catch (e: Exception) {
            Toast.makeText(
                this,
                "वीडियो शुरू नहीं हुआ: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
        // IPTV FULL SCREEN

    private fun toggleIptvFullScreen() {
        val root = iptvRoot ?: return
        val frame = iptvVideoFrame ?: return

        iptvFullScreen = !iptvFullScreen

        if (iptvFullScreen) {
            iptvTopControls?.visibility = View.GONE
            iptvChannelTitle?.visibility = View.GONE
            iptvSearchRow?.visibility = View.GONE
            iptvChannelScroll?.visibility = View.GONE

            frame.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )

            requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        } else {
            iptvTopControls?.visibility = View.VISIBLE
            iptvChannelTitle?.visibility = View.VISIBLE
            iptvSearchRow?.visibility = View.VISIBLE
            iptvChannelScroll?.visibility = View.VISIBLE

            frame.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(220)
            )

            requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_VISIBLE
        }

        root.requestLayout()
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
                    RecognizerIntent.EXTRA_LANGUAGE,
                    Locale("hi", "IN").toLanguageTag()
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

        if (requestCode == VOICE_REQUEST && resultCode == RESULT_OK) {
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
                "https://www.google.com/search?q=" + Uri.encode(query)
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

    // RELEASE VIDEO PLAYER

    private fun releasePlayer() {
        iptvPlayerView?.player = null
        iptvPlayerView = null

        player?.release()
        player = null

        iptvRoot = null
        iptvVideoFrame = null
        iptvTopControls = null
        iptvChannelTitle = null
        iptvSearchRow = null
        iptvChannelScroll = null
        channelList = null
        searchInput = null
    }

    // ANDROID BACK BUTTON

    @Deprecated("Deprecated Android back callback")
    override fun onBackPressed() {
        if (customVideoView != null) {
            hideBrowserVideo()
            return
        }

        if (iptvFullScreen) {
            toggleIptvFullScreen()
            return
        }

        if (webView?.canGoBack() == true) {
            webView?.goBack()
            return
        }

        showHome()
    }

    // CLEAN UP

    override fun onDestroy() {
        customVideoCallback = null
        customVideoView = null

        releasePlayer()

        webView?.stopLoading()
        webView?.destroy()
        webView = null

        tts?.shutdown()
        tts = null

        super.onDestroy()
    }
}
