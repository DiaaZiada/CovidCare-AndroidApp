# CovidCare
CovidCare is an attempt to helping to contain the disease (Covid-19) by finding out the Covid-19 status of people you have met along your days and the location of this meeting

![Logo](https://github.com/DiaaZiada/CovidCare-AndroidApp/blob/master/images/Screenshot%20from%202020-12-08%2015-04-29.png)

CovidCare System is consists of to part
* Mobile Application
* REST API Server
# Mobile Application 
this is the mobile app repo. You can find the REST API Server repo from [here](https://github.com/DiaaZiada/CovidCare-Server)

## How the App works 
### Location Permission
The app will ask your permission to access your location (the app is location based)

![permission](https://github.com/DiaaZiada/CovidCare-AndroidApp/blob/master/images/Screenshot_2020-12-08-15-42-12-635_com.google.android.packageinstaller.jpg)

### Covid Status
After installing the app you should add  your covid-19 status from the drop list:
* Healthy
* Infected
* Recovered

![Covid-19 status](https://github.com/DiaaZiada/CovidCare-AndroidApp/blob/master/images/Screenshot_2020-12-08-15-01-53-702_com.example.covidcare.jpg)

### Upload Current location
The application upload Latitude and Longitude whenever the current location is changed 
### Receive Data
The app request the API to get the info of people were in the same location and time

![INFO](https://github.com/DiaaZiada/CovidCare-AndroidApp/blob/master/images/Screenshot_2020-12-08-15-01-44-173_com.example.covidcare.jpg)

### Find Location
at each meeting you will find the location button to find the location of the meeting 

![map](https://github.com/DiaaZiada/CovidCare-AndroidApp/blob/master/images/Screenshot_2020-12-08-15-02-06-101_com.example.covidcare.jpg)

### Foreground Serivce
When you close the app or clear all app from memory the will still working in the backgourd using a foreground service with notificatin.
the notification gives two options 
* launch activit to reopen the app
* stop service to stop service from the working in background

![foreground serice](https://github.com/DiaaZiada/CovidCare-AndroidApp/blob/master/images/IMG_20201208_155519.jpg)


# DEMO
![demo](https://github.com/DiaaZiada/CovidCare-AndroidApp/blob/master/images/gifout.gif)





