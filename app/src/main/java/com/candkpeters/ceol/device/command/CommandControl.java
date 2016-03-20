package com.candkpeters.ceol.device.command;

import com.candkpeters.ceol.model.PlayStatusType;

/**
 * Created by crisp on 25/01/2016.
 */
public class CommandControl extends Command {

    private PlayStatusType playStatusType;
    private boolean isToggle = false;

    public CommandControl() {
        this( PlayStatusType.Stop);
        isToggle = true;
    }

    public CommandControl(PlayStatusType playStatusType) {
        super();
        this.playStatusType = playStatusType;
    }

    @Override
    protected boolean isSuccessful() {
        if ( isToggle) {
/*
            switch ( ceolDevice.getPlayStatus()) {
                case Unknown:
                    break;
                case Play:
                    break;
                case Pause:
                case Stop:
                    return playStatusType == PlayStatusType.Pause
                    break;
            }
*/
            return true;
        }
        else {

            if (ceolDevice.getPlayStatus() == playStatusType) return true;
            else return false;
        }
    }

    @Override
    public void execute() {
        String commandString = null;

        switch (ceolDevice.getSIStatus()) {

            case Unknown:
                break;
            case CD:
                // TODO
                break;
            case Tuner:
                break;
            case IRadio:
                break;
            case NetServer:
                if ( isToggle) {
                    playStatusType = toggleCurrentStatus();
                }
                switch (playStatusType) {
                    case Unknown:
                        break;
                    case Play:
                        commandString = "NS9A";
                        break;
                    case Pause:
                        commandString = "NS9B";
                        break;
                    case Stop:
                        commandString = "NS9C";
                        break;
                }
                break;
            case AnalogIn:
                break;
        }
        ceolCommandManager.sendCommand(commandString);
    }

    private PlayStatusType toggleCurrentStatus() {
        PlayStatusType currentPlayStatus;
        switch (ceolDevice.getPlayStatus()) {
            case Play:
                currentPlayStatus = PlayStatusType.Pause;
                break;
            default:
            case Unknown:
            case Pause:
            case Stop:
                currentPlayStatus = PlayStatusType.Play;
                break;
        }
        return currentPlayStatus;
    }

/*
    @Override
    protected Intent fillIntent(Intent intent) {
        return intent.putExtra(CeolService_Old.EXECUTE_COMMAND_VALUE, playStatusType.toString());
    }
*/

    @Override
    public String getParameterAsString() {
        return playStatusType.toString();
    }

    @Override
    public void initialize(String valueString) {
        playStatusType = PlayStatusType.valueOf(valueString);
    }

    public String toString() {
        return this.getClass().getSimpleName() + "(" + playStatusType+ ")";
    }

}
