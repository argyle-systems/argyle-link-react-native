import Foundation
import UIKit

@objc(ARArgyleSdk)
class ARArgyleSdk: RCTEventEmitter {

    private var webLinkController: ARArgyleWebLinkViewController?

    override func supportedEvents() -> [String]! {
        return [
            "onAccountCreated",
            "onAccountConnected",
            "onAccountRemoved",
            "onAccountError",
            "onDDSSuccess",
            "onDDSError",
            "onUIEvent",
            "onError",
            "onClose",
            "onCantFindItemClicked",
            "onExitIntroClicked",
            "onTokenExpired",
            "onFormSubmitted",
            "onDocumentsSubmitted"
        ]
    }

    override static func requiresMainQueueSetup() -> Bool {
        return false
    }

    @objc(start:)
    func start(config: NSDictionary) {
        DispatchQueue.main.async {
            guard self.webLinkController == nil, let presenter = self.topViewController() else {
                return
            }
            let controller = ARArgyleWebLinkViewController(
                config: config,
                onCallback: { [weak self] name, data in
                    self?.sendEvent(withName: name, body: ARArgyleSdk.payload(name: name, data: data))
                },
                onFinish: { [weak self] in
                    self?.webLinkController = nil
                }
            )
            self.webLinkController = controller
            presenter.present(controller, animated: true)
        }
    }

    @objc(close)
    func close() {
        DispatchQueue.main.async {
            self.webLinkController?.close()
        }
    }

    @objc(updateToken:)
    func updateToken(newToken: String) {
        DispatchQueue.main.async {
            self.webLinkController?.updateToken(newToken)
        }
    }

    private func topViewController() -> UIViewController? {
        let window = UIApplication.shared.connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .flatMap { $0.windows }
            .first { $0.isKeyWindow }
        var controller = window?.rootViewController
        while let presented = controller?.presentedViewController {
            controller = presented
        }
        return controller
    }

    // Keeps the payloads emitted to JS identical to the ones of the previous native SDK based bridge.
    private static func payload(name: String, data: [String: Any]) -> Any {
        let pick = { (keys: [String]) -> [String: Any] in
            Dictionary(uniqueKeysWithValues: keys.map { ($0, data[$0] ?? NSNull()) })
        }
        switch name {
        case "onAccountCreated", "onAccountConnected", "onAccountRemoved", "onAccountError", "onDDSSuccess", "onDDSError":
            return pick(["accountId", "userId", "itemId"])
        case "onFormSubmitted", "onDocumentsSubmitted":
            return pick(["userId", "accountId"])
        case "onUIEvent":
            return pick(["name", "properties"])
        case "onError":
            return [
                "errorType": data["errorType"] as? String ?? "generic",
                "errorMessage": data["errorMessage"] as? String ?? "",
                "errorDetails": data["errorDetails"] ?? NSNull()
            ]
        case "onCantFindItemClicked":
            return ["query": data["search"] ?? NSNull()]
        case "onClose", "onExitIntroClicked":
            return [Any]()
        default:
            return [String: Any]()
        }
    }
}
