package com.candkpeters.ceol.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.candkpeters.ceol.controller.CeolController;
import com.candkpeters.ceol.device.ImageDownloaderResult;
import com.candkpeters.ceol.device.ImageDownloaderTask;
import com.candkpeters.ceol.model.AudioItem;
import com.candkpeters.ceol.model.CeolDeviceOpenHome;
import com.candkpeters.chris.ceol.R;


/**
 * Created by crisp on 27/04/2017.
 */

public class PlaylistRecyclerAdapter extends RecyclerView.Adapter<PlaylistRecyclerAdapter.AudioItemViewHolder> {
    protected static String TAG = "PlaylistRecyclerAdapter";
    private final CeolController controller;
    //    private List<FeedItem> feedItemList;
    private final Context mContext;
//    private final CeolDeviceOpenHome openHoemDevice;

    public PlaylistRecyclerAdapter(Context context, CeolController controller) {
//        this.feedItemList = feedItemList;
        this.controller = controller;
//        this.openHoemDevice = contro.getOpenHome();
        this.mContext = context;
    }

    @Override
    public AudioItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.audioitem_row, null);
        AudioItemViewHolder viewHolder = new AudioItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AudioItemViewHolder audioItemViewHolder, int i) {
//        if ( controller.isOpenHomeOperating()) {
        if ( controller != null && controller.getCeolDevice()!=null ) {
            Log.d(TAG, "onBindViewHolder: Requesting item " + i);
            AudioItem audioItem = controller.getCeolDevice().getOpenHome().getPlaylistAudioItem(i);

            if (audioItem != null) {
                audioItemViewHolder.setAudioItem(audioItem);
            }

//        }
        }
/*
        FeedItem feedItem = feedItemList.get(i);

        //Render image using Picasso library
        if (!TextUtils.isEmpty(feedItem.getThumbnail())) {
            Picasso.with(mContext).load(feedItem.getThumbnail())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(customViewHolder.thumbnail);
        }

        //Setting text view title
        customViewHolder.title.setText(Html.fromHtml(feedItem.getTitle()));
*/
    }


    private CeolDeviceOpenHome getOpenHome() {
        if ( controller != null && controller.getCeolDevice()!=null ) {
            return controller.getCeolDevice().getOpenHome();
        } else {
            return null;
        }
    }

    @Override
    public int getItemCount() {
//        if ( controller.isOpenHomeOperating()) {
        CeolDeviceOpenHome openHome = getOpenHome();
        if ( openHome != null ) {
            int len = openHome.getPlaylistLen();
//            Log.d(TAG, "getItemCount: " + len);
            return len;
        } else {
            return 0;
        }
//        } else
//            return 0;
    }


    class AudioItemViewHolder extends RecyclerView.ViewHolder implements  ImageDownloaderResult {
        protected ImageView thumbnail;
        protected TextView title;
        protected TextView artist;
        private AudioItem audioItem;

        protected AudioItemViewHolder(View view) {
            super(view);
            Log.d(TAG, "AudioItemViewHolder: Created");
            this.thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            this.title = (TextView) view.findViewById(R.id.title);
            this.artist = (TextView) view.findViewById(R.id.artist);
        }

        public void setAudioItem(AudioItem audioItem) {
            this.audioItem = audioItem;
            Log.d(TAG, "setAudioItem: " + audioItem.toString());
            title.setText(audioItem.getTrack());
            artist.setText(audioItem.getArtist());
            downloadImage( audioItem);
        }
        private void downloadImage(AudioItem audioItem) {
            if ( audioItem != null) {

                Bitmap bitmap = audioItem.getImageBitmap();
                if ( bitmap == null ) {
                    ImageDownloaderTask imageDownloaderTask;

                    thumbnail.setImageBitmap(null);
                    imageDownloaderTask = new ImageDownloaderTask(this);
                    if ( audioItem.getImageBitmapUri() != null ) {
                        imageDownloaderTask.execute(audioItem.getImageBitmapUri().toString());
                    }
                } else {
                    thumbnail.setImageBitmap(bitmap);
                }
            }
        }

        @Override
        public void imageDownloaded(Bitmap bitmap) {
            thumbnail.setImageBitmap(bitmap);
            audioItem.setImageBitmap(bitmap);
        }

    }

}

