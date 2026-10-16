package model.files;

public class Video extends File {
    private static final long serialVersionUID = 1L;
    private Quality quality;
    private VideoFormat videoFormat;
    private int videoTime;

    public enum Quality {
        Low(360), Medium(720), High(1080);
        private final int value;
        Quality(int value) { this.value = value; }
        public int getValue() { return value; }
    }

    public enum VideoFormat {
        MP4, MKV, MOV, WMV;
    }

    public Video(String filePath, Quality quality, VideoFormat videoFormat, int videoTime) {
        super(filePath);
        this.quality = quality;
        this.videoFormat = videoFormat;
        this.videoTime = videoTime;
    }

    public Quality getQuality() { return quality; }
    public void setQuality(Quality quality) { this.quality = quality; }
    public VideoFormat getVideoFormat() { return videoFormat; }
    public void setVideoFormat(VideoFormat videoFormat) { this.videoFormat = videoFormat; }
    public int getVideoTime() { return videoTime; }
    public void setVideoTime(int videoTime) { this.videoTime = videoTime; }
}