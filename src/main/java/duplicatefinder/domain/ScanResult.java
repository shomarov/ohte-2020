package duplicatefinder.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class contains result of a directory scan organized in a HashSet by
 * files md5 hash value
 */
public class ScanResult {
    private HashMap<String, List<MediaFileInfo>> scanResult;

    public ScanResult() {
        scanResult = new HashMap<>();
    }

    /**
     * Method adds file to result set
     *
     * @param hash          md5
     * @param mediaFileInfo instance of MediaFileInfo
     */
    public void add(String hash, MediaFileInfo mediaFileInfo) {
        scanResult.putIfAbsent(hash, new ArrayList<>());
        scanResult.get(hash).add(mediaFileInfo);
    }

    public HashMap<String, List<MediaFileInfo>> getResults() {
        return scanResult;
    }
}
