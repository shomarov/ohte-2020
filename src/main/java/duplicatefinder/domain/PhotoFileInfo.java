package duplicatefinder.domain;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Locale;
import java.util.Objects;

/**
 * This class encapsulates all essential information and metadata of a photo file
 */
public class PhotoFileInfo implements MediaFileInfo {
    private final File file;
    private final String checksum;
    private String humanReadableSize;

    private Metadata metadata;

    public PhotoFileInfo(File file)
            throws IOException {
        this.file = file;
        setHumanReadableSize();
        this.checksum = calculateChecksum();
    }

    @Override
    public String getAbsolutePath() {
        return file.getAbsolutePath();
    }

    @Override
    public String getFilename() {
        return file.getName();
    }

    @Override
    public long getSize() {
        return file.length();
    }

    public String getHumanReadableSize() {
        return humanReadableSize;
    }

    /**
     * Method generates human readable size of type String
     */
    public void setHumanReadableSize() {
        this.humanReadableSize = FileUtils.byteCountToDisplaySize(this.getSize());
    }

    @Override
    public String getModifiedDate() {
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
        return df.format(file.lastModified());
    }

    @Override
    public String getChecksum() {
        return checksum;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    private String calculateChecksum() throws IOException {
        return DigestUtils.md5Hex(new FileInputStream(file));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PhotoFileInfo photoFileInfo = (PhotoFileInfo) o;
        return checksum.equals(photoFileInfo.checksum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(checksum);
    }

    @Override
    public String toString() {
        return file.getAbsolutePath();
    }
}
