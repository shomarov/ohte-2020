package duplicatefinder.domain;

import duplicatefinder.dao.MediaFileDao;

import java.io.IOException;
import java.util.List;

public class MediaFileService {
  private MediaFileDao mediaFileDao;

  public MediaFileService(MediaFileDao mediaFileDao) {
    this.mediaFileDao = mediaFileDao;
  }

  public ScanResult scanFolderForDuplicates(String path) throws IOException {
    ScanResult scanResult = new ScanResult();

    List<String> filepaths = mediaFileDao.scanFolderForMediaFiles(path);

    for (String filepath : filepaths) {
      MediaFile mediaFile = readFile(filepath);
      scanResult.add(mediaFile.getChecksum(), mediaFile);
    }

    return scanResult;
  }

  private MediaFile readFile(String path) throws IOException {
    return mediaFileDao.read(path);
  }

  private void save(MediaFile mediaFile) {
    mediaFileDao.update(mediaFile);
  }
}
