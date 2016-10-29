package com.candkpeters.ceol.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import com.candkpeters.ceol.device.command.Command;
import com.candkpeters.ceol.device.command.CommandControlStop;
import com.candkpeters.ceol.device.command.CommandControlToggle;
import com.candkpeters.ceol.device.command.CommandCursorDown;
import com.candkpeters.ceol.device.command.CommandCursorEnter;
import com.candkpeters.ceol.device.command.CommandCursorRight;
import com.candkpeters.ceol.device.command.CommandCursorUp;
import com.candkpeters.ceol.device.command.CommandMasterVolumeDown;
import com.candkpeters.ceol.device.command.CommandMasterVolumeUp;
import com.candkpeters.ceol.device.command.CommandSetPowerToggle;
import com.candkpeters.ceol.device.command.CommandSetSI;
import com.candkpeters.ceol.device.command.CommandSkipBackward;
import com.candkpeters.ceol.device.command.CommandSkipForward;
import com.candkpeters.ceol.model.CeolDevice;
import com.candkpeters.ceol.model.DeviceStatusType;
import com.candkpeters.ceol.model.SIStatusType;
import com.candkpeters.chris.ceol.R;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
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
        setContentView(R.layout.drawer_layout);

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

        // Menu item
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setNavigationMenuStrings(navigationView);

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

            String currString = Long.toString(System.currentTimeMillis() % 1000);
            TextView update = (TextView) viewPager.findViewById(R.id.textUpdate);
            if (update != null) update.setText(currString);
            update = (TextView)findViewById(R.id.textUpdateA);
            if (update != null) update.setText(currString);

            TextView volume = (TextView)findViewById(R.id.volume1);
            if (volume != null) volume.setText(ceolDevice.getMasterVolumeString());
            volume = (TextView)findViewById(R.id.volume2);
            if (volume != null) volume.setText(ceolDevice.getMasterVolumeString());

            ImageView imageV = (ImageView)findViewById(R.id.imageTrack);
            if (imageV != null) imageV.setImageBitmap(ceolDevice.NetServer.getImageBitmap());

            updateSIEntries(ceolDevice);

            updateMacroButtons();

            updatePowerButton(ceolDevice);

            showConnection( ceolDevice.getDeviceStatus() != DeviceStatusType.Connecting ) ;

            updateNavigation( ceolDevice);


        } catch (Exception e) {
            Log.e(TAG, "onCeolStatusChanged: Exception " + e);
            e.printStackTrace();
        }
    }

    private void updateSIEntries(CeolDevice ceolDevice) {
        Button siB = (Button) findViewById(R.id.siB);
        if (siB != null) siB.setText(ceolDevice.getSIStatus().name);

        int id = 0;
        switch ( ceolDevice.getSIStatus()) {

            case Unknown:
                break;
            case CD:
                break;
            case Tuner:
                id = R.id.nav_tuner;
                break;
            case IRadio:
                id = R.id.nav_iradio;
                break;
            case NetServer:
                id = R.id.nav_server;
                break;
            case AnalogIn:
                break;
            case DigitalIn1:
                break;
            case DigitalIn2:
                break;
            case Bluetooth:
                id = R.id.nav_bluetooth;
                break;
            case Ipod:
                id = R.id.nav_usb;
                break;
            case Spotify:
                break;
        }
        if ( id != 0 ) {
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            MenuItem item = navigationView.getMenu().findItem(id);
            if (item != null) {
                item.setChecked(true);
            }
        }
    }

    private void setNavigationMenuStrings(NavigationView navigationView) {
        Menu menu = navigationView.getMenu();

        menu.findItem(R.id.nav_bluetooth).setTitle(SIStatusType.Bluetooth.name);
        menu.findItem(R.id.nav_iradio).setTitle(SIStatusType.IRadio.name);
        menu.findItem(R.id.nav_server).setTitle(SIStatusType.NetServer.name);
        menu.findItem(R.id.nav_tuner).setTitle(SIStatusType.Tuner.name);
        menu.findItem(R.id.nav_usb).setTitle(SIStatusType.Ipod.name);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

    private Command getCommandFromId(int id) {
        switch (id) {
            // Navigation
            case R.id.navUpB:
                return new CommandCursorUp();
            case R.id.navDownB:
                return new CommandCursorDown();
            case R.id.navLeftB:
                return new CommandCursorRight();
            case R.id.navRightB:
                return new CommandCursorRight();
            case R.id.navEnterB:
                return new CommandCursorEnter();

            // Control
            case R.id.powerB:
                return new CommandSetPowerToggle();
            case R.id.skipBackwardsB:
                return new CommandSkipBackward();
            case R.id.skipForwardsB:
                return new CommandSkipForward();
            case R.id.playpauseB:
                return new CommandControlToggle();
            case R.id.stopB:
                return new CommandControlStop();
            case R.id.volumedownB:
                return new CommandMasterVolumeDown();
            case R.id.volumeupB:
                return new CommandMasterVolumeUp();

            // Select
            case R.id.siInternetRadioB:
                return new CommandSetSI(SIStatusType.IRadio);
            case R.id.siIpodB:
                return new CommandSetSI(SIStatusType.Ipod);
            case R.id.siMusicServerB:
                return new CommandSetSI(SIStatusType.NetServer);
            case R.id.siTunerB:
                return new CommandSetSI(SIStatusType.Tuner);
            case R.id.siAnalogInB:
                return new CommandSetSI(SIStatusType.AnalogIn);
            case R.id.siDigitalInB:
                return new CommandSetSI(SIStatusType.DigitalIn1);
            case R.id.siBluetoothB:
                return new CommandSetSI(SIStatusType.Bluetooth);
            case R.id.siCdB:
                return new CommandSetSI(SIStatusType.CD);

            default:
                return null;
        }
    }

    public void buttonClick(View view) {
        Log.d(TAG, "buttonClick: " + view.getTag());
        Command command = getCommandFromId(view.getId());
        if ( command != null) {
            ceolController.performCommand(command);
        }
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

    public void siBClick(View view) {
        // Open menu
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.openDrawer(GravityCompat.START);
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
                    fragment = new CeolRemoteFragmentNavigatorControl();
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
            View rootView = inflater.inflate(R.layout.appwidget_layout_toplevel, container, false);
            ceolController.setViewCommandHandlers( rootView );

            return rootView;
        }

    }

    /**
     * Section 2 - Ceol navigator control.
     */
    public static class CeolRemoteFragmentNavigatorControl extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "3";

        public CeolRemoteFragmentNavigatorControl() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.appwidget_layout_navigator, container, false);
//            View rootView = inflater.inflate(R.layout.appwidget_layout_navigator, container, false);

            return rootView;
        }
    }

    /**
     * Section 3 - Ceol player control.
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
            View rootView = inflater.inflate(R.layout.appwidget_layout_player, container, false);

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

    // NAVIGATION VIEW
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Command command = null;

        if (id == R.id.nav_tuner) {
            command = new CommandSetSI(SIStatusType.Tuner);
        } else if (id == R.id.nav_server) {
            command = new CommandSetSI(SIStatusType.NetServer);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (command != null) {
            ceolController.performCommand(command);
        }
        return true;
    }

}
