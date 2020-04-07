package duplicatefinder.domain;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Locale;
import java.util.Objects;

public class PhotoFileInfo implements MediaFileInfo {
    private File file;
    private String humanReadableSize;
    private String checksum;

    private Metadata metadata;

    public PhotoFileInfo(File file)
            throws IOException {
        this.file = file;
        setHumanReadableSize();
        this.checksum = calculateChecksum();
    }

    public PhotoFileInfo(
            File file, Metadata metadata)
            throws IOException {
        this.file = file;
        setHumanReadableSize();
        this.metadata = metadata;
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
