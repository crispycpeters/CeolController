package com.candkpeters.ceol.device.command;

import android.util.Log;

import com.candkpeters.ceol.device.OnCeolStatusChangedListener;
import com.candkpeters.ceol.model.CeolBrowseEntry;
import com.candkpeters.ceol.model.DirectionType;
import com.candkpeters.ceol.model.PlayStatusType;
import com.candkpeters.ceol.model.SIStatusType;
import com.candkpeters.ceol.model.StreamingStatus;

/**
 * Created by crisp on 25/01/2016.
 */
class CommandBrowseInto extends CommandBaseString {

    private int startPosition;
    private boolean isFound;
    private int targetPosition;
    private boolean playFirstEntry;

    private enum SearchSteps {
        NotStarted,
        MovingDown,
        Playing,
        MovingRight
    }
    private SearchSteps searchSteps = SearchSteps.NotStarted;
    private static final String TAG = "CommandBrowseInto";

    CommandBrowseInto(String value, boolean playFirstEntry) {
        super(value);
        isFound = false;
        this.playFirstEntry = playFirstEntry;
    }

    private CommandBrowseInto(String value) {
        this(value, false);
    }

    CommandBrowseInto() {
        this(null);
    }

    @Override
    protected void onCeolStatusChangedListener() {
        executeActions();
    }

    @Override
    public boolean isSuccessful() {
        Log.d(TAG, "isSuccessful: isfound=" + isFound + " here=" + ceolModel.inputControl.trackControl.getAudioItem().getTitle() + " value=" + getValue());

        if (getValue() != null &&
                ceolModel.inputControl.getSIStatus() == SIStatusType.NetServer ) {
            if ( ceolModel.inputControl.trackControl.getAudioItem().getTitle().equalsIgnoreCase(getValue())) {
                if ( playFirstEntry ) {
                    return (ceolModel.inputControl.trackControl.getPlayStatus() == PlayStatusType.Playing);
                } else {
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void execute() {
        if (getValue() != null && ceolModel.inputControl.getStreamingStatus() == StreamingStatus.CEOL &&
                ceolModel.inputControl.navigatorControl.getListMax() >0 ) {
            resetPosition();
            checkForEntry();
        }
    }

    private void executeActions() {

        switch ( searchSteps) {
            case NotStarted:
                break;
            case MovingDown:
                checkForEntry();
                break;
            case MovingRight:
                checkForRight();
                break;
//            case Playing:
//                checkForPlaying();
//                break;
        }
    }

    private void checkForRight() {
        Log.d(TAG, "checkForRight: titleView=" +ceolModel.inputControl.trackControl.getAudioItem().getTitle() + " startpos=" + startPosition +
                " targetPos=" + targetPosition + ", selpos=" + ceolModel.inputControl.navigatorControl.getSelectedPosition());
        if ( ceolModel.inputControl.trackControl.getAudioItem().getTitle().equalsIgnoreCase(getValue())) {

            if ( playFirstEntry ) {
                Log.d(TAG, "checkForRight: Playing entry");
//                searchSteps = SearchSteps.Playing;
                new CommandControl(PlayStatusType.Playing).execute(ceolManager, new OnCeolStatusChangedListener() {
                    @Override
                    public void onCeolStatusChanged() {
                        if ( ceolModel.inputControl.trackControl.getPlayStatus() == PlayStatusType.Playing) {
                            finish(true);
                        }
                    }
                });
            } else {
                Log.d(TAG, "checkForRight: Not playing entry. We're done.");
                // That's it
                finish( true);
            }
        }
    }

    private void checkForEntry() {
        Log.d(TAG, "checkForEntry: startpos=" + startPosition + " targetPos=" + targetPosition + ", selpos=" + ceolModel.inputControl.navigatorControl.getSelectedPosition());
        if ( ceolModel.inputControl.navigatorControl.getSelectedPosition() == targetPosition) {

            CeolBrowseEntry entry = ceolModel.inputControl.navigatorControl.getSelectedEntry();
            if (entry == null) {
                Log.e(TAG, "checkForEntry: GetBrowseList: Problem - no selected entry");
                finish(false);
            }
            else if (entry.isEmpty()) {
                Log.e(TAG, "checkForEntry: GetBrowseList: Problem - entry is empty");
                finish(false);
            }
            else if (!entry.Text.equalsIgnoreCase(getValue())) {
                if ( isBackToWhereWeStarted()) {
                    Log.e(TAG, "checkForEntry: GetBrowseList: Sorry, could not find \"" + getValue() + "\"");
                    finish(false);
                } else {
                    Log.d(TAG, "checkForEntry: GetBrowseList: Wrong entry \"" + entry.Text + "\" found. Go down.");
                    searchSteps = SearchSteps.MovingDown;
                    advancePosition();
                    //Todo - use callback method - need to send string to Cursor down like cursor left
                    new CommandCursor(DirectionType.Down).execute(ceolManager);
                }
            } else {
                Log.d(TAG, "checkForEntry: GetBrowseList: Found entry \"" + entry.Text + "\". Go right. ");
                resetPosition();
                searchSteps = SearchSteps.MovingRight;
                new CommandCursor(DirectionType.Right).execute(ceolManager);
            }
        } else {
            // We are waiting for the Move Down to complete
            Log.d(TAG, "checkForEntry: GetBrowseList: Waiting for entry \"" + targetPosition + "\"");
            //TODO TImeout?
        }
    }

    private boolean isBackToWhereWeStarted() {
        if ( startPosition == -1 ) {
            // First time
            startPosition = ceolModel.inputControl.navigatorControl.getSelectedPosition();
            return false;
        } else {
            return ( startPosition == ceolModel.inputControl.navigatorControl.getSelectedPosition());
        }
    }

    private void finish(boolean isFound) {
        this.isFound = isFound;
        setIsDone(true);
    }

    private void resetPosition() {
        targetPosition = ceolModel.inputControl.navigatorControl.getSelectedPosition();
        startPosition = -1;
    }

    private void advancePosition() {
        targetPosition = (targetPosition) % ceolModel.inputControl.navigatorControl.getListMax() + 1;
    }

}
