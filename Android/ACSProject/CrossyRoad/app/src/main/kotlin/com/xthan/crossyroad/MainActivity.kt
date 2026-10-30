package com.xthan.crossyroad

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private val PREF_NAME = "CrossyRoadPrefs"
    private val KEY_HIGH_SCORE = "high_score"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.loadsImagesAutomatically = true
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webSettings.mediaPlaybackRequiresUserGesture = false

        // Inject the Javascript Interface to handle high score storage
        webView.addJavascriptInterface(WebAppInterface(this), "AndroidBridge")
        
        webView.webViewClient = WebViewClient()

        // Load your bundled HTML file from assets
        webView.loadUrl("file:///android_asset/index.html")
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    inner class WebAppInterface(private val mContext: Context) {

        @JavascriptInterface
        fun saveHighScore(score: Int) {
            val sharedPreferences = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val currentHigh = sharedPreferences.getInt(KEY_HIGH_SCORE, 0)
            if (score > currentHigh) {
                sharedPreferences.edit().putInt(KEY_HIGH_SCORE, score).apply()
            }
        }

        @JavascriptInterface
        fun getHighScore(): Int {
            val sharedPreferences = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getInt(KEY_HIGH_SCORE, 0)
        }
    }
}
