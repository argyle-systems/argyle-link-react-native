import UIKit
import WebKit
import SafariServices

// Hosts web Link in a WKWebView. Mirrors WebLinkViewController in the iOS SDK.
class ARArgyleWebLinkViewController: UIViewController, WKScriptMessageHandler, WKUIDelegate {

    private let config: NSDictionary
    private let onCallback: (String, [String: Any]) -> Void
    private let onFinish: () -> Void

    private lazy var webView: WKWebView = {
        let configuration = WKWebViewConfiguration()
        configuration.userContentController.add(
            WeakScriptMessageHandler(self),
            name: ARArgyleWebLinkPage.bridgeName
        )
        let view = WKWebView(frame: self.view.bounds, configuration: configuration)
        view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        return view
    }()

    init(
        config: NSDictionary,
        onCallback: @escaping (String, [String: Any]) -> Void,
        onFinish: @escaping () -> Void
    ) {
        self.config = config
        self.onCallback = onCallback
        self.onFinish = onFinish
        super.init(nibName: nil, bundle: nil)
        modalPresentationStyle = .fullScreen
        view.backgroundColor = .white
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        view.addSubview(webView)

        webView.allowsBackForwardNavigationGestures = true
        webView.scrollView.contentInsetAdjustmentBehavior = .never // needed for CSS env() insets to work
        webView.uiDelegate = self

        #if DEBUG
        if #available(iOS 16.4, *) {
            webView.isInspectable = true
        }
        #endif

        webView.loadHTMLString(
            ARArgyleWebLinkPage.html(config: config),
            baseURL: ARArgyleWebLinkPage.baseUrl(config: config)
        )
    }

    func close() {
        webView.evaluateJavaScript("window.argyle ? (window.argyle.close(), true) : false") { [weak self] result, _ in
            // web Link calls back with action_close_sdk once closed; dismiss directly if it never loaded
            if (result as? Bool) != true {
                self?.closeSdk()
            }
        }
    }

    func updateToken(_ token: String) {
        webView.evaluateJavaScript(ARArgyleWebLinkPage.tokenUpdateScript(token: token))
    }

    func webView(
        _ webView: WKWebView,
        createWebViewWith configuration: WKWebViewConfiguration,
        for navigationAction: WKNavigationAction,
        windowFeatures: WKWindowFeatures
    ) -> WKWebView? {
        guard let url = navigationAction.request.url else { return nil }
        if url.scheme == "https" || url.scheme == "http" {
            let controller = SFSafariViewController(url: url)
            controller.modalPresentationStyle = .formSheet
            present(controller, animated: true)
        } else {
            UIApplication.shared.open(url)
        }
        return nil
    }

    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        guard message.name == ARArgyleWebLinkPage.bridgeName else { return }
        if let body = message.body as? String, body == "action_close_sdk" {
            closeSdk()
        } else if let body = message.body as? [String: Any],
                  let name = body["name"] as? String,
                  let data = (body["data"] as? String)?.data(using: .utf8),
                  let object = (try? JSONSerialization.jsonObject(with: data)) as? [String: Any] {
            onCallback(name, object)
        }
    }

    private func closeSdk() {
        guard !isBeingDismissed else { return }
        dismiss(animated: true)
        onFinish()
    }
}

// WKUserContentController retains its handlers, which would keep the controller alive after dismissal.
private class WeakScriptMessageHandler: NSObject, WKScriptMessageHandler {
    private weak var target: WKScriptMessageHandler?

    init(_ target: WKScriptMessageHandler) {
        self.target = target
    }

    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        target?.userContentController(userContentController, didReceive: message)
    }
}
