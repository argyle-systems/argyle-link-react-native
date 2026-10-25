import Foundation

// Builds the HTML page that boots web Link (argyle.web.v5.js) with the config passed from JS.
// Mirrors WebLinkViewController / WebLinkInitParamsProvider / WebCallbacksBridge in the iOS SDK.
enum ARArgyleWebLinkPage {

    static let bridgeName = "argyleNativeSdkBridge"
    static let tokenUpdateFunc = "window.argyleInterfaceOnTokenUpdate"

    static let callbacksWithData = [
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
    ]

    static let callbacksWithoutData = [
        "onClose",
        "onExitIntroClicked"
    ]

    private static let template = """
    <!DOCTYPE html>
    <html lang="en">
    <head>
    <meta charset="utf-8" />
    <!-- This is needed in order to apply proper scaling on mobile devices -->
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no, viewport-fit=cover" />
    <title>Argyle Link</title>
    </head>
    <body>
    <script src="__WEB_LINK_URL__"></script>
    <script type="text/javascript">
      window.argyle = Argyle.create({
        // ARGYLE_INIT_PARAMS
      })
      window.argyle.open()
    </script>
    </body>
    </html>
    """

    static func baseUrl(config: NSDictionary) -> URL {
        return isDevelop(config: config)
            ? URL(string: "https://plugin.develop.argyle.io/")!
            : URL(string: "https://plugin.argyle.io/")!
    }

    static func html(config: NSDictionary) -> String {
        let scriptUrl = baseUrl(config: config).appendingPathComponent("argyle.web.v5.js").absoluteString
        let params = (initParams(config: config) + callbackDefinitions(config: config)).joined(separator: "\n")
        return template
            .replacingOccurrences(of: "// ARGYLE_INIT_PARAMS", with: params)
            .replacingOccurrences(of: "__WEB_LINK_URL__", with: scriptUrl)
    }

    static func tokenUpdateScript(token: String) -> String {
        return "\(tokenUpdateFunc)(\(jsLiteral(token)));"
    }

    // Internal builds point Link at a develop API host; the web Link bundle must come from the same env.
    private static func isDevelop(config: NSDictionary) -> Bool {
        return (config["apiHost"] as? String)?.contains("develop") == true
    }

    private static func initParams(config: NSDictionary) -> [String] {
        var params: [(String, Any?)] = []
        if let apiHost = config["apiHost"] as? String {
            var apiHostComps = URLComponents(string: apiHost)
            apiHostComps?.scheme = "https"
            params.append(("apiHost", apiHostComps?.url?.absoluteString ?? ""))
        } else {
            params.append(("sandbox", config["sandbox"] as? Bool ?? false))
        }
        params.append(("userToken", config["userToken"] as? String))
        params.append(("connectUrl", config["connectUrl"] as? String))
        params.append(("flowId", (config["flowId"] as? String) ?? (config["customizationId"] as? String)))
        params.append(("items", (config["items"] as? [Any])?.compactMap { $0 as? String }))
        params.append(("ddsConfig", config["ddsConfig"] as? String))
        params.append(("accountId", config["accountId"] as? String))
        params.append(("nativeSdkContext", ["sdk": "iOS", "wrapperSdk": "React Native"]))
        params.append(("language", config["language"] as? String))
        return params.compactMap { key, value in
            value.map { "\(key): \(jsLiteral($0))," }
        }
    }

    private static func callbackDefinitions(config: NSDictionary) -> [String] {
        let callbacks = Set((config["callbacks"] as? [Any])?.compactMap { $0 as? String } ?? [])
        var defs: [String] = []
        for name in callbacksWithData where callbacks.contains(name) {
            defs.append(
                """
                \(name): (ev) => {
                    window.webkit.messageHandlers.\(bridgeName).postMessage({ name: '\(name)', data: JSON.stringify(ev) })
                },
                """
            )
        }
        for name in callbacksWithoutData where callbacks.contains(name) {
            defs.append(
                """
                \(name): () => {
                    window.webkit.messageHandlers.\(bridgeName).postMessage({ name: '\(name)', data: JSON.stringify({}) })
                },
                """
            )
        }
        if callbacks.contains("onTokenExpired") {
            defs.append(
                """
                onTokenExpired: (refreshToken) => {
                    \(tokenUpdateFunc) = refreshToken;
                    window.webkit.messageHandlers.\(bridgeName).postMessage({ name: 'onTokenExpired', data: JSON.stringify({}) })
                },
                """
            )
        }
        return defs
    }

    private static func jsLiteral(_ value: Any) -> String {
        guard let data = try? JSONSerialization.data(withJSONObject: value, options: [.fragmentsAllowed]),
              let json = String(data: data, encoding: .utf8) else {
            return "null"
        }
        return json
    }
}
