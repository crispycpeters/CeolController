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

import com.candkpeters.ceol.controller.CeolController2;
import com.candkpeters.ceol.device.ImageDownloaderResult;
import com.candkpeters.ceol.model.AudioStreamItem;
import com.candkpeters.ceol.model.control.PlaylistControlBase;
import com.candkpeters.chris.ceol.R;
import com.squareup.picasso.Picasso;


/**
 * Created by crisp on 27/04/2017.
 */

public class PlaylistRecyclerAdapter extends RecyclerView.Adapter<PlaylistRecyclerAdapter.AudioItemViewHolder> {
    protected static String TAG = "PlaylistRecyclerAdapter";
    private final CeolController2 controller;
    //    private List<FeedItem> feedItemList;
    private final Context mContext;

    public PlaylistRecyclerAdapter(Context context, CeolController2 controller) {
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
        if ( controller != null && controller.isBound() ) {
            Log.d(TAG, "onBindViewHolder: Requesting item " + i);
            PlaylistControlBase playlistControl = getPlaylistControl();
            if ( playlistControl != null) {
                AudioStreamItem audioItem = playlistControl.getPlaylistAudioItem(i);
                if (audioItem != null) {
                    audioItemViewHolder.setAudioItem(audioItem);
                }
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
                    .into(customViewHolder.thumbnailView);
        }

        //Setting text view titleView
        customViewHolder.titleView.setText(Html.fromHtml(feedItem.getTitle()));
*/
    }


    private PlaylistControlBase getPlaylistControl() {
        if ( controller != null && controller.isBound() ) {
            return controller.getCeolModel().inputControl.playlistControl;
        } else {
            return null;
        }
    }

    @Override
    public int getItemCount() {
        PlaylistControlBase playlistControl = getPlaylistControl();
        if ( getPlaylistControl() != null ) {
            int len = playlistControl.getPlaylistLen();
            return len;
        } else {
            return 0;
        }
    }


    class AudioItemViewHolder extends RecyclerView.ViewHolder implements  ImageDownloaderResult {
        private ImageView thumbnailView;
        private TextView titleView;
        private TextView artistView;
        private AudioStreamItem audioItem;

        protected AudioItemViewHolder(View view) {
            super(view);
            Log.d(TAG, "AudioItemViewHolder: Created");
            this.thumbnailView = (ImageView) view.findViewById(R.id.thumbnail);
            this.titleView = (TextView) view.findViewById(R.id.title);
            this.artistView = (TextView) view.findViewById(R.id.artist);
        }

        void setAudioItem(AudioStreamItem audioItem) {
            if ( audioItem != null ) {
                if ( this.audioItem == null || this.audioItem.getId() != audioItem.getId()) {
                    Log.d(TAG, "setAudioItem: Set new item: " + audioItem.toString());
                    this.audioItem = audioItem;
                    titleView.setText(audioItem.getTitle());
                    artistView.setText(audioItem.getArtist());
                }
                downloadImage( audioItem);
            }
        }
        private void downloadImage(AudioStreamItem audioItem) {
            if ( audioItem != null) {

                Picasso.with(mContext)
                        .load(String.valueOf(audioItem.getImageBitmapUri()))
                        .into(thumbnailView);

/*
                Bitmap bitmap = audioItem.getImageBitmap();
                if ( bitmap == null ) {
                    ImageDownloaderTask imageDownloaderTask;

                    thumbnailView.setImageBitmap(null);
                    imageDownloaderTask = new ImageDownloaderTask(this);
                    if ( audioItem.getImageBitmapUri() != null ) {
                        imageDownloaderTask.execute(audioItem.getImageBitmapUri().toString());
                    }
                } else {
                    thumbnailView.setImageBitmap(bitmap);
                }
*/
            }
        }

        @Override
        public void imageDownloaded(Bitmap bitmap) {
            thumbnailView.setImageBitmap(bitmap);
            audioItem.setImageBitmap(bitmap);
        }

    }

}

