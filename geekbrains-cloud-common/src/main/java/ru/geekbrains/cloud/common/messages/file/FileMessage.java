package ru.geekbrains.cloud.common.messages.file;

import ru.geekbrains.cloud.common.messages.abs.AbstractMessage;

public class FileMessage extends AbstractMessage {
    public String filename;
    public String path;
    public int partNumber;
    public int partsCount;
    public byte[] data;

    public FileMessage(String filename, String path, int partNumber, int partsCount, byte[] data) {
        this.filename = filename;
        this.path = path;
        this.partNumber = partNumber;
        this.partsCount = partsCount;
        this.data = data;
    }
}

