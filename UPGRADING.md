# React Native SDK version upgrade guide
## Learn how to upgrade to the newest Link version.
If you are looking to integrate Argyle Link for the first time navigate to [integration guide](https://github.com/argyle-systems/argyle-link-react-native#readme).

- [React Native SDK upgrade guide](#react-native-sdk-upgrade-guide)
- [Migrating to Link 4](#migrating-to-link-4)

---
## React Native SDK upgrade guide
### Review the changes in the latest version
Before you upgrade to a new version of the Link SDK, you should review the [Releases page](https://github.com/argyle-systems/argyle-link-react-native/releases). This will help you to understand any changes that you will need to make in your code as the result of an upgrade.

We strongly recommend upgrading the SDK as soon as there is a new version available. Doing so will help you to provide the best Argyle Link experience in your application.

---
### Upgrade to a new SDK version

1. Update `package.json` with the newer React Native SDK version.

2. Run `npm install` to update the SDK with the new version.

3. Run `cd ios` to navigate to `ios` folder.

4. Run `pod update` to update the SDK with the new version.

5. Make any necessary changes as a result of the upgrade.

---
# Migrating to Link 4

If you're using a previous version of Link and would like to migrate to the new version, please review updates described below, make necessary changes and then follow the usual [React Native Link SDK version upgrade instruction](#upgrade-to-a-new-sdk-version) defined above.

---
### Updated configuration attributes

Please find below a list of all configuration attributes that have been removed or changed.

---

**introSearchPlaceholder: removed**

Became redundant as search is no longer available in the [Intro screen](https://argyle.com/docs/products/link-4#intro-screen).

---
**searchScreenTitle: removed**

No longer applicable due to [Search screen](https://argyle.com/docs/products/link-4#search-screen) changes.

---
**searchScreenSubtitle: removed**

No longer applicable due to [Search screen](https://argyle.com/docs/products/link-4#search-screen) changes.

---
**dataPartners: removed**

The property has been deprecated since introduction of Link 3 in favor of `linkItems`.

---

### Updated error code

[Link Initialization error](https://argyle.com/docs/developer-tools/link-initialization-errors#) **invalid_plugin_key: renamed**

The error was renamed to `invalid_link_key`.

---
