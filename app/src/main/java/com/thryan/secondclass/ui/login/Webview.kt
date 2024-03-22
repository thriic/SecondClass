package com.thryan.secondclass.ui.login

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.thryan.secondclass.BuildConfig
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebView(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel,
    targetUrl: String
) {
    val activity = LocalContext.current as Activity
    lateinit var webView: WebView
    val jwcPattern = Pattern.compile(".*\\.cuit\\.edu\\.cn[^/]*/authserver/.*")
    val webViewChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            if (newProgress < 100) {
                viewModel.send(LoginIntent.UpdatePending(true))
            } else {
                viewModel.send(LoginIntent.UpdatePending(false))
            }
            super.onProgressChanged(view, newProgress)
        }
    }
    val webViewClient = object : WebViewClient() {
        var twfid: String = ""
        var token: String = ""
        override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
            val url = request.url
            val host = url.host!!
            val path = url.path!!
//            Log.d("webview", path)

            //由于Cloudflare在璃月访问慢且Android WebView缓存大小限制为20MB，因此手动替换为本地资源
            if (path.contains("cuit/captcha")) {
                val filename = path.split("/").last()
                val inputStream = activity.assets.open(filename)
                val mimeType = if (filename.contains(".js")) {
                    "application/javascript"
                } else {
                    "application/wasm"
                }
                val response = WebResourceResponse(mimeType, "UTF-8", inputStream)
                val headers = HashMap<String, String>()
                headers["Access-Control-Allow-Origin"] = "*"
                headers["Cache-Control"]="no-cache, no-store"
                response.responseHeaders = headers
                return response
            } else if (host.contains("ekty-cuit-edu-cn")) {
                val cookie = CookieManager.getInstance().getCookie(url.toString())
                Log.d("webLogin", "cookie $cookie")
                if (cookie != null) {
                    twfid = cookie.split("TWFID=")[1]
                }
            } else if (host.contains("ekt-cuit-edu-cn")) {
                val authorization = request.requestHeaders["Authorization"]
                Log.d("webLogin", "auth $authorization")
                if (authorization != null) {
                    token = authorization.split(" ")[1]
                }
            }

            if (twfid != "" && token != "") {
                viewModel.send(LoginIntent.WebLogin(twfid, token, {
                    webView.destroy()
                }))
            }
            return null
        }

        override fun onPageFinished(view: WebView, url: String) {
            Log.d("webview", url)
            if (url == "https://webvpn.cuit.edu.cn/portal/#!/service") {
                Log.d("webview", "service")
                view.loadUrl(targetUrl)
            } else if (jwcPattern.matcher(url).matches() or (url == "https://webvpn.cuit.edu.cn/portal/#!/login")) {
                //如果是登录页面则注入填写验证码的js脚本
                Log.d("webview", "inject script")
                view.loadUrl("javascript:(()=>{window.resourcePath=\"https://static.wed0n.top/cuit/captcha/\";const script=document.createElement(\"script\");script.src=resourcePath+\"script.js\";script.type=\"module\";document.body.appendChild(script);})()")
            }
        }
    }

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
            settings.apply {
                //支持js交互
                javaScriptEnabled = true
                javaScriptCanOpenWindowsAutomatically = true
                domStorageEnabled = true
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
            loadUrl(targetUrl)
        }
    })
    BackHandler {
        coroutineScope.launch {
            if (webView.canGoBack()) {
                webView.goBack()
            } else {
                activity.finish()
            }
        }
    }
}