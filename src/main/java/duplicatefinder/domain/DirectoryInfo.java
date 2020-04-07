package duplicatefinder.domain;

import java.io.File;
import java.util.List;

public class DirectoryInfo {
    private File directory;
    private List<DirectoryInfo> folders;
    private List<MediaFileInfo> files;
    private String humanReadableSize;

    public DirectoryInfo(File directory) {
        this.directory = directory;
    }

    public String getAbsolutePath() {
        return directory.getAbsolutePath();
    }

    public String getFilename() {
        return directory.getName();
    }

    public long getSize() {
        if (files == null) {
            return 0;
        }
        return files.size();
    }

    public List<DirectoryInfo> getFolders() {
        return folders;
    }

    public void setFolders(List<DirectoryInfo> folders) {
        this.folders = folders;
    }

    public List<MediaFileInfo> getFiles() {
        return files;
    }

    public void setFiles(List<MediaFileInfo> files) {
        this.files = files;
        this.humanReadableSize = this.files.size() + " items";
    }

    public String getHumanReadableSize() {
        return humanReadableSize;
    }

    @Override
    public String toString() {
        return directory.getAbsolutePath();
    }
}
