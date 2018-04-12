# Space Launch Now
A space launch tracker for Android using data from the Launch Library API. 

[View the latest releases here.](https://github.com/ItsCalebJones/SpaceLaunchNow-Android/releases)
## Changelog
#### Updated 2-7-2017

---
### Version 2.4.0 (Latest)
#### Overview
Improved News section, more reliable notifications.

#### Changelog
* Move from Onesignal to Firebase for Notifications
* Improve the News section, stil more work to do there.
* Bug fixes.


---
### Version 2.3.0 (Latest)
#### Overview
Add French and German translations, support new launch statuses, and a news section!

#### Changelog
* Added French and German translations - join Discord to help add more!
* Added new Launch Statuses - Hold, In Flight and Partial Failure.
* Added a News section with a Space Launch News Twitter List and articles from top Spaceflight reporters.
* Fixed a few crashes and bugs.


---

### Version 2.2.4
#### Overview
Bug fixes, work to support translations.

#### Changelog
* Add menu item to hide floating action button on Next launch screen.
* Fixed a text issues on the countdown widget.
* Fixed incorrect text showing up for the vehicle agency.
* Potentially fixed wrong launch displaying for widgets.
* Integrate with Google's Firebase for Analytics and Crash logging.

Added 2018 Supporter in-app-products for those who wanted to donate again. I will be looking at small things I can add to thank those that became supporters last year and are choosing to support development another year.

---

### Version 2.2.3
#### Overview
Bug fixes, work to support translations.

#### Changelog
* Fix crash on Wear complications.
* Fix issue when no Wearable is detected.
* Fix asynchronous related crashes.

---

### Version 2.2.2
#### Overview
Another small release with bug fixes.

#### Changelog
* Add quick link to Notification settings in filter menu.
* Add additional onboarding information.
* Fix issue with wrong time showing in 24 hour mode for Android Wear watchface.
* Fix crash when switching landscape mode.
* Fix other miscellaneous crashes.
* Improve Notifications bundling.

---
### Version 2.2.0
#### Overview
Added a few small features and fixed some crashes found during the craziness of Falcon Heavy.

#### Changelog
* Add Do Not Disturb hours for pre-Marshmallow devices.
* Add bundled notifications.
* Add option to hide UTC time on Android Wear watchface.
* Add background image complication for Android Wear.
* Fix UI bugs and crashes.

---
### Version 2.1.6

#### Overview
Quick bug fix release - addressing a few issues.

#### Changelog
* Added an option to hide launches that have an unconfirmed date.
* Fixed an issue with some users not receiving notifications.
* Fixed issue where the incorrect launcher data was being shown.
* Fixed a few crashes.

---
### Version 2.1.5
#### Overview
I've been wanting to revisit Android Wear - had a chance to sit down and work through it. This is just the initial release, let me know if you have suggestions.

#### Changelog
* Added a standalone Android Wear 2.0 app for tracking launches from your watch!
* Added a 'Next Launch' complication for Android Wear watchfaces.
* Improved legacy Space Launch Now watchface.

---
### Version 2.0.0
#### Overview
Took a lot of time to refine the current UI design, adding color and tightening up keylines wherever I could. Also added a new widget for Supporters - a list of upcoming launches.

#### Changelog
* Added a new Launch List widget for Supporters.
* Embedded YouTube videos in Launch Details screen.
* Added option to donate via BTC, ETH, and LTC.
* Added links to Discord, Twitter, Facebook, and Website.
* Improved feedback mechanism - now have the option to email from the app.
* Improved general UI with colors and better layouts.
* Refined layouts and function of existing widgets.
* Fixed a bug with not displaying the time in 24-hour mode on Android wear.

---
### Version 1.8.2
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
