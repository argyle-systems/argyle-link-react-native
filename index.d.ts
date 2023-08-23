export interface AccountCallbackPayload {
    accountId: string
    userId: string
    itemId: string
}

export interface ErrorCallbackPayload {
    errorType: string,
    errorMessage: string
    errorDetails?: string
}

export interface FormCallbackPayload {
    accountId: string
    userId: string
}

type AccountCallback = (payload: AccountCallbackPayload) => void
type ItemNotFoundCallback = (search: string) => void
type UIEventCallback = (payload: any) => void
type ErrorCallback = (payload: ErrorCallbackPayload) => void
type TokenExpiredCallback = (updateToken: (token: string) => void) => void
type FormCallback = (payload: FormCallbackPayload) => void

export interface LinkConfig {
    /**
     * @deprecated The use of `linkKey` has been discontinued. `LinkConfig` can now be initialized without it.
     */
    linkKey?: string
    sandbox: boolean
    userToken: string
    flowId?: string
    /**
     * @deprecated Use flowId instead
     */
    customizationId?: string
    accountId?: string
    items?: string[] | null
    ddsConfig?: string | null
    onAccountCreated?: AccountCallback
    onAccountConnected?: AccountCallback
    onAccountRemoved?: AccountCallback
    onAccountError?: AccountCallback
    onDDSSuccess?: AccountCallback
    onDDSError?: AccountCallback
    onUIEvent?: UIEventCallback
    onError?: ErrorCallback
    onClose?: any
    onCantFindItemClicked?: ItemNotFoundCallback
    onExitIntroClicked?: () => void
    onTokenExpired?: TokenExpiredCallback
    onFormSubmitted?: FormCallback
    onDocumentsSubmitted?: FormCallback
}

export declare class ArgyleLink {
    static start(config: LinkConfig): void
    static close(): void
}
