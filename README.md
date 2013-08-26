geolocation-no-permission
=========================

This Android app finds out the geolocation of the user using only the wireless networks around

Permissions required:
```
  <uses-permission android:name="android.permission.INTERNET" />
  <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
  <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```
Not required:
```
  <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
or
  <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```  
Nor a GPS hardware device is needed.

The localization relies on the geolocation services used by firefox using Google hidden API service [UNDOCUMENTED]
<https://maps.googleapis.com/maps/api/browserlocation/json>

This is just a proof of concept. I discourage the use of this code for any real world application. Please take a look to the official Google Play Location Services <https://developer.android.com/google/play-services/location.html>
