package duplicatefinder.dao;

import duplicatefinder.domain.MediaFileInfo;
import duplicatefinder.domain.Metadata;
import duplicatefinder.domain.PhotoFileInfo;
import duplicatefinder.domain.PhotoMetadata;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;

/**
 * This class offers methods used for interaction with files of image type
 */
public class PhotoFileDao implements MediaFileDao {

    /**
     * Method reads the file specified and its metadata
     *
     * @param file client specified file
     * @return MediaFileInfo object with metadata if existing
     * @throws IOException        if file not found
     * @throws ImageReadException if file not of type image
     */
    @Override
    public PhotoFileInfo read(File file) throws IOException, ImageReadException {
        if (file.isDirectory() || !FilenameUtils.getExtension(file.getName()).equals("jpg")) {
            return null;
        }

        // TODO: Check if file is an image

        PhotoFileInfo photoFileInfo = new PhotoFileInfo(file);

        photoFileInfo.setMetadata(readMetadata(file));

        return photoFileInfo;
    }

    /**
     * Method saves changes to the file
     *
     * @param mediaFile client specified file
     */
    @Override
    public void save(MediaFileInfo mediaFile) {
        // TODO
    }

    /**
     * Method deletes file from file system
     *
     * @param mediaFile client specified file
     * @return true if successfull, false if not
     */
    @Override
    public boolean delete(MediaFileInfo mediaFile) {
        return new File(mediaFile.getAbsolutePath()).delete();
    }

    /**
     * Method reads metadata of the image file
     *
     * @param file client specified file
     * @return Metadata type object
     * @throws IOException        if file not found
     * @throws ImageReadException if file not of type image
     */
    @Override
    public Metadata readMetadata(File file) throws IOException, ImageReadException {
        ImageMetadata metadata = Imaging.getMetadata(file);

        if (metadata instanceof JpegImageMetadata) {
            JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;

            PhotoMetadata meta = new PhotoMetadata();

            meta.addMake(returnTagValue(jpegMetadata, TiffTagConstants.TIFF_TAG_MAKE));
            meta.addModel(returnTagValue(jpegMetadata, TiffTagConstants.TIFF_TAG_MODEL));
            meta.addDate(returnTagValue(jpegMetadata, TiffTagConstants.TIFF_TAG_DATE_TIME));
            meta.addIso(returnTagValue(jpegMetadata, ExifTagConstants.EXIF_TAG_ISO));
            meta.addShutterSpeed(returnTagValue(jpegMetadata,
                    ExifTagConstants.EXIF_TAG_SHUTTER_SPEED_VALUE));
            meta.addAperture(returnTagValue(jpegMetadata,
                    ExifTagConstants.EXIF_TAG_APERTURE_VALUE));
            meta.addBrightness(returnTagValue(jpegMetadata,
                    ExifTagConstants.EXIF_TAG_BRIGHTNESS_VALUE));

            final TiffImageMetadata exifMetadata = jpegMetadata.getExif();
            if (null != exifMetadata) {
                final TiffImageMetadata.GPSInfo gpsInfo = exifMetadata.getGPS();
                if (null != gpsInfo) {
                    final double longitude = gpsInfo.getLongitudeAsDegreesEast();
                    final double latitude = gpsInfo.getLatitudeAsDegreesNorth();

                    meta.addGpsLongitude(String.valueOf(longitude));
                    meta.addGpsLatitude(String.valueOf(latitude));
                }
            }

            return meta;
        }

        return null;
    }

    private String returnTagValue(JpegImageMetadata jpegMetadata,
                                  TagInfo tagInfo) throws ImageReadException {
        TiffField field = jpegMetadata.findEXIFValueWithExactMatch(tagInfo);
        if (field == null) {
            return null;
        } else {
            return field.getValue().toString();
        }
    }

//    public void deleteGpsData() {
//
//    }

}
