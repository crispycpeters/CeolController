package com.candkpeters.ceol.device;

import com.candkpeters.ceol.device.command.Command;
import com.candkpeters.ceol.device.command.CommandBaseString;
import com.candkpeters.ceol.model.CommandType;

/**
 * Created by crisp on 22/01/2016.
 */
public class NotNeeded_WebCommandTuner extends NotNeeded_WebCommand {

    public NotNeeded_WebCommandTuner(Command command) {
        super( command);
    }

    public String getWebQuery( Command command) {
        CommandType ct = CommandType.Cursor_Down;
        switch (ct) {
            case Tuner_Frequency_Up:
                return "TFANUP";
            case Tuner_Frequency_Down:
                return "TFANDOWN";
            case Tuner_Preset_Up:
                return "TPANUP";
            case Tuner_Preset_Down:
                return "TPANDOWN";
            case Set_Tuner_Band:
                if ( command instanceof CommandBaseString) {
                    return "TMAN" + ((CommandBaseString)command).getValue(); // FM or AM
                } else {
                    return NOCOMMAND;
                }
            case Tuner_Set_Search_Mode:
                if ( command instanceof CommandBaseString) {
                    return "TMAN" + ((CommandBaseString)command).getValue(); // AUTO or MANUAL
                } else {
                    return NOCOMMAND;
                }
            default:
                return super.getWebQuery();
        }
    }
}
