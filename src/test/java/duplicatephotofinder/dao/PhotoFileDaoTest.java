package duplicatephotofinder.dao;

import duplicatephotofinder.domain.Metadata;
import duplicatephotofinder.domain.PhotoFileInfo;
import org.apache.commons.imaging.ImageReadException;
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

import static org.junit.Assert.*;

public class PhotoFileDaoTest {
    PhotoFileDao photoFileDao = new PhotoFileDao();
    PhotoFileInfo photoFileInfo;
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

        File file = new File(absolutePath + "/DSCN0010.jpg");

        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());

        photoFileInfo = new PhotoFileInfo(file);
    }

    @Test
    public void fileReadRight() throws IOException, ImageReadException {
        PhotoFileInfo mediaFileRead = photoFileDao.read(new File("src/test/resources/DSCN0010.jpg"));
        assertEquals(photoFileInfo.getChecksum(), mediaFileRead.getChecksum());
    }

    @Test
    public void fileDeletedSuccessfully() throws IOException {
        File original = new File("src/test/resources/DSCN0010.jpg");
        File forDeletion = new File("src/test/resources/forDeletion.jpg");
        FileUtils.copyFile(original, forDeletion);

        assertTrue(forDeletion.exists());
        assertTrue(FileUtils.contentEquals(original, forDeletion));
        assertTrue(photoFileDao.delete(photoFileDao.read(forDeletion)));
    }

    @Test
    public void readMetadataReturnsNullIfFileNotAnImage() throws IOException {
        File blank = new File("src/test/resources/blank.txt");
        blank.createNewFile();

        Metadata metadata = photoFileDao.readMetadata(blank);

        assertNull(metadata);
    }
}
