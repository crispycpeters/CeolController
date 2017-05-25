package com.candkpeters.ceol.device;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

import retrofit.client.Response;

/**
 * Created by crisp on 25/03/2016.
 */
public class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
    private static final String TAG = "ImageDownloaderTask";
    private final WeakReference<ImageDownloaderResult> imageDownloaderResultRef;

    public ImageDownloaderTask(ImageDownloaderResult imageDownloaderResult) {
        this.imageDownloaderResultRef = new WeakReference<ImageDownloaderResult>(imageDownloaderResult);
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        return downloadBitmap(params[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        Log.d(TAG, "onPostExecute: Bitmap downloaded");
        if (isCancelled()) {
            bitmap = null;
        } else {
            ImageDownloaderResult imageDownloaderResult = imageDownloaderResultRef.get();
            if ( imageDownloaderResult != null ) {
                imageDownloaderResult.imageDownloaded(bitmap);
            }
        }

/*
        if (imageViewReference != null) {
            ImageView thumbnailView = imageViewReference.get();
            if (thumbnailView != null) {
                if (bitmap != null) {
                    thumbnailView.setImageBitmap(bitmap);
                } else {
                    Drawable placeholder = thumbnailView.getContext().getResources().getDrawable(R.drawable.placeholder);
                    thumbnailView.setImageDrawable(placeholder);
                }
            }
        }
*/
    }

 /*   private Bitmap downloadBitmap_old() {
        WebSvcApiService webSvcApiService = ceolDeviceWebSvcMonitorRef.get().webSvcApiService;

        Bitmap bitmap = null;
        InputStream inputStream = null;
        Response response = null;
        try {
            response = webSvcApiService.appGetImage();

            inputStream = response.getBody().in();
            if (inputStream != null) {
//                bitmap = BitmapFactory.decodeStream(new FlushedInputStream(inputStream));
                bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if ( bitmap != null ) {
            Log.d(TAG, "downloadBitmap: Size:" + bitmap.getWidth() + "x" + bitmap.getHeight());
        } else {
            Log.d(TAG, "downloadBitmap: Null !");
        }
        return bitmap;
    }
*/
    static private Bitmap downloadBitmap(String urlString) {
        Bitmap bitmap = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(urlString);

            URLConnection conn = url.openConnection();
            conn.connect();

            inputStream = conn.getInputStream();
            if (inputStream != null) {
                bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if ( bitmap != null ) {
//            Log.d(TAG, "downloadBitmap: Size:" + bitmap.getWidth() + "x" + bitmap.getHeight());
        } else {
            Log.d(TAG, "downloadBitmap: Null !");
        }
        return bitmap;
    }

    public boolean isRunning() {
        return (this.getStatus() == Status.RUNNING);
    }

}
