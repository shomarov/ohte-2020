package duplicatefinder.domain;

import duplicatefinder.dao.MediaFileDao;
import duplicatefinder.dao.PhotoFileDao;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PhotoFileServiceTest {

    PhotoFileService photoFileService;
    MediaFileDao mediaFileDao;

    @Before
    public void setup() {
        mediaFileDao = new PhotoFileDao();
        photoFileService = new PhotoFileService(mediaFileDao);
    }

    @Test
    public void findsAllDuplicatesInFolder() throws IOException {
        File folderToScan = new File("src/test/resources/folderWithDuplicatePhotos");

        List<DuplicateSet> duplicateSets = photoFileService.scanFolderForDuplicates(folderToScan);

        assertEquals(9, duplicateSets.size());
    }
}
