package duplicatefinder.dao;

import duplicatefinder.domain.MediaFile;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

public interface MediaFileDao {
  List<String> scanFolderForMediaFiles(String path);

  default MediaFile read(String path) throws IOException {
    File file = new File(path);

    String filename = getFilename(file);
    long size = getFileSize(file);
    String humanReadableSize = getHumanReadableFileSize(file);
    String modified = getModifiedDate(file);

    return new MediaFile(path, filename, size, humanReadableSize, modified);
  }

  default void update(MediaFile mediaFile) {
    // TODO
  }

  default void delete(MediaFile mediaFile) {
    // TODO
  }

  private String getFilename(File file) {
    return file.getName();
  }

  private long getFileSize(File file) {
    return file.length();
  }

  private String getHumanReadableFileSize(File file) {
    long size = getFileSize(file);
    return FileUtils.byteCountToDisplaySize(size);
  }

  private String getModifiedDate(File file) {
    DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
    return df.format(file.lastModified());
  }
}
