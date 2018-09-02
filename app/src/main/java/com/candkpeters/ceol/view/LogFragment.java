package com.candkpeters.ceol.view;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.candkpeters.ceol.controller.CeolController;
import com.candkpeters.ceol.device.CeolManager;
import com.candkpeters.ceol.model.AudioStreamItem;
import com.candkpeters.ceol.model.CeolModel;
import com.candkpeters.ceol.model.control.TrackControl;
import com.candkpeters.chris.ceol.R;

public class LogFragment extends DialogFragment implements View.OnClickListener{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "3";
    private CeolController ceolController;
    private CeolManager ceolManager;

    public LogFragment() {
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
        View rootView = inflater.inflate(R.layout.log_fragment, container, false);
        ceolController = ((MainActivity)getActivity()).getCeolController();
        ceolManager = ceolController.getCeolManager();

        ListView logListView = (ListView)rootView.findViewById(R.id.logListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),R.layout.logview_item,R.id.logRow,ceolManager.getLogItems());

        logListView.setAdapter(adapter);

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

/*
    public class LogAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        private ArrayList<String[]> mKisiListesi;

        public LogAdapter(Activity activity, ArrayList<String[]> kisiler) {
            //XML'i alıp View'a çevirecek inflater'ı örnekleyelim
            mInflater = (LayoutInflater) activity.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            //gösterilecek listeyi de alalım
            mKisiListesi = kisiler;
        }

        @Override
        public int getCount() {
            return mKisiListesi.size();
        }

        @Override
        public Object getItem(int position) {
            //şöyle de olabilir: public Object getItem(int position)
            return mKisiListesi.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View satirView;

            satirView = mInflater.inflate(R.layout.lst_layout, null);
            TextView textView1 =
                    (TextView) satirView.findViewById(R.id.lstLayout1);
            TextView textView2 =
                    (TextView) satirView.findViewById(R.id.lstLayout2);
            TextView textView3 =
                    (TextView) satirView.findViewById(R.id.lstLayout3);
            TextView textView4 =
                    (TextView) satirView.findViewById(R.id.lstLayout4);

            textView1.setText(mKisiListesi.get(position)[0]);
            textView2.setText(mKisiListesi.get(position)[1]);
            textView3.setText(mKisiListesi.get(position)[2]);
            textView4.setText(mKisiListesi.get(position)[3]);

            return satirView;
        }

    }
*/
}

