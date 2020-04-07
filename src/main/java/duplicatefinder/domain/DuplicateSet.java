package duplicatefinder.domain;

import org.apache.commons.io.FileUtils;

import java.util.List;

public class DuplicateSet {
    private String hash;
    private List<MediaFileInfo> files;

    public DuplicateSet(String hash, List<MediaFileInfo> files) {
        this.hash = hash;
        this.files = files;
    }

    public String getHash() {
        return hash;
    }

    public int getSize() {
        return files.size();
    }

    public String getHumanReadableSizeOfAllFiles() {
        long sum = 0;

        for (MediaFileInfo file : files) {
            sum += file.getSize();
        }

        return FileUtils.byteCountToDisplaySize(sum);
    }

    public List<MediaFileInfo> getMediaFileInfos() {
        return files;
    }
}
