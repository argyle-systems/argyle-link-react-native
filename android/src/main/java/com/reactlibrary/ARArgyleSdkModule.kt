package com.reactlibrary

import android.content.Context
import com.argyle.AccountData
import com.argyle.LinkConfig
import com.argyle.ArgyleLink
import com.argyle.Language
import com.argyle.LinkError
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule

class ARArgyleSdkModule(context: ReactApplicationContext) : ReactContextBaseJavaModule(context) {
    private var reactContext: ReactApplicationContext = context
    private var tokenHandler: ((String) -> Unit)? = null

    override fun getName(): String {
        return "ARArgyleSdk"
    }

    private fun sendEvent(eventName: String, params: WritableMap) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit(eventName, params)
    }

    @ReactMethod
    fun start(config: ReadableMap) {
        val nativeConfig = LinkConfig(
            userToken = config.getString("userToken")!!,
            sandbox = config.getBoolean("sandbox")
        )

        nativeConfig.wrapperSdk = "React Native"
        config.getString("apiHost")?.let { nativeConfig.apiHost = it }
        config.getString("customizationId")?.let { nativeConfig.customizationId = it }
        config.getString("flowId")?.let { nativeConfig.flowId = it }
        config.getString("accountId")?.let { nativeConfig.accountId = it }
        config.getString("ddsConfig")?.let { nativeConfig.ddsConfig = it }
        config.getString("language")?.let {
            nativeConfig.language = try {
                Language.valueOf(it)
            } catch (e: IllegalArgumentException) {
                Language.EN
            }
        }
        config.getArray("items")?.let {
            nativeConfig.items = it.toArrayList().filterIsInstance<String>()
        }

        if (config.hasKey("onError")) {
            nativeConfig.onError = {
                val translatedError = when (it.errorType) {
                    LinkError.Type.INVALID_LINK_KEY -> "invalid_link_key"
                    LinkError.Type.INVALID_USER_TOKEN -> "invalid_user_token"
                    LinkError.Type.INVALID_DDS_CONFIG -> "invalid_dds_config"
                    LinkError.Type.INVALID_ITEMS -> "invalid_items"
                    LinkError.Type.INVALID_ACCOUNT_ID -> "invalid_account_id"
                    LinkError.Type.CALLBACK_UNDEFINED -> "callback_undefined"
                    LinkError.Type.CARD_ISSUER_UNAVAILABLE -> "card_issuer_unavailable"
                    LinkError.Type.EXPIRED_USER_TOKEN -> "expired_user_token"
                    LinkError.Type.DDS_NOT_SUPPORTED -> "dds_not_supported"
                    LinkError.Type.INCOMPATIBLE_DDS_CONFIG -> "incompatible_dds_config"
                    LinkError.Type.GIG_ITEMS_NOT_SUPPORTED -> "gig_items_not_supported"
                    LinkError.Type.BENEFITS_ITEMS_NOT_SUPPORTED -> "benefits_items_not_supported"
                    LinkError.Type.EXCLUDED_ITEM -> "excluded_item"
                    LinkError.Type.EMBEDDED_EMPLOYER_INACCESSIBLE -> "embedded_employer_inaccessible"
                    LinkError.Type.GENERIC -> "generic"
                }

                val params = Arguments.createMap()
                params.putString("errorType", translatedError)
                params.putString("errorMessage", it.errorMessage)
                params.putString("errorDetails", it.errorDetails)
                sendEvent("onError", params)
            }
        }

        val createPayloadFromAccountData = { it: AccountData ->
            val params = Arguments.createMap()
            params.putString("accountId", it.accountId)
            params.putString("userId", it.userId)
            params.putString("itemId", it.itemId)
            params
        }

        if (config.hasKey("onAccountCreated")) {
            nativeConfig.onAccountCreated = {
                sendEvent("onAccountCreated", createPayloadFromAccountData(it))
            }
        }

        if (config.hasKey("onAccountConnected")) {
            nativeConfig.onAccountConnected = {
                sendEvent("onAccountConnected", createPayloadFromAccountData(it))
            }
        }

        if (config.hasKey("onAccountRemoved")) {
            nativeConfig.onAccountRemoved = {
                sendEvent("onAccountRemoved", createPayloadFromAccountData(it))
            }
        }

        if (config.hasKey("onAccountError")) {
            nativeConfig.onAccountError = {
                sendEvent("onAccountError", createPayloadFromAccountData(it))
            }
        }

        if (config.hasKey("onDDSSuccess")) {
            nativeConfig.onDDSSuccess = {
                sendEvent("onDDSSuccess", createPayloadFromAccountData(it))
            }
        }

        if (config.hasKey("onDDSError")) {
            nativeConfig.onDDSError = {
                sendEvent("onDDSError", createPayloadFromAccountData(it))
            }
        }

        if (config.hasKey("onUIEvent")) {
            nativeConfig.onUIEvent = {
                val params = Arguments.createMap()
                params.putString("name", it.name)
                params.putMap(
                    "properties",
                    it.properties?.let { properties -> writableMapOf(properties) })
                sendEvent("onUIEvent", params)
            }
        }

        if (config.hasKey("onClose")) {
            nativeConfig.onClose = {
                val params = Arguments.createMap()
                sendEvent("onClose", params)
            }
        }

        if (config.hasKey("onExitIntroClicked")) {
            nativeConfig.onExitIntroClicked = {
                val params = Arguments.createMap()
                sendEvent("onExitIntroClicked", params)
            }
        }

        if (config.hasKey("onFormSubmitted")) {
            nativeConfig.onFormSubmitted = {
                val params = Arguments.createMap()
                params.putString("userId", it.userId)
                params.putString("accountId", it.accountId)
                sendEvent("onFormSubmitted", params)
            }
        }

        if (config.hasKey("onDocumentsSubmitted")) {
            nativeConfig.onDocumentsSubmitted = {
                val params = Arguments.createMap()
                params.putString("userId", it.userId)
                params.putString("accountId", it.accountId)
                sendEvent("onDocumentsSubmitted", params)
            }
        }

        if (config.hasKey("onCantFindItemClicked")) {
            nativeConfig.onCantFindItemClicked = {
                val params = Arguments.createMap()
                params.putString("query", it)
                sendEvent("onCantFindItemClicked", params)
            }
        }

        if (config.hasKey("onTokenExpired")) {
            nativeConfig.onTokenExpired = {
                tokenHandler = it
                val params = Arguments.createMap()
                sendEvent("onTokenExpired", params)
            }
        }

        ArgyleLink.start(context = reactApplicationContext.currentActivity as Context, config = nativeConfig)
    }

    @ReactMethod
    fun close() {
        ArgyleLink.close()
    }

    @ReactMethod
    fun updateToken(newToken: String) {
        tokenHandler?.invoke(newToken)
        tokenHandler = null
    }

    @ReactMethod
    fun addListener(eventName: String) {
        // required to comply with NativeEventEmitter interface
    }

    @ReactMethod
    fun removeListeners(count: Int) {
        // required to comply with NativeEventEmitter interface
    }
}

fun writableMapOf(values: Map<String, Any>): WritableMap {
    val map = Arguments.createMap()
    for ((key, value) in values) {
        when (value) {
            is Boolean -> map.putBoolean(key, value)
            is Double -> map.putDouble(key, value)
            is Int -> map.putInt(key, value)
            is String -> map.putString(key, value)
            is Map<*, *> -> map.putMap(key, writableMapOf(mapToJsonMap(value)))
            is Array<*> -> map.putArray(key, writableArrayOf(mapToJsonArray(value)))
            else -> throw IllegalArgumentException("Unsupported value type ${value::class.java.name} for key [$key]")
        }
    }
    return map
}

fun writableArrayOf(values: List<Any>): WritableArray {
    val array = Arguments.createArray()
    for (value in values) {
        when (value) {
            is Boolean -> array.pushBoolean(value)
            is Double -> array.pushDouble(value)
            is Int -> array.pushInt(value)
            is String -> array.pushString(value)
            is Map<*, *> -> array.pushMap(writableMapOf(mapToJsonMap(value)))
            is Array<*> -> array.pushArray(writableArrayOf(mapToJsonArray(value)))
            else -> throw IllegalArgumentException("Unsupported type ${value::class.java.name}")
        }
    }
    return array
}

fun mapToJsonMap(value: Map<*, *>): Map<String, Any> {
    val result: MutableMap<String, Any> = mutableMapOf()
    for ((k, v) in value) {
        if (k is String && v is Any) {
            result[k] = v
        }
    }
    return result
}

fun mapToJsonArray(values: Array<*>): List<Any> {
    val result: MutableList<Any> = mutableListOf()
    for (v in values) {
        if (v is Any) {
            result.add(v)
        }
    }
    return result
}
