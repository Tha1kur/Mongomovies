package com.mavilon.mongomovies.presentation.trailer

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun TrailerPlayerScreen(
    youtubeKey: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val watchUrl = "https://www.youtube.com/watch?v=$youtubeKey"
    val embedUrl = "https://www.youtube.com/embed/$youtubeKey?autoplay=1&modestbranding=1&rel=0"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trailer") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(watchUrl)))
                        }
                    ) {
                        Text(
                            text = "Open",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.mediaPlaybackRequiresUserGesture = false
                        webChromeClient = WebChromeClient()
                        webViewClient = WebViewClient()
                        loadUrl(embedUrl)
                    }
                },
                update = { webView ->
                    val current = webView.url
                    if (current == null || !current.contains(youtubeKey)) {
                        webView.loadUrl(embedUrl)
                    }
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        // Best-effort: if something goes wrong, user can still hit "Open".
    }
}
