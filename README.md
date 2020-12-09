# CovidCare
CovidCare is an attempt to help contain the disease (Covid-19) by finding out the Covid-19 status of people you have come to contact within your day and the location of these contacts.

![Logo](https://github.com/DiaaZiada/CovidCare-AndroidApp/blob/master/images/Screenshot%20from%202020-12-08%2015-04-29.png)

CovidCare System consists of
* Mobile Application
* REST API Server

# Mobile Application 
This repo is about the mobile app, you can find the server repo at [here](https://github.com/DiaaZiada/CovidCare-Server)

## How the app works 
### Location permission
The app will ask your permission to access your location (the app is location-based)

![permission](https://github.com/DiaaZiada/CovidCare-AndroidApp/blob/master/images/Screenshot_2020-12-08-15-42-12-635_com.google.android.packageinstaller.jpg)

### Covid status
After installing the app you have to add your covid-19 status from the drop list:
* Healthy
* Infected
* Recovered

![Covid-19 status](https://github.com/DiaaZiada/CovidCare-AndroidApp/blob/master/images/Screenshot_2020-12-08-15-01-53-702_com.example.covidcare.jpg)

### Auto location update
The application uploads Latitude and Longitude whenever the current location is changed.

### Receive data
The app requests the API to get information about the people who are at the same location and time.

![INFO](https://github.com/DiaaZiada/CovidCare-AndroidApp/blob/master/images/Screenshot_2020-12-08-15-01-44-173_com.example.covidcare.jpg)

### Find location
Each contact is recorded with the location and time so that you can review them later.

![map](https://github.com/DiaaZiada/CovidCare-AndroidApp/blob/master/images/Screenshot_2020-12-08-15-02-06-101_com.example.covidcare.jpg)

### Foreground serivce
When you close the app or clear all app from memory the app will still be working in the background using a foreground service with notification.
the notification gives two options 
* launch activity to reopen the app
* stop service to stop service from working in the background

![foreground serice](https://github.com/DiaaZiada/CovidCare-AndroidApp/blob/master/images/IMG_20201208_155519.jpg)


# DEMO
![demo](https://github.com/DiaaZiada/CovidCare-AndroidApp/blob/master/images/gifout.gif)





