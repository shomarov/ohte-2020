package duplicatefinder.dao;

import duplicatefinder.domain.MediaFile;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class PhotoFileDaoTest {
  PhotoFileDao photoFileDao = new PhotoFileDao();
  MediaFile mediaFile;
  String path;
  String absolutePath;
  List<String> filesInFolder;

  @Before
  public void setUp() throws IOException {
    path = "src/test/resources";
    absolutePath = new File(path).getAbsolutePath();

    try (Stream<Path> stream = Files.walk(Paths.get(absolutePath))) {
      filesInFolder =
          stream
              .filter(file -> !Files.isDirectory(file))
              .map(Path::toString)
              .filter(file -> file.endsWith(".jpg"))
              .collect(Collectors.toList());
    } catch (IOException e) {
      e.printStackTrace();
    }

    File file = new File(absolutePath + "/IMG_20180902_211000.jpg");

    DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());

    mediaFile =
        new MediaFile(
            file.getAbsolutePath(),
            file.getName(),
            file.length(),
            FileUtils.byteCountToDisplaySize(file.length()),
            df.format(file.lastModified()));
  }

  @Test
  public void amountOfFilesFoundWithScanIsRight() {
    assertThat(filesInFolder.size(), is(photoFileDao.scanFolderForMediaFiles(path).size()));
  }

  @Test
  public void fileReadRight() throws IOException {
    MediaFile mediaFileRead = photoFileDao.read(absolutePath + "/IMG_20180902_211000.jpg");
    assertThat(mediaFile, is(mediaFileRead));
  }

  @Test
  public void fileSizeIsReadRight() throws IOException {
    MediaFile mediaFileRead = photoFileDao.read(absolutePath + "/IMG_20180902_211000.jpg");
    assertThat(mediaFile.getSize(), is(mediaFileRead.getSize()));
  }

  @Test
  public void humanReadableFileSizeReadRight() throws IOException {
    MediaFile mediaFileRead = photoFileDao.read(absolutePath + "/IMG_20180902_211000.jpg");
    assertThat(mediaFile.getHumanReadableSize(), is(mediaFileRead.getHumanReadableSize()));
  }

  @Test
  public void dateModifiedReadRight() throws IOException {
    MediaFile mediaFileRead = photoFileDao.read(absolutePath + "/IMG_20180902_211000.jpg");
    assertThat(mediaFile.getModified(), is(mediaFileRead.getModified()));
  }
}
