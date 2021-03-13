package com.candkpeters.ceol.device.wss;

/*
    Example JSON:
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
I/webSocket: Got volume: 15
 */
public class CeolData {
    public static class CeolDataBrowseItem {
        public String title;
        public String desc;
        public boolean current;
    }
    public static class CeolDataBrowse {
        public String scridValue;
        public String scrid;
        public String listmax;
        public String listposition;
        public CeolDataBrowseItem[] items;
        public String sourceicon;
        public String title;
    }
    public static class CeolDataNet {
        public String type;
        public String scridValue;
        public String scrid;
        public String sourceicon;
        public String title;
        public String track;
        public String artist;
        public String album;
        public String albart;
        public String format;
        public String bitrate;
        public String playstatus;
        public String playcontents;
        public String shuffle;
        public String repeat;
        public int duration = 0;
    }
    public static class CeolDataTuner {
        public String name;
        public String frequency;
        public String band;
    }
    public CeolDataBrowse browse;
    public CeolDataNet net;
    public CeolDataTuner tuner;

    public boolean connection;
    public String power;
    public String input;
    public String source;
    public String volumeDB;
    public int volume;
    public String type;
    public String status;
    public long lastUpdate;
    public long imageTimeStamp;
}
