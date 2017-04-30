package com.candkpeters.ceol.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.candkpeters.ceol.controller.CeolController;
import com.candkpeters.ceol.device.OnCeolStatusChangedListener;
import com.candkpeters.ceol.device.command.Command;
import com.candkpeters.ceol.device.command.CommandBaseApp;
import com.candkpeters.ceol.device.command.CommandControlStop;
import com.candkpeters.ceol.device.command.CommandControlToggle;
import com.candkpeters.ceol.device.command.CommandCursorDown;
import com.candkpeters.ceol.device.command.CommandCursorEnter;
import com.candkpeters.ceol.device.command.CommandCursorLeft;
import com.candkpeters.ceol.device.command.CommandCursorRight;
import com.candkpeters.ceol.device.command.CommandCursorUp;
import com.candkpeters.ceol.device.command.CommandMacro;
import com.candkpeters.ceol.device.command.CommandMasterVolume;
import com.candkpeters.ceol.device.command.CommandMasterVolumeDown;
import com.candkpeters.ceol.device.command.CommandMasterVolumeUp;
import com.candkpeters.ceol.device.command.CommandSetPowerToggle;
import com.candkpeters.ceol.device.command.CommandSetSI;
import com.candkpeters.ceol.device.command.CommandSkipBackward;
import com.candkpeters.ceol.device.command.CommandSkipForward;
import com.candkpeters.ceol.model.CeolDevice;
import com.candkpeters.ceol.model.DeviceStatusType;
import com.candkpeters.ceol.model.DirectionType;
import com.candkpeters.ceol.model.SIStatusType;
import com.candkpeters.ceol.service.CeolService;
import com.candkpeters.chris.ceol.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private static final float DIMMED = 0.8f;
    private static final float TRANSPARENT = 0;
    ProgressDialog waitingDialog;
//    PlaylistRecyclerAdapter playlistRecyclerAdapter = new PlaylistRecyclerAdapter(getContext(), ((MainActivity)getActivity()).getCeolController());
    private PlaylistRecyclerAdapter playlistRecyclerAdapter;

    private Prefs prefs = null;

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private CeolController ceolController;

    private Animation powerAnimation;
    private boolean isLargeDevice;
    private boolean isSelectSIStart = false;
    private String action = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        setupWaitingDialog();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        prefs = new Prefs(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        // Set up the ViewPager with the sections adapter.
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        if ( viewPager != null) {
            isLargeDevice=false;
            SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(sectionsPagerAdapter);
            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);

        } else {
            isLargeDevice=true;
        }

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

        // Menu item
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Log.i(TAG, "Created");
        ceolController = new CeolController(this, new OnCeolStatusChangedListener() {
            @Override
            public void onCeolStatusChanged() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        updateViewsOnDeviceChange(ceolController.getCeolDevice());
                    }
                });

            }
        });

        setNavigationMenuStrings(navigationView);

//        ImageView imageV = (ImageView) viewPager.findViewById(R.id.imageTrack);
//        if (imageV != null) imageV.setMinimumHeight();width(ceolDevice.getAudioItem().getImageBitmap());

        powerAnimation = new AlphaAnimation(1,0);
        powerAnimation.setDuration(1000);
        powerAnimation.setInterpolator( new LinearInterpolator());
        powerAnimation.setRepeatCount(Animation.INFINITE);
        powerAnimation.setRepeatMode(Animation.REVERSE);

        playlistRecyclerAdapter = new PlaylistRecyclerAdapter(this, getCeolController());

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

            setTextViewText(R.id.textTrack, ceolDevice.getAudioItem().getTrack());

            setTextViewText(R.id.textArtist, ceolDevice.getAudioItem().getArtist());

            setTextViewText(R.id.textAlbum, ceolDevice.getAudioItem().getAlbum());

            setTextViewText(R.id.playStatus, ceolDevice.getPlayStatus().toString());

            String currString = Long.toString(System.currentTimeMillis() % 100);
            setTextViewText(R.id.textUpdate, currString);

            setTextViewText(R.id.volume, ceolDevice.getMasterVolumeString());

            ImageView imageV = (ImageView)findViewById(R.id.imageTrack);
            if (imageV != null) imageV.setImageBitmap(ceolDevice.getAudioItem().getImageBitmap());

            View tunerPanel = findViewById(R.id.tunerPanel);
            View netPanel = findViewById(R.id.netPanel);
            if ( tunerPanel != null && netPanel != null ) {
                switch (ceolDevice.getSIStatus()) {
                    case CD:
                    case AnalogIn:
                    case NotConnected:
                        tunerPanel.setVisibility(View.INVISIBLE);
                        netPanel.setVisibility(View.INVISIBLE);
                        break;
                    case Tuner:
                        tunerPanel.setVisibility(View.VISIBLE);
                        netPanel.setVisibility(View.GONE);

                        setTextViewText(R.id.tunerName, ceolDevice.Tuner.getName());
                        setTextViewText(R.id.tunerFrequency, ceolDevice.Tuner.getFrequency());
                        setTextViewText(R.id.tunerUnits, ceolDevice.Tuner.getUnits());
                        setTextViewText(R.id.tunerBand, ceolDevice.Tuner.getBand());
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
            updateSIEntries(ceolDevice);

//            updateMacroButtons();

            updatePowerButton(ceolDevice);

            showConnection( ceolDevice.getDeviceStatus() != DeviceStatusType.Connecting ) ;

            updateNavigation( ceolDevice);

            updateSeekbar( ceolDevice);

            playlistRecyclerAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            Log.e(TAG, "onCeolStatusChanged: Exception " + e);
            e.printStackTrace();
        }
    }

    private void updateSeekbar(CeolDevice ceolDevice) {
        if ( ceolDevice.isOpenHomeOperating() ) {
            int progressSize = (int)ceolDevice.getOpenHome().getDuration();
            int progress = (int)ceolDevice.getOpenHome().getSeconds();

            SeekBar seekBar = (SeekBar) findViewById(R.id.trackSeekBar);
            if (seekBar != null) {
                seekBar.setMax(progressSize);
                seekBar.setProgress(progress);
            }
        }
    }

    private void setTextViewText(int tunerName2, String name) {
        TextView tunerName = (TextView) findViewById(tunerName2);
        if (tunerName != null) tunerName.setText(name);
    }

    private void updateSIEntries(CeolDevice ceolDevice) {
        Button siB = (Button) findViewById(R.id.siB);
        if (siB != null) {
//            if ( ceolDevice.getDeviceStatus() != DeviceStatusType.On ) {
//                siB.setText(SIStatusType.NotConnected.name);
//            } else {
                siB.setText(ceolDevice.getSIStatus().name);
//            }
        }

        int id = 0;
        switch ( ceolDevice.getSIStatus()) {

            case NotConnected:
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
        Prefs prefs = new Prefs(this);

        Menu menu = navigationView.getMenu();

        menu.findItem(R.id.nav_tuner).setTitle(SIStatusType.Tuner.name);
        menu.findItem(R.id.nav_server).setTitle(SIStatusType.NetServer.name);
        menu.findItem(R.id.nav_bluetooth).setTitle(SIStatusType.Bluetooth.name);
        menu.findItem(R.id.nav_iradio).setTitle(SIStatusType.IRadio.name);
        menu.findItem(R.id.nav_usb).setTitle(SIStatusType.Ipod.name);
        menu.findItem(R.id.nav_analog).setTitle(SIStatusType.AnalogIn.name);
        menu.findItem(R.id.nav_cd).setTitle(SIStatusType.CD.name);
        menu.findItem(R.id.nav_digital1).setTitle(SIStatusType.DigitalIn1.name);
        menu.findItem(R.id.nav_digital2).setTitle(SIStatusType.DigitalIn2.name);

        menu.findItem(R.id.nav_macro1).setTitle(prefs.getMacro1Name());
        menu.findItem(R.id.nav_macro2).setTitle(prefs.getMacro2Name());
        menu.findItem(R.id.nav_macro3).setTitle(prefs.getMacro3Name());
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

    private void openDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (!drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(GravityCompat.START);
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

//        ListView entriesList = (ListView)findViewById(R.id.entriesList);
//        ListAdapter adapter = entriesList.getAdapter();

    }

    private void updateNavigationRow(CeolDevice ceolDevice, int rowResId, int rowIndex) {
        TextView textV = (TextView)findViewById(rowResId);
        if ( textV != null) {
//            if ( ceolDevice.getSIStatus() == SIStatusType.AudioItem  ) {
            if ( ceolDevice.getSIStatus() == SIStatusType.NetServer && ceolDevice.CeolNetServer.isBrowsing() ) {
//                String s = ceolDevice.AudioItem.getEntries().getBrowseLineText(rowIndex);
                SpannableString s = new SpannableString(ceolDevice.CeolNetServer.getEntries().getBrowseLineText(rowIndex));
                if ( ceolDevice.CeolNetServer.getEntries().getSelectedEntryIndex() == rowIndex) {
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

/*
    public void updateMacroButtons() {
        Prefs prefs = new Prefs(this);
        String[] macroNames = prefs.getMacroNames();

        updateMacroButton( R.id.performMacro1B, macroNames, 0);
        updateMacroButton( R.id.performMacro2B, macroNames, 1);
        updateMacroButton( R.id.performMacro3B, macroNames, 2);

    }
*/

/*
    private void updateMacroButton(int resId, String[] macroNames, int macroIndex) {

        if ( macroIndex < macroNames.length) {
            Button b = (Button)findViewById(resId);
            if ( b != null ) {
                b.setText(macroNames[macroIndex]);
            }
        }
    }
*/

    @Override
    public void onStart() {
        super.onStart();
        ceolController.activityOnStart();
        if ( action != null ) {
            if ( action.equals( CommandBaseApp.Action.SELECTSI.name())) {
                openDrawer();
            } else if ( action.equals( CommandBaseApp.Action.INFO.name())) {
                showInfo();
            }
        }
        action = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        ceolController.activityOnStop();
    }

    @Override
    public void onNewIntent(Intent intent) {
        Bundle b = intent.getExtras();
        if (b != null ) {
            action = b.getString(CeolService.START_ACTIVITY_ACTION);
        }
    }

    private Command getCommandFromId(int id) {
        Command command = null;

        switch (id) {
            // Navigation
            case R.id.navUpB:
                command = new CommandCursorUp();
                break;
            case R.id.navDownB:
                command = new CommandCursorDown();
                break;
            case R.id.navLeftB:
                command = new CommandCursorLeft();
                break;
            case R.id.navRightB:
                command = new CommandCursorRight();
                break;
            case R.id.navEnterB:
                command = new CommandCursorEnter();
                break;

            // Control
            case R.id.powerB:
                command = new CommandSetPowerToggle();
                break;
            case R.id.skipBackwardsB:
                command = new CommandSkipBackward();
                break;
            case R.id.skipForwardsB:
                command = new CommandSkipForward();
                break;
            case R.id.playpauseB:
                command = new CommandControlToggle();
                break;
            case R.id.stopB:
                command = new CommandControlStop();
                break;
            case R.id.volumedownB:
                command = new CommandMasterVolumeDown();
                break;
            case R.id.volumeupB:
                command = new CommandMasterVolumeUp();
                break;

            // Other
            case R.id.infoB:
                showInfo();
                break;
        }
        return command;
    }

    private void showInfo() {
        showInfoDialog();
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

    // NAVIGATION VIEW
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        int macroId = -1;
        Command command = null;

        SIStatusType siStatusType = SIStatusType.NotConnected;
        switch (id) {
            case R.id.nav_tuner:
                siStatusType = SIStatusType.Tuner;
                break;
            case R.id.nav_server:
                siStatusType = SIStatusType.NetServer;
                break;
            case R.id.nav_bluetooth:
                siStatusType = SIStatusType.Bluetooth;
                break;
            case R.id.nav_iradio:
                siStatusType = SIStatusType.IRadio;
                break;
            case R.id.nav_usb:
                siStatusType = SIStatusType.Ipod;
                break;
            case R.id.nav_analog:
                siStatusType = SIStatusType.AnalogIn;
                break;
            case R.id.nav_cd:
                siStatusType = SIStatusType.CD;
                break;
            case R.id.nav_digital1:
                siStatusType = SIStatusType.DigitalIn1;
                break;
            case R.id.nav_digital2:
                siStatusType = SIStatusType.DigitalIn2;
                break;
            case R.id.nav_macro1:
                macroId = 1;
                break;
            case R.id.nav_macro2:
                macroId = 2;
                break;
            case R.id.nav_macro3:
                macroId = 3;
                break;
            default:
                siStatusType = SIStatusType.NotConnected;
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (siStatusType != SIStatusType.NotConnected) {
            command = new CommandSetSI(siStatusType);
        } else if ( macroId != -1 ) {
            command = new CommandMacro( macroId);
        }

        if ( command != null) {
            ceolController.performCommand(command);
        }

        return true;
    }

    private void showInfoDialog() {
        FragmentManager fm = getSupportFragmentManager();
        InfoFragment infoFragment = new InfoFragment();
        infoFragment.show(fm,"infoFragment");
    }

    public CeolController getCeolController() {
        return ceolController;
    }

    public PlaylistRecyclerAdapter getPlaylistRecyclerAdapter() {
        return playlistRecyclerAdapter;
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
                    fragment = new CeolRemoteFragmentPlayerControl();
                    break;
                case 2:
                    fragment = new CeolRemoteFragmentPlaylistControl();
                    break;
                case 3:
                    fragment = new CeolRemoteFragmentNavigatorControl();
                    break;
            }
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            return null;
        }
    }

    public void showConnection( boolean isConnected) {

        View rootView = findViewById(R.id.dimV);
        if (rootView == null) return;

        boolean isFullyUnDimmed = ( rootView.getAlpha() == TRANSPARENT || rootView.getVisibility() != View.VISIBLE );
        boolean isFullyDimmed = ( rootView.getAlpha() == DIMMED && rootView.getVisibility() == View.VISIBLE);
//        Log.d(TAG, "showConnection: alpha="+rootView.getAlpha()+" isFullyUnDimmed="+isFullyUnDimmed + " isFullyDimmed="+isFullyDimmed);
        if ( isFullyUnDimmed ) {
            if (isConnected) {
                // Connected - ensure view is removed
                rootView.setVisibility(View.GONE);
            } else {
                // Animate to disconnected
                rootView.setVisibility(View.VISIBLE);
                rootView.animate().alpha(DIMMED);
            }
        }
        if ( isFullyDimmed) {
            if (isConnected) {
                // Animate to connected
                rootView.setVisibility(View.VISIBLE);
                rootView.animate().alpha(TRANSPARENT);
            } else {
                // Already not connected
            }
        }
    }

    /**
     * Section 1 - Ceol player control.
     */
    public static class CeolRemoteFragmentPlayerControl extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        public CeolRemoteFragmentPlayerControl() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.tablayout_player, container, false);

            return rootView;
        }
    }

    /**
     * Section 1 - Ceol playerlist control.
     */
    public static class CeolRemoteFragmentPlaylistControl extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        public CeolRemoteFragmentPlaylistControl() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.tablayout_playlist, container, false);
            startPlaylistRetrieval(rootView);
            return rootView;
        }

        private void startPlaylistRetrieval(View rootView) {

            RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//            PlaylistRecyclerAdapter playlistRecyclerAdapter = new PlaylistRecyclerAdapter(getContext(), ((MainActivity)getActivity()).getCeolController());
            recyclerView.setAdapter( ((MainActivity)getActivity()).getPlaylistRecyclerAdapter() ) ;
        }

        @Override
        public void onStart() {
            super.onStart();
            Log.d(TAG, "onStart in playlist fragment: ");
        }

    }

    /**
     * Section 3 - Ceol navigator control.
     */
    public static class CeolRemoteFragmentNavigatorControl extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        public CeolRemoteFragmentNavigatorControl() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.tablayout_navigator, container, false);
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
            String[] section_title = getResources().getStringArray(R.array.section_page_title);
            return section_title.length;
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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if ( action == KeyEvent.ACTION_DOWN) {
                    doVolumeControl(DirectionType.Up);
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if ( action == KeyEvent.ACTION_DOWN) {
                    doVolumeControl(DirectionType.Down);
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    private void doVolumeControl( DirectionType direction) {
        Command command = new CommandMasterVolume(direction);
        ceolController.performCommand(command);
        String text = String.format(getResources().getString(R.string.volume_toast),direction.toString());
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(getApplicationContext(), text, duration );
        toast.show();
    }


}
