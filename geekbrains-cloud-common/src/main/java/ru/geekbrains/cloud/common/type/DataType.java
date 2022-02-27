package ru.geekbrains.cloud.common.type;

public enum DataType {
  EMPTY((byte) -1), FILE((byte) 15), UPDATE_FILELIST((byte) 16), DOWNLOAD((byte) 4);

  byte firstMessageByte;

  DataType(byte firstMessageByte) {
    this.firstMessageByte = firstMessageByte;
  }

  public static DataType getDataTypeFromByte(byte b) {
    if (b == FILE.firstMessageByte) {
      return FILE;
    }
    if (b == UPDATE_FILELIST.firstMessageByte) {
      return UPDATE_FILELIST;
    }
    if (b == DOWNLOAD.firstMessageByte) {
      return DOWNLOAD;
    }
    return EMPTY;
  }
}
