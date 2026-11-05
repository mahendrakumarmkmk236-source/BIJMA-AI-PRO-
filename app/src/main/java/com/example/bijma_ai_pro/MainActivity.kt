package com.example.bijma_ai_pro

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

class MainActivity : Activity() {

    private val backgroundColor = Color.rgb(10, 13, 32)
    private val cardColor = Color.rgb(28, 34, 65)
    private val accentColor = Color.rgb(112, 82, 255)
    private val textColor = Color.WHITE

    private lateinit var root: LinearLayout

    private var browser: WebView? = null
    private var player: ExoPlayer? = null
    private var playerView: PlayerView? = null

    private val channels = mutableListOf<Pair<String, String>>()
    private var currentChannelIndex = -1

    private var fullScreenView: View? = null
    private var fullScreenCallback:
        WebChromeClient.CustomViewCallback? = null

    private var browserAddress: EditText? = null

    companion object {
        private const val FILE_REQUEST = 1001
        private const val VOICE_REQUEST = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showHome()
    }

    private fun setupRoot() {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(backgroundColor)
            setPadding(16, 12, 16, 12)
        }
        setContentView(root)
    }

    private fun addTitle(title: String) {
        val heading = TextView(this).apply {
            text = title
            textSize = 25f
            setTextColor(textColor)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(8, 18, 8, 18)
        }
        root.addView(heading)
    }

    private fun makeButton(
        label: String,
        action: () -> Unit
    ): Button {
        return Button(this).apply {
            text = label
            textSize = 16f
            isAllCaps = false
            setTextColor(textColor)
            background = android.graphics.drawable.GradientDrawable().apply {
                setColor(cardColor)
                cornerRadius = 24f
                setStroke(2, accentColor)
            }
            setPadding(12, 13, 12, 13)
            setOnClickListener { action() }
        }
    }

    private fun addButton(
        label: String,
        action: () -> Unit
    ) {
        root.addView(
            makeButton(label, action),
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 6, 0, 6)
            }
        )
    }

    private fun addHomeButton(
        parent: LinearLayout,
        label: String,
        action: () -> Unit
    ) {
        parent.addView(
            makeButton(label, action),
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 7, 0, 7)
            }
        )
    }

    private fun showHome() {
        closeBrowser()
        stopPlayer()
        setupRoot()

        val scroll = ScrollView(this)

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
        }

        val logoId = resources.getIdentifier(
            "bijma_ai_pro_",
            "drawable",
            packageName
        )

        if (logoId != 0) {
            val logo = ImageView(this).apply {
                setImageResource(logoId)
                adjustViewBounds = true
                scaleType = ImageView.ScaleType.FIT_CENTER
                setPadding(16, 12, 16, 12)
            }

            content.addView(
                logo,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    190
                )
            )
        }

        val title = TextView(this).apply {
            text = "BIJMA AI PRO"
            textSize = 29f
            setTextColor(textColor)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(8, 10, 8, 8)
        }
        content.addView(title)

        val subtitle = TextView(this).apply {
            text = "Smart Entertainment • Browser • IPTV"
            textSize = 14f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(8, 4, 8, 20)
        }
        content.addView(subtitle)

        val browserCard = LinearLayout(this).apply {
    orientation = LinearLayout.VERTICAL
    gravity = Gravity.CENTER
    setPadding(16, 18, 16, 18)

    background = android.graphics.drawable.GradientDrawable().apply {
        setColor(cardColor)
        cornerRadius = 28f
        setStroke(4, Color.rgb(255, 105, 0))
    }

    setOnClickListener {
        showBrowser("https://www.google.com")
    }
}

val browserIcon = ImageView(this).apply {
    setImageResource(R.drawable.browser_icon)
    scaleType = ImageView.ScaleType.FIT_CENTER
}

browserCard.addView(
    browserIcon,
    LinearLayout.LayoutParams(90, 90)
)

val browserLabel = TextView(this).apply {
    text = "Browser\nवेब ब्राउज़र"
    textSize = 19f
    setTextColor(Color.WHITE)
    typeface = Typeface.DEFAULT_BOLD
    gravity = Gravity.CENTER
    setPadding(8, 10, 8, 4)
}

browserCard.addView(browserLabel)

content.addView(
    browserCard,
    LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    ).apply {
        setMargins(0, 7, 0, 7)
    }
)

        addHomeButton(content, "▶  YouTube") {
            showBrowser("https://m.youtube.com")
        }

        addHomeButton(content, "📺  Live TV / IPTV") {
            showIPTV()
        }

        addHomeButton(content, "🎙  Voice Assistant") {
            showVoiceAssistant()
        }

        addHomeButton(content, "📁  File Manager") {
            openFileManager()
        }

        addHomeButton(content, "⬇  Downloads") {
            showDownloads()
        }

        scroll.addView(content)

        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    private fun showBrowser(startUrl: String) {
        stopPlayer()
        closeBrowser()
        setupRoot()

        addTitle("BIJMA SMART BROWSER")

        val address = EditText(this).apply {
            hint = "वेबसाइट या URL लिखें"
            setText(startUrl)
            setTextColor(textColor)
            setHintTextColor(Color.LTGRAY)
            setSingleLine(true)
            setPadding(12, 12, 12, 12)
            setBackgroundColor(cardColor)
        }

        browserAddress = address
        root.addView(address)

        val controls = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val goButton = makeButton("GO") {
            val typed = address.text.toString().trim()

            if (typed.isNotEmpty()) {
                val finalUrl =
                    if (typed.startsWith("http://") ||
                        typed.startsWith("https://")) {
                        typed
                    } else {
                        "https://$typed"
                    }

                browser?.loadUrl(finalUrl)
            }
        }

        val backButton = makeButton("Back") {
            if (browser?.canGoBack() == true) {
                browser?.goBack()
            }
        }

        val homeButton = makeButton("Home") {
            showHome()
        }

        controls.addView(
            goButton,
            LinearLayout.LayoutParams(0, -2, 1f)
        )
        controls.addView(
            backButton,
            LinearLayout.LayoutParams(0, -2, 1f)
        )
        controls.addView(
            homeButton,
            LinearLayout.LayoutParams(0, -2, 1f)
        )

        root.addView(controls)

        val web = WebView(this)
        browser = web

        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.settings.loadsImagesAutomatically = true
        web.settings.useWideViewPort = true
        web.settings.loadWithOverviewMode = true
        web.settings.mediaPlaybackRequiresUserGesture = false

        web.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return false
            }

            override fun onPageFinished(
                view: WebView?,
                url: String?
            ) {
                if (url != null) {
                    address.setText(url)
                }
            }
        }

        web.webChromeClient = object : WebChromeClient() {

            override fun onShowCustomView(
                view: View?,
                callback: CustomViewCallback?
            ) {
                if (view == null) return

                if (fullScreenView != null) {
                    callback?.onCustomViewHidden()
                    return
                }

                fullScreenView = view
                fullScreenCallback = callback
                root.visibility = View.GONE

                addContentView(
                    view,
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )

                window.addFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
            }

            override fun onHideCustomView() {
                exitWebFullScreen()
            }
        }

        root.addView(
            web,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        web.loadUrl(startUrl)
    }

    private fun exitWebFullScreen() {
        val view = fullScreenView ?: return

        (view.parent as? ViewGroup)?.removeView(view)
        fullScreenView = null

        val callback = fullScreenCallback
        fullScreenCallback = null
        callback?.onCustomViewHidden()

        root.visibility = View.VISIBLE
        window.clearFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    private fun closeBrowser() {
        if (fullScreenView != null) {
            exitWebFullScreen()
        }

        browser?.apply {
            stopLoading()
            loadUrl("about:blank")
            removeAllViews()
            destroy()
        }

        browser = null
        browserAddress = null
    }

        // ==============================
    // IPTV PLAYER
    // ==============================

    private fun showIPTV() {
        closeBrowser()
        stopPlayer()
        setupRoot()

        addTitle("BIJMA AI PRO • LIVE TV")

        val description = TextView(this).apply {
            text = "अपनी M3U Playlist से TV चैनल चलाएँ"
            textSize = 15f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(8, 10, 8, 20)
        }
        root.addView(description)

        addButton("M3U Playlist URL से Load करें") {
            showPlaylistUrlDialog()
        }

        addButton("मोबाइल से M3U फाइल चुनें") {
            openPlaylistFile()
        }

        addButton("AI Playlist सुविधा") {
            showAIPlaylist()
        }

        addButton("मेरे चैनल") {
            showChannelList()
        }

        addButton("Home") {
            showHome()
        }
    }

    private fun showPlaylistUrlDialog() {
        val input = EditText(this).apply {
            hint = "https://example.com/playlist.m3u"
            setTextColor(textColor)
            setHintTextColor(Color.GRAY)
            setPadding(15, 15, 15, 15)
        }

        AlertDialog.Builder(this)
            .setTitle("M3U Playlist URL")
            .setView(input)
            .setPositiveButton("LOAD") { _, _ ->
                val url = input.text.toString().trim()

                if (url.isNotEmpty()) {
                    loadM3UFromUrl(url)
                } else {
                    showMessage("Playlist URL डालें")
                }
            }
            .setNegativeButton("रद्द करें", null)
            .show()
    }

    private fun openPlaylistFile() {
        try {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            }

            startActivityForResult(intent, FILE_REQUEST)

        } catch (e: Exception) {
            showMessage("फाइल नहीं खुल सकी")
        }
    }

    private fun loadM3UFromUrl(url: String) {
        showMessage("Playlist लोड हो रही है...")

        Thread {
            var connection: HttpURLConnection? = null

            try {
                val finalUrl = if (
                    url.startsWith("http://") ||
                    url.startsWith("https://")
                ) {
                    url
                } else {
                    "https://$url"
                }

                connection =
                    URL(finalUrl).openConnection() as HttpURLConnection

                connection.connectTimeout = 20000
                connection.readTimeout = 20000
                connection.requestMethod = "GET"
                connection.setRequestProperty(
                    "User-Agent",
                    "BIJMA-AI-PRO"
                )

                connection.connect()

                if (connection.responseCode !in 200..299) {
                    throw Exception(
                        "Server Error: ${connection.responseCode}"
                    )
                }

                val content = connection.inputStream
                    .bufferedReader()
                    .use { it.readText() }

                val parsedChannels = parseM3U(content)

                runOnUiThread {
                    channels.clear()
                    channels.addAll(parsedChannels)

                    if (channels.isEmpty()) {
                        showMessage("इस Playlist में चैनल नहीं मिले")
                    } else {
                        showMessage(
                            "${channels.size} चैनल लोड हो गए"
                        )
                        showChannelList()
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    showMessage(
                        "Playlist लोड नहीं हुई: ${e.message}"
                    )
                }
            } finally {
                connection?.disconnect()
            }
        }.start()
    }

    private fun parseM3U(
        content: String
    ): List<Pair<String, String>> {

        val result = mutableListOf<Pair<String, String>>()
        var channelName = "Unknown Channel"

        content.lineSequence().forEach { rawLine ->

            val line = rawLine.trim()

            if (line.startsWith("#EXTINF", ignoreCase = true)) {

                val commaPosition = line.lastIndexOf(",")

                channelName = if (commaPosition >= 0) {
                    line.substring(commaPosition + 1).trim()
                } else {
                    line.substringAfter("tvg-name=\"")
                        .substringBefore("\"")
                        .ifEmpty { "Unknown Channel" }
                }

                if (channelName.isBlank()) {
                    channelName = "Unknown Channel"
                }

            } else if (
                line.isNotEmpty() &&
                !line.startsWith("#") &&
                (
                    line.startsWith("http://") ||
                    line.startsWith("https://")
                )
            ) {
                result.add(channelName to line)
                channelName = "Unknown Channel"
            }
        }

        return result
    }

    // ==============================
    // CHANNEL LIST
    // ==============================

    private fun showChannelList() {
        stopPlayer()
        setupRoot()

        addTitle("LIVE TV CHANNELS")

        val count = TextView(this).apply {
            text = "कुल चैनल: ${channels.size}"
            textSize = 15f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(8, 5, 8, 15)
        }

        root.addView(count)

        if (channels.isEmpty()) {
            val empty = TextView(this).apply {
                text = "अभी कोई चैनल उपलब्ध नहीं है।\nपहले M3U Playlist लोड करें।"
                textSize = 16f
                setTextColor(textColor)
                gravity = Gravity.CENTER
                setPadding(12, 30, 12, 30)
            }

            root.addView(empty)

            addButton("Playlist लोड करें") {
                showIPTV()
            }

            addButton("Home") {
                showHome()
            }

            return
        }

        val search = EditText(this).apply {
            hint = "चैनल खोजें..."
            setTextColor(textColor)
            setHintTextColor(Color.GRAY)
            setSingleLine(true)
            setPadding(12, 12, 12, 12)
            setBackgroundColor(cardColor)
        }

        root.addView(search)

        val scroll = ScrollView(this)

        val list = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        scroll.addView(list)

        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        fun populateChannels(query: String) {
            list.removeAllViews()

            channels.forEachIndexed { index, channel ->

                if (
                    query.isNotBlank() &&
                    !channel.first.contains(
                        query,
                        ignoreCase = true
                    )
                ) {
                    return@forEachIndexed
                }

                val button = makeButton(
                    "${index + 1}. ${channel.first}"
                ) {
                    playChannel(index)
                }

                list.addView(
                    button,
                    LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 4, 0, 4)
                    }
                )
            }
        }

        populateChannels("")

        search.addTextChangedListener(
            object : android.text.TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    populateChannels(s?.toString().orEmpty())
                }

                override fun afterTextChanged(
                    s: android.text.Editable?
                ) {}
            }
        )

        addButton("नई Playlist") {
            showIPTV()
        }

        addButton("Home") {
            showHome()
        }
    }

    // ==============================
    // VIDEO PLAYER
    // ==============================

    private fun playChannel(index: Int) {
        if (index !in channels.indices) {
            showMessage("चैनल उपलब्ध नहीं है")
            return
        }

        stopPlayer()

        currentChannelIndex = index

        setupRoot()

        val channel = channels[index]

        addTitle(channel.first)

        val videoView = PlayerView(this).apply {
            useController = true
            resizeMode =
                AspectRatioFrameLayout.RESIZE_MODE_FIT

            setShowBuffering(
                PlayerView.SHOW_BUFFERING_ALWAYS
            )
        }

        playerView = videoView

        root.addView(
            videoView,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        player = ExoPlayer.Builder(this).build().also { exo ->

            videoView.player = exo

            exo.setMediaItem(
                MediaItem.fromUri(channel.second)
            )

            exo.prepare()
            exo.playWhenReady = true

            exo.addListener(
                object : Player.Listener {

                    override fun onPlayerError(
                        error: PlaybackException
                    ) {
                        showMessage(
                            "चैनल चलाने में समस्या: ${error.message}"
                        )
                    }

                    override fun onPlaybackStateChanged(
                        playbackState: Int
                    ) {
                        if (
                            playbackState ==
                            Player.STATE_READY
                        ) {
                            window.addFlags(
                                WindowManager.LayoutParams
                                    .FLAG_KEEP_SCREEN_ON
                            )
                        }
                    }
                }
            )
        }

        // वीडियो Zoom और Fit नियंत्रण

        addButton("Zoom / Fill") {
            playerView?.resizeMode =
                AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        }

        addButton("Fit Screen") {
            playerView?.resizeMode =
                AspectRatioFrameLayout.RESIZE_MODE_FIT
        }

        addButton("Stretch Screen") {
            playerView?.resizeMode =
                AspectRatioFrameLayout.RESIZE_MODE_FILL
        }

        addButton("Fullscreen") {
            togglePlayerFullScreen()
        }

        addButton("अगला चैनल") {
            if (currentChannelIndex < channels.lastIndex) {
                playChannel(currentChannelIndex + 1)
            } else {
                showMessage("यह आखिरी चैनल है")
            }
        }

        addButton("पिछला चैनल") {
            if (currentChannelIndex > 0) {
                playChannel(currentChannelIndex - 1)
            } else {
                showMessage("यह पहला चैनल है")
            }
        }

        addButton("चैनल सूची") {
            showChannelList()
        }

        addButton("Home") {
            showHome()
        }
    }

    private fun stopPlayer() {
        playerView?.player = null

        player?.release()
        player = null

        playerView = null

        window.clearFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
    }

    // ==============================
    // PLAYER FULLSCREEN
    // ==============================

    private var playerFullScreen = false
    private var playerOriginalParent: ViewGroup? = null
    private var playerOriginalIndex = -1
    private var playerOriginalLayoutParams:
        ViewGroup.LayoutParams? = null

    private fun togglePlayerFullScreen() {
        val view = playerView ?: return

        if (!playerFullScreen) {

            val parent = view.parent as? ViewGroup ?: return

            playerOriginalParent = parent
            playerOriginalIndex = parent.indexOfChild(view)
            playerOriginalLayoutParams = view.layoutParams

            parent.removeView(view)

            val fullScreenContainer = android.widget.FrameLayout(this)

            fullScreenContainer.addView(
                view,
                android.widget.FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )

            setContentView(fullScreenContainer)

            window.addFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )

            window.decorView.systemUiVisibility =
                (
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                )

            playerFullScreen = true

        } else {

            val container = view.parent as? ViewGroup
            container?.removeView(view)

            val parent = playerOriginalParent

            if (parent != null) {
                val index = playerOriginalIndex.coerceIn(
                    0,
                    parent.childCount
                )

                parent.addView(
                    view,
                    index,
                    playerOriginalLayoutParams
                )
            }

            setContentView(root)

            window.clearFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )

            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE

            playerFullScreen = false
        }
    }

        // ==============================
    // VOICE ASSISTANT
    // ==============================

    private fun showVoiceAssistant() {
        closeBrowser()
        stopPlayer()
        setupRoot()

        addTitle("BIJMA AI VOICE ASSISTANT")

        val info = TextView(this).apply {
            text = "नमस्कार! मैं आपकी क्या मदद कर सकती हूँ?"
            textSize = 18f
            setTextColor(textColor)
            gravity = Gravity.CENTER
            setPadding(12, 20, 12, 20)
        }

        root.addView(info)

        addButton("🎙 बोलकर कमांड दें") {
            startVoiceRecognition()
        }

        addButton("Home") {
            showHome()
        }
    }

    private fun startVoiceRecognition() {
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
                    "अपनी कमांड बोलें"
                )
                putExtra(
                    RecognizerIntent.EXTRA_MAX_RESULTS,
                    3
                )
            }

            startActivityForResult(intent, VOICE_REQUEST)

        } catch (e: Exception) {
            showMessage(
                "Voice Recognition उपलब्ध नहीं है"
            )
        }
    }

    private fun handleVoiceCommand(command: String) {
        val text = command.lowercase(Locale.ROOT)

        when {
            text.contains("youtube") ||
            text.contains("यूट्यूब") -> {
                showBrowser("https://m.youtube.com")
            }

            text.contains("google") ||
            text.contains("गूगल") -> {
                showBrowser("https://www.google.com")
            }

            text.contains("browser") ||
            text.contains("ब्राउज़र") -> {
                showBrowser("https://www.google.com")
            }

            text.contains("tv") ||
            text.contains("टीवी") ||
            text.contains("चैनल") -> {
                showIPTV()
            }

            text.contains("home") ||
            text.contains("होम") -> {
                showHome()
            }

            text.contains("फाइल") ||
            text.contains("file") -> {
                openFileManager()
            }

            text.contains("download") ||
            text.contains("डाउनलोड") -> {
                showDownloads()
            }

            else -> {
                showMessage(
                    "कमांड समझ नहीं आई: $command"
                )
            }
        }
    }

    // ==============================
    // AI PLAYLIST INTERFACE
    // ==============================

    private fun showAIPlaylist() {
        setupRoot()

        addTitle("AI M3U PLAYLIST")

        val info = TextView(this).apply {
            text = """
                BIJMA AI PRO Playlist Assistant

                आप अपनी M3U Playlist लोड कर सकते हैं।
                ऐप चैनलों की सूची पढ़कर उन्हें खोजने और
                चलाने में मदद करेगा।

                वास्तविक AI निर्माण और खराब चैनलों की
                स्वतः मरम्मत के लिए AI API सेवा आवश्यक है।
            """.trimIndent()

            textSize = 15f
            setTextColor(textColor)
            setPadding(12, 15, 12, 20)
        }

        root.addView(info)

        addButton("M3U Playlist लोड करें") {
            showIPTV()
        }

        addButton("चैनल सूची देखें") {
            showChannelList()
        }

        addButton("Home") {
            showHome()
        }
    }

    // ==============================
    // FILE MANAGER
    // ==============================

    private fun openFileManager() {
        try {
            val intent = Intent(
                Intent.ACTION_OPEN_DOCUMENT
            ).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            }

            startActivityForResult(
                intent,
                FILE_REQUEST
            )

        } catch (e: Exception) {
            showMessage(
                "File Manager नहीं खुल सका"
            )
        }
    }

    // ==============================
    // DOWNLOADS
    // ==============================

    private fun showDownloads() {
        try {
            val intent = Intent(
                Intent.ACTION_VIEW
            ).apply {
                data = Uri.parse(
                    "content://downloads/all_downloads"
                )
            }

            startActivity(intent)

        } catch (e: Exception) {
            openFileManager()
        }
    }

    // ==============================
    // ACTIVITY RESULTS
    // ==============================

    @Deprecated("Deprecated in Android")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (resultCode != RESULT_OK || data == null) {
            return
        }

        when (requestCode) {

            FILE_REQUEST -> {
                val uri = data.data ?: return

                try {
                    val content = contentResolver
                        .openInputStream(uri)
                        ?.bufferedReader()
                        ?.use { it.readText() }

                    if (content.isNullOrBlank()) {
                        showMessage("फाइल खाली है")
                        return
                    }

                    val parsed = parseM3U(content)

                    channels.clear()
                    channels.addAll(parsed)

                    if (channels.isNotEmpty()) {
                        showChannelList()
                    } else {
                        showMessage(
                            "इस फाइल में M3U चैनल नहीं मिले"
                        )
                    }

                } catch (e: Exception) {
                    showMessage(
                        "फाइल पढ़ने में समस्या: ${e.message}"
                    )
                }
            }

            VOICE_REQUEST -> {
                val results =
                    data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS
                    )

                val command = results?.firstOrNull()

                if (!command.isNullOrBlank()) {
                    handleVoiceCommand(command)
                }
            }
        }
    }

    // ==============================
    // BACK BUTTON
    // ==============================

    @Deprecated("Deprecated in Android")
    override fun onBackPressed() {

        if (fullScreenView != null) {
            exitWebFullScreen()
            return
        }

        if (playerFullScreen) {
            togglePlayerFullScreen()
            return
        }

        if (browser?.canGoBack() == true) {
            browser?.goBack()
            return
        }

        if (player != null) {
            showChannelList()
            return
        }

        if (browser != null) {
            closeBrowser()
        }

        showHome()
    }

    // ==============================
    // CLEANUP
    // ==============================

    override fun onPause() {
        super.onPause()

        if (player != null) {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }
    }

    override fun onDestroy() {
        closeBrowser()
        stopPlayer()
        super.onDestroy()
    }
    private fun showMessage(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}
