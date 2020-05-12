package duplicatephotofinder.domain;

import duplicatephotofinder.dao.DirectoryDao;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This class contains methods for interactions with directories on file system
 */
public class DirectoryService {

    final private DirectoryDao directoryDao;

    public DirectoryService(DirectoryDao directoryDao) {
        this.directoryDao = directoryDao;
    }

    /**
     * This method takes a user selected folder and scans for all folders and subfolders in it
     * @param selectedDirectory class File
     * @return DirectoryInfo object
     */
    public DirectoryInfo readDirectoryTree(File selectedDirectory) {
        return directoryDao.readDirectoryTree(selectedDirectory);
    }

    /**
     * Scans user selected directory for MediaFile objects
     * @param directory class File
     * @return List of MediaFileInfo
     * @throws IOException if error
     */
    public List<MediaFileInfo> read(File directory) throws IOException {
        return directoryDao.read(directory);
    }

    /**
     * Scans user selected directory and all its subdirectories for MediaFile objects
     * @param directory class File
     * @return List of MediaFileInfo
     * @throws IOException if error
     */
    public List<MediaFileInfo> readRecursively(File directory) throws IOException {
        return directoryDao.readRecursively(directory);
    }
}
