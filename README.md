# Cordova plugin CropImage
Cordova plugin Crop Image using camera and gallery With Cordova/Phonegap android application before install this plugin must you have install [cordova camera plugin](https://www.npmjs.com/package/cordova-plugin-camera)

## Master branch:
 
 ```
cordova plugin add https://github.com/LokeshPatel/Cordova-Android-CropImage-Plugin.git
 ```
## local folder:

 ``` 
cordova plugin add Cordova-Android-CropImage-Plugin --searchpath path

```

## 1) Image take using camera method : Success return base64 image format

 ```  
     navigator.cropimage.getPictureAndCrop(function(resultBase64){
 
    // result in base64 image format 
    console.log(resultBase64);
 
    }, function(error){
    console.log(error);
    });
     
 ``` 
  
## 2) Image take using gallery method : Success return base64 image format
  ```
     navigator.cropimage.getGalleryPictureAndCrop(function(resultBase64){
    // result in base64 image format 
    console.log(resultBase64);
    }, function(error){
    console.log(error);
    });  
```

Reference: [crop image lib](https://github.com/biokys/cropimage)