package com.candkpeters.ceol.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import com.candkpeters.ceol.controller.CeolController;
import com.candkpeters.ceol.device.OnCeolStatusChangedListener;
import com.candkpeters.ceol.model.CeolDevice;
import com.candkpeters.chris.ceol.R;

public class MainActivity2 extends Activity {
    private static final String TAG = "MainActivity";
    ProgressDialog waitingDialog;

    private Prefs prefs = null;

    private static CeolController ceolController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        setupWaitingDialog();

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setActionBar(toolbar);

        prefs = new Prefs(this);

/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                ceolController.volumeDown();
            }
        });
*/

        Log.i(TAG, "Running");
        ceolController = new CeolController(this, new OnCeolStatusChangedListener() {
            @Override
            public void onCeolStatusChanged(CeolDevice ceolDevice) {
                updateViewsOnDeviceChange(ceolDevice);
            }
        });

    }


    private void setupWaitingDialog() {
        waitingDialog = new ProgressDialog(this);
        waitingDialog.setMessage("Waiting...");
        waitingDialog.setIndeterminate(true);
        waitingDialog.setCancelable(false);
    }

    private void showWaitingDialog() {
        waitingDialog.show();
    }

    private void hideWaitingDialog() {
        waitingDialog.hide();
    }

    public void updateViewsOnDeviceChange(CeolDevice ceolDevice) {
        try {

/*
            hideWaitingDialog();
            TextView textView = (TextView) viewPager.findViewById(R.id.title);
            textView.setText("Status=" + ceolDevice.getDeviceStatus() +
                            " SI=" + ceolDevice.getSIStatus() + " Vol=" + ceolDevice.getMasterVolume() +
                            " ScridValue=" + ceolDevice.NetServer.getScridValue()
            );

            TextView trackTB = (TextView) viewPager.findViewById(R.id.track);
            if (trackTB != null) trackTB.setText(ceolDevice.NetServer.getTrack());

            TextView artistTB = (TextView) viewPager.findViewById(R.id.artist);
            if (artistTB != null) artistTB.setText("selpos = " + ceolDevice.NetServer.getSelectedPosition());

            TextView albumTB = (TextView) viewPager.findViewById(R.id.album);
            if (albumTB != null) albumTB.setText("selentry = " + ceolDevice.NetServer.getSelectedEntry());

            ImageView imageV = (ImageView) viewPager.findViewById(R.id.imageV);
            if (imageV != null) imageV.setImageBitmap(ceolDevice.NetServer.getImageBitmap());
*/
        } catch (Exception e) {
            Log.e(TAG, "onCeolStatusChanged: Exception " + e);
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ceolController.activityOnStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        ceolController.activityOnStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static Fragment newInstance(int sectionNumber) {

            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            Fragment fragment;

            switch (sectionNumber) {
                default:
                case 1:
                    fragment = new CeolRemoteFragmentMainControls();
                    break;
                case 2:
                    fragment = new PlaceholderFragment();
                    break;
                case 3:
                    fragment = new CeolRemoteFragmentPlayerControl();
                    break;
            }
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_placeholder, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.title);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * Section 1 - Ceol main controls page.
     */
    public static class CeolRemoteFragmentMainControls extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "1";

        public CeolRemoteFragmentMainControls() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_ceolremote, container, false);
//            View rootView = inflater.inflate(R.layout.ceol_appwidget_layout_navigator, container, false);

            return rootView;
        }
    }

    /**
     * Section 1 - Ceol main controls page.
     */
    public static class CeolRemoteFragmentPlayerControl extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "3";

        public CeolRemoteFragmentPlayerControl() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.ceol_appwidget_layout_player, container, false);
//            View rootView = inflater.inflate(R.layout.ceol_appwidget_layout_navigator, container, false);

            return rootView;
        }
    }

    public void volumeUp(View view) {
        ceolController.volumeUp();
    }

    public void volumeDown(View view) {
        ceolController.volumeDown();
    }

    public void skipBackwards(View view) {
        ceolController.skipBackwards();
    }

    public void skipForwards(View view) {
        ceolController.skipForwards();
    }

    public void pausePlay(View view) {

    }

    public void performMacro(View view) {
        showWaitingDialog();
        ceolController.performMacro();
    }

    public void stop(View view) {

    }

    public void fastBackwards(View view) {

    }

    public void fastForwards(View view) {

    }


}
