import Foundation
import Argyle

@objc(ARArgyleSdk)
class ARArgyleSdk: RCTEventEmitter {

    static var emitter: ARArgyleSdk?

    override init() {
        super.init()
        ARArgyleSdk.emitter = self
    }

    var tokenHandler: ((String) -> ())? = nil

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
        DispatchQueue.main.sync {
            var nativeConfig = LinkConfig(
                userToken: config.value(forKey: "userToken") as! String,
                sandbox: config.value(forKey: "sandbox") as! Bool
            )

            nativeConfig.wrapperSdk = "React Native"

            (config.value(forKey: "apiHost") as? String).map { nativeConfig.baseUrl = $0 }
            (config.value(forKey: "customizationId") as? String).map { nativeConfig.customizationId = $0 }
            (config.value(forKey: "flowId") as? String).map { nativeConfig.flowId = $0 }
            (config.value(forKey: "accountId") as? String).map { nativeConfig.accountId = $0 }
            (config.value(forKey: "ddsConfig") as? String).map { nativeConfig.ddsConfig = $0 }
            (config.value(forKey: "items") as? NSArray).map {
                let items = $0.compactMap({ $0 as? String })
                nativeConfig.items = items
            }

            config.value(forKey: "onError").map {_ in
                nativeConfig.onError = { error in
                    var translatedError = "generic"

                    switch error.errorType {
                    case .INVALID_LINK_KEY:
                        translatedError = "invalid_link_key"
                        break
                    case .INVALID_USER_TOKEN:
                        translatedError = "invalid_user_token"
                        break
                    case .EXPIRED_USER_TOKEN:
                        translatedError = "expired_user_token"
                        break
                    case .INVALID_DDS_CONFIG:
                        translatedError = "invalid_dds_config"
                        break
                    case .INVALID_ITEMS:
                        translatedError = "invalid_items"
                        break
                    case .INVALID_ACCOUNT_ID:
                        translatedError = "invalid_account_id"
                        break
                    case .CALLBACK_UNDEFINED:
                        translatedError = "callback_undefined"
                        break
                    case .CARD_ISSUER_UNAVAILABLE:
                        translatedError = "card_issuer_unavailable"
                        break
                    case .DDS_NOT_SUPPORTED:
                        translatedError = "dds_not_supported"
                        break
                    case .INCOMPATIBLE_DDS_CONFIG:
                        translatedError = "incompatible_dds_config"
                        break
                    case .GIG_ITEMS_NOT_SUPPORTED:
                        translatedError = "gig_items_not_supported"
                        break
                    case .BENEFITS_ITEMS_NOT_SUPPORTED:
                        translatedError = "benefits_items_not_supported"
                    default:
                        translatedError = "generic"
                    }

                    ARArgyleSdk.emitter?.sendEvent(withName: "onError", body: ["errorType": translatedError, "errorMessage": error.errorMessage, "errorDetails": error.errorDetails])
                }
            }

            let createPayloadFromAccountData = { (it: AccountData) -> [String : Any] in
                return ["accountId": it.accountId, "userId": it.userId, "itemId": it.itemId]
            }

            config.value(forKey: "onAccountCreated").map {_ in
                nativeConfig.onAccountCreated = {
                    ARArgyleSdk.emitter?.sendEvent(withName: "onAccountCreated", body: createPayloadFromAccountData($0))
                }
            }

            config.value(forKey: "onAccountConnected").map {_ in
                nativeConfig.onAccountConnected = {
                    ARArgyleSdk.emitter?.sendEvent(withName: "onAccountConnected", body: createPayloadFromAccountData($0))
                }
            }

            config.value(forKey: "onAccountRemoved").map {_ in
                nativeConfig.onAccountRemoved = {
                    ARArgyleSdk.emitter?.sendEvent(withName: "onAccountRemoved", body: createPayloadFromAccountData($0))
                }
            }

            config.value(forKey: "onAccountError").map {_ in
                nativeConfig.onAccountError = {
                    ARArgyleSdk.emitter?.sendEvent(withName: "onAccountError", body: createPayloadFromAccountData($0))
                }
            }

            config.value(forKey: "onDDSSuccess").map {_ in
                nativeConfig.onDDSSuccess = {
                    ARArgyleSdk.emitter?.sendEvent(withName: "onDDSSuccess", body: createPayloadFromAccountData($0))
                }
            }

            config.value(forKey: "onDDSError").map {_ in
                nativeConfig.onDDSError = {
                    ARArgyleSdk.emitter?.sendEvent(withName: "onDDSError", body: createPayloadFromAccountData($0))
                }
            }

            config.value(forKey: "onUIEvent").map {_ in
                nativeConfig.onUIEvent = {
                    ARArgyleSdk.emitter?.sendEvent(withName: "onUIEvent", body: ["name": $0.name, "properties": $0.properties])
                }
            }

            config.value(forKey: "onClose").map {_ in
                nativeConfig.onClose = {
                    ARArgyleSdk.emitter?.sendEvent(withName: "onClose", body: [])
                }
            }

            config.value(forKey: "onExitIntroClicked").map {_ in
                nativeConfig.onExitIntroClicked = {
                    ARArgyleSdk.emitter?.sendEvent(withName: "onExitIntroClicked", body: [])
                }
            }

            config.value(forKey: "onFormSubmitted").map {_ in
                nativeConfig.onFormSubmitted = {
                    ARArgyleSdk.emitter?.sendEvent(withName: "onFormSubmitted", body: ["userId": $0.userId, "accountId": $0.accountId])
                }
            }

            config.value(forKey: "onDocumentsSubmitted").map {_ in
                nativeConfig.onDocumentsSubmitted = {
                    ARArgyleSdk.emitter?.sendEvent(withName: "onDocumentsSubmitted", body: ["userId": $0.userId, "accountId": $0.accountId])
                }
            }

            config.value(forKey: "onCantFindItemClicked").map {_ in
                nativeConfig.onCantFindItemClicked = {
                    ARArgyleSdk.emitter?.sendEvent(withName: "onCantFindItemClicked", body: ["query": $0])
                }
            }

            config.value(forKey: "onTokenExpired").map {_ in
                nativeConfig.onTokenExpired = {
                    self.tokenHandler = $0
                    ARArgyleSdk.emitter?.sendEvent(withName: "onTokenExpired", body: [])
                }
            }

            if let controller = UIApplication.shared.keyWindow?.rootViewController {
                ArgyleLink.start(from: controller, config: nativeConfig)
            }
        }
    }

    @objc(close)
    func close() {
        DispatchQueue.main.sync {
            ArgyleLink.close()
        }
    }

    @objc(updateToken:)
    func updateToken(newToken: String) {
        DispatchQueue.main.sync {
            if let handler = tokenHandler {
                handler(newToken)
                tokenHandler = nil
            }
        }
    }
}
