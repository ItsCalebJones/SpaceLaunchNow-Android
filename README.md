# SpaceLaunchNow
A space launchCategory tracker for Android using data from the Launch Library API, the current roadmap can be viewed on [Trello](https://trello.com/b/DwLCfv7g/space-launchCategory-now).

## Data Sources

A majority of the data is currently sourced from [Launch Libary](https://launchlibrary.net/) a wonderful set of API's and Librarians that are constantly tracking and updating launches around the world. Additionally I utilize [Space Launch Now - Server](https://github.com/ItsCalebJones/SpaceLaunchNow-Server) to provide additional vehicle data and push notifications for launch times.

## Setup

To properly build this project you will need to create a few files and add a few extra string keys.

NOTE: I will not be able to provide support beyond what is inside of this setup guide. It is on you as a developer to get the correct keys and create the correct accounts to build the project.

### Keystore File
From the root of the project create a keystore.properties file used by both mobile and wear modules.

keystore.properties
```
storePassword=yourStorePassword
keyPassword=yourKeyPassword
keyAlias=yourAlias
storeFile=/full/path/to/keystore
```

### API Keys
Add a api_keys.xml to res/values.
```
<resources>
    <string name="wunderground_key">yourWeatherUndergroundKey</string>
    <string name="forecast_io_key">yourForecastIOKey</string>
    <string name="GoogleMapsKey">yourGoogleMapsKey</string>
    <string name="banner_ad_unit_id">yourAdUnitID</string>
    <string name="rsa_key">yourGoogleBillingRSAKey</string>
</resources>
```

### version.properties
In the mobile and wear modules add a version.properties or reset AI_VERSION to 0 if it exists.

```
#Thu Feb 15 11:52:58 EST 2018
AI_VERSION_CODE=3
```

### Replace google-services.json 
Replce the google-services.json with your own.

## Screenshot

![alt tag](https://raw.github.com/caman9119/SpaceLaunchNow/master/screenshot.png)

## License

This project utilizes the [MIT License](https://raw.github.com/caman9119/SpaceLaunchNow/master/LICENSE.md).
