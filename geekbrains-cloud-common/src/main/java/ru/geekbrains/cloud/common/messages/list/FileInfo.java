package ru.geekbrains.cloud.common.messages.list;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class FileInfo implements Serializable {

  public enum FileType {
    FILE("F"), DIRECTORY("D");

    private String name;

    public String getName() {
      return name;
    }

    FileType(String name) {
      this.name = name;
    }

    public static FileType getTypeByString(String string) {
      if (string.equals("FILE")) {
        return FILE;
      }
      if (string.equals("DIRECTORY")) {
        return DIRECTORY;
      }
      return null;
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

  public FileInfo(String fileName, String type, long size, LocalDateTime lastModified) {
    this.fileName = fileName;
    this.type = FileType.getTypeByString(type);
    this.size = size;
    this.lastModified = lastModified;
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

  @Override
  public String toString() {
    return fileName + '$' + type + '$' + size + '$' + lastModified;
  }
}
