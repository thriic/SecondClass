package com.thryan.secondclass.ui.login

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.thryan.secondclass.BuildConfig
import kotlinx.coroutines.launch

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun webView(
    modifier: Modifier = Modifier,
    url: String,
) {
    val webViewChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            //回调网页内容加载进度
            //TODO
            super.onProgressChanged(view, newProgress)
        }
    }
    val webViewClient = object : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
        }
    }

    lateinit var webView: WebView
    val coroutineScope = rememberCoroutineScope()
    AndroidView(modifier = modifier, factory = { context ->
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
            this.webChromeClient = webViewChromeClient
            this.webViewClient = webViewClient
            this.addJavascriptInterface(JavaScriptInterface(), "app")
            settings.apply {
                //支持js交互
                javaScriptEnabled = true
                javaScriptCanOpenWindowsAutomatically = true
                //将图片调整到适合webView的大小
                useWideViewPort = true
                //缩放至屏幕的大小
                loadWithOverviewMode = true
                //启用缩放功能
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
            }
            webView = this
            loadUrl(url)
        }
    })
    BackHandler {
        coroutineScope.launch {
            //自行控制点击了返回按键之后，关闭页面还是返回上一级网页
            if (webView.canGoBack()) {
                webView.goBack()
            } else {
                //TODO
            }
        }
    }
}

class JavaScriptInterface {
    @get:JavascriptInterface
    @set:JavascriptInterface
    var username: String = ""

    @get:JavascriptInterface
    @set:JavascriptInterface
    var password: String = ""
}
