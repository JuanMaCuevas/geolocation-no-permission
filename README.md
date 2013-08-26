geolocation-no-permission
=========================

This app finds out the Geolocation (Lat,Lng) of the user using only the wireless network

Requirements:
  <uses-permission android:name="android.permission.INTERNET" />
  <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />

Doesn't require:

  <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
  or
  <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
  
Nor a GPS hardware device.
The localization replies on this Google API service [UNDOCUMENTED]
https://maps.googleapis.com/maps/api/browserlocation/json
