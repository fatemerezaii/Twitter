package model.files;

public class Image extends File {
    private static final long serialVersionUID = 1L;
    private ImgFormat imgFormat;

    public enum ImgFormat {
        JPEG, PNG, SVG, WEBP;
    }

    public Image(String filePath, ImgFormat imgFormat) {
        super(filePath);
        this.imgFormat = imgFormat;
    }

    public ImgFormat getImgFormat() { return imgFormat; }
    public void setImgFormat(ImgFormat imgFormat) { this.imgFormat = imgFormat; }
}