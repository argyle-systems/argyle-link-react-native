# Argyle React Native SDK

Argyle Link React Native SDK provides a way to integrate [Argyle Link](https://argyle.com/docs/link/overview) into your React Native app.

If you are looking to update Argyle Link to the newest version, navigate to [upgrade guide](UPGRADING.md).

- Required react-native version 0.68.x+

**Requirements for iOS:**

- The minimum deployment target needs to be at least 14.0

**Requirements for Android:**

- The `minSdkVersion` need to be at least `26`
- Android 8.0 (API level 26)+
- Kotlin 1.7.10+
- Android Gradle Plugin 7.2+
- Gradle 7.2+

**Important:** When using tools like Proguard to obfuscate your code, make sure to exclude Android Link SDK package (`com.argyle.*`) from the process, it may cause unexpected runtime issues otherwise. You can do this by adding this line to your `proguard-rules.pro:-keep class com.argyle. { *; }`

In case of runtime issues related to `okhttp3` library, note that Link SDK currently has a dependency of `okhttp3:4.9.2` but in case some of your dependencies use an older version (`okhttp3:3.x.x`) they may need updating to a version that also uses `okhttp3:4.x.x`. Alternatively, you may try forcing the version of the dependency as follows:
```
configurations.all {
    resolutionStrategy.force 'com.squareup.okhttp3:okhttp:4.9.x'
    resolutionStrategy.force 'com.squareup.okhttp3:okhttp-urlconnection:4.9.x'
}
```

## 1. Add the SDK dependency

[![npm](https://img.shields.io/npm/v/@argyleio/argyle-plugin-react-native?style=for-the-badge)](https://www.npmjs.com/package/@argyleio/argyle-plugin-react-native)

`$ npm install @argyleio/argyle-plugin-react-native --save` or

`$ yarn add @argyleio/argyle-plugin-react-native`

Update the Argyle pod with:

`$ cd ios`

`$ pod install`

`$ pod update`

in case not the most recent version of Argyle pod was installed.

--- 

## 2. Configure and integrate Link

### 1. Access your Link API Key

1. Log into your [Console](https://console.argyle.com/api-keys) instance
2. Navigate to the [API Keys](https://console.argyle.com/api-keys) area under the Developer menu
3. Copy your Sandbox or Production Link API Key for use in the next steps

### 2. Utilize user tokens

To prevent your API key and secret from being exposed on the front-end, [request user tokens on your server side](https://argyle.com/docs/link/user-tokens#creating-a-user-token).

### 3. Integrate Link

For detailed guidance on how to integrate our SDK please review the [example app](example/src/LinkExample.tsx), and also check out our [Link SDK Documentation](https://argyle.com/docs/link/overview).
