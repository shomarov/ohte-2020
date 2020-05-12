package duplicatephotofinder.domain;

import java.util.HashMap;

/**
 * Interface for metadata
 */
public interface Metadata {
    /**
     * Method returns metadata
     *
     * @return HashMap with Tag-Value type metadata
     */
    HashMap<String, String> getMetadata();

    /**
     * Checks if Metadata has gps data
     *
     * @return true if Metadata contains gps data
     */
    boolean hasGpsData();

    /**
     * Get gps data
     *
     * @return HashMap with Tag-Value type gps data
     */
    HashMap<String, String> getGpsData();

}
