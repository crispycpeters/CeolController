package com.candkpeters.ceol.model;

/**
 * Created by crisp on 22/01/2016.
 */
public enum CommandType {
    Set_Power,
    MasterVolume_Up,
    MasterVolume_Down,
    Set_MasterValume,
    Set_Mute,
    Set_SI,
    Goto_Favorite,
    Set_Favorite,
    Delete_Favorite,
    Tuner_Frequency_Up,
    Tuner_Frequency_Down,
    Tuner_Preset_Up,
    Tuner_Preset_Down,
    Set_Tuner_Band,
    Tuner_Set_Search_Mode,
    Cursor_Up,
    Cursor_Down,
    Cursor_Left,
    Cursor_Right,
    Play_Pause, //TODO
    Play,
    Pause,
    Stop,
    Skip_Forwards,
    Skip_Backwards,
    Start_Fast_Forwards,
    Start_Fast_Backwards,
    End_Fast,
    Repeat_One,
    Repeat_All,
    Repeat_Off,
    Random_On,
    Random_Off,
    Ipod_Browse_Toggle, //TODO
    Page_Up,
    Page_Down,
    Macro                           // Series of other commands with possible checking of statuses
}
