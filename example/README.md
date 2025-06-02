# Instructions on how to run example app locally

1. Go to SDK [`package.json`](../package.json) and replace `%LINK_VERSION%` with the latest release version
2. 
```bash
cd example
npm install
```
3.
```bash
cd ..
rsync -Rr . example/node_modules/@argyleio/argyle-plugin-react-native
cd example
```
Repeat step 3 every time something is changed on SDK code.
   - Alternatively, work in `example/node_modules/@argyleio/argyle-plugin-react-native` and copy the changes to root when finished.

## Android
`npm run android`
   - If it fails, use `npm start` + press a

## iOS

1. Use XCode 16.2 due to this [bug](https://github.com/facebook/react-native/issues/50411)
   -  [How to downgrade](https://github.com/facebook/react-native/issues/50411#issuecomment-2768819763)
2. Replace boost URL in [`example/node_modules/node_modules/react-native/third-party-podspecs/boost.podspec`](node_modules/react-native/third-party-podspecs/boost.podspec) due to this [bug](https://github.com/facebook/react-native/issues/42110).
```diff
-spec.source = { :http => 'https://boostorg.jfrog.io/artifactory/main/release/1.83.0/source/boost_1_83_0.tar.bz2',
+spec.source = { :http => 'https://archives.boost.io/release/1.83.0/source/boost_1_83_0.tar.bz2',
```
3.
```bash
cd ios
rm Podfile.lock
pod install
```
4.
`npm run ios`
- If it fails, use `npm start` + press i

## Common issues

### Too many open files
```text
node:events:502
      throw er; // Unhandled 'error' event
      ^

Error: EMFILE: too many open files, watch
    at FSWatcher._handle.onchange (node:internal/fs/watchers:207:21)
Emitted 'error' event on NodeWatcher instance at:
    at FSWatcher._checkedEmitError (/Users/mind/Projects/argyle-link-react-native/example/node_modules/metro-file-map/src/watchers/NodeWatcher.js:82:12)
    at FSWatcher.emit (node:events:524:28)
    at FSWatcher._handle.onchange (node:internal/fs/watchers:213:12) {
  errno: -24,
  syscall: 'watch',
  code: 'EMFILE',
  filename: null
}
```

```bash
rm -rf node_modules
npm install
export WATCHMAN_DISABLE=true

cd ..
rsync -Rr . example/node_modules/@argyleio/argyle-plugin-react-native
cd example
```