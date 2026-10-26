package com.reactlibrary

import android.content.Intent
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule
import org.json.JSONArray
import org.json.JSONObject

class ARArgyleSdkModule(context: ReactApplicationContext) : ReactContextBaseJavaModule(context) {
    private var reactContext: ReactApplicationContext = context

    override fun getName(): String {
        return "ARArgyleSdk"
    }

    private fun sendEvent(eventName: String, params: WritableMap) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit(eventName, params)
    }

    @ReactMethod
    fun start(config: ReadableMap) {
        val activity = reactApplicationContext.currentActivity ?: return
        val session = ARArgyleWebLinkSession(
            page = ARArgyleWebLinkPage(config),
            onCallback = { name, data -> sendEvent(name, payload(name, data)) }
        )
        if (!ARArgyleWebLinkSession.begin(session)) {
            return
        }
        try {
            activity.startActivity(Intent(activity, ARArgyleWebLinkActivity::class.java))
        } catch (e: Throwable) {
            ARArgyleWebLinkSession.cancel(session)
        }
    }

    @ReactMethod
    fun close() {
        UiThreadUtil.runOnUiThread {
            ARArgyleWebLinkSession.current?.activity?.closeLink()
        }
    }

    @ReactMethod
    fun updateToken(newToken: String) {
        UiThreadUtil.runOnUiThread {
            ARArgyleWebLinkSession.current?.activity?.evaluateJavascript(
                ARArgyleWebLinkPage.tokenUpdateScript(newToken)
            )
        }
    }

    @ReactMethod
    fun addListener(eventName: String) {
        // required to comply with NativeEventEmitter interface
    }

    @ReactMethod
    fun removeListeners(count: Int) {
        // required to comply with NativeEventEmitter interface
    }

    private fun payload(name: String, data: JSONObject): WritableMap {
        val params = Arguments.createMap()
        when (name) {
            "onAccountCreated", "onAccountConnected", "onAccountRemoved", "onAccountError",
            "onDDSSuccess", "onDDSError" -> {
                params.putString("accountId", data.optStringOrNull("accountId"))
                params.putString("userId", data.optStringOrNull("userId"))
                params.putString("itemId", data.optStringOrNull("itemId"))
            }
            "onFormSubmitted", "onDocumentsSubmitted" -> {
                params.putString("userId", data.optStringOrNull("userId"))
                params.putString("accountId", data.optStringOrNull("accountId"))
            }
            "onUIEvent" -> {
                params.putString("name", data.optStringOrNull("name"))
                params.putMap("properties", data.optJSONObject("properties")?.let { writableMapOf(it) })
            }
            "onError" -> {
                params.putString("errorType", data.optStringOrNull("errorType") ?: "generic")
                params.putString("errorMessage", data.optStringOrNull("errorMessage") ?: "")
                params.putString("errorDetails", data.optStringOrNull("errorDetails"))
            }
            "onCantFindItemClicked" -> {
                params.putString("query", data.optStringOrNull("search"))
            }
        }
        return params
    }
}

private fun JSONObject.optStringOrNull(key: String): String? =
    if (has(key) && !isNull(key)) optString(key) else null

fun writableMapOf(values: JSONObject): WritableMap {
    val map = Arguments.createMap()
    for (key in values.keys()) {
        when (val value = values.get(key)) {
            JSONObject.NULL -> map.putNull(key)
            is Boolean -> map.putBoolean(key, value)
            is Int -> map.putInt(key, value)
            is Number -> map.putDouble(key, value.toDouble())
            is String -> map.putString(key, value)
            is JSONObject -> map.putMap(key, writableMapOf(value))
            is JSONArray -> map.putArray(key, writableArrayOf(value))
        }
    }
    return map
}

fun writableArrayOf(values: JSONArray): WritableArray {
    val array = Arguments.createArray()
    for (i in 0 until values.length()) {
        when (val value = values.get(i)) {
            JSONObject.NULL -> array.pushNull()
            is Boolean -> array.pushBoolean(value)
            is Int -> array.pushInt(value)
            is Number -> array.pushDouble(value.toDouble())
            is String -> array.pushString(value)
            is JSONObject -> array.pushMap(writableMapOf(value))
            is JSONArray -> array.pushArray(writableArrayOf(value))
        }
    }
    return array
}
