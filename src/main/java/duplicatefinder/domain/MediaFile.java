package duplicatefinder.domain;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

public class MediaFile {
  private String path;
  private String filename;
  private long size;
  private String humanReadableSize;
  private String modified;
  private String checksum;

  private Metadata metadata;

  public MediaFile(
      String path, String filename, long size, String humanReadableSize, String modified)
      throws IOException {
    this.path = path;
    this.filename = filename;
    this.size = size;
    this.humanReadableSize = humanReadableSize;
    this.modified = modified;
    this.checksum = calculateChecksum();
  }

  public MediaFile(
      String path,
      String filename,
      long size,
      String humanReadableSize,
      String modified,
      Metadata metadata)
      throws IOException {
    this.path = path;
    this.filename = filename;
    this.size = size;
    this.humanReadableSize = humanReadableSize;
    this.modified = modified;
    this.metadata = metadata;
    this.checksum = calculateChecksum();
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public String getHumanReadableSize() {
    return humanReadableSize;
  }

  public void setHumanReadableSize(String humanReadableSize) {
    this.humanReadableSize = humanReadableSize;
  }

  public String getModified() {
    return modified;
  }

  public void setModified(String modified) {
    this.modified = modified;
  }

  public String getChecksum() {
    return checksum;
  }

  public void setChecksum(String checksum) {
    this.checksum = checksum;
  }

  public Metadata getMetadata() {
    return metadata;
  }

  public void setMetadata(Metadata metadata) {
    this.metadata = metadata;
  }

  private String calculateChecksum() throws IOException {
    return DigestUtils.md5Hex(new FileInputStream(path));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MediaFile mediaFile = (MediaFile) o;
    return checksum.equals(mediaFile.checksum);
  }

  @Override
  public int hashCode() {
    return Objects.hash(checksum);
  }

  @Override
  public String toString() {
    return "MediaFile{"
        + "path='"
        + path
        + '\''
        + ", filename='"
        + filename
        + '\''
        + ", size="
        + size
        + ", humanReadableSize='"
        + humanReadableSize
        + '\''
        + ", modified='"
        + modified
        + '\''
        + ", metadata="
        + metadata
        + '}';
  }
}
