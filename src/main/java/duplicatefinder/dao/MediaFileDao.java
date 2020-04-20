package duplicatefinder.dao;

import duplicatefinder.domain.MediaFileInfo;
import org.apache.commons.imaging.ImageReadException;

import java.io.File;
import java.io.IOException;

public interface MediaFileDao {
    MediaFileInfo read(File file) throws IOException, ImageReadException;

    void save(MediaFileInfo mediaFile);

    boolean delete(MediaFileInfo mediaFile);

    // TODO
    // Metadata readMetadata(File file);
}
