package com.example.yarubwebviewtemplate

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : ComponentActivity() {

    private lateinit var webView: WebView
    private var pendingPermissionRequest: PermissionRequest? = null

    private val requestAndroidPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            val allowedResources = mutableListOf<String>()

            if (permissions[Manifest.permission.CAMERA] == true) {
                allowedResources.add(PermissionRequest.RESOURCE_VIDEO_CAPTURE)
            }

            if (permissions[Manifest.permission.RECORD_AUDIO] == true) {
                allowedResources.add(PermissionRequest.RESOURCE_AUDIO_CAPTURE)
            }

            pendingPermissionRequest?.let { request ->
                if (allowedResources.isNotEmpty()) {
                    request.grant(allowedResources.toTypedArray())
                } else {
                    request.deny()
                }

                pendingPermissionRequest = null
            }
        }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)
        setContentView(webView)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        webView.webViewClient = WebViewClient()

        webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                runOnUiThread {
                    pendingPermissionRequest = request

                    requestAndroidPermissions.launch(
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO
                        )
                    )
                }
            }
        }

        webView.loadUrl("file:///android_asset/www/index.html")
    }
}