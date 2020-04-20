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
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;

public class PhotoFileDao implements MediaFileDao {

    @Override
    public PhotoFileInfo read(File file) throws IOException, ImageReadException {
        if (file.isDirectory() || !FilenameUtils.getExtension(file.getName()).equals("jpg")) {
            return null;
        }

        // TODO: Check if file is an image

        PhotoFileInfo photoFileInfo = new PhotoFileInfo(file);

        photoFileInfo.setMetadata(readMetadata(file));

        if (photoFileInfo.getMetadata() != null) {
            photoFileInfo.getMetadata().printToConsole();
        }

        return photoFileInfo;
    }

    @Override
    public void save(MediaFileInfo mediaFile) {
        // TODO
    }

    @Override
    public boolean delete(MediaFileInfo mediaFile) {
        return new File(mediaFile.getAbsolutePath()).delete();
    }

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
