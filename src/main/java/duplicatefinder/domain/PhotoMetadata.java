package duplicatefinder.domain;

import java.util.HashMap;

/**
 * Class that implements Metadata for Photos
 */
public class PhotoMetadata implements Metadata {

    private HashMap<String, String> metadata;
    private HashMap<String, String> gpsData;
    private boolean hasGpsData;

    public PhotoMetadata() {
        metadata = new HashMap<>();
        gpsData = new HashMap<>();
    }

    public void addMake(String make) {
        if (make == null) {
            return;
        }

        metadata.put("Make", make);
    }

    public void addModel(String model) {
        if (model == null) {
            return;
        }

        metadata.put("Model", model);
    }

    public void addDate(String date) {
        if (date == null) {
            return;
        }

        metadata.put("Date", date);
    }

    public void addIso(String iso) {
        if (iso == null) {
            return;
        }

        metadata.put("ISO", iso);
    }

    public void addShutterSpeed(String shutterSpeed) {
        if (shutterSpeed == null) {
            return;
        }

        metadata.put("Shutter speed", shutterSpeed);
    }

    public void addAperture(String aperture) {
        if (aperture == null) {
            return;
        }

        metadata.put("Aperture", aperture);
    }

    public void addBrightness(String brightness) {
        if (brightness == null) {
            return;
        }

        metadata.put("Brightness", brightness);
    }

    public void addGpsLatitude(String gpsLatitute) {
        if (gpsLatitute == null) {
            return;
        }

        hasGpsData = true;
        gpsData.put("GPSLatitude", gpsLatitute);
    }

    public void addGpsLongitude(String gpsLongitude) {
        if (gpsLongitude == null) {
            return;
        }

        hasGpsData = true;
        gpsData.put("GPSLongitude", gpsLongitude);
    }

    public boolean hasGpsData() {
        return hasGpsData;
    }


    @Override
    public HashMap<String, String> getMetadata() {
        return metadata;
    }

    public HashMap<String, String> getGpsData() {
        return gpsData;
    }

    @Override
    public void printToConsole() {
        for (String tag : metadata.keySet()) {
            System.out.println("Tag: " + tag + ", Value: " + metadata.get(tag));
        }
    }

}
