package com.candkpeters.ceol.view;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.candkpeters.ceol.controller.CeolController;
import com.candkpeters.ceol.model.AudioStreamItem;
import com.candkpeters.ceol.model.CeolModel;
import com.candkpeters.ceol.model.control.TrackControl;
import com.candkpeters.chris.ceol.R;

public class InfoFragment extends DialogFragment implements View.OnClickListener{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "3";
    private CeolController ceolController;
    CeolModel ceolModel ;
    TrackControl trackControl ;

    public InfoFragment() {
    }

    private void setTextViewText(View parentView, int tunerName2, String name) {
        TextView textView = (TextView) parentView.findViewById(tunerName2);
        if (textView != null) textView.setText(name);
    }

    private String getTextViewText( int tunerName2) {
        TextView textView = (TextView) getView().findViewById(tunerName2);
        if (textView != null) {
            return textView.getText().toString();
        } else {
            return "";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.info_fragment, container, false);
        ceolController = ((MainActivity)getActivity()).getCeolController();
        ceolModel = ceolController.getCeolModel();
        trackControl = ceolModel.inputControl.trackControl;

        setButtonListener(rootView, R.id.infoDone);
        setButtonListener(rootView, R.id.infoArtist);
        setButtonListener(rootView, R.id.infoAlbum);
        setButtonListener(rootView, R.id.infoTrack);
        setButtonListener(rootView, R.id.infoLyrics);

        AudioStreamItem audioStreamItem = trackControl.getAudioItem();
        setTextViewText(rootView, R.id.infoArtist, audioStreamItem.getArtist());
        setTextViewText(rootView, R.id.infoAlbum, audioStreamItem.getAlbum());
        setTextViewText(rootView, R.id.infoTrack, audioStreamItem.getTitle());
        if (!audioStreamItem.getBitrate().isEmpty()) {
            setTextViewText(rootView, R.id.infoFormat, audioStreamItem.getFormat() + "(" +
                    audioStreamItem.getBitrate() + ")");
        } else {
            setTextViewText(rootView, R.id.infoFormat, audioStreamItem.getFormat());
        }
        return rootView;
    }

    private void setButtonListener(View rootView, int id) {
        View view = rootView.findViewById(id);
        if ( view != null) {
            view.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        String query = null;
        switch (view.getId()) {
            case R.id.infoDone:
                break;
            case R.id.infoArtist:
                query = getTextViewText(R.id.infoArtist);
                break;
            case R.id.infoAlbum:
                query = getTextViewText(R.id.infoArtist) + " " + getTextViewText(R.id.infoAlbum);
                break;
            case R.id.infoTrack:
                query = getTextViewText(R.id.infoTrack);
                break;
            case R.id.infoLyrics:
                query = getTextViewText(R.id.infoArtist) + " " + getTextViewText(R.id.infoTrack) + " lyrics";
                break;
        }

        if ( query != null) {
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, query);
            startActivity(intent);
        }
        this.dismiss();
        return;
    }
}

