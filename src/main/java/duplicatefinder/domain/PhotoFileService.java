package duplicatefinder.domain;

import duplicatefinder.dao.DirectoryDao;
import duplicatefinder.dao.MediaFileDao;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains methods for interactions with Photos
 */
public class PhotoFileService implements MediaFileService {

    private final MediaFileDao mediaFileDao;

    public PhotoFileService(MediaFileDao mediaFileDao) {
        this.mediaFileDao = mediaFileDao;
    }

    /**
     * This method scans directory for duplicate photos
     * and returns a list of results
     *
     * @param folder client specified directory
     * @return list of DuplicateSet objects
     * @throws IOException if file not found
     */
    public List<DuplicateSet> scanFolderForDuplicates(File folder) throws IOException {
        DirectoryDao directoryDao = new DirectoryDao(mediaFileDao);
        ScanResult scanResult = new ScanResult();
        List<DuplicateSet> duplicates = new ArrayList<>();

        List<MediaFileInfo> files = directoryDao.readRecursively(folder);

        for (MediaFileInfo file : files) {
            scanResult.add(file.getChecksum(), file);
        }

        for (String hash : scanResult.getResults().keySet()) {
            if (scanResult.getResults().get(hash).size() > 1) {
                duplicates.add(new DuplicateSet(hash, scanResult.getResults().get(hash)));
            }
        }

        return duplicates;
    }
}
