package duplicatefinder.domain;

import java.util.HashMap;

// FIXME: Class not ready
public class EXIF implements Metadata {
    private String make;
    private String model;
    private String date;
    private String iso;
    private String shutterSpeed;
    private String aperture;
    private String brightness;

    private HashMap<String, String> metadata;

    public EXIF() {
        metadata = new HashMap<>();
    }

    public void add(String tag, String value) {
        if (value.isEmpty()) {
            return;
        }
        metadata.put(tag, value);
    }

    @Override
    public HashMap<String, String> getMetadata() {
        return metadata;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getShutterSpeed() {
        return shutterSpeed;
    }

    public void setShutterSpeed(String shutterSpeed) {
        this.shutterSpeed = shutterSpeed;
    }

    public String getAperture() {
        return aperture;
    }

    public void setAperture(String aperture) {
        this.aperture = aperture;
    }

    public String getBrightness() {
        return brightness;
    }

    public void setBrightness(String brightness) {
        this.brightness = brightness;
    }

    @Override
    public String toString() {
        return metadata.toString();
    }
}
