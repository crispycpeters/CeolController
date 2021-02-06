package com.candkpeters.ceol.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media.app.NotificationCompat.MediaStyle;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;

import com.candkpeters.ceol.device.CeolManager;
import com.candkpeters.ceol.device.command.Command;
import com.candkpeters.ceol.device.command.CommandControlPause;
import com.candkpeters.ceol.device.command.CommandControlPlay;
import com.candkpeters.ceol.device.command.CommandMasterVolumeDown;
import com.candkpeters.ceol.device.command.CommandMasterVolumeUp;
import com.candkpeters.ceol.device.command.CommandSetPowerToggle;
import com.candkpeters.ceol.device.command.CommandSkipForward;
import com.candkpeters.ceol.device.wss.CeolManagerWss;
import com.candkpeters.ceol.model.CeolModel;
import com.candkpeters.ceol.model.DeviceStatusType;
import com.candkpeters.ceol.model.ObservedControlType;
import com.candkpeters.ceol.model.OnControlChangedListener;
import com.candkpeters.ceol.model.PlayStatusType;
import com.candkpeters.ceol.model.control.ControlBase;
import com.candkpeters.ceol.view.CeolIntentFactory;
import com.candkpeters.ceol.view.MainActivity;
import com.candkpeters.ceol.widget.CeolWidgetController;
import com.candkpeters.chris.ceol.R;

import static android.app.Notification.*;

/**
 * Created by crisp on 14/02/2016.
 */
public class CeolService extends Service {

    private static final String TAG = "CeolService";
    public static final String EXECUTE_COMMAND = "ExecuteCommand";
    public static final String SCREEN_OFF = "ScreenOff";
    public static final String SCREEN_ON = "ScreenOn";
    public static final String CONFIG_CHANGED = "ConfigChanged";
    public static final String START_SERVICE = "StartService";
    public static final String STOP_SERVICE = "StopService";
    public static final String BOOT_COMPLETED = "BootCompleted";
    public static final String START_ACTIVITY_ACTION = "CeolAction";
    public static final String CONNECTIVITY_ACTION = "ConnectivityAction";
    public static final String STOP_CLING = "StopCling";
    public static final String START_CLING = "StartCling";
    private static final String CHANNEL_ID = "CeolServiceChannel";
    enum NotificationType {
        Minimal,
        StandBy,
        NonServer,
        NavigationPaused,
        NavigationPlaying
    }

    final Context context = this;

    final CeolWidgetController ceolWidgetController;
    private final CeolManager ceolManager;
    private Notification notification;
    private NotificationManagerCompat notificationManagerCompat;
    private final int notifyId = 1;

    public CeolService() {
//        ceolManager = new CeolManagerWebSvc(context);
        ceolManager = new CeolManagerWss(context);
        ceolWidgetController = new CeolWidgetController(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: Entering");
//        initializeService();
        return new CeolServiceBinder(this);
    }

//    @Nullable
//    @Override
//    public boolean onUnbind(Intent intent) {
//        Log.d(TAG, "onUnbind: Entering");
//        if ( ceolManager.ceolModel.registerCount() <= 1) {
//
//        }
//        return false;
//    }

    public CeolManager getCeolManager() {
        return ceolManager;
    }

    /*
    * On first creation
    * Start up the service in background mode
    */
    @Override
    public void onCreate() {

        ceolManager.logd(TAG, "onCreate: Entering");

        createNotificationChannel();

        notification = buildNotification(null);
        notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notifyId,notification);

        ceolManager.logd(TAG, "onCreate: Starting in foreground");
        startForeground(1, notification);

        ceolManager.initialize();
        ceolWidgetController.initialize(ceolManager);
        ceolManager.ceolModel.register(new OnControlChangedListener() {
            @Override
            public void onControlChanged(CeolModel ceolModel, ObservedControlType observedControlType, ControlBase controlBase) {
                updateNotification( ceolModel );
            }
        });

        // register receiver that handles various events
        CeolServiceReceiver mReceiver = new CeolServiceReceiver();
        registerReceiver(mReceiver, mReceiver.createIntentFilter());

    }

    private void updateNotification(CeolModel ceolModel) {
        notification = buildNotification(ceolModel);
        notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notifyId,notification);
    }

    SpannableString makeStandby( String str) {
        SpannableString ss = new SpannableString(str);
        ss.setSpan(new StyleSpan(Typeface.BOLD), 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new BackgroundColorSpan(Color.WHITE), 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(Color.argb(0xff,0xA0,0,0)), 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    SpannableString makeOn( String str) {
        SpannableString ss = new SpannableString(str);
        ss.setSpan(new StyleSpan(Typeface.BOLD), 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new BackgroundColorSpan(Color.WHITE), 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    Notification buildNotification( CeolModel ceolModel) {

        SpannableString line1;
        SpannableString line2 = null;
        SpannableString line3 = null;
        int colour = ContextCompat.getColor(context, R.color.colorPrimary);
        Bitmap bitmap = null;
        NotificationType notificationType = NotificationType.Minimal;

        if ( ceolModel == null || !ceolModel.connectionControl.isConnected()) {
            line1 = makeStandby(" Not Connected " );
            return buildNotification2(line1, null, null, bitmap, Color.argb(0, 0x40, 0, 0), notificationType);
        }
        String titleStr = " " + ceolModel.powerControl.getDeviceStatus().name() + " - " +
                ceolModel.inputControl.getSIStatus().name() + " - Vol: " + ceolModel.audioControl.getMasterVolumeString() + " ";
        if ( ceolModel.powerControl.getDeviceStatus()==DeviceStatusType.Standby ) {
            line1 = makeStandby(titleStr);
        } else {
            line1 = makeOn(titleStr);
        }
        switch ( ceolModel.inputControl.getSIStatus()) {
            case Spotify:
            case NetServer:
            case Bluetooth:
                line2 = new SpannableString( ceolModel.inputControl.trackControl.getAudioItem().getTitle() );
                line3 = new SpannableString( ceolModel.inputControl.trackControl.getAudioItem().getArtist() );
                bitmap = ceolModel.inputControl.trackControl.getAudioItem().getImageBitmap();
                notificationType = ceolModel.inputControl.trackControl.getPlayStatus()== PlayStatusType.Playing ?
                        NotificationType.NavigationPlaying : NotificationType.NavigationPaused;
                break;
            case Tuner:
                line2 = new SpannableString(ceolModel.inputControl.trackControl.getAudioItem().getTitle());
                line3 = new SpannableString( ceolModel.inputControl.trackControl.getAudioItem().getFrequency() +
                        " " + ceolModel.inputControl.trackControl.getAudioItem().getUnits() + " " + ceolModel.inputControl.trackControl.getAudioItem().getBand() );
                notificationType = NotificationType.NonServer;
                break;
            default:
                notificationType = NotificationType.NonServer;
                break;
        }
        if ( ceolModel.powerControl.getDeviceStatus()==DeviceStatusType.Standby ) {
            notificationType = NotificationType.StandBy;
        }
        return buildNotification2(line1, line2, line3, bitmap,
                colour, notificationType);

    }

    NotificationCompat.Action createCommandAction(Command command, int res, CharSequence title) {

        Intent intent = CeolIntentFactory.getIntent(command);
        intent.setClass(this,CeolServiceReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, 0);
        return new NotificationCompat.Action.Builder(res, title, pendingIntent).build();
    }

    Notification buildNotification2(
            CharSequence line1, CharSequence line2, CharSequence line3,
            Bitmap bitmap, int colour, NotificationType notificationType) {

        // Intent to open app
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationCompat.Action powerAction = createCommandAction(new CommandSetPowerToggle(), R.drawable.ic_av_power, "Power" );
        NotificationCompat.Action volumeUpAction = createCommandAction(new CommandMasterVolumeUp(), R.drawable.ic_av_up, "Vol up" );
        NotificationCompat.Action volumeDownAction = createCommandAction(new CommandMasterVolumeDown(), R.drawable.ic_av_down, "Vol down" );
        NotificationCompat.Action playAction = createCommandAction(new CommandControlPlay(), R.drawable.ic_av_play, "Play" );
        NotificationCompat.Action pauseAction = createCommandAction(new CommandControlPause(), R.drawable.ic_av_pause, "Pause" );
        NotificationCompat.Action skipForwardAction = createCommandAction(new CommandSkipForward(), R.drawable.ic_av_skip_forward, "Skip" );

        Intent stopIntent = new Intent(this, CeolServiceReceiver.class);
//        stopIntent.setClass(this, CeolServiceReceiver.class);
        stopIntent.setAction(CeolService.STOP_SERVICE);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(this, 0, stopIntent, 0);
        NotificationCompat.Action exitAction = new NotificationCompat.Action.Builder(R.drawable.ic_exit, "Exit", stopPendingIntent).build();

        Intent refreshIntent = new Intent(this, CeolServiceReceiver.class);
//        refreshIntent.setClass(this, CeolServiceReceiver.class);
        refreshIntent.setAction(CeolService.BOOT_COMPLETED);
        PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(this, 0, refreshIntent, 0);
        NotificationCompat.Action refreshAction = new NotificationCompat.Action.Builder(R.drawable.ic_refresh, "Refresh", refreshPendingIntent).build();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSubText(line1)
            .setContentTitle(line2)
            .setContentText(line3)
            .setSmallIcon(R.drawable.ic_ceol_navigation)                         // Icon to use
            .setPriority(PRIORITY_LOW)           // Only relevant for 7.1 or earlier
            .setContentIntent(notificationPendingIntent)                            // What to send if notification is clicked
            .setDeleteIntent(stopPendingIntent)
            .setColor(colour)
            .setColorized(true)
            .setLargeIcon( bitmap)
            .setShowWhen(false);

        switch ( notificationType) {
            case Minimal:
                builder
                        .addAction(exitAction)
                        .addAction(refreshAction)
                        .setStyle( new MediaStyle()
                             .setShowActionsInCompactView(0));
                break;
            case StandBy:
                builder
                        .addAction(powerAction)
                        .addAction(exitAction)
                        .setStyle( new MediaStyle()
                                .setShowActionsInCompactView(0,1));
                break;
            case NonServer:
                builder
                        .addAction(powerAction)
                        .addAction(volumeDownAction)
                        .addAction(volumeUpAction)
                        .addAction(exitAction)
                        .setStyle( new MediaStyle()
                            .setShowActionsInCompactView(0,1,2));
                break;
            case NavigationPaused:
                builder
                        .addAction(powerAction)
                        .addAction(volumeDownAction)
                        .addAction(volumeUpAction)
                        .addAction(skipForwardAction)
                        .addAction(playAction)
                        .setStyle( new MediaStyle()
                                .setShowActionsInCompactView(0,3,4));
                break;
            case NavigationPlaying:
                builder
                        .addAction(powerAction)
                        .addAction(volumeDownAction)
                        .addAction(volumeUpAction)
                        .addAction(skipForwardAction)
                        .addAction(pauseAction)
                        .setStyle( new MediaStyle()
                                .setShowActionsInCompactView(0,3,4));
                break;
        }

        return builder.build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_LOW                  // Priority for 26+
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    /* Checks if external storage is available for read and write */
//    public boolean isExternalStorageWritable() {
//        String state = Environment.getExternalStorageState();
//        return Environment.MEDIA_MOUNTED.equals(state);
//    }

    /* Checks if external storage is available to at least read */
//    public boolean isExternalStorageReadable() {
//        String state = Environment.getExternalStorageState();
//        return Environment.MEDIA_MOUNTED.equals(state) ||
//                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
//    }

    /*
    * On receiving a command
    */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        ceolManager.logd(TAG, "This is the intent " + intent);

//        if (!ceolManager.isOnWifi()) {
//            stopGathering();
//            ceolWidgetController.updateWidgets("No wifi");
//            return START_NOT_STICKY;
//        } else {
            if (intent != null) {
                String requestedAction = intent.getAction();
//            Log.i(TAG, "This is the action " + requestedAction);
                if (requestedAction != null) {
                    switch (requestedAction) {
                        case START_SERVICE:
                            ceolManager.logd(TAG, "onStartCommand: START_SERVICE");
                            ceolWidgetController.startUpdates();
//                            ceolManager.startGatherers();
                            ceolWidgetController.updateWidgets("Starting");
                            break;
                        case EXECUTE_COMMAND:
                            int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
//                        Log.i(TAG, "Package is " + this.getPackageName() + " and the widget is " + widgetId);
                            AppWidgetManager appWidgetMan = AppWidgetManager.getInstance(this);
                            ceolWidgetController.executeCommand(widgetId, appWidgetMan, intent);
                            ceolWidgetController.startUpdates();
                            break;
                        case SCREEN_OFF:
                            ceolManager.logd(TAG, "onStartCommand: SCREEN_OFF");
//                        ceolManager.pauseGatherers();
//                            stopGathering();
                            break;
                        case SCREEN_ON:
                            ceolManager.logd(TAG, "onStartCommand: SCREEN_ON");
//                            startGathering();
                            break;
                        case CONNECTIVITY_ACTION:
                            ceolManager.logd(TAG, "onStartCommand: CONNECTIVITY_ACTION");
//                            startGathering();
                            break;
                        case CONFIG_CHANGED:
                            ceolManager.logd(TAG, "onStartCommand: CONFIG_CHANGED");
                            ceolWidgetController.executeConfigChanged();
//                            stopGathering();
                            startGathering();
                            break;
                        case BOOT_COMPLETED:
                            ceolManager.logd(TAG, "onStartCommand: BOOT_COMPLETED");
                            startGathering();
                            break;
                        case STOP_CLING:
                            ceolManager.logd(TAG, "onStartCommand: STOP_CLING");
                            ceolManager.stopCling();
                            break;
                        case START_CLING:
                            ceolManager.logd(TAG, "onStartCommand: START_CLING");
                            startGathering();
                            break;
                        case STOP_SERVICE:
                            ceolManager.logd(TAG, "onStartCommand: STOP_SERVICE");
                            ceolWidgetController.stopUpdates();
                            ceolWidgetController.updateWidgets("Stopped");
                            stopGathering();
                            stopForeground(true);
                            stopSelf();
                            break;
                        default:
                            break;
                    }
                }
            } else {
                // Service was restarted
                ceolManager.logd(TAG, "onStartCommand: Restarting service - no Intent");
                startGathering();
            }
//            ceolWidgetController.updateWidgets("wifi");

            return START_STICKY;    // Try to restart if service has to be destroyed
//        }
    }

    private void stopGathering() {

        ceolManager.engineStopGatherers();
//        ceolWidgetController.executeScreenOff();
    }

    private void startGathering() {
        ceolManager.engineResumeGatherers();
        ceolWidgetController.executeScreenOn();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: Entering");
        stopGathering();
        ceolWidgetController.destroy();
    }


}
