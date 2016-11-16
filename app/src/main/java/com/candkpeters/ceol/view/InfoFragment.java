package com.candkpeters.ceol.view;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.candkpeters.ceol.controller.CeolController;
import com.candkpeters.ceol.model.CeolDevice;
import com.candkpeters.chris.ceol.R;

public class InfoFragment extends DialogFragment implements View.OnClickListener{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "3";
    private CeolController ceolController;

    public InfoFragment() {
    }

    private void setTextViewText(View parentView, int tunerName2, String name) {
        TextView tunerName = (TextView) parentView.findViewById(tunerName2);
        if (tunerName != null) tunerName.setText(name);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.info_fragment, container, false);
//            View rootView = inflater.inflate(R.layout.appwidget_layout_navigator, container, false);
        ceolController = new CeolController(getContext(), null);
        CeolDevice ceolDevice = ceolController.getCeolDevice();

        setButtonListener(rootView, R.id.infoDone);
        setButtonListener(rootView, R.id.infoArtist);
        setButtonListener(rootView, R.id.infoAlbum);
        setButtonListener(rootView, R.id.infoTrack);
        setButtonListener(rootView, R.id.infoLyrics);

        setTextViewText(rootView, R.id.infoArtist, ceolDevice.NetServer.getArtist());
        setTextViewText(rootView, R.id.infoAlbum, ceolDevice.NetServer.getAlbum());
        setTextViewText(rootView, R.id.infoTrack, ceolDevice.NetServer.getTrack());
        if ( !ceolDevice.NetServer.getBitrate().isEmpty() ) {
            setTextViewText(rootView, R.id.infoFormat, ceolDevice.NetServer.getFormat() + "(" +
                    ceolDevice.NetServer.getBitrate() + ")");
        } else {
            setTextViewText(rootView, R.id.infoFormat, ceolDevice.NetServer.getFormat());
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
        CeolDevice ceolDevice = ceolController.getCeolDevice();
        String query = null;

        switch (view.getId()) {
            case R.id.infoDone:
                break;
            case R.id.infoArtist:
                query = ceolDevice.NetServer.getArtist();
                break;
            case R.id.infoAlbum:
                query = ceolDevice.NetServer.getArtist() + " " + ceolDevice.NetServer.getAlbum();
                break;
            case R.id.infoTrack:
                query = ceolDevice.NetServer.getTrack();
                break;
            case R.id.infoLyrics:
                query = ceolDevice.NetServer.getArtist() + " " + ceolDevice.NetServer.getTrack()
                        + " lyrics";
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

