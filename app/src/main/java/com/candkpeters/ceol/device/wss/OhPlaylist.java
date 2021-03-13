package com.candkpeters.ceol.device.wss;

/*
    Example JSON:
    {"ohPlaylist":
        {"entries":
            {"1":
                {"Id":"1",
                "Url":"http://192.168.0.4:9790/minimserver//music/Music/_Pop/The*20Beatles/Abbey*20Road/01*20Come*20Together.mp3",
                "title":"Come Together",
                "creator":"The Beatles",
                "artist":"The Beatles",
                "albumArtUri":"http://192.168.0.4:9790/minimserver//music/Music/_Pop/The*20Beatles/Abbey*20Road/01*20Come*20Together.mp3/$!picture-337-46554.jpg",
                "genre":"Rock",
                "date":"1969-01-01",
                "album":"Abbey Road",
                "originalTrackNumber":"1",
                "bitrate":"40000",
                "duration":"0:04:20.000"
                },
             "2":
                {"Id":"2",
                "Url":"http://192.168.0.4:9790/minimser...
                ..."duration":"0:00:23.000"}
             },
         "Id":"1"}
      }
 */
public class OhPlaylist {
    public static class OhEntry {
        public String Id;
        public String Url;
        public String title;
        public String creator;
        public String artist;
        public String albumArtUri;
        public String genre;
        public String date;
        public String album;
        public String originalTrackNumber;
        public String bitrate;
        public String duration;
    }
    public OhPlaylist.OhEntry[] entries;
    public String Id;                       // Currently playing item
}
