package file.manager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class FileInfo {

  public enum FileType {
    FILE("F"), DIRECTORY("D");

    private String name;

    public String getName() {
      return name;
    }

    FileType(String name) {
      this.name = name;
    }
  }

  private String fileName;
  private FileType type;
  private long size;
  private LocalDateTime lastModified;

  public FileInfo(Path path) {
    try {
      this.fileName = path.getFileName().toString();
      this.type = Files.isDirectory(path) ? FileType.DIRECTORY : FileType.FILE;
      this.size = Files.isDirectory(path) ? -1L : Files.size(path);
      this.lastModified = LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneOffset.ofHours(0));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String getFileName() {
    return fileName;
  }

  public FileType getType() {
    return type;
  }

  public long getSize() {
    return size;
  }

  public LocalDateTime getLastModified() {
    return lastModified;
  }
}
