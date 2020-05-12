package duplicatephotofinder.domain;

import java.io.File;
import java.util.List;

/**
 * DirectoryInfo class that encapsulates information on the directory tree structure
 * and the files they contain
 */
public class DirectoryInfo {
    private final File directory;
    private List<DirectoryInfo> folders;
    private List<MediaFileInfo> files;

    public DirectoryInfo(File directory) {
        this.directory = directory;
    }

    public String getAbsolutePath() {
        return directory.getAbsolutePath();
    }

    public String getFilename() {
        return directory.getName();
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
    }

    @Override
    public String toString() {
        return directory.getAbsolutePath();
    }
}
