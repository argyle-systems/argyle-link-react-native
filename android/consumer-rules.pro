# Methods called by web Link through the WebView JavaScript bridge
-keepclassmembers class com.reactlibrary.** {
    @android.webkit.JavascriptInterface <methods>;
}
