package duplicatefinder.dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PhotoFileDao implements MediaFileDao {

  @Override
  public List<String> scanFolderForMediaFiles(String path) {
    List<String> filepaths = new ArrayList<>();

    try (Stream<Path> stream = Files.walk(Paths.get(path))) {
      filepaths =
          stream
              .filter(file -> !Files.isDirectory(file))
              .map(Path::toString)
              .filter(file -> file.endsWith(".jpg"))
              .collect(Collectors.toList());
    } catch (IOException e) {
      e.printStackTrace();
    }

    return filepaths;
  }

}
