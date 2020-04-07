package duplicatefinder.dao;

import duplicatefinder.domain.MediaFileInfo;
import duplicatefinder.domain.PhotoFileInfo;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;

public class PhotoFileDao implements MediaFileDao {

    @Override
    public PhotoFileInfo read(File file) throws IOException {
        if (file.isDirectory() || !FilenameUtils.getExtension(file.getName()).equals("jpg")) {
            return null;
        }

        PhotoFileInfo photoFileInfo = new PhotoFileInfo(file);

//        mediaFileInfo.setMetadata(readMetadata(file));
//
//        if (mediaFileInfo.getMetadata() != null) {
//            System.out.println(mediaFileInfo.getMetadata());
//        }

        return photoFileInfo;
    }

    @Override
    public void save(MediaFileInfo mediaFile) {
        // TODO
    }

    @Override
    public void delete(MediaFileInfo mediaFile) {
        // TODO
    }

//    public Metadata readMetadata(File file) throws IOException, ImageReadException {
//        ImageMetadata metadata = Imaging.getMetadata(file);
//
//        if (metadata instanceof JpegImageMetadata) {
//            JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
//
//            EXIF metadataToReturn = new EXIF();
//
//            System.out.println("file: " + file.getPath());
//
//            metadataToReturn.add("Make", returnTagValue(jpegMetadata, TiffTagConstants.TIFF_TAG_MAKE));
//            metadataToReturn.add("Model", returnTagValue(jpegMetadata, TiffTagConstants.TIFF_TAG_MODEL));
//            metadataToReturn.add("DateTime", returnTagValue(jpegMetadata, TiffTagConstants.TIFF_TAG_DATE_TIME));
//            metadataToReturn.add("ISO", returnTagValue(jpegMetadata, ExifTagConstants.EXIF_TAG_ISO));
//            metadataToReturn.add("Shutter speed", returnTagValue(jpegMetadata,
//                    ExifTagConstants.EXIF_TAG_SHUTTER_SPEED_VALUE));
//            metadataToReturn.add("Aperture", returnTagValue(jpegMetadata,
//                    ExifTagConstants.EXIF_TAG_APERTURE_VALUE));
//            metadataToReturn.add("Brightness", returnTagValue(jpegMetadata,
//                    ExifTagConstants.EXIF_TAG_BRIGHTNESS_VALUE));
//
//            return metadataToReturn;
//        }
//
//        return null;
//    }
//
//    private String returnTagValue(JpegImageMetadata jpegMetadata,
//                                      TagInfo tagInfo) throws ImageReadException {
//        TiffField field = jpegMetadata.findEXIFValueWithExactMatch(tagInfo);
//        if (field == null) {
//            return "Not Found.";
//        } else {
//            return field.getValue().toString();
//        }
//    }

}
