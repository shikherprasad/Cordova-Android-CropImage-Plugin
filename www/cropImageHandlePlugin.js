var exec = require('cordova/exec');
var cameraCustomHandlePlugin = {
	getPictureAndCrop:function(successCallback, errorCallback) {
		return cordova.exec(successCallback, errorCallback, "CameraCropImage", "captureImage", []);
	},
	getGalleryPictureAndCrop:function(successCallback, errorCallback) {
		return cordova.exec(successCallback, errorCallback, "CameraCropImage", "galleryImage", []);
	},
	getDefaultCropAppExist:function(successCallback, errorCallback) {
		return cordova.exec(successCallback, errorCallback, "CameraCropImage", "DefaultCropAppExist", []);
	}
};
module.exports = cameraCustomHandlePlugin;
