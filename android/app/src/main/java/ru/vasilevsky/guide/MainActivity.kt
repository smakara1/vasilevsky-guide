package ru.vasilevsky.guide

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.content.pm.PackageManager
import android.webkit.GeolocationPermissions
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebResourceRequest
import android.view.ViewGroup
import android.widget.Toast

class MainActivity : Activity() {
 private lateinit var web: WebView
 private var permissionCallback: GeolocationPermissions.Callback? = null
 private var permissionOrigin: String? = null
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  web = WebView(this)
  web.layoutParams = ViewGroup.LayoutParams(-1,-1)
  setContentView(web)
  web.settings.javaScriptEnabled = true
  web.settings.domStorageEnabled = true
  web.settings.setGeolocationEnabled(true)
  web.webChromeClient = object : WebChromeClient() {
   override fun onGeolocationPermissionsShowPrompt(origin: String?, callback: GeolocationPermissions.Callback?) {
    if (origin == null || callback == null) return
    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
     callback.invoke(origin,true,false)
    } else {
     permissionOrigin = origin; permissionCallback = callback
     requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 44)
    }
   }
  }
  web.webViewClient = object : WebViewClient() {
   override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
    val uri = request.url
    if (uri.host == "smakara1.github.io") return false
    return try { startActivity(Intent(Intent.ACTION_VIEW,uri)); true } catch (e: Exception) { false }
   }
  }
  web.loadUrl("https://smakara1.github.io/vasilevsky-guide/")
 }
 override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
  super.onRequestPermissionsResult(requestCode, permissions, grantResults)
  if (requestCode == 44) {
   permissionCallback?.invoke(permissionOrigin, grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED,false)
   permissionCallback = null; permissionOrigin = null
  }
 }
 @Deprecated("Deprecated in Java")
 override fun onBackPressed() { if (web.canGoBack()) web.goBack() else super.onBackPressed() }
 override fun onDestroy() { web.destroy(); super.onDestroy() }
}
