package duplicatefinder.domain;

public interface MediaFileInfo {
    String getAbsolutePath();

    String getFilename();

    long getSize();

    String getModifiedDate();

    Metadata getMetadata();

    String getChecksum();
}
