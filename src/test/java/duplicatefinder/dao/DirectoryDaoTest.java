package duplicatefinder.dao;

import duplicatefinder.domain.DirectoryInfo;
import duplicatefinder.domain.MediaFileInfo;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DirectoryDaoTest {
    DirectoryDao directoryDao;

    @Before
    public void setup() {
        directoryDao = new DirectoryDao(new PhotoFileDao());
    }

    @Test
    public void readsDirectoryTreeOfGivenFolderCorrectly() {
        DirectoryInfo directoryInfo = directoryDao.readDirectoryTree(new File("src"));
        assertEquals(2, directoryInfo.getFolders().size());
    }

    @Test
    public void readsAmountOfFilesCorrectly() throws IOException {
        directoryDao.setMediaFileDao(new PhotoFileDao());
        List<MediaFileInfo> files = directoryDao.read(new File("src/test/resources"));
        assertEquals(9, files.size());
    }

}
