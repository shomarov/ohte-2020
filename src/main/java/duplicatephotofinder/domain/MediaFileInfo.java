package duplicatephotofinder.domain;

import java.io.File;

/**
 * Interface for file info of media files
 */
public interface MediaFileInfo {
    File getFile();

    String getAbsolutePath();

    String getFilename();

    long getSize();

    String getModifiedDate();

    Metadata getMetadata();

    String getChecksum();
}
