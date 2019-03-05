# Space Launch Now
A space launch tracker for Android using data from the Launch Library API.

[View the latest releases here.](https://github.com/ItsCalebJones/SpaceLaunchNow-Android/releases)
## Changelog
#### Updated 6-11-2018
---
### Version 3.0.0
#### Overview
A large update adding Astronaut information, Spacestation data and Event notifications!

#### Changelog
* Added Astronauts - check crew onboard a flight or Spacestation.
* Added Spacestations - view history of Spacestations and expeditions to active stations.
* Added Events - track post-launch events like dockings, departures, landings, etc.
* Added additional launch notification filters.
* Various bug fixes and usability improvements.

### Version 2.7.0
#### Overview
Added notifications for launch failures and partial-failure, bug fixes!

#### Changelog
* Added notifications for more then just a 'success'.
* Fixed a bug where that was sharing the wrong URL.
* Fixed a few bugs that caused crashes.


### Version 2.6.3
#### Overview
Add In Flight and Launch Success notification types and other small fixes.

#### Changelog
* Add new notifications for in-flight and success.
* Redid the notification system to make it easier for adding new types.
* Made it easier to edit notification channels on Android 8 and above.
* Links to Translator application directly in the application.

##### HOTFIX
* Changes to eliminate unintended wakelocks and issues with background crashes.
* Fix an issue preventing closing the app with the back button.

### Version 2.6.2
#### Overview
Finally, SpaceX landing information! It's a long time coming but its finally here. 

#### Changelog
* Add labels for SpaceX landing information.
* Add labels to indicate status of the launch.
* Add labels to indicate intended orbit.
* Fix a whole lotta  bugs.
* Remove the Google Map view from the home page due to performance issues rendering multiple maps.

### Version 2.6.0
#### Overview
Change over to using my own server to support future Blue Origin launches and additional data about SpaceX launches and LANDINGS. Coming soon!

#### Changelog
* Iconography changes to improve user experience.
* Added a 'View More' buttons for launcherConfigs and agencies.
* Moved away from using Launch Library API to our own server.

Special shoutout to the translators that have helped fully translate Space Launch Now into over five languages!

Fosco85, Francescog91, Ndre85f, Ajtudela, Pedroleon, SwGustav, Ogoidmatos, Ludi.vogt, Bullinger.mathis, Lukas Affolter, Castelle.arnaud, Nem.meric, Arnaud.muller1308, Jaros.jan.j, Jirkatp, Peter.handless

---
### Version 2.5.4
#### Overview
Slight changes to handling GDPR users and their choices. Starting work to support a UI revamp.

#### Changelog
* New support email address added.
* Removed the 'Missions' page to make room for new UI changes.
* Changes to Supporter page.
* Fixed a few random crashes.
---
### Version 2.5.0
#### Overview
Support for EU's General Data Protection Regulations, Space Launch Now will now ask for consent before showing personalized advertisements. If advertisements aren't your thing, you can always support Space Launch Now with an in-app-purchase.

#### Changelog
* Require consent globally before showing personalized advertisements.
* Bug Fixes

---
### Version 2.4.0
#### Overview
Improved News section, more reliable notifications.

#### Changelog
* Move from Onesignal to Firebase for Notifications
* Improve the News section, still more work to do there.
* Bug fixes.


---
### Version 2.3.0
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
* Fixed incorrect text showing up for the vehicle agency_menu.
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
* Fixed issue where the incorrect launcherConfig data was being shown.
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
