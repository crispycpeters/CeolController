package com.candkpeters.ceol.device;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.lang.ref.WeakReference;

import retrofit.client.Response;

/**
 * Created by crisp on 25/03/2016.
 */
class ImageDownloaderTask extends AsyncTask<Void, Void, Bitmap> {
    private static final String TAG = "ImageDownloader";
    private final WeakReference<CeolDeviceWebSvcMonitor> ceolDeviceWebSvcMonitorRef;

    private Bitmap bitmap;


    public ImageDownloaderTask(CeolDeviceWebSvcMonitor ceolDeviceWebSvcMonitor) {
        this.ceolDeviceWebSvcMonitorRef = new WeakReference<CeolDeviceWebSvcMonitor>(ceolDeviceWebSvcMonitor);
    }


    @Override
    protected Bitmap doInBackground(Void... params) {
        return downloadBitmap();
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        Log.d(TAG, "onPostExecute: Bitmap downloaded");
        if (isCancelled()) {
            bitmap = null;
        } else {
            CeolDeviceWebSvcMonitor ceolDeviceWebSvcMonitor = ceolDeviceWebSvcMonitorRef.get();
            ceolDeviceWebSvcMonitor.updateDeviceImage(bitmap);
        }

/*
        if (imageViewReference != null) {
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.placeholder);
                    imageView.setImageDrawable(placeholder);
                }
            }
        }
*/
    }

    private Bitmap downloadBitmap() {
        WebSvcApiService webSvcApiService = ceolDeviceWebSvcMonitorRef.get().webSvcApiService;

        Bitmap bitmap = null;
        try {
            Response response = webSvcApiService.appGetImage();

            InputStream inputStream = response.getBody().in();
            if (inputStream != null) {
                bitmap = BitmapFactory.decodeStream(inputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public boolean isRunning() {
        return (this.getStatus() == Status.RUNNING);
    }
}
