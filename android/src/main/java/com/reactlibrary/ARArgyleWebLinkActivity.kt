package com.reactlibrary

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.FrameLayout
import android.window.OnBackInvokedDispatcher
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONObject
import java.io.File

// Hosts web Link in a WebView. Mirrors WebLinkScreen / ArgyleMainActivity in the Android SDK.
internal class ARArgyleWebLinkActivity : Activity() {

    private var webView: WebView? = null
    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private var cameraImageUri: Uri? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val session = ARArgyleWebLinkSession.current
        // Link's config lives in memory only, so a restored activity (e.g. after process death) can't resume
        if (savedInstanceState != null || session == null) {
            session?.activity = this
            finish()
            return
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }

        val container = FrameLayout(this).apply { setBackgroundColor(Color.WHITE) }
        ViewCompat.setOnApplyWindowInsetsListener(container) { view, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or
                    WindowInsetsCompat.Type.displayCutout() or
                    WindowInsetsCompat.Type.ime()
            )
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            WindowInsetsCompat.CONSUMED
        }

        val webView = WebView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            settings.javaScriptEnabled = true
            settings.allowContentAccess = true
            settings.domStorageEnabled = true
            // hardware acceleration
            setLayerType(View.LAYER_TYPE_HARDWARE, null)

            setBackgroundColor(Color.WHITE)

            addJavascriptInterface(
                object {
                    @JavascriptInterface
                    fun onActionCloseSdk() = runOnUiThread { finish() }

                    @JavascriptInterface
                    fun onCallbackHandle(name: String, data: String) = runOnUiThread {
                        try {
                            session.onCallback(name, JSONObject(data))
                        } catch (_: Throwable) {}
                    }

                    @JavascriptInterface
                    fun onAppAvailabilityCheck(data: String) = runOnUiThread {
                        handleAppAvailabilityCheck(data)
                    }

                    @JavascriptInterface
                    fun onOpenApp(data: String) = runOnUiThread {
                        handleAppOpenRequest(data)
                    }
                },
                ARArgyleWebLinkPage.BRIDGE_NAME
            )

            webChromeClient = object : WebChromeClient() {
                override fun onShowFileChooser(
                    webView: WebView?,
                    callback: ValueCallback<Array<Uri>>?,
                    fileChooserParams: FileChooserParams?
                ): Boolean {
                    launchFilePicker(callback, fileChooserParams)
                    return true
                }
            }

            loadDataWithBaseURL(session.page.baseUrl, session.page.html, "text/html", "UTF-8", null)
        }

        container.addView(webView)
        setContentView(container)
        this.webView = webView
        session.activity = this

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_DEFAULT) {
                closeLink()
            }
        }
    }

    @Deprecated("Handled by OnBackInvokedDispatcher on API 33+")
    override fun onBackPressed() {
        closeLink()
    }

    fun closeLink() {
        // web Link calls back with onActionCloseSdk once closed; finish directly if it never loaded
        webView?.evaluateJavascript("window.argyle ? (window.argyle.close(), true) : false") { result ->
            if (result != "true") {
                finish()
            }
        } ?: finish()
    }

    fun evaluateJavascript(script: String) {
        webView?.evaluateJavascript(script, null)
    }

    override fun onDestroy() {
        if (isFinishing) {
            ARArgyleWebLinkSession.end(this)
        }
        webView?.destroy()
        webView = null
        super.onDestroy()
    }

    private fun handleAppAvailabilityCheck(data: String) {
        try {
            val id = JSONObject(data).optString("id").takeIf { it.isNotEmpty() } ?: return
            val canOpenApp = packageManager.getLaunchIntentForPackage(id) != null
            evaluateJavascript(
                """
                window.dispatchEvent(
                  new CustomEvent('ArgyleSupportedNativeAppCheck', { detail: { canOpen: $canOpenApp } })
                );
                """.trimIndent()
            )
        } catch (_: Throwable) {}
    }

    private fun handleAppOpenRequest(data: String) {
        try {
            val id = JSONObject(data).optString("id").takeIf { it.isNotEmpty() } ?: return
            packageManager.getLaunchIntentForPackage(id)?.let { startActivity(it) }
        } catch (_: Throwable) {}
    }

    private fun launchFilePicker(
        callback: ValueCallback<Array<Uri>>?,
        fileChooserParams: WebChromeClient.FileChooserParams?
    ) {
        filePathCallback?.onReceiveValue(null)
        filePathCallback = callback
        cameraImageUri = null

        val fileSelectionIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            fileChooserParams?.acceptTypes?.filter { it.isNotEmpty() }?.takeIf { it.isNotEmpty() }?.let { mimeTypes ->
                // web link returns .pdf as allowed type, though android needs application/pdf
                putExtra(
                    Intent.EXTRA_MIME_TYPES,
                    mimeTypes.map { if (it == ".pdf") "application/pdf" else it }.toTypedArray()
                )
            }
        }

        val chooserIntent = Intent(Intent.ACTION_CHOOSER).apply {
            putExtra(Intent.EXTRA_INTENT, fileSelectionIntent)
            if (canUseCamera()) {
                val uri = prepareCameraPhotoUri()
                cameraImageUri = uri
                val imageCaptureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                    putExtra(MediaStore.EXTRA_OUTPUT, uri)
                }
                putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(imageCaptureIntent))
            }
        }

        try {
            startActivityForResult(chooserIntent, FILE_CHOOSER_REQUEST_CODE)
        } catch (_: Throwable) {
            filePathCallback?.onReceiveValue(null)
            filePathCallback = null
        }
    }

    @Deprecated("Activity result API requires androidx.activity")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != FILE_CHOOSER_REQUEST_CODE) {
            super.onActivityResult(requestCode, resultCode, data)
            return
        }
        val results: Array<Uri>? = if (resultCode == RESULT_OK) {
            data?.data?.let { arrayOf(it) } ?: cameraImageUri?.let { arrayOf(it) }
        } else null

        filePathCallback?.onReceiveValue(results)
        filePathCallback = null
        cameraImageUri = null
    }

    // Launching the camera throws when the host app declares the CAMERA permission without it being granted
    private fun canUseCamera(): Boolean {
        val requestedPermissions = try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS).requestedPermissions
        } catch (_: Throwable) {
            null
        }
        val declaresCamera = requestedPermissions?.contains(Manifest.permission.CAMERA) == true
        return !declaresCamera || checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun prepareCameraPhotoUri(): Uri {
        cacheDir.listFiles()?.filter { it.name.startsWith(PHOTO_NAME_PREFIX) }?.forEach { it.delete() }
        val file = File.createTempFile(PHOTO_NAME_PREFIX, ".jpeg", cacheDir)
        return FileProvider.getUriForFile(this, "$packageName.argyle.rn.provider", file)
    }

    companion object {
        private const val FILE_CHOOSER_REQUEST_CODE = 4711
        private const val PHOTO_NAME_PREFIX = "ArgyleCameraPhoto"
    }
}
