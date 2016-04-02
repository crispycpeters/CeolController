package com.candkpeters.ceol.device.command;

import android.content.Intent;
import android.util.Log;

import com.candkpeters.ceol.model.CeolBrowseEntry;
import com.candkpeters.ceol.model.DirectionType;
import com.candkpeters.ceol.model.PlayStatusType;
import com.candkpeters.ceol.model.SIStatusType;

/**
 * Created by crisp on 25/01/2016.
 */
public class CommandBrowseInto extends CommandBaseString {

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

    public CommandBrowseInto(String value, boolean playFirstEntry) {
        super(value);
        isFound = false;
        this.playFirstEntry = playFirstEntry;
    }

    public CommandBrowseInto(String value) {
        this(value, false);
    }

    public CommandBrowseInto() {
        this(null);
    }

    @Override
    protected void onCeolStatusChangedListener() {
        executeActions();
    }

    @Override
    public boolean isSuccessful() {
        Log.d(TAG, "isSuccessful: isfound=" + isFound + " here=" + ceolDevice.NetServer.getTitle() + " value=" + getValue());
        Log.d(TAG, "isSuccessful: isDone=" + isDone());

        if (getValue() != null && ceolDevice != null &&
                ceolDevice.getSIStatus() == SIStatusType.NetServer ) {
            if ( ceolDevice.NetServer.getTitle().equalsIgnoreCase(getValue())) {
                if ( playFirstEntry ) {
                    return (ceolDevice.getPlayStatus() == PlayStatusType.Play);
                } else {
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
//        return isFound;
    }

    @Override
    public void execute() {
        if (getValue() != null && ceolDevice != null && ceolDevice.getSIStatus() == SIStatusType.NetServer &&
                ceolDevice.NetServer.getListMax() >0 ) {
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
            case Playing:
                checkForPlaying();
                break;
        }
    }

    private void checkForPlaying() {
        if ( ceolDevice.getPlayStatus() == PlayStatusType.Play) {
            finish(true);
        }
    }

    private void checkForRight() {
        Log.d(TAG, "checkForRight: title=" +ceolDevice.NetServer.getTitle() + " startpos=" + startPosition + " targetPos=" + targetPosition + ", selpos=" + ceolDevice.NetServer.getSelectedPosition());
        if ( ceolDevice.NetServer.getTitle().equalsIgnoreCase(getValue())) {

            if ( playFirstEntry ) {
                Log.d(TAG, "checkForRight: Playing entry");
                searchSteps = SearchSteps.Playing;
                new CommandControl(PlayStatusType.Play).execute(ceolCommandManager);
            } else {
                Log.d(TAG, "checkForRight: Not playing entry. We're done.");
                // That's it
                finish( true);
            }
        }
    }

    private void checkForEntry() {
        Log.d(TAG, "checkForEntry: startpos=" + startPosition + " targetPos=" + targetPosition + ", selpos=" + ceolDevice.NetServer.getSelectedPosition());
        if ( ceolDevice.NetServer.getSelectedPosition() == targetPosition) {

            CeolBrowseEntry entry = ceolDevice.NetServer.getSelectedEntry();
            if (entry == null) {
                Log.e(TAG, "checkForEntry: GetBrowseList: Problem - no selected entry");
                finish(false);
            }
            if (entry.isEmpty()) {
                Log.e(TAG, "checkForEntry: GetBrowseList: Problem - entry is empty");
                finish(false);
            }
            if (!entry.Text.equalsIgnoreCase(getValue())) {
                if ( isBackToWhereWeStarted()) {
                    Log.e(TAG, "checkForEntry: GetBrowseList: Sorry, could not find \"" + getValue() + "\"");
                    finish(false);
                } else {
                    Log.d(TAG, "checkForEntry: GetBrowseList: Wrong entry \"" + entry.Text + "\" found. Go down.");
                    searchSteps = searchSteps.MovingDown;
                    advancePosition();
                    new CommandCursor(DirectionType.Down).execute(ceolCommandManager);
                }
            } else {
                Log.d(TAG, "checkForEntry: GetBrowseList: Found entry \"" + entry.Text + "\". Go right. ");
                resetPosition();
                searchSteps = SearchSteps.MovingRight;
                new CommandCursor(DirectionType.Right).execute(ceolCommandManager);
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
            startPosition = ceolDevice.NetServer.getSelectedPosition();
            return false;
        } else {
            return ( startPosition == ceolDevice.NetServer.getSelectedPosition());
        }
    }

    private void finish(boolean isFound) {
        this.isFound = isFound;
        setIsDone(true);
    }

    private void resetPosition() {
        targetPosition = ceolDevice.NetServer.getSelectedPosition();
        startPosition = -1;
    }

    private void advancePosition() {
        targetPosition = (targetPosition)%ceolDevice.NetServer.getListMax() + 1;
    }

}
