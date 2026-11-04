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
    private var iptvPlayerView: PlayerView? = null
private var iptvVideoFrame: FrameLayout? = null
private var iptvChannelTitle: TextView? = null
private var iptvChannelSearch: EditText? = null
private var iptvFullScreen = false
private var iptvRoot: LinearLayout? = null
private var iptvChannelScroll: ScrollView? = null

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
    layout.setPadding(dp(16), dp(20), dp(16), dp(16))

    val scroll = ScrollView(this)
    val content = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
    }

    val header = TextView(this).apply {
        text = "BIJMA AI PRO"
        textSize = 29f
        setTextColor(Color.WHITE)
        gravity = Gravity.CENTER
        setTypeface(null, android.graphics.Typeface.BOLD)
        setPadding(0, dp(12), 0, dp(4))
    }
    content.addView(header)

    val subtitle = TextView(this).apply {
        text = "YOUR SMART DIGITAL WORLD"
        textSize = 11f
        setTextColor(Color.rgb(90, 205, 230))
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
        setPadding(dp(20), dp(22), dp(20), dp(22))
    }

    val welcomeTitle = TextView(this).apply {
        text = "नमस्ते! 👋"
        textSize = 23f
        setTextColor(Color.WHITE)
        setTypeface(null, android.graphics.Typeface.BOLD)
    }
    welcome.addView(welcomeTitle)

    val welcomeText = TextView(this).apply {
        text = "मैं आपकी क्या मदद कर सकता हूँ?"
        textSize = 15f
        setTextColor(Color.WHITE)
        setPadding(0, dp(7), 0, 0)
    }
    welcome.addView(welcomeText)

    val welcomeParams = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    welcomeParams.bottomMargin = dp(22)
    content.addView(welcome, welcomeParams)

    val section = TextView(this).apply {
        text = "आपकी स्मार्ट सुविधाएँ"
        textSize = 19f
        setTextColor(Color.WHITE)
        setTypeface(null, android.graphics.Typeface.BOLD)
        setPadding(dp(3), dp(5), 0, dp(12))
    }
    content.addView(section)

    val grid = android.widget.GridLayout(this).apply {
        columnCount = 2
        alignmentMode = android.widget.GridLayout.ALIGN_BOUNDS
        useDefaultMargins = false
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
            setPadding(dp(15), dp(12), dp(10), dp(12))
            background = android.graphics.drawable.GradientDrawable().apply {
                setColor(Color.rgb(24, 37, 60))
                cornerRadius = dp(19).toFloat()
                setStroke(dp(1), Color.rgb(48, 68, 95))
            }
            isClickable = true
            isFocusable = true
            setOnClickListener { action() }
        }

        val iconView = TextView(this).apply {
            text = icon
            textSize = 29f
            setPadding(0, 0, 0, dp(8))
        }
        item.addView(iconView)

        val titleView = TextView(this).apply {
            text = title
            textSize = 15f
            setTextColor(Color.WHITE)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        item.addView(titleView)

        val descriptionView = TextView(this).apply {
            text = description
            textSize = 11f
            setTextColor(Color.LTGRAY)
            setPadding(0, dp(4), 0, 0)
        }
        item.addView(descriptionView)

        val params = android.widget.GridLayout.LayoutParams().apply {
            width = 0
            height = dp(132)
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

    val footer = TextView(this).apply {
        text = "BIJMA AI PRO  •  SMART EXPERIENCE"
        textSize = 11f
        gravity = Gravity.CENTER
        setTextColor(Color.LTGRAY)
        setPadding(0, dp(25), 0, dp(15))
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

// M3U PLAYLIST - PREMIUM JIOTV STYLE

private fun showM3uScreen() {
    player?.release()
    player = ExoPlayer.Builder(this).build()

    iptvFullScreen = false
    zoomed = false

    val root = newLayout()
    iptvRoot = root

    // TOP CONTROLS
    val topControls = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
    }

    val topRow = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
    }

    val back = Button(this).apply {
        text = "← वापस"
        setOnClickListener {
            showHome()
        }
    }

    topRow.addView(back)

    val heading = TextView(this).apply {
        text = "  LIVE TV"
        textSize = 22f
        setTextColor(white)
        setTypeface(null, android.graphics.Typeface.BOLD)
    }

    topRow.addView(heading)
    topControls.addView(topRow)

    // PLAYLIST URL
    val urlInput = EditText(this).apply {
        hint = "M3U Playlist URL डालें"
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

    // VIDEO PLAYER
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

    // CURRENT CHANNEL NAME
    val channelTitle = TextView(this).apply {
        text = "📺 चैनल चुनें"
        textSize = 16f
        setTextColor(white)
        setPadding(dp(8), dp(10), dp(8), dp(10))
    }

    iptvChannelTitle = channelTitle
    root.addView(channelTitle)

    // CHANNEL SEARCH
    val searchRow = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
    }

    val searchInput = EditText(this).apply {
        hint = "चैनल खोजें..."
        setTextColor(white)
        setHintTextColor(Color.LTGRAY)
        setSingleLine(true)
        setBackgroundColor(card)
    }

    iptvChannelSearch = searchInput

    searchRow.addView(
        searchInput,
        LinearLayout.LayoutParams(0, dp(48), 1f)
    )

    val searchButton = Button(this).apply {
        text = "खोजें"
        setOnClickListener {
            renderChannels(searchInput.text.toString())
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

// CHANNEL CARDS

private fun renderChannels(search: String = "") {
    val list = channelList ?: return

    list.removeAllViews()

    val filtered = channels.filter {
        it.first.contains(search, ignoreCase = true)
    }

    for ((name, url) in filtered) {

        val item = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(12), dp(12), dp(12))

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

        val icon = TextView(this).apply {
            text = "▶"
            textSize = 20f
            setTextColor(Color.rgb(70, 210, 230))
        }

        item.addView(icon)

        val channelName = TextView(this).apply {
            text = "  $name"
            textSize = 15f
            setTextColor(white)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        item.addView(
            channelName,
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
        val empty = TextView(this).apply {
            text = if (channels.isEmpty()) {
                "पहले M3U Playlist Load करें"
            } else {
                "कोई चैनल नहीं मिला"
            }
            setTextColor(Color.LTGRAY)
            textSize = 15f
            gravity = Gravity.CENTER
            setPadding(dp(12), dp(25), dp(12), dp(25))
        }

        list.addView(empty)
    }
}

// PLAY CHANNEL IN SAME VIDEO PLAYER

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

// FULL SCREEN

private fun toggleIptvFullScreen() {
    val controls = iptvRoot ?: return
    val frame = iptvVideoFrame ?: return

    iptvFullScreen = !iptvFullScreen

    if (iptvFullScreen) {

        controls.getChildAt(0).visibility = View.GONE
        iptvChannelTitle?.visibility = View.GONE
        iptvChannelSearch?.let {
    (it.parent as? View)?.visibility = View.GONE
        }
        iptvChannelScroll?.visibility = View.GONE

        frame.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0,
            1f
        )

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

    } else {

        controls.getChildAt(0).visibility = View.VISIBLE
        iptvChannelTitle?.visibility = View.VISIBLE
        iptvChannelSearch?.let {
    (it.parent as? View)?.visibility = View.VISIBLE
        }
        iptvChannelScroll?.visibility = View.VISIBLE

        frame.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(220)
        )

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_VISIBLE
    }

    controls.requestLayout()
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

