package com.techweblearn.mediastreaming.EventBus;

import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.techweblearn.mediastreaming.Models.PlayerInfo;
import com.techweblearn.mediastreaming.Models.VideoInfo;
import com.techweblearn.mediastreaming.Streaming.StreamStatusExtended;

public class Events {


        public static class PlayerInfoBus
        {
            private PlayerInfo playerInfo;
            public PlayerInfoBus(PlayerInfo playerInfo)
            {
                this.playerInfo=playerInfo;
            }

            public PlayerInfo getPlayerInfo() {
                return playerInfo;
            }
        }


        public static class StreamStatusExtendedBus
        {

            private StreamStatusExtended streamStatusExtended;

            public StreamStatusExtendedBus(StreamStatusExtended streamStatusExtended) {
                this.streamStatusExtended = streamStatusExtended;
            }

            public StreamStatusExtended getStreamStatusExtended() {
                return streamStatusExtended;
            }
        }


        public static class StreamStatusBus
        {

            private StreamStatus streamStatus;

            public StreamStatusBus(StreamStatus streamStatus) {
                this.streamStatus = streamStatus;
            }

            public StreamStatus getStreamStatus() {
                return streamStatus;
            }
        }


        public static class StreamStartedBus
        {
          private VideoInfo videoInfo;

            public StreamStartedBus(VideoInfo videoInfo) {
                this.videoInfo = videoInfo;
            }

            public VideoInfo getVideoInfo() {
                return videoInfo;
            }
        }


        public static class VideoInfoBus
        {
            private VideoInfo videoInfo;

            public VideoInfoBus(VideoInfo videoInfo) {
                this.videoInfo = videoInfo;
            }

            public VideoInfo getVideoInfo() {
                return videoInfo;
            }
        }

        public static class StreamReadyBus
        {
            public StreamReadyBus() {
            }
        }

        public static class StreamDownloadComplete
        {
            public StreamDownloadComplete() {
            }
        }


        public static class StreamPrepared
        {
            public StreamPrepared() {
            }
        }



}
