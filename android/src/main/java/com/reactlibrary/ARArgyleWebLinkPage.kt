package com.reactlibrary

import com.facebook.react.bridge.ReadableMap
import org.json.JSONArray
import org.json.JSONObject

// Builds the HTML page that boots web Link (argyle.web.v5.js) with the config passed from JS.
// Mirrors WebLinkViewModel / WebCallbacksBridge in the Android SDK.
internal class ARArgyleWebLinkPage(config: ReadableMap) {

    val baseUrl = if (config.optString("apiHost")?.contains("develop") == true)
        "https://plugin.develop.argyle.io/"
    else
        "https://plugin.argyle.io/"

    val html: String

    init {
        val callbacks = config.getArrayOrNull("callbacks")?.toArrayList()?.filterIsInstance<String>()?.toSet() ?: emptySet()
        val params = initParams(config) + callbackDefinitions(callbacks)
        html = TEMPLATE.replace("// ARGYLE_INIT_PARAMS", params.joinToString("\n"))
    }

    private fun initParams(config: ReadableMap): List<String> {
        val apiHost = config.optString("apiHost")
        val envParam = if (apiHost != null)
            "apiHost" to JSONObject.quote(apiHost)
        else
            "sandbox" to (config.hasKey("sandbox") && config.getBoolean("sandbox")).toString()

        val nativeSdkContext = JSONObject()
            .put("sdk", "Android")
            .put("wrapperSdk", "React Native")

        return listOfNotNull(
            envParam,
            config.optString("userToken")?.let { "userToken" to JSONObject.quote(it) },
            config.optString("connectUrl")?.let { "connectUrl" to JSONObject.quote(it) },
            (config.optString("flowId") ?: config.optString("customizationId"))?.let { "flowId" to JSONObject.quote(it) },
            config.getArrayOrNull("items")?.let { items ->
                "items" to JSONArray(items.toArrayList().filterIsInstance<String>()).toString()
            },
            config.optString("ddsConfig")?.let { "ddsConfig" to JSONObject.quote(it) },
            config.optString("accountId")?.let { "accountId" to JSONObject.quote(it) },
            config.optString("language")?.let { "language" to JSONObject.quote(it) },
            "nativeSdkContext" to nativeSdkContext.toString()
        ).map { (key, value) -> "$key: $value," }
    }

    private fun callbackDefinitions(callbacks: Set<String>): List<String> {
        val withData = CALLBACKS_WITH_DATA.filter { it in callbacks }.map {
            "$it: (ev) => { window.$BRIDGE_NAME.onCallbackHandle('$it', JSON.stringify(ev)) },"
        }
        val withoutData = CALLBACKS_WITHOUT_DATA.filter { it in callbacks }.map {
            "$it: () => { window.$BRIDGE_NAME.onCallbackHandle('$it', JSON.stringify({})) },"
        }
        val tokenExpired = if ("onTokenExpired" in callbacks)
            listOf(
                """
                onTokenExpired: (refreshToken) => {
                    $TOKEN_UPDATE_FUNC = refreshToken;
                    window.$BRIDGE_NAME.onCallbackHandle('onTokenExpired', JSON.stringify({}))
                },
                """.trimIndent()
            )
        else
            emptyList()
        return withData + withoutData + tokenExpired
    }

    companion object {
        const val BRIDGE_NAME = "ArgyleAndroidNativeSdkBridge"
        private const val TOKEN_UPDATE_FUNC = "window.argyleInterfaceOnTokenUpdate"

        private val CALLBACKS_WITH_DATA = listOf(
            "onAccountCreated",
            "onAccountConnected",
            "onAccountRemoved",
            "onAccountError",
            "onDDSSuccess",
            "onDDSError",
            "onUIEvent",
            "onError",
            "onCantFindItemClicked",
            "onFormSubmitted",
            "onDocumentsSubmitted"
        )

        private val CALLBACKS_WITHOUT_DATA = listOf(
            "onClose",
            "onExitIntroClicked"
        )

        private val TEMPLATE = """
            <!DOCTYPE html>
            <html>
            <head>
            <meta charset="utf-8" />
            <!-- This is needed in order to apply proper scaling on mobile devices -->
            <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
            </head>
            <body>
            <script src="argyle.web.v5.js"></script>
            <script type="text/javascript">
              window.argyle = Argyle.create({
                // ARGYLE_INIT_PARAMS
              })
              window.argyle.open()
            </script>
            </body>
            </html>
        """.trimIndent()

        fun tokenUpdateScript(token: String) = "$TOKEN_UPDATE_FUNC(${JSONObject.quote(token)});"
    }
}

private fun ReadableMap.optString(key: String): String? =
    if (hasKey(key) && !isNull(key)) getString(key) else null

private fun ReadableMap.getArrayOrNull(key: String) =
    if (hasKey(key) && !isNull(key)) getArray(key) else null
