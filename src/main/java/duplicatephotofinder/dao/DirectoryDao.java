package duplicatephotofinder.dao;

import duplicatephotofinder.domain.DirectoryInfo;
import duplicatephotofinder.domain.MediaFileInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * This class offers methods used for interaction with directories
 */
public class DirectoryDao {
    private final MediaFileDao mediaFileDao;

    public DirectoryDao(MediaFileDao mediaFileDao) {
        this.mediaFileDao = mediaFileDao;
    }

    /**
     * Method reads specified directory and returns a list of MediaFileInfo objects
     *
     * @param directory client specified directory
     * @return List of MediaFileInfo objects that hold file information
     * @throws IOException if directory does not exist
     */
    public List<MediaFileInfo> read(File directory) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(directory.getAbsolutePath()), 1)) {
            return walk(paths);
        }
    }

    /**
     * Method reads specified directory RECURSIVELY and returns a list of MediaFileInfo objects
     *
     * @param directory client defined directory
     * @return List of MediaFileInfo objects that hold file information
     * @throws IOException if directory does not exist
     */
    public List<MediaFileInfo> readRecursively(File directory) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(directory.getAbsolutePath()))) {
            return walk(paths);
        }
    }

    /**
     * Method reads directory tree structure starting from the specified directory
     *
     * @param directory client specified directory
     * @return populated DirectoryInfo
     */
    public DirectoryInfo readDirectoryTree(File directory) {
        DirectoryInfo di = new DirectoryInfo(directory);
        List<DirectoryInfo> folders = new ArrayList<>();

        File[] folderList =
                directory.listFiles(file -> (file.isDirectory() && !file.getName().startsWith(".")));

        if (folderList == null) {
            return null;
        }

        Arrays.sort(folderList);

        for (File item : folderList) {
            folders.add(readDirectoryTree(item));
        }

        di.setFolders(folders);

        return di;
    }

    /**
     * Walks through directory tree and collects MediaFile type objects
     * @param paths directory path
     * @return List of MediaFileInfo
     */
    private List<MediaFileInfo> walk(Stream<Path> paths) {
        return paths
                .map(Path::toFile)
                .filter(file -> !file.isDirectory())
                .filter(file -> !file.getName().startsWith("."))
                .map(file -> {
                    try {
                        return mediaFileDao.read(file);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}
