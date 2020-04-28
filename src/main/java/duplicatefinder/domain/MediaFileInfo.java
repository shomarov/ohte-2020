package duplicatefinder.domain;

/**
 * Interface for file info of media files
 */
public interface MediaFileInfo {
    String getAbsolutePath();

    String getFilename();

    long getSize();

    String getModifiedDate();

    Metadata getMetadata();

    String getChecksum();
}
