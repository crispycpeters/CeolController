package com.candkpeters.ceol.device.wss;

/*
    Example JSON:
    {"ceolData":
        {"browse":{
            "scridValue":"",
            "scrid":"",
            "listmax":"",
            "listposition":"",
            "items":[]
            },
        "net":{
            "type":"",
            "scridValue":"100",
            "scrid":"MediaServer",
            "sourceicon":"25",
            "title":"Now Playing",
            "track":"Little Trip Heaven (On The Wings Of Your Love)",
            "artist":"Tom Waits",
            "album":"Closing Time",
            "albart":"yes",
            "format":"FLAC",
            "bitrate":"44.1kHz",
            "playstatus":"Play",
            "playcontents":"play",
            "shuffle":"OFF",
            "repeat":"OFF"
            },
         "tuner":{},
         "connection":true,
         "power":"ON",
         "input":"NET",
         "source":"MUSIC SERVER",
         "volumeDB":"-65.0",
         "volume":15,
         "type":"play",
         "imageTimeStamp":1610487720258}

        {"browse":{
            "scridValue":"102.7",
            "scrid":"MediaServer.7",
            "listmax":"13",
            "listposition":"2",
            "items":[
                {"title":">> Tag View","desc":"d","current":false},
                {"title":"Ol' '55","desc":"p","current":true},
                {"title":"I Hope That I Don't Fall In Love With You","desc":"p","current":false},
                {"title":"Virginia Avenue","desc":"p","current":false},
                {"title":"Old Shoes (& Picture Postcards)","desc":"p","current":false},
                {"title":"Midnight Lullaby","desc":"p","current":false},
                {"title":"Martha","desc":"p","current":false}
            ],
            "sourceicon":"25",
            "title":"Closing Time"
            },
        "net":{
            "type":"",
            "scridValue":"100",
            "scrid":"MediaServer",
            "sourceicon":"25",
            "title":"Now Playing",
            "track":"Little Trip Heaven (On The Wings Of Your Love)",
            "artist":"Tom Waits",
            "album":"Closing Time",
            "albart":"yes",
            "format":"FLAC",
            "bitrate":"44.1kHz",
            "playstatus":"Play",
            "playcontents":"browse",
            "shuffle":"OFF",
            "repeat":"OFF"
        },
        "tuner":{},
        "connection":true,
        "power":"ON",
        "input":"NET",
        "source":"MUSIC SERVER",
        "volumeDB":"-65.0",
        "volume":15,
        "type":"browse",
        "imageTimeStamp":1610487905435
        }
     }

     or

     {"ohPlaylist:

I/webSocket: Got volume: 15
 */
public class CeolDataRoot {
    public CeolData ceolData;
    public OhPlaylist ohPlaylist;
}
