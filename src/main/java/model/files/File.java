package model.files;

import java.io.Serializable;

public abstract class File implements Serializable {
    private static final long serialVersionUID = 1L;
    private String filePath;

    public File(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
}