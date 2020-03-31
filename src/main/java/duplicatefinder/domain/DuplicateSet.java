package duplicatefinder.domain;

import org.apache.commons.io.FileUtils;

import java.util.List;

public class DuplicateSet {
    private String hash;
    private List<MediaFile> mediaFiles;

    public DuplicateSet(String hash, List<MediaFile> mediaFiles) {
        this.hash = hash;
        this.mediaFiles = mediaFiles;
    }

    public String getHash() {
        return hash;
    }

    public int getSize() {
        return mediaFiles.size();
    }

    public String getHumanReadableSizeOfAllFiles() {
        long sum = 0;

        for (MediaFile mediaFile : mediaFiles) {
            sum += mediaFile.getSize();
        }

        return FileUtils.byteCountToDisplaySize(sum);
    }

    public List<MediaFile> getMediaFiles() {
        return mediaFiles;
    }

}
