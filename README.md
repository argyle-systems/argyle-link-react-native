# Argyle React Native SDK

Argyle Link React Native SDK provides a way to integrate [Argyle Link](https://argyle.com/docs/link/overview) into your React Native app.

Link runs in a webview hosted by the plugin, loading the same web Link experience as the Argyle native iOS and Android SDKs. The plugin has no native SDK dependencies.

- Required react-native version 0.76+

**Requirements for iOS:**

- iOS 15.0+
- Xcode 16.0+

**Requirements for Android:**

Set the `minSdkVersion` in `android/app/build.gradle`:

```
android {
    compileSdk <sdk_version>
    defaultConfig {
        minSdk 26
        ...
    }
    ...
}
```

The plugin ships its own ProGuard/R8 rules, so no extra configuration is needed when minifying.

## 1. Add the SDK dependency

[![npm](https://img.shields.io/npm/v/@argyleio/argyle-plugin-react-native?style=for-the-badge)](https://www.npmjs.com/package/@argyleio/argyle-plugin-react-native)

`$ npm install @argyleio/argyle-plugin-react-native --save` or

`$ yarn add @argyleio/argyle-plugin-react-native`

Then install the iOS pods:

`$ cd ios`

`$ pod install`

--- 

## 2. Configure and integrate Link

### 1. Utilize user tokens

To prevent your API key and secret from being exposed on the front-end, [request user tokens on your server side](https://argyle.com/docs/link/user-tokens#creating-a-user-token).

### 2. Integrate Link

For detailed guidance on how to integrate our SDK please review the [example app](example/src/LinkExample.tsx), and also check out our [Link SDK Documentation](https://argyle.com/docs/link/overview).
