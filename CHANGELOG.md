This is a changelog of features up to version 1.5.17. For more recent releases check [here](https://github.com/argyle-systems/argyle-link-react-native/releases).

1.5.17

- Improved Pay Distribution flow coverage
- Improved Pay Distribution flow error handling
- Fixed a possible crash when entering single partner flow
- Fixed a possible crash when entering Login screen

1.5.16

- Improved MFA error handling
- Minor bugfixes and improvements

1.5.15

- Improved Pay Distribution update flow
- Added initial support for new MFA types for some link items
- Bug fixes and improvements

1.5.14

- Updated Pay Distribution Allocations screen UX
- Added image selection authentication type support for some link items
- Minor bug fixes and improvements

1.5.13

- Stability improvements
- Various bugfixes

1.5.12

- Pay distribution confirmation screen UX revamp
- Multiple bugfixes

1.5.11

- Added Pay Distribution flow
- Improved data partner list loading experience
- Minor improvements and bugfixes
- Added Xcode 12.2 (Swift Compiler 5.3.1) support

1.5.10

- iOS performance improvements

1.5.9

- Multiple bug fixes
- Improved authentication form layout
- Fixed a bug where soft keyboard blurs text input fields (Android 11)

1.5.8

- Fix an issue with form validation highlighting

1.5.7

- Locked SDK build to Swift compiler 5.3.0
- Fixed possible crash on Android

1.5.6

- Fix potential crash

1.5.5

- Fix issues with dark theme on Android

1.5.3

- Support Xcode 12

1.5.2

- added introTitle and introSubtitle params
- added onAccountError callback
- fixed feedback link callback behavior and added "query" param
- bugfixes and improvements

1.5.1

- Added tappable login help button
- Various UI fixes
- Removed permissions param

**1.5.0**

- new flow that splits companies and platforms
- clicking disabled items in the list now navigates to a Disabled screen
- company login URL (if any) is shown in the Login screen
- updated company search flow
- added optional Intro screen
- now company name is loaded from Argyle Console settings (unless explicitly set via companyName param)
- added showIntroScreen and showPlatformsToggle params

1.4.5

- Added income source suggestion form
- Show full text keyboard when entering MFA code
