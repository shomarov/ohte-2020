package duplicatephotofinder.domain;

import duplicatephotofinder.dao.DirectoryDao;
import duplicatephotofinder.dao.MediaFileDao;
import duplicatephotofinder.dao.PhotoFileDao;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class DirectoryServiceTest {

    MediaFileDao mediaFileDao;
    DirectoryDao directoryDao;
    DirectoryService directoryService;

    @Before
    public void setup() {
        this.mediaFileDao = new PhotoFileDao();
        this.directoryDao = new DirectoryDao(mediaFileDao);
        directoryService = new DirectoryService(directoryDao);

        File dir = new File("src/test/resources/folderToTestDeletion");
        dir.mkdir();
    }

    @Test
    public void readsAmountOfFoldersOfGivenDirectoryCorrectly() {
        DirectoryInfo dir = directoryService.readDirectoryTree(new File("src/test"));

        int amount = countFoldersInDirectoryTree(dir, 0);

        assertEquals(7, amount);
    }

    private int countFoldersInDirectoryTree(DirectoryInfo dir, int amount) {
        if (dir.getFolders().isEmpty()) {
            return amount;
        }

        for (DirectoryInfo d : dir.getFolders()) {
            amount = countFoldersInDirectoryTree(d, amount);
        }

        return amount + dir.getFolders().size();
    }

    @Test
    public void readsAmountOfFilesCorrectly() throws IOException {
        List<MediaFileInfo> files = directoryService.read(new File("src/test/resources"));
        assertEquals(10, files.size());
    }

    @Test
    public void readsAmountOfFilesCorrectlyIfZeroFilesInFolder() throws IOException {
        List<MediaFileInfo> files = directoryService.read(new File("src/test"));
        assertEquals(0, files.size());
    }

    @Test
    public void recursiveScanFindsRightAmountOfFiles() throws IOException {
        List<MediaFileInfo> files = directoryService.readRecursively(new File("src/test/resources"));
        assertEquals(28, files.size());
    }

}
