package duplicatephotofinder.domain;

import duplicatephotofinder.dao.DirectoryDao;
import duplicatephotofinder.dao.PhotoFileDao;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PhotoFileServiceTest {

    PhotoFileService photoFileService;
    PhotoFileDao photoFileDao;
    DirectoryDao directoryDao;
    DirectoryService directoryService;

    @Before
    public void setup() {
        photoFileDao = new PhotoFileDao();
        directoryDao = new DirectoryDao(photoFileDao);
        directoryService = new DirectoryService(directoryDao);
        photoFileService = new PhotoFileService(directoryService, photoFileDao);

        File dir = new File("src/test/resources/folderToTestDeletion");
        dir.mkdir();
    }

    @After
    public void cleanup() {
        File dir = new File("src/test/resources/folderToTestDeletion");
        String[]entries = dir.list();
        for(String s: entries){
            File currentFile = new File(dir.getPath(),s);
            currentFile.delete();
        }
    }

    @Test
    public void findsAllDuplicatesInFolder() throws IOException {
        File folderToScan = new File("src/test/resources/folderWithDuplicatePhotos");

        List<DuplicateSet> duplicateSets = photoFileService.scanFolderForDuplicates(folderToScan);

        assertEquals(9, duplicateSets.size());
        assertEquals(2, duplicateSets.get(0).getSize());
    }

    @Test
    public void findsAllDuplicatesInFolderAndSubfolders() throws IOException {
        File folderToScan = new File("src/test/resources");

        List<DuplicateSet> duplicateSets = photoFileService.scanFolderForDuplicatesRecursively(folderToScan);

        assertEquals(9, duplicateSets.size());
    }

    @Test
    public void scansForPhotosAndFindsTheRightAmount() throws IOException {
        DirectoryInfo dir = directoryService.readDirectoryTree(new File("src/test/resources"));

        photoFileService.scanFolderForMediaFiles(dir);

        assertEquals(10, dir.getFiles().size());
    }

    @Test
    public void scansForPhotosRecursivelyAndFindsTheRightAmount() throws IOException {
        DirectoryInfo dir = directoryService.readDirectoryTree(new File("src/test/resources"));

        List<MediaFileInfo> photos = photoFileService.scanFolderForMediaFilesRecursively(dir);

        assertEquals(28, photos.size());
    }

    @Test
    public void deletesFilesFromDisk() throws IOException {
        DirectoryInfo original = directoryService.readDirectoryTree(
                new File("src/test/resources"));

        File directory = new File("src/test/resources/folderToTestDeletion");
        directory.mkdir();

        DirectoryInfo forDeletion = directoryService.readDirectoryTree(
                new File("src/test/resources/folderToTestDeletion"));

        photoFileService.scanFolderForMediaFiles(original);
        photoFileService.scanFolderForMediaFiles(forDeletion);

        assertEquals(10, original.getFiles().size());
        assertEquals(0, forDeletion.getFiles().size());

        original.getFiles().forEach(f -> {
            try {
                FileUtils.copyFileToDirectory(f.getFile(), directory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        photoFileService.scanFolderForMediaFiles(forDeletion);
        assertEquals(10, forDeletion.getFiles().size());

        photoFileService.deleteMany(forDeletion.getFiles());
        photoFileService.scanFolderForMediaFiles(forDeletion);

        assertEquals(0, forDeletion.getFiles().size());
    }

}
