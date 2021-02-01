package com.candkpeters.ceol.view;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.candkpeters.ceol.controller.CeolController;
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
import com.candkpeters.ceol.model.ObservedControlType;
import com.candkpeters.ceol.model.control.AudioControl;
import com.candkpeters.ceol.model.CeolModel;
import com.candkpeters.ceol.model.control.ConnectionControl;
import com.candkpeters.ceol.model.control.ControlBase;
import com.candkpeters.ceol.model.DirectionType;
import com.candkpeters.ceol.model.control.InputControl;
import com.candkpeters.ceol.model.OnControlChangedListener;
import com.candkpeters.ceol.model.control.PowerControl;
import com.candkpeters.ceol.model.SIStatusType;
import com.candkpeters.ceol.service.CeolService;
import com.candkpeters.chris.ceol.R;

import static com.candkpeters.chris.ceol.R.*;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private static final float DIMMED = 0.8f;
    private static final float TRANSPARENT = 0;
    ProgressDialog waitingDialog;
//    PlaylistRecyclerAdapter playlistRecyclerAdapter = new PlaylistRecyclerAdapter(getContext(), ((MainActivity)getActivity()).getCeolController());

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
    private String action = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Entering");
        setContentView(layout.drawer_layout);

        ceolController = new CeolController(this);

        setupWaitingDialog();

        Toolbar toolbar = findViewById(id.toolbar);
        setSupportActionBar(toolbar);

//        prefs = new Prefs(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        // Set up the ViewPager with the sections adapter.
        ViewPager viewPager = findViewById(id.container);
        if ( viewPager != null) {
            SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(sectionsPagerAdapter);
            TabLayout tabLayout = findViewById(id.tabs);
            tabLayout.setupWithViewPager(viewPager);
        }

        // Menu item
        DrawerLayout drawer = findViewById(id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, string.navigation_drawer_open, string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        powerAnimation = new AlphaAnimation(1,0);
        powerAnimation.setDuration(1000);
        powerAnimation.setInterpolator( new LinearInterpolator());
        powerAnimation.setRepeatCount(Animation.INFINITE);
        powerAnimation.setRepeatMode(Animation.REVERSE);

        setNavigationMenuStrings(navigationView);

        Log.i(TAG, "onCreate: Done");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Entering");
        ceolController.destroy();
    }





/*
    public void updateViewsOnDeviceChange(CeolModel ceolModel, ControlBase control) {
        try {



//            updateSeekbar( ceolDevice);
            playlistRecyclerAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            Log.e(TAG, "onCeolStatusChanged: Exception " + e);
            e.printStackTrace();
        }
    }
*/

    private void setupWaitingDialog() {
        waitingDialog = new ProgressDialog(this);
        waitingDialog.setMessage("Waiting...");
        waitingDialog.setIndeterminate(true);
        waitingDialog.setCancelable(false);
    }

    private void setTextViewText(int tunerName2, String name) {
        TextView tunerName = findViewById(tunerName2);
        if (tunerName != null) tunerName.setText(name);
    }

    private void updateSIEntries(InputControl inputControl) {
        Button siB = findViewById(id.siB);
        if (siB != null) {
            if ( getCeolController().isConnected() ) {
                siB.setText(inputControl.getSIStatus().name);
            }
//            if ( ceolDevice.getDeviceStatus() != DeviceStatusType.On ) {
//                siB.setText(SIStatusType.Unknown.name);
//            } else {
//            }
        }

        int id = 0;
        switch ( inputControl.getSIStatus()) {

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
            NavigationView navigationView = findViewById(R.id.nav_view);
            MenuItem item = navigationView.getMenu().findItem(id);
            if (item != null) {
                item.setChecked(true);
            }
        }
    }

    private void setNavigationMenuStrings(NavigationView navigationView) {
        Prefs prefs = new Prefs(this);

        Menu menu = navigationView.getMenu();

        menu.findItem(id.nav_tuner).setTitle(SIStatusType.Tuner.name);
        menu.findItem(id.nav_server).setTitle(SIStatusType.NetServer.name);
        menu.findItem(id.nav_bluetooth).setTitle(SIStatusType.Bluetooth.name);
        menu.findItem(id.nav_iradio).setTitle(SIStatusType.IRadio.name);
        menu.findItem(id.nav_usb).setTitle(SIStatusType.Ipod.name);
        menu.findItem(id.nav_analog).setTitle(SIStatusType.AnalogIn.name);
        menu.findItem(id.nav_cd).setTitle(SIStatusType.CD.name);
        menu.findItem(id.nav_digital1).setTitle(SIStatusType.DigitalIn1.name);
        menu.findItem(id.nav_digital2).setTitle(SIStatusType.DigitalIn2.name);

        menu.findItem(id.nav_macro1).setTitle(prefs.getMacro1Name());
        menu.findItem(id.nav_macro2).setTitle(prefs.getMacro2Name());
        menu.findItem(id.nav_macro3).setTitle(prefs.getMacro3Name());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void openDrawer() {
        DrawerLayout drawer = findViewById(id.drawer_layout);
        if (!drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    private boolean isPowerAnimating = false;

    private void updatePowerButton(PowerControl powerControl) {
        ImageButton powerB = findViewById(id.powerB);

        if (powerB != null) {

            switch ( powerControl.getDeviceStatus()) {

                case Connecting:
                case Standby:
                    if ( isPowerAnimating ) {
                        powerB.clearAnimation();
                        isPowerAnimating = false;
                    }
                    powerB.setImageResource(drawable.ic_av_power_off);
                    break;
                case Starting:
                    if ( !isPowerAnimating ) {
                        powerB.setImageResource(drawable.ic_av_power );
                        powerB.startAnimation(powerAnimation);
                        isPowerAnimating = true;
                    }
                    break;
                case On:
                    if ( isPowerAnimating ) {
                        powerB.clearAnimation();
                        isPowerAnimating = false;
                    }
                    powerB.setImageResource(drawable.ic_av_power );
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
        Log.d(TAG, "Activity onStart: Entered");
//        ceolController.activityOnStart();

        ceolController.start( new OnControlChangedListener() {

            @Override
            public void onControlChanged(final CeolModel ceolModel, final ObservedControlType observedControlType, final ControlBase controlBase) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        switch (observedControlType) {

                            case All:
                                showConnection( ceolModel.connectionControl.isConnected() ) ;
                                updatePowerButton( ceolModel.powerControl);
                                setTextViewText(id.volume, ceolModel.audioControl.getMasterVolumeString());
                                updateSIEntries(ceolModel.inputControl);
                                break;
                            case Connection:
                                showConnection( ((ConnectionControl)controlBase).isConnected() ) ;
                                break;
                            case Power:
                                updatePowerButton((PowerControl)controlBase);
                                break;
                            case Audio:
                                setTextViewText(id.volume, ((AudioControl)controlBase).getMasterVolumeString());
                                break;
                            case Input:
                                updateSIEntries((InputControl)controlBase);
                                break;
                            case Track:
                                break;
                            case Navigator:
                                break;
                            case Playlist:
                                break;
                            case Progress:
                                break;
                        }
                    }
                });
            }
        }
        );

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
        Log.d(TAG, "onStop: Entering");
//        ceolController.activityOnStop();
        ceolController.stop();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle b = intent.getExtras();
        if (b != null) {
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
//                showVolumeChangeTemporarily(-1);
                command = new CommandMasterVolumeDown();
                break;
            case R.id.volumeupB:
//                showVolumeChangeTemporarily(1);
                command = new CommandMasterVolumeUp();
                break;

            // Other
//            case R.id.infoB:
//                showInfo();
//                break;
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

        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_rescan) {
            ceolController.restart();
            return true;
        }
        if (id == R.id.action_unbind) {
            Intent intent = new Intent(this, CeolService.class);
            intent.setAction(CeolService.STOP_CLING);
            startService(intent);
            return true;
        }
        if (id == R.id.action_bind) {
            Intent intent = new Intent(this, CeolService.class);
            intent.setAction(CeolService.START_CLING);
            startService(intent);
            return true;
        }
        if (id == R.id.action_showlog) {
            showLogDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void siBClick(View view) {
        // Open menu
        DrawerLayout drawer = findViewById(id.drawer_layout);
        drawer.openDrawer(GravityCompat.START);
    }

    // NAVIGATION VIEW
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        int macroId = -1;
        Command command = null;

        SIStatusType siStatusType = SIStatusType.Unknown;
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
            case R.id.nav_library:
                // TODO Just testing
                showLibrary();
                break;
            default:
                siStatusType = SIStatusType.Unknown;
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (siStatusType != SIStatusType.Unknown) {
            command = new CommandSetSI(siStatusType);
        } else if ( macroId != -1 ) {
            command = new CommandMacro( macroId);
        }

        if ( command != null) {
            ceolController.performCommand(command);
        }

        return true;
    }

    private void showLibrary() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragmentLibrary = fm.findFragmentById(id.library_fragment);
        if ( fragmentLibrary == null) {
            fragmentLibrary = new FragmentLibrary();
            fm.beginTransaction()
                    .replace(id.library_fragment, fragmentLibrary)
                    .commit();
        }

    }

    private void showInfoDialog() {
        FragmentManager fm = getSupportFragmentManager();
        InfoFragment infoFragment = new InfoFragment();
        infoFragment.show(fm,"infoFragment");
    }

    private void showLogDialog() {
        FragmentManager fm = getSupportFragmentManager();
        LogFragment logFragment = new LogFragment();
        logFragment.show(fm,"logFragment");
    }

    public CeolController getCeolController() {
        return ceolController;
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
                    fragment = new FragmentPlayer();
                    break;
                case 2:
                    fragment = new FragmentPlaylist();
                    break;
                case 3:
                    fragment = new FragmentNavigator();
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

        View rootView = findViewById(id.dimV);
        if (rootView == null) return;

        Button siB = findViewById(id.siB);
        rootView.setVisibility(View.GONE);
        if (siB != null) {
            if (!isConnected) {
                siB.setText(getString(string.not_connected_short));
            } else {
                siB.setText(ceolController.getCeolModel().inputControl.getSIStatus().name);
            }
        }

        if ( isConnected) {
            rootView.setVisibility(View.GONE);
        } else {
            rootView.setVisibility(View.VISIBLE);
        }
/*
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
*/
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
            String[] section_title = getResources().getStringArray(array.section_page_title);
            return section_title.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String[] section_title = getResources().getStringArray(array.section_page_title);
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
        String text = String.format(getResources().getString(string.volume_toast),direction.toString());
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(getApplicationContext(), text, duration );
        toast.show();
    }


}
