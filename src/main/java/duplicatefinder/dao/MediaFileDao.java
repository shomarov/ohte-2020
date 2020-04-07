package duplicatefinder.dao;

import duplicatefinder.domain.MediaFileInfo;

import java.io.File;
import java.io.IOException;

public interface MediaFileDao {
    MediaFileInfo read(File file) throws IOException;

    void save(MediaFileInfo mediaFile);

    void delete(MediaFileInfo mediaFile);

    // TODO
    // Metadata readMetadata(File file);
}
