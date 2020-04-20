package duplicatefinder.domain;

import java.io.File;
import java.util.List;

public interface MediaFileRenamer {
    void renameOne(File file);

    void renameMany(List<File> files);
}