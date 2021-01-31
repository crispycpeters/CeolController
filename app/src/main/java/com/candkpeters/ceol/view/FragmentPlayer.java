package com.candkpeters.ceol.view;

/**
 * Created by crisp on 01/06/2017.
 */

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.candkpeters.ceol.controller.CeolController;
import com.candkpeters.ceol.model.AudioStreamItem;
import com.candkpeters.ceol.model.CeolModel;
import com.candkpeters.ceol.model.ObservedControlType;
import com.candkpeters.ceol.model.OnControlChangedListener;
import com.candkpeters.ceol.model.StreamingStatus;
import com.candkpeters.ceol.model.control.ControlBase;
import com.candkpeters.ceol.model.control.ProgressControl;
import com.candkpeters.ceol.model.control.TrackControl;
import com.candkpeters.chris.ceol.R;

/**
 * Section 1 - Ceol player control.
 */
public class FragmentPlayer extends Fragment implements View.OnClickListener {
    private static final String TAG="FragmentPlayer";
    CeolController ceolController;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public FragmentPlayer() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tablayout_player, container, false);

        Button infoB = rootView.findViewById(R.id.infoB);
        infoB.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Entering");
        ceolController = new CeolController(getContext());

        ceolController.create();

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart in player fragment: ");

        ceolController.start( new OnControlChangedListener() {

            @Override
            public void onControlChanged(final CeolModel ceolModel, final ObservedControlType observedControlType, final ControlBase controlBase) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        switch (observedControlType) {

                            case Connection:
                            case Power:
                            case Audio:
                            case Input:
                            case Track:
                                updateTrackViews();
                                break;
                            case Navigator:
                            case Playlist:
                                break;
                            case Progress:
                                updateProgress((ProgressControl)controlBase);
                                break;
                            case All:
                                updateProgress(ceolModel.progressControl);
                                updateTrackViews();
                        }
                    }
                });

            }
        }
        );

        SeekBar seekBar = (SeekBar) (getView().findViewById(R.id.trackSeekBar));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int i = seekBar.getProgress();
                ceolController.setTrackPosition(i);
            }
        });


    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Entering");
//        ceolController.activityOnStop();
        ceolController.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ceolController.destroy();
    }

    public void updateTrackViews() {
        if ( ceolController.isBound()) {
            Log.d(TAG, "updateTrackViews: Is bound");
            CeolModel ceolModel = ceolController.getCeolModel();
            TrackControl trackControl = ceolController.getCeolModel().inputControl.trackControl;

            AudioStreamItem audioStreamItem = trackControl.getAudioItem();
            setTextViewText(R.id.textTrack, audioStreamItem.getTitle());
            setTextViewText(R.id.textArtist, audioStreamItem.getArtist());
            setTextViewText(R.id.textAlbum, audioStreamItem.getAlbum());
            setTextViewText(R.id.playStatus, trackControl.getPlayStatus().toString());

            setImage(audioStreamItem);
//            viewUpdateForAllNotifications();
            String currString = Long.toString(System.currentTimeMillis() % 100);
            setTextViewText(R.id.textUpdate, currString);

            View tunerPanel = getView().findViewById(R.id.tunerPanel);
            View netPanel = getView().findViewById(R.id.netPanel);
            if (tunerPanel != null && netPanel != null) {
                switch (ceolModel.inputControl.getSIStatus()) {
                    case CD:
                    case AnalogIn:
//                    case Unknown:
//                        tunerPanel.setVisibility(View.INVISIBLE);
//                        netPanel.setVisibility(View.INVISIBLE);
//                        break;
                    case Tuner:
                        tunerPanel.setVisibility(View.VISIBLE);
                        netPanel.setVisibility(View.GONE);

                        AudioStreamItem audioTunerItem = trackControl.getAudioItem();
                        setTextViewText(R.id.tunerName, audioTunerItem.getTitle());
                        setTextViewText(R.id.tunerFrequency, audioTunerItem.getFrequency());
                        setTextViewText(R.id.tunerUnits, audioTunerItem.getUnits());
                        setTextViewText(R.id.tunerBand, audioTunerItem.getBand());
                        break;
                    case DigitalIn1:
                    case DigitalIn2:
                    case IRadio:
                    case NetServer:
                    case Bluetooth:
                    case Ipod:
                    case Spotify:
                    default:
                        tunerPanel.setVisibility(View.GONE);
                        netPanel.setVisibility(View.VISIBLE);
                        break;
                }
            }


        }
        else {
            Log.d(TAG, "updateTrackViews: Not bound");
        }
    }

    private void setImage(AudioStreamItem audioStreamItem) {
        ImageView imageV = (ImageView) (getView().findViewById(R.id.imageTrack));
        if (imageV != null) {

/*
            Picasso.with(getContext())
                    .load(String.valueOf(audioStreamItem.getImageBitmapUrl()))
                    .stableKey(audioStreamItem.getKey())
                    .into(imageV);
*/

            imageV.setImageBitmap(audioStreamItem.getImageBitmap());
        }
    }

    private void setTextViewText(int tunerName2, String name) {
        TextView tunerName = (TextView) (getView().findViewById(tunerName2));
        if (tunerName != null) tunerName.setText(name);
    }

    private void updateProgress(ProgressControl progressControl) {
        CeolModel ceolModel = ceolController.getCeolModel();
        updateSeekbar(ceolModel);
    }

    private void updateSeekbar(CeolModel ceolModel) {

        SeekBar seekBar = (SeekBar) (getView().findViewById(R.id.trackSeekBar));
        TextView progressT = (TextView)(getView().findViewById(R.id.trackProgressT));
        TextView reverseProgressT = (TextView)(getView().findViewById(R.id.trackReverseProgressT));

        if ( ceolModel.inputControl.getStreamingStatus() == StreamingStatus.OPENHOME ) {
            int progressSize = (int)ceolModel.inputControl.trackControl.getAudioItem().getDuration();
            int progress = (int)ceolModel.progressControl.getProgress();

            progressT.setText(DateUtils.formatElapsedTime(progress));
            reverseProgressT.setText(DateUtils.formatElapsedTime(progressSize-progress));
            if (seekBar != null) {
                seekBar.setMax(progressSize);
                seekBar.setProgress(progress);
            }
        }  else {

        }
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "buttonClick: " + v.getId());

        switch( v.getId()) {
            case R.id.infoB:
                FragmentManager fm = getActivity().getSupportFragmentManager();
                InfoFragment infoFragment = new InfoFragment();
                infoFragment.show(fm,"infoFragment");
                break;
            default:
                break;
        }

    }


/*
    private void viewUpdateForAllNotifications() {
        String currString = Long.toString(System.currentTimeMillis() % 100);
        setTextViewText(R.id.textUpdate, currString);
        hideWaitingDialog();
    }
*/


}
