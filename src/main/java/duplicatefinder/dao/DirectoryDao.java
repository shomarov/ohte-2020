package duplicatefinder.dao;

import duplicatefinder.domain.DirectoryInfo;
import duplicatefinder.domain.MediaFileInfo;
import org.apache.commons.imaging.ImageReadException;

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

public class DirectoryDao {
    private MediaFileDao mediaFileDao;

    public DirectoryDao() {
    }

    // Use strategy design pattern here
    public void setMediaFileDao(MediaFileDao mediaFileDao) {
        this.mediaFileDao = mediaFileDao;
    }

    public List<MediaFileInfo> read(File directory) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(directory.getAbsolutePath()), 1)) {
            return walk(paths);
        }
    }

    public List<MediaFileInfo> readRecursively(File directory) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(directory.getAbsolutePath()))) {
            return walk(paths);
        }
    }

    public DirectoryInfo readDirectoryTree(File directory) {
        DirectoryInfo di = new DirectoryInfo(directory);
        List<DirectoryInfo> folders = new ArrayList<>();

        File[] folderList =
                directory.listFiles(file -> (file.isDirectory() && !file.getName().startsWith(".")));

        assert folderList != null;
        Arrays.sort(folderList);

        for (File item : folderList) {
            folders.add(readDirectoryTree(item));
        }

        di.setFolders(folders);

        return di;
    }

    private List<MediaFileInfo> walk(Stream<Path> paths) {
        return paths
                .map(Path::toFile)
                .filter(file -> !file.isDirectory())
                .filter(file -> !file.getName().startsWith("."))
                .map(file -> {
                    try {
                        return mediaFileDao.read(file);
                    } catch (IOException | ImageReadException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}
