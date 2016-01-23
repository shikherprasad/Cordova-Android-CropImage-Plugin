package com.cordova.cropimage.plugin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;

import com.android.croplib.CropImage;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;
public class cropHandlePlugin extends CordovaPlugin{

	public static final String LOG_TAG = "cropImageHandlePlugin";
	public static final String TAG = "MainActivity";

    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";

	
    private static CallbackContext cropImageHandleContext;

	public static final int REQUEST_CODE_GALLERY      = 0x1;
	public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
	public static final int REQUEST_CODE_CROP_IMAGE   = 0x3;
    private File mFileTemp;
	@Override
	public boolean execute(final String action, final JSONArray data,final CallbackContext callbackContext) {
			cropHandlePlugin.cropImageHandleContext = callbackContext;
			
		    if ("captureImage".equals(action)) {
		    	String state = Environment.getExternalStorageState();
		    	if (Environment.MEDIA_MOUNTED.equals(state)) {
		    		mFileTemp = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME);
		    	}
		    	else {
		    		mFileTemp = new File(this.cordova.getActivity().getFilesDir(), TEMP_PHOTO_FILE_NAME);
		    	}

		    	cordova.getThreadPool().execute(new Runnable() {
					public void run() {
						 try{
							 takePicture();
						  }
						 catch(Exception e)
						 {}
						}
					});
 		   }
 		    else if ("galleryImage".equals(action)) {
 		    	String state = Environment.getExternalStorageState();
 		    	if (Environment.MEDIA_MOUNTED.equals(state)) {
 		    		mFileTemp = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME);
 		    	}
 		    	else {
 		    		mFileTemp = new File(this.cordova.getActivity().getFilesDir(), TEMP_PHOTO_FILE_NAME);
 		    	}

 	 		    	cordova.getThreadPool().execute(new Runnable() {
 						public void run() {
 							 try{
 								openGallery();
 							 }
 							 catch(Exception e)
 							 {}
 							}
 						});
 	 		} 	
 		    else if ("DefaultCropAppExist".equals(action)) {
	 		    	cordova.getThreadPool().execute(new Runnable() {
						public void run() {
							 try{
								 Intent cropIntent = new Intent("com.android.camera.action.CROP");
						            cropIntent.setType("image/*");
						            List<ResolveInfo> list = cordova.getActivity().getPackageManager().queryIntentActivities(cropIntent, 0);
						      		int size = list.size();
						      		if(size == 1)
						      		{
						      		 cropHandlePlugin.cropImageHandleContext.success();
						      		}	
						      		else
						      		{
						      			cropHandlePlugin.cropImageHandleContext.error("Not");	
						      		}	
							 }
							 catch(Exception e)
							 {
								 cropHandlePlugin.cropImageHandleContext.error("Not");	
							 }
							}
						});
	 		} 	 
		    return true;
	}
	
	
	private void takePicture() {
		  final CordovaInterface cordova = this.cordova;
	    	cordova.setActivityResultCallback(this);
	        Runnable runnable = new Runnable() {
	     public void run() {
	    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
        	Uri mImageCaptureUri = null;
        	String state = Environment.getExternalStorageState();
        	if (Environment.MEDIA_MOUNTED.equals(state)) {
        		mImageCaptureUri = Uri.fromFile(mFileTemp);
        	}
        	else {
	          	mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
        	}	
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            intent.putExtra("return-data", true);
            cordova.getActivity().startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
        } catch (ActivityNotFoundException e) {

            Log.d(TAG, "cannot take picture", e);
        }
	       };
	    };  
	    this.cordova.getActivity().runOnUiThread(runnable); 
    }

	  private void openGallery() {
		  final CordovaInterface cordova = this.cordova;
	    	cordova.setActivityResultCallback(this);
	        Runnable runnable = new Runnable() {
	            public void run() {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
	  	        photoPickerIntent.setType("image/*");
	  	        cordova.getActivity().startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY);
	            };
	        };
	        this.cordova.getActivity().runOnUiThread(runnable); 
	    }

	    private void startCropImage() {

	        Intent intent = new Intent(this.cordova.getActivity(), CropImage.class);
	        intent.putExtra(CropImage.IMAGE_PATH, mFileTemp.getPath());
	        intent.putExtra(CropImage.SCALE, true);
	        intent.putExtra(CropImage.ASPECT_X, 1);
	        intent.putExtra(CropImage.ASPECT_Y, 1);
	        this.cordova.setActivityResultCallback(this);
	        this.cordova.getActivity().startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
	    }

	    public void onActivityResult(int requestCode, int resultCode, Intent data) {
 	        Bitmap bitmap;
	        switch (requestCode) {
	            case REQUEST_CODE_GALLERY:
	                try {
	                    InputStream inputStream = this.cordova.getActivity().getContentResolver().openInputStream(data.getData());
	                    FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
	                    copyStream(inputStream, fileOutputStream);
	                    fileOutputStream.close();
	                    inputStream.close();
	                    startCropImage();
	                } catch (Exception e) {

	                    Log.e(TAG, "Error while creating temp file", e);
	                }
	                break;
	            case REQUEST_CODE_TAKE_PICTURE:
	            try {
	                 startCropImage();
	                } catch (Exception e) {

	                 Log.e(TAG, "Error while creating temp file", e);
	                }
	                break;
	            case REQUEST_CODE_CROP_IMAGE:
	            	String path ="";
	                try {
	                	 path = data.getStringExtra(CropImage.IMAGE_PATH);
		                if (path == null) {
		                    return;
		                }
		                bitmap = BitmapFactory.decodeFile(mFileTemp.getPath());
		                captureImageEditAndBackBase64(bitmap);
					} catch (Exception e) {
						// TODO: handle exception
						Toast.makeText(this.cordova.getActivity(), "Image not found,Please select other image",
								   Toast.LENGTH_LONG).show();
					}
	            	
	                break;
	        }
	     }


	    public static void copyStream(InputStream input, OutputStream output)
	            throws IOException {

	        byte[] buffer = new byte[1024];
	        int bytesRead;
	        while ((bytesRead = input.read(buffer)) != -1) {
	            output.write(buffer, 0, bytesRead);
	        }
	    }

	    public void captureImageEditAndBackBase64(Bitmap bitmap) {
	    	 int mQuality = 80;
	        ByteArrayOutputStream jpeg_data = new ByteArrayOutputStream();
	        try {
	            if (bitmap.compress(CompressFormat.JPEG, mQuality, jpeg_data)) {
	                byte[] code = jpeg_data.toByteArray();
	                byte[] output = Base64.encode(code, Base64.NO_WRAP);
	                String js_out = new String(output);
	                cropHandlePlugin.cropImageHandleContext.success(js_out);
	                js_out = null;
	                output = null;
	                code = null;
	            }
	        } catch (Exception e) {
	        	cropHandlePlugin.cropImageHandleContext.error("Error compressing image.");
	        }
	        jpeg_data = null;
	    }
	    
	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
	}

	@Override
	public void onPause(boolean multitasking) {
		super.onPause(multitasking);
	}

	@Override
	public void onResume(boolean multitasking) {
		super.onResume(multitasking);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}