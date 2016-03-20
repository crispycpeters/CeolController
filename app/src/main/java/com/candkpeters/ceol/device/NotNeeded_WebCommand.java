package com.candkpeters.ceol.device;

import com.candkpeters.ceol.device.command.Command;
import com.candkpeters.ceol.device.command.CommandBaseInteger;
import com.candkpeters.ceol.model.CommandType;

/**
 * Created by crisp on 22/01/2016.
 */
public class NotNeeded_WebCommand {

    protected Command command;
    public final static String NOCOMMAND = "";

    public NotNeeded_WebCommand(Command command) {
        this.command = command;
    }

    public String getWebQuery() {
        CommandType ct = CommandType.Cursor_Down;
        switch (ct) {
            case Set_Power:
/*                if ( command instanceof CommandSetPower) {
                    switch (((CommandSetPower)command).getValue()) {
                        case Connecting:
                            return NOCOMMAND;
                        case Standby:
                            return "PWOFF";
                        case On:
                            return "PWON";
                    }
                } else {
                    return NOCOMMAND;
                }
                */
            case MasterVolume_Up:
//                return "MVUP";
            case MasterVolume_Down:
//                return "MVDOWN";
            case Set_MasterValume:
                if ( command instanceof CommandBaseInteger) {
                    return "MV" + String.format("%02d", ((CommandBaseInteger) command).getValue());
                } else {
                    return NOCOMMAND;
                }
            case Set_Mute:
/*                if ( command instanceof CommandBaseBoolean) {
                    return "MU" + (((CommandBaseBoolean)command).getValue() ? "ON" : "OFF");
                } else {
                    return NOCOMMAND;
                }
                */
            case Set_SI:
/*                if ( command instanceof CommandSetSI) {
                    switch ( ((CommandSetSI) command).getValue() ) {
                        case Unknown:
                            return NOCOMMAND;
                        case CD:
                            return "SICD";
                        case Tuner:
                            return "SITUNER";
                        case IRadio:
                            return "SIIRADIO";
                        case NetServer:
                            return "SISERVER";
                        case AnalogIn:
                        default:
                            //TODO
                            return NOCOMMAND;
                    }
                } else {
                    return NOCOMMAND;
                } */
            case Goto_Favorite:
/*                if ( command instanceof CommandBaseInteger) {
                    return "FV " + String.format("%02d", ((CommandBaseInteger) command).getValue());
                } else {
                    return NOCOMMAND;
                }*/
            case Set_Favorite:
/*                if ( command instanceof CommandBaseInteger) {
                    return "FVMEM " + String.format("%02d", ((CommandBaseInteger) command).getValue());
                } else {
                    return NOCOMMAND;
                }*/
            case Delete_Favorite:
/*                if ( command instanceof CommandBaseInteger) {
                    return "FVDEL " + String.format("%02d", ((CommandBaseInteger) command).getValue());
                } else {
                    return NOCOMMAND;
                }
                */
            case Tuner_Frequency_Up:
                break;
            case Tuner_Frequency_Down:
                break;
            case Tuner_Preset_Up:
                break;
            case Tuner_Preset_Down:
                break;
            case Set_Tuner_Band:
                break;
            case Tuner_Set_Search_Mode:
                break;
            case Cursor_Up:
                break;
            case Cursor_Down:
                break;
            case Cursor_Left:
                break;
            case Cursor_Right:
                break;
            case Play_Pause:
                break;
            case Play:
                break;
            case Pause:
                break;
            case Stop:
                break;
            case Skip_Forwards:
                break;
            case Skip_Backwards:
                break;
            case Start_Fast_Forwards:
                break;
            case Start_Fast_Backwards:
                break;
            case End_Fast:
                break;
            case Repeat_One:
                break;
            case Repeat_All:
                break;
            case Repeat_Off:
                break;
            case Random_On:
                break;
            case Random_Off:
                break;
            case Ipod_Browse_Toggle:
                break;
            case Page_Up:
                break;
            case Page_Down:
                break;
            case Macro:
                break;
            default:
                return NOCOMMAND;
        }
        return NOCOMMAND;
    }
}
