package com.candkpeters.ceol.model;

/**
 * Created by crisp on 22/01/2016.
 */
public enum SIStatusType {
    NotConnected( "Not connected"),
    CD          ( "CD"),
    Tuner       ( "Tuner"),
    IRadio      ( "IRadio"),
    NetServer   ( "Music Server"),
    AnalogIn    ( "Analog In"),
    DigitalIn1  ( "Digital In 1"),
    DigitalIn2  ( "Digital In 2"),
    Bluetooth   ( "Bluetooth"),
    Ipod        ( "USB / iPod"),
    Spotify     ( "Spotify"),
    OpenHome    ( "OpenHome" );

    public final String name;

    SIStatusType(String name ) {
        this.name = name;
    }
}
