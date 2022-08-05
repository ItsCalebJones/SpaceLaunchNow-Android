# Space Launch Now
A space launch tracker for Android using data from the Launch Library API.

[View the latest releases here.](https://github.com/ItsCalebJones/SpaceLaunchNow-Android/releases)
## Changelog
---
### Version 3.14.1
#### Changelog
Fixed an issue related to clicking on items in widget, also per Google Play policy added a button to request all personal data to be deleted.
Please note - I do not collect, store or sell any personal data... all I have is anonymized meta-data that Google collects to use its SDKs (Firebase, Admob, etc)

### Version 3.13.0
#### Changelog
Small updates to the launch title to shorten long agency names, fixed some margins and improved logic for selecting agency to show in the detailed view.

### Version 3.12.0
#### Changelog
Fix an issue with sorting Spacestations.

### Version 3.11.0
#### Changelog
Whoopsie - this wasnt getting filled out.
Better conditional date formatting to help with confusions around dates that are not certain.

### Version 3.9.5
#### Changelog
Update to using SNAPI v3!

### Version 3.9.3
#### Changelog
Add mission patches to detailed view.

### Version 3.9.0
#### Changelog
Add launch and landing statistics to agencies, launchers and cores.
Add links to FlightClub.io

### Version 3.8.0
#### Changelog
Add updates from The Space Dev staff right into the app!
Find these updates on the Starship dashboard and in the launch and events details.

### Version 3.7.2
#### Changelog
Cleanup some style issues in Launcher details page.
Improve UX contrast on tab views.
Make default notification settings less spammy - by default only ten minute notifications.

### Version 3.7.1
#### Changelog
Updated in-app-purchases for 2021.
Updated translations.

### Version 3.6.6
#### Changelog
Various bug fixes and improvements.

### Version 3.6.0
#### Changelog
Add Starship development tracking page - follow along with livestreams, view Road Closures and testing events.

### Version 3.3.6
#### Changelog
Fix a crash on refreshing launches in the list.


### Version 3.3.5
#### Changelog
Fix a few themeing problems.

### Version 3.3.4
#### Changelog
* Disable Theme customization due to a performance bug in the framework.

This is an unfortunate bug that I will have to take more time to figure out a long term solution. 

### Version 3.3.2
#### Changelog
* Small patch for bug fixes and 2020 new year changes.

### Version 3.3.0
#### Changelog
* Add 'Related News' to launches via the Spaceflight News API.
* Update in-app translations.
* Fix bug with notification sounds on Android versions prior to 8.0

### Version 3.2.0
#### Changelog
* Add 'Related News' to launches via the Spaceflight News API.
* Update in-app translations.

### Version 3.1.5 
#### Changelog
* Updates to backend networking services.
* Translation improvements.
* Improvements to some UI elements.

### Version 3.1.1
#### Changelog
* Updates to translations.
* Improvements to Calendar sync - should no longer see duplicates.
* Add on-board crew information to Launch details. Check out the Saturn V launch for details.

### Version 3.1.0
#### Changelog
* Re-engineered the theme engine to support more varied styles. Unfortunately this means previous theme settings will be reset.
* Added Dark Mode for all users!
* Fix an issue where launch data for SpaceX cores wasn't loading for some users.

### Version 3.0.4 
#### Changelog
* Fixed an issue where the Japanese switch wasn't working properly.
* Fixed an issue with background workers not respecting schedules.

### Version 3.0.3
#### Changelog
* Fixed an issue where sometimes notifications ignored Do Not Disturb settings.
* Fixed an issue where launch history wasn't loading for specific SpaceX boosters.
* Added filter for deceased Astronauts
* Improved Badge icon for number of launches counter.
* Changed Navigation drawer image.

### Version 3.0.2
#### Changelog
* Added News notifications for featured articles.
* Added Webcast live notifications.
* Added option to force use of English language.
* Fixed issue with using UTC and local date format.
* Added Day to date string formats.
* Improvements to Event notifications.
* Fix a bunch of new crashes.

### Version 3.0.0
#### Overview
A large update adding Astronaut information, Spacestation data and Event notifications!

#### Changelog
* Added Astronauts - check crew onboard a flight or Spacestation.
* Added Spacestations - view history of Spacestations and expeditions to active stations.
* Added Events - track post-launch events like dockings, departures, landings, etc.
* Added additional launch notification filters.
* Various bug fixes and usability improvements.

### Version 2.6.7
#### Overview
Fix a bunch of bugs and get the latest translations.

### Version 2.6.5
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