# Space Launch Now
A space launch tracker for Android using data from the Launch Library API. 

[View the latest releases here.](https://github.com/ItsCalebJones/SpaceLaunchNow-Android/releases)
## Changelog
#### Updated 12-31-2017
---
### Version 2.0.0 (Latest)
#### Overview
Took a lot of time to refine the current UI design, adding color and tightening up keylines wherever I could. Also added a new widget for Supporters - a list of upcoming launches.

#### Changelog
* Added a new Launch List widget for Supporters.
* Embedded YouTube videos in Launch Details screen.
* Added links to Discord, Twitter, Facebook, and Website.
* Improved feedback mechanism - now have the option to email from the app.
* Improved general UI with colors and better layouts.
* Refined layouts and function of existing widgets.
* Fixed a bug with not displaying the time in 24-hour mode on Android wear.

---
### Version 1.8.2 (Beta)
#### Overview
Fix a bug with refreshing from widget.

---
### Version 1.8.1
#### Overview
Just a few bug-fixes and cleanups from the previous release.

#### Changelog
* Fixed an issue with receiving notification payload.
* Fixed a text issues with Day/Night theme.
* Updated Vehicles section.
* Removed old code and unnecessary background syncs.
* Usability changes to make it easier to find various settings.
---
### Version 1.8.0
#### Overview
This release includes full support for new Android 8.0 features.

If you are a not a Supporter you may receive advertisements with this version. To support Android 8.0's removal of background services, notifications are now being handled by my own servers.

Sharing from the Space Launch Now app includes a link to the new https://spacelaunchnow.me to better share the launch countdown experience with your friends.

#### Changelog
* New ways to share launches with the official [Space Launch Now](https://spacelaunchnow.me) website.
* Fully reworked and customizable widgets for Supporters - more to come! 
* Remove all local alarms, services and wakelocks in favor of using [Evernotes Job](https://github.com/evernote/android-job) library.
* Improved battery life with server-side notifications with Android 8.0 support.
* Updated on-boarding and initial application configuration.
* Reduced network and local storage (90% reduced - usage.
* A plethora of bugfixes - crashes, UI, non-fatals... blah blah.
