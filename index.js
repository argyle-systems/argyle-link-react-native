import {NativeModules, NativeEventEmitter} from 'react-native'

const {ARArgyleSdk} = NativeModules

const callbacks = [
  'onAccountCreated',
  'onAccountConnected',
  'onAccountRemoved',
  'onAccountError',
  'onDDSSuccess',
  'onDDSError',
  'onUIEvent',
  'onError',
  'onClose',
  'onCantFindItemClicked',
  'onExitIntroClicked',
  'onFormSubmitted',
  'onDocumentsSubmitted'
]

export class ArgyleLink {
  static eventsEmitter = new NativeEventEmitter(ARArgyleSdk)
  static listeners = {}

  static start(config) {
    const {sandbox, userToken} = config

    if (sandbox === undefined || userToken === undefined) {
      throw '[ArgyleLink] userToken and sandbox must be defined.'
    }

    callbacks.forEach(name => {
      ArgyleLink.addListener(name, payload => {
        config[name]?.(payload)
      })
    })

    ArgyleLink.addListener('onTokenExpired', () => {
      config?.onTokenExpired(newToken => {
        ARArgyleSdk.updateToken(newToken)
      })
    })

    ARArgyleSdk.start(config)
  }

  static close() {
    ARArgyleSdk.close()
  }

  static removeListenerIfAdded(key) {
    const listener = ArgyleLink.listeners[key]

    if (listener) {
      listener.remove()
      delete ArgyleLink.listeners[key]
    }
  }

  static addListener(key, callback) {
    ArgyleLink.removeListenerIfAdded(key)
    ArgyleLink.listeners[key] = ArgyleLink.eventsEmitter.addListener(
        key,
        callback
    )
  }
}
