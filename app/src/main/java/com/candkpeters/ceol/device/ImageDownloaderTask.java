package com.candkpeters.ceol.device;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.candkpeters.ceol.model.AudioStreamItem;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by crisp on 25/03/2016.
 */
public class ImageDownloaderTask extends AsyncTask<AudioStreamItem, Void, AudioStreamItem> {
    private static final String TAG = "ImageDownloaderTask";
    private final WeakReference<ImageDownloaderResult> imageDownloaderResultRef;

    public ImageDownloaderTask(ImageDownloaderResult imageDownloaderResult) {
        this.imageDownloaderResultRef = new WeakReference<>(imageDownloaderResult);
    }

    @Override
    protected AudioStreamItem doInBackground(AudioStreamItem... params) {
        return downloadBitmap(params[0]);
    }

    @Override
    protected void onPostExecute(AudioStreamItem audioStreamItem) {
        Log.d(TAG, "onPostExecute: Bitmap downloaded");
        if (!isCancelled()) {
            ImageDownloaderResult imageDownloaderResult = imageDownloaderResultRef.get();
            if ( imageDownloaderResult != null ) {
                imageDownloaderResult.imageDownloaded(audioStreamItem);
            }
        }
    }

    static private AudioStreamItem downloadBitmap(AudioStreamItem item) {

        if ( item.getImageBitmapUrl() != null ) {
            String urlString = item.getImageBitmapUrl().toString();
            Bitmap bitmap = null;
            InputStream inputStream;
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
            if (bitmap == null) {
                Log.d(TAG, "downloadBitmap: Null !");
            }
            item.setImageBitmap(bitmap);
        }
        return item;
    }

    public boolean isRunning() {
        return (this.getStatus() == Status.RUNNING);
    }

}
