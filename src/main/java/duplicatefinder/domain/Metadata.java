package duplicatefinder.domain;

import java.util.HashMap;

public interface Metadata {
    HashMap<String, String> getAll();

    void printToConsole();
}
