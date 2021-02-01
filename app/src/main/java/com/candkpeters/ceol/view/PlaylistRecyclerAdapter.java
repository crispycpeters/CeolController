package com.candkpeters.ceol.view;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.candkpeters.ceol.controller.CeolController;
import com.candkpeters.ceol.model.AudioStreamItem;
import com.candkpeters.ceol.model.control.PlaylistControlBase;
import com.candkpeters.ceol.model.control.TestPlaylistControl;
import com.candkpeters.ceol.model.control.TrackControl;
import com.candkpeters.chris.ceol.R;
import com.squareup.picasso.Picasso;


/**
 * Created by crisp on 27/04/2017.
 */

public class PlaylistRecyclerAdapter extends RecyclerView.Adapter<PlaylistRecyclerAdapter.AudioItemViewHolder> {
    protected static String TAG = "PlaylistRecyclerAdapter";
    private final CeolController controller;
    //    private List<FeedItem> feedItemList;
    private final Context mContext;
    private final OnAudioItemClickListener onAudioItemClickListener;
    private TestPlaylistControl testPlaylistControl;

    public PlaylistRecyclerAdapter(Context context, CeolController controller, OnAudioItemClickListener onAudioItemClickListener) {
//        this.feedItemList = feedItemList;
        this.controller = controller;
//        this.openHoemDevice = contro.getOpenHome();
        this.mContext = context;
        this.onAudioItemClickListener = onAudioItemClickListener;
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
//            Log.d(TAG, "onBindViewHolder: Requesting item " + i);
            PlaylistControlBase playlistControl = getPlaylistControl();
            if ( playlistControl != null) {
                AudioStreamItem audioItem = playlistControl.getPlaylistAudioItem(i);
                if (audioItem != null) {
                    audioItemViewHolder.setAudioItem(audioItem);
                    boolean isCurrent = (playlistControl.getCurrentTrackPosition() == i);
//                    Log.d(TAG, "onBindViewHolder: Populating: " + i + " isCurrentTrack=" + isCurrentTrack);
                    audioItemViewHolder.setIsCurrent(isCurrent);
                    audioItemViewHolder.setListener(onAudioItemClickListener);
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
//            if ( controller.isDebugMode()) {
//                if ( testPlaylistControl == null ) {
//                    testPlaylistControl = new TestPlaylistControl(controller.getCeolModel());
//                }
//                return testPlaylistControl;
//            } else {
                return controller.getCeolModel().inputControl.playlistControl;
//            }
        } else {
            return null;
        }
    }

    private TrackControl getTrackControl() {
        if ( controller != null && controller.isBound() ) {
            return controller.getCeolModel().inputControl.trackControl;
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


    class AudioItemViewHolder extends RecyclerView.ViewHolder  {
        private final View itemView;
        private final ImageView thumbnailView;
        private final TextView titleView;
        private final TextView artistView;
        private final ImageView playstateView;
        private AudioStreamItem audioItem;
        private boolean isCurrentTrack;

        protected AudioItemViewHolder(View view) {
            super(view);
            this.itemView = view;
//            Log.d(TAG, "AudioItemViewHolder: Created");
            this.thumbnailView = view.findViewById(R.id.thumbnail);
            this.titleView = view.findViewById(R.id.title);
            this.artistView = view.findViewById(R.id.artist);
            this.playstateView = view.findViewById(R.id.playstate);
        }

        void setAudioItem(AudioStreamItem audioItem) {
            if ( audioItem != null ) {
                if ( this.audioItem == null || audioItem.getId() == 0 || this.audioItem.getId() != audioItem.getId()) {
//                    Log.d(TAG, "setAudioItem: Set new item: " + audioItem.toString());
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
                        .load(String.valueOf(audioItem.getImageBitmapUrl()))
                        .into(thumbnailView);

/*
                Bitmap bitmap = audioItem.getImageBitmap();
                if ( bitmap == null ) {
                    ImageDownloaderTask imageDownloaderTask;

                    thumbnailView.setImageBitmap(null);
                    imageDownloaderTask = new ImageDownloaderTask(this);
                    if ( audioItem.getImageBitmapUrl() != null ) {
                        imageDownloaderTask.execute(audioItem.getImageBitmapUrl().toString());
                    }
                } else {
                    thumbnailView.setImageBitmap(bitmap);
                }
*/
            }
        }

        public void setIsCurrent(boolean isCurrent) {
            if ( isCurrent) {
                playstateView.setVisibility(View.VISIBLE);
                setPlaystate();
            } else {
                playstateView.setVisibility(View.INVISIBLE);
            }
            this.isCurrentTrack = isCurrent;
        }

        private void setPlaystate() {
            TrackControl trackControl = getTrackControl();
            int playResource = 0;
            if (trackControl != null) {
                switch( trackControl.getPlayStatus()) {
                    case Unknown:
                        break;
                    case Playing:
                        playResource = R.drawable.ic_av_play;
                        break;
                    case Paused:
                        playResource = R.drawable.ic_av_pause;
                        break;
                    case Stopped:
                        playResource = R.drawable.ic_av_stop;
                        break;
                }
            }
            playstateView.setImageResource(playResource);
        }

        public void setListener(final OnAudioItemClickListener onAudioItemClickListener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onAudioItemClickListener.onAudioItemClick(audioItem, isCurrentTrack);
                }
            });
        }
    }


}

