package duplicatephotofinder.dao;

import duplicatephotofinder.domain.MediaFileInfo;
import duplicatephotofinder.domain.Metadata;

import java.io.File;

/**
 * Interface to be used for interaction with different
 * types of media files (image, video, pdf, etc...)
 */
public interface MediaFileDao {
    /**
     * Method reads file and returns its information
     *
     * @param file client specified
     * @return MediaFileInfo type object
     * @throws Exception if file not found
     */
    MediaFileInfo read(File file) throws Exception;

    /**
     * Method deletes file from the file system
     *
     * @param mediaFile client specified MediaFileInfo object
     * @return true if successfull, false if failed
     */
    boolean delete(MediaFileInfo mediaFile);

    /**
     * Method reads metadata of the file specified
     *
     * @param file client specified file
     * @return Metadata type object
     * @throws Exception if file not found
     */
    Metadata readMetadata(File file) throws Exception;
}
