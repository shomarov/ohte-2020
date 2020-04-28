package duplicatefinder.domain;

import org.apache.commons.io.FileUtils;

import java.util.List;

/**
 * DuplicateSet class that contains a list of identical files
 * located at different locations on the file system
 */
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

    /**
     * Method counts and returns the human readable sum of sizes all
     * files in the set of duplicates
     *
     * @return human readable sum of all files in the set
     */
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
