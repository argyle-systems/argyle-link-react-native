# Argyle React Native SDK

Argyle’s React Native SDK provides a way to integrate [Link](https://docs.argyle.com/guides/docs/argyle-link-overview) into your React Native app.

First-time installation instructions are below. To update versions, visit our [upgrade guide](https://github.com/argyle-systems/argyle-link-react-native/blob/master/UPGRADING.md).

**************************Requirements:**************************

- React Native version 0.65.0 or higher

**iOS-specific requirements:**

- The minimum deployment target needs to be at least 14.0

**Android-specific requirements:**

- If you are using tools like ProGuard to obfuscate your code…
    - Make sure to exclude the Link SDK package `com.argyle.*`
    - For example, add the following line to the bottom of your ProGuard configuration:
    
    ```
    -keep class com.argyle. { *; }
    ```
    
- In case of runtime issues related to the `okhttp3` library…
    - The Link SDK package currently has a dependency of `okhttp3:4.9.2`
    - If your dependencies use an older version (e.g. `okhttp3.3.xx`) they may need updating to a version that uses `okhttp3.4.x.x`
    - Alternatively, you can attempt to force the Link SDK dependency as follows:
    
    ```
    configurations.all {
        resolutionStrategy.force 'com.squareup.okhttp3:okhttp:4.9.x'
        resolutionStrategy.force 'com.squareup.okhttp3:okhttp-urlconnection:4.9.x'
    }
    ```
    

## Installing the SDK

1. Navigate to the directory for your React Native project
2. Install the packages from your terminal…

**…using NPM:**

`npm install @argyleio/argyle-plugin-react-native --save`

**…using yarn:**

`yarn add @argyleio/argyle-plugin-react-native`

1. Type `cd ios` to navigate to `ios` folder
2. Type `pod install` to install the Argyle pod
3. Type `pod update` to ensure the most recent Argyle pod is installed

## Implementing Link

1. Log-in to Console and retrieve a copy of your [Link key](https://console.argyle.com/link-key)
2. Create a user token:
- **New users**
    1. Create a new user by sending a **POST** to the [users endpoint](https://docs.argyle.com/guides/reference/create-a-user) of the Argyle API
    2. The response payload will include an `id` and `user_token`
    3. Save the `id` for quickly creating user tokens for this user in the future
    4. Initialize Link by passing the `user_token` as the value for the `userToken` parameter
- **Returning users**
    1. Send a **POST** request to the [user-tokens endpoint](https://docs.argyle.com/guides/reference/create-a-user-token) of the Argyle API
        - Include the `id` of the user in the request body as a JSON object in the format `{"user": "<id>"}`
    2. A `user_token` will be included in the response payload
    3. Initialize Link by passing the `user_token` as the value for the `userToken` parameter
1. Initialize Link using the Link key and user token. 

<aside>
ℹ️ Make sure the Link key matches the environment of the `sandbox` parameter.
</aside>


Example Link initialization using React Native:

```js
import { ArgyleLink } from '@argyleio/argyle-plugin-react-native';

// ...

const config = {
    linkKey: 'YOUR_LINK_KEY',
    userToken: 'USER_TOKEN',
    sandbox: true, // Set to false for production environment.
    // (Optional) Limit Link search to these Items:
    items: ['item_000001422', 'item_000025742'],
    // (Optional) Callback examples:	
    onAccountConnected: payload => console.log('onAccountConnected', payload),
    onAccountError: payload => console.log('onAccountError', payload),
    onDDSSucess: payload => console.log('onDDSSuccess', payload),
    onDDSError: payload => console.log('onDDSError', payload),
    onTokenExpired: updateToken => {
        console.log('onTokenExpired')
        // generate your new token here
        // updateToken(newToken)
    }
}

ArgyleLink.start(config)
```
