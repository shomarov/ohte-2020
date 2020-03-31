package duplicatefinder.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScanResult {
    private HashMap<String, List<MediaFile>> scanResult;

    public ScanResult() {
        scanResult = new HashMap<>();
    }

    public void add(String hash, MediaFile mediaFile) {
        scanResult.putIfAbsent(hash, new ArrayList<MediaFile>());
        scanResult.get(hash).add(mediaFile);
    }

    public HashMap<String, List<MediaFile>> getResults() {
        return scanResult;
    }

}
