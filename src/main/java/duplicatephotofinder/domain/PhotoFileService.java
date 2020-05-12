package duplicatephotofinder.domain;

import duplicatephotofinder.dao.PhotoFileDao;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class contains methods for interactions with Photos
 */
public class PhotoFileService implements MediaFileService {

    private final DirectoryService directoryService;
    private final PhotoFileDao photoFileDao;

    public PhotoFileService(DirectoryService directoryService, PhotoFileDao photoFileDao) {
        this.directoryService = directoryService;
        this.photoFileDao = photoFileDao;
    }

    /**
     * This method scans directory for duplicate photos recursively
     * and returns a list of DuplicateSet objects
     *
     * @param folder client specified directory
     * @return list of DuplicateSet objects
     * @throws IOException if file not found
     */
    @Override
    public List<DuplicateSet> scanFolderForDuplicates(File folder) throws IOException {
        List<MediaFileInfo> files = directoryService.read(folder);

        return listOfDuplicates(files);
    }

    /**
     * This method scans directory for duplicate photos recursively
     * and returns a list of DuplicateSet objects
     *
     * @param folder client specified directory
     * @return list of DuplicateSet objects
     * @throws IOException if file not found
     */
    @Override
    public List<DuplicateSet> scanFolderForDuplicatesRecursively(File folder) throws IOException {
        List<MediaFileInfo> files = directoryService.readRecursively(folder);

        return listOfDuplicates(files);
    }

    /**
     * Helper method that creates and returns a List of DuplicateSet objects
     * @param files takes a List<MediaFileInfo>
     * @return a List of DuplicateSet objects
     */
    private List<DuplicateSet> listOfDuplicates(List<MediaFileInfo> files) {
        List<DuplicateSet> duplicates = new ArrayList<>();

        HashMap<String, List<MediaFileInfo>> hashmap = new HashMap<>();

        for (MediaFileInfo file : files) {
            hashmap.putIfAbsent(file.getChecksum(), new ArrayList<>());
            hashmap.get(file.getChecksum()).add(file);
        }

        for (String hash : hashmap.keySet()) {
            if (hashmap.get(hash).size() > 1) {
                duplicates.add(new DuplicateSet(hashmap.get(hash)));
            }
        }

        return duplicates;
    }

    /**
     * Method scans folder for MediaFile type objects
     * @param dir DirectoryInfo object
     * @throws IOException if error
     */
    @Override
    public void scanFolderForMediaFiles(DirectoryInfo dir) throws IOException {
        dir.setFiles(directoryService.read(new File(dir.getAbsolutePath())));
    }

    /**
     * Method scans folder and all its subfolders for MediaFile type objects
     * @param dir DirectoryInfo object
     * @return List of MediaFileInfo type objects
     * @throws IOException if error
     */
    @Override
    public List<MediaFileInfo> scanFolderForMediaFilesRecursively(DirectoryInfo dir) throws IOException {
        return directoryService.readRecursively(new File(dir.getAbsolutePath()));
    }

    /**
     * Deletes a list of files
     * @param mediafiles List of MediaFileInfo objects
     */
    @Override
    public void deleteMany(List<MediaFileInfo> mediafiles) {
        for (MediaFileInfo mf : mediafiles) {
            photoFileDao.delete(mf);
        }
    }
}
