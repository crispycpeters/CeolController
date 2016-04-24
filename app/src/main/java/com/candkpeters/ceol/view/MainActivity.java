package com.candkpeters.ceol.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.candkpeters.ceol.controller.CeolController;
import com.candkpeters.ceol.device.OnCeolStatusChangedListener;
import com.candkpeters.ceol.model.CeolDevice;
import com.candkpeters.ceol.model.DeviceStatusType;
import com.candkpeters.ceol.model.SIStatusType;
import com.candkpeters.chris.ceol.R;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final float DIMMED = 0.9f;
    private static final float NOTDIMMED = 0;
    ProgressDialog waitingDialog;

    private Prefs prefs = null;

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter sectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager viewPager;

    private static CeolController ceolController;

    private Animation powerAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupWaitingDialog();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);

        prefs = new Prefs(this);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                ceolController.volumeDown();
            }
        });

        Log.i(TAG, "Created");
        ceolController = new CeolController(this, new OnCeolStatusChangedListener() {
            @Override
            public void onCeolStatusChanged(CeolDevice ceolDevice) {
                updateViewsOnDeviceChange(ceolDevice);
            }
        });

        ImageView imageV = (ImageView) viewPager.findViewById(R.id.imageTrack);
//        if (imageV != null) imageV.setMinimumHeight();width(ceolDevice.NetServer.getImageBitmap());

        powerAnimation = new AlphaAnimation(1,0);
        powerAnimation.setDuration(1000);
        powerAnimation.setInterpolator( new LinearInterpolator());
        powerAnimation.setRepeatCount(Animation.INFINITE);
        powerAnimation.setRepeatMode(Animation.REVERSE);

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

            hideWaitingDialog();
/*
            TextView textView = (TextView) viewPager.findViewById(R.id.title);
            textView.setText("Status=" + ceolDevice.getDeviceStatus() +
                            " SI=" + ceolDevice.getSIStatus() + " Vol=" + ceolDevice.getMasterVolume() +
                            " ScridValue=" + ceolDevice.NetServer.getScridValue()
            );
*/

            TextView trackTB = (TextView)findViewById(R.id.textTrack);
            if (trackTB != null) trackTB.setText(ceolDevice.NetServer.getTrack());

            TextView artistTB = (TextView)findViewById(R.id.textArtist);
            if (artistTB != null) artistTB.setText(ceolDevice.NetServer.getArtist());

            TextView albumTB = (TextView) viewPager.findViewById(R.id.textAlbum);
            if (albumTB != null) albumTB.setText(ceolDevice.NetServer.getAlbum());

            ImageView imageV = (ImageView) viewPager.findViewById(R.id.imageTrack);
            if (imageV != null) imageV.setImageBitmap(ceolDevice.NetServer.getImageBitmap());

            updateMacroButtons();

            updatePowerButton(ceolDevice);

            showConnection( ceolDevice.getDeviceStatus() != DeviceStatusType.Connecting ) ;

            updateNavigation( ceolDevice);

        } catch (Exception e) {
            Log.e(TAG, "onCeolStatusChanged: Exception " + e);
            e.printStackTrace();
        }
    }

    private void updateNavigation(CeolDevice ceolDevice) {

        updateNavigationRow( ceolDevice, R.id.textRow0, 0);
        updateNavigationRow( ceolDevice, R.id.textRow1, 1);
        updateNavigationRow( ceolDevice, R.id.textRow2, 2);
        updateNavigationRow( ceolDevice, R.id.textRow3, 3);
        updateNavigationRow( ceolDevice, R.id.textRow4, 4);
        updateNavigationRow( ceolDevice, R.id.textRow5, 5);
        updateNavigationRow( ceolDevice, R.id.textRow6, 6);
        updateNavigationRow( ceolDevice, R.id.textRow7, 7);
    }

    private void updateNavigationRow(CeolDevice ceolDevice, int rowResId, int rowIndex) {
        TextView textV = (TextView)findViewById(rowResId);
        if ( textV != null) {
            if ( ceolDevice.getSIStatus() == SIStatusType.NetServer && ceolDevice.NetServer.isBrowsing() ) {
//                String s = ceolDevice.NetServer.getEntries().getBrowseLineText(rowIndex);
                SpannableString s = new SpannableString(ceolDevice.NetServer.getEntries().getBrowseLineText(rowIndex));
                if ( ceolDevice.NetServer.getEntries().getSelectedEntryIndex() == rowIndex) {
                    s.setSpan(new StyleSpan(Typeface.BOLD_ITALIC),0, s.length(),0);
                }
                textV.setText(s);
            } else {
                textV.setText("");
            }
        }
    }

    private boolean isPowerAnimating = false;

    private void updatePowerButton(CeolDevice ceolDevice) {
        ImageButton powerB = (ImageButton)findViewById(R.id.powerB);

        if (powerB != null) {

            switch ( ceolDevice.getDeviceStatus()) {

                case Connecting:
                case Standby:
                    if ( isPowerAnimating ) {
                        powerB.clearAnimation();
                        isPowerAnimating = false;
                    }
                    powerB.setImageResource(R.drawable.ic_av_power_back );
                    break;
                case Starting:
                    if ( !isPowerAnimating ) {
                        powerB.setImageResource(R.drawable.ic_av_power );
                        powerB.startAnimation(powerAnimation);
                        isPowerAnimating = true;
                    }
                    break;
                case On:
                    if ( isPowerAnimating ) {
                        powerB.clearAnimation();
                        isPowerAnimating = false;
                    }
                    powerB.setImageResource(R.drawable.ic_av_power );
                    break;
            }
        }
    }

    public void updateMacroButtons() {
        Prefs prefs = new Prefs(this);
        String[] macroNames = prefs.getMacroNames();

        updateMacroButton( R.id.performMacro1B, macroNames, 0);
        updateMacroButton( R.id.performMacro2B, macroNames, 1);
        updateMacroButton( R.id.performMacro3B, macroNames, 2);

    }

    private void updateMacroButton(int resId, String[] macroNames, int macroIndex) {

        if ( macroIndex < macroNames.length) {
            Button b = (Button)findViewById(resId);
            if ( b != null ) {
                b.setText(macroNames[macroIndex]);
            }
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

    public void showConnection( boolean isConnected) {

        View rootView = findViewById(R.id.dimV);
        if (rootView == null) return;
        boolean isDimmerVisible = ( rootView.getAlpha() != NOTDIMMED );
//        Log.d(TAG, "showConnection: alpha="+rootView.getAlpha()+" isDimmerVisible="+isDimmerVisible);
        if ( isConnected  ) {
            if ( isDimmerVisible ) {
                rootView.setVisibility(View.INVISIBLE);
                rootView.animate().alpha(NOTDIMMED);
            }
        } else {
            if ( !isDimmerVisible ) {
                rootView.setAlpha(DIMMED);

                rootView.setVisibility(View.VISIBLE);
                rootView.animate().alpha(DIMMED);
            }
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

            ceolController.setViewCommandHandlers( rootView );

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


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String[] section_title = getResources().getStringArray(R.array.section_page_title);
            if ( position >= 0 && position < section_title.length ) {
                return section_title[position];
            } else {
                return null;
            }
        }
    }

}
