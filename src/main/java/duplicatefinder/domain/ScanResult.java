package duplicatefinder.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScanResult {
    private HashMap<String, List<MediaFileInfo>> scanResult;

    public ScanResult() {
        scanResult = new HashMap<>();
    }

    public void add(String hash, MediaFileInfo file) {
        scanResult.putIfAbsent(hash, new ArrayList<>());
        scanResult.get(hash).add(file);
    }

    public HashMap<String, List<MediaFileInfo>> getResults() {
        return scanResult;
    }
}
