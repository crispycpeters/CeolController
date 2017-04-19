package com.candkpeters.ceol.device;

/**
 * Created by crisp on 08/01/2016.
 */
public class CeolDeviceCommandString {

    public static String setPower( boolean onOff){
        return "PW" + (onOff?"ON":"OFF");
    }

    public static String setMute( boolean onOff){
        return "MU" + (onOff?"ON":"OFF");
    }

/*    public static String setVolume( int volume) {
        return "MV" + String.format("%02d",volume);
    }
*/
/*    public static String setVolumeUp() {
        return "MVUP";
    }
*/
/*    public static String setVolumeDown() {
        return "MVDOWN";
    }
*/
/*    public static String setSkipBackwards(CeolDevice ceolDevice) {
        switch ( ceolDevice.getSIStatus()) {
            case CD:
            case IRadio:
            case NetServer:
            case Tuner:
                return "NS9E";
            default:
                return "";
        }
    }
*/
/*    public static String setSkipForwards(CeolDevice ceolDevice) {
        switch ( ceolDevice.getSIStatus()) {
            case CD:
            case IRadio:
            case getAudioItem():
            case Tuner:
                return "NS9D";
            default:
                return "";
        }
    }
*/
}
