[![discord](https://discordapp.com/api/guilds/380226438584074242/embed.png?style=shield)](https://discord.gg/WVfzEDW) [![Open Source Love](https://badges.frapsoft.com/os/v1/open-source.svg?v=102)](https://github.com/ellerbrock/open-source-badge/) [![Semver](http://img.shields.io/SemVer/2.6.0.png)](http://semver.org/spec/v2.0.0.html)
# SpaceLaunchNow
A space launch tracker for Android using data from the Launch Library API, the current roadmap can be viewed on [Trello](https://trello.com/b/DwLCfv7g/space-launchCategory-now).

## Data Sources

A majority of the data is currently sourced from [Launch Libary](https://launchlibrary.net/) a wonderful set of API's and Librarians that are constantly tracking and updating launches around the world. Additionally I utilize [Space Launch Now - Server](https://github.com/ItsCalebJones/SpaceLaunchNow-Server) to provide additional vehicle data and push notifications for launch times.

## Translations
Space Launch Now is now translated into five languages, huge thanks to those that have contributed. If you are interested in helping improve the translations feel free to take a look [here](https://spacelaunchnow.oneskyapp.com).

Thanks to the following translators for their work:

Fosco85, Francescog91, Ndre85f, Ajtudela, Pedroleon, SwGustav, Ogoidmatos, Ludi.vogt, Bullinger.mathis, Lukas Affolter, Castelle.arnaud, Nem.meric, Arnaud.muller1308, Jaros.jan.j, Jirkatp, Peter.handless

## Screenshot

![alt tag](https://raw.github.com/caman9119/SpaceLaunchNow/master/screenshot.png)

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
    <string name="sln_token">noNeedToChangeThis</string>
</resources>
```

### version.properties
In the mobile and wear modules add a version.properties or reset AI_VERSION to 0 if it exists.

```
#Thu Feb 15 11:52:58 EST 2018
AI_VERSION_CODE=3
```

### Replace google-services.json 
Replace the google-services.json with your own.

### AndroidX
Update `gradle.properties` to include the following lines if you have issues pertaining to AndroidX,
e.g. an Adapter or Activity doesn't match the required type:

```groovy
android.useAndroidX=true
android.enableJetifier=true
```

### Memory issues when building
If you're seeing stalled command-line builds, or warnings/errors pertaining to running out of memory
while building, you can increase the amount of memory allocated to Gradle using the following line
in `gradle.properties`

```groovy
org.gradle.jvmargs=-Xmx3g -XX:MaxPermSize=2048m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8
```

## License

This project utilizes the [MIT License](https://raw.github.com/caman9119/SpaceLaunchNow/master/LICENSE.md).
