[![codebeat badge](https://codebeat.co/badges/77e2b3ca-e5d8-40e1-896f-5c4efab4ddfa)](https://codebeat.co/a/mohamed-nageh/projects/github-com-mohnage7-photoweather-master)

# PhotoWeather

## Preview 

Capture , Generate , Share. And check your history.

<img src="https://github.com/MohNage7/PhotoWeather/blob/master/art/device-2019-11-25-122956.png"  width="241" height="500" /> <img src="https://github.com/MohNage7/PhotoWeather/blob/master/art/device-2019-11-25-123708.png"   width="241" height="500" />
<img src="https://github.com/MohNage7/PhotoWeather/blob/master/art/device-2019-11-25-125029.png"  width="241" height="500" /><img src="https://github.com/MohNage7/PhotoWeather/blob/master/art/device-2019-11-25-123221.png"  width="241" height="500" />
<img src="https://github.com/MohNage7/PhotoWeather/blob/master/art/device-2019-11-25-123510.png"  width="241" height="500" />


## Project Overview
A Weather application based on openweathermap-api that let the users take a photo, add current weather information (e.g. place name, temperature, weather condition, …) as a banner overlay on top of the photo and finally, share it.

* Capture new photo by built-in camera using camera2 API
* Save photo locally
* Detect current location 
* Request openweathermap-api to get current location weather data.
* Set retrieved data in views and then convert all the ViewGroup to image.
* Share the generated image accross applications.


## Libraries 
* [Retrofit](https://square.github.io/retrofit/) for consuming REST APIs
* [Dagger2](https://github.com/google/dagger) for dependency injection 
* [Picasso](https://square.github.io/picasso/) for loading images from remote servers
* [Facebook Shimmer](https://github.com/facebook/shimmer-android) library as loading animation
* [Room](https://developer.android.com/topic/libraries/architecture/room) as presistance library. 
* [Gson](https://github.com/google/gson) as data parser.
* [RxAndroid](https://github.com/ReactiveX/RxAndroid) for reactive components.
* [Navigation component](https://developer.android.com/guide/navigation/navigation-getting-started) For navigation between fragments.



