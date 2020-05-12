package duplicatephotofinder.domain;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This interface to be used for interactions with Media files
 */
public interface MediaFileService {
    List<DuplicateSet> scanFolderForDuplicates(File folder) throws IOException;

    List<DuplicateSet> scanFolderForDuplicatesRecursively(File folder) throws IOException;

    void scanFolderForMediaFiles(DirectoryInfo dir) throws IOException;

    List<MediaFileInfo> scanFolderForMediaFilesRecursively(DirectoryInfo dir) throws IOException;

    void deleteMany(List<MediaFileInfo> mediafiles);
}
