package com.candkpeters.ceol.device;

import com.candkpeters.ceol.device.command.Command;
import com.candkpeters.ceol.model.CommandType;

/**
 * Created by crisp on 22/01/2016.
 */
public class NotNeeded_WebCommandNetServer extends NotNeeded_WebCommand {

    public NotNeeded_WebCommandNetServer(Command command) {
        super(command);
    }

    public String getWebQuery() {
        CommandType ct = CommandType.Cursor_Down;
        switch (ct) {
            case Cursor_Up:
                return "NS90";
            case Cursor_Down:
                return "NS91";
            case Cursor_Left:
                return "NS92";
            case Cursor_Right:
                return "NS93";
//            case Play_Pause:
//                return "NS94";
            case Play:
                return "NS9A";
            case Pause:
                return "NS9B";
            case Stop:
                return "NS9C";
            case Skip_Forwards:
                return "NS9D";
            case Skip_Backwards:
                return "NS9E";
            case Start_Fast_Forwards:
                return "NS9F";
            case Start_Fast_Backwards:
                return "NS9G";
            case End_Fast:
                return "NS9Z";
            case Repeat_One:
                return "NS9H";
            case Repeat_All:
                return "NS9I";
            case Repeat_Off:
                return "NS9J";
            case Random_On:
                return "NS9K";
            case Random_Off:
                return "NS9M";
            case Page_Up:
                return "NS9X";
            case Page_Down:
                return "NS9Y";
            default:
                return super.getWebQuery();
        }
    }
}
