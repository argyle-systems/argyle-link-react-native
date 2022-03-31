# Argyle Link for React Native

Argyle Link React Native SDK provides a way to integrate [Argyle Link](https://argyle.io/docs/argyle-link) into your React Native app.

If you are looking to update Argyle Link to the newest version, navigate to [upgrade guide](https://github.com/argyle-systems/argyle-link-react-native/blob/master/UPGRADING.md).

**Requirements for iOS:**

- The minimum deployment target needs to be at least 11.0
- Required react-native version 0.60.x+

**Requirements for Android:**

**Important:** When using tools like Proguard to obfuscate your code, make sure to exclude Android Link SDK package (`com.argyle.*`) from the process, it may cause unexpected runtime issues otherwise. You can do this by adding this line to your `proguard-rules.pro:-keep class com.argyle. { *; }`

In case of runtime issues related to `okhttp3` library, note that Link SDK currently has a dependency of `okhttp3:4.9.2` but in case some of your dependencies use an older version (`okhttp3:3.x.x`) they may need updating to a version that also uses `okhttp3:4.x.x`. Alternatively, you may try forcing the version of the dependency as follows:
```
configurations.all {
    resolutionStrategy.force 'com.squareup.okhttp3:okhttp:4.9.x'
    resolutionStrategy.force 'com.squareup.okhttp3:okhttp-urlconnection:4.9.x'
}
```

### 1. Adding the SDK dependency

`$ npm install @argyleio/argyle-plugin-react-native --save` or

`$ yarn add @argyleio/argyle-plugin-react-native`

Update the Argyle pod with:

`$ cd ios`

`$ pod install`

`$ pod update`

in case not the most recent version of Argyle pod was installed.

### 2. Configuring and starting the flow

```
import ArgyleSdk from '@argyleio/argyle-plugin-react-native'

// Configure the SDK before hand, once. only call ArgyleSdk.start() when the UI is needed
ArgyleSdk.loginWith("YOUR_LINK_KEY", "https://api-sandbox.argyle.com/v1", "")

ArgyleSdk.onUserCreated(res => console.log("onUserCreated", res))
ArgyleSdk.onAccountCreated(res => console.log("onAccountCreated", res))
ArgyleSdk.onAccountConnected(res => console.log("onAccountConnected", res))
ArgyleSdk.onAccountUpdated(res => console.log("onAccountUpdated", res))
ArgyleSdk.onAccountRemoved(res => console.log("onAccountRemoved", res))
ArgyleSdk.onAccountError(res => console.log('onAccountError', res))
ArgyleSdk.onPayDistributionSuccess(res => console.log('onPayDistributionSuccess', res))
ArgyleSdk.onPayDistributionError(res => console.log('onPayDistributionError', res))
ArgyleSdk.onError(error => console.log("onError", error))
ArgyleSdk.onTokenExpired(res => console.log("onTokenExpired", res))
ArgyleSdk.onClose(() => console.log('onClose'))
ArgyleSdk.onUIEvent(res => console.log('onUIEvent', JSON.stringify(res, null, 2)))

ArgyleSdk.linkItems(["uber", "lyft"]) // Can be skipped if all Link items are needed

// Launch the SDK
ArgyleSdk.start()
```

#### Closing Link programmatically

Normally, Link is closed by the user but it can also be closed by calling `ArgyleSdk.close()`
