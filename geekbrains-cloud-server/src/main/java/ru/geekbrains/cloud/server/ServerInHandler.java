package ru.geekbrains.cloud.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import ru.geekbrains.cloud.common.type.DataType;
import ru.geekbrains.cloud.common.type.FileInfo;

public class ServerInHandler extends ChannelInboundHandlerAdapter {

  private DataType type = DataType.EMPTY;

  public enum State {
    IDLE, NAME_LENGTH, NAME, FILE_LENGTH, FILE
  }

  private State currentState = State.IDLE;
  private int nextLength;
  private long fileLength;
  private long receivedFileLength;
  private BufferedOutputStream out;

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    System.out.println("Client connected");
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ByteBuf buf = (ByteBuf) msg;

    if (buf.readableBytes() > 0) {

      if (currentState == State.IDLE) {
        byte firstByte = buf.readByte();
        type = DataType.getDataTypeFromByte(firstByte);
        currentState = State.NAME_LENGTH;
        receivedFileLength = 0L;
        System.out.println(type);
      }

      if (type == DataType.COMMAND) {
        sendFileList(ctx);
      }  else if (type == DataType.FILE) {
        uploadFile(ctx, buf);
      }
    }

    if (buf.readableBytes() == 0) {
      buf.release();
    }



  }

  private void uploadFile(ChannelHandlerContext ctx, ByteBuf buf) throws IOException {
    if (currentState == State.NAME_LENGTH) {
      if (buf.readableBytes() >= 4) {
        System.out.println("STATE: Get filename length");
        nextLength = buf.readInt();
        currentState = State.NAME;
      }
    }

    if (currentState == State.NAME) {
      if (buf.readableBytes() >= nextLength) {
        byte[] fileName = new byte[nextLength];
        buf.readBytes(fileName);
        System.out.println("STATE: Filename received - _" + new String(fileName, "UTF-8"));
        Path path = Paths.get("server_repository").resolve(new String(fileName));
        out = new BufferedOutputStream(new FileOutputStream(path.toString()));
        currentState = State.FILE_LENGTH;
      }
    }

    if (currentState == State.FILE_LENGTH) {
      if (buf.readableBytes() >= 8) {
        fileLength = buf.readLong();
        System.out.println("STATE: File length received - " + fileLength);
        currentState = State.FILE;
      }
    }

    if (currentState == State.FILE) {
      while (buf.readableBytes() > 0) {
        out.write(buf.readByte());
        receivedFileLength++;
        if (fileLength == receivedFileLength) {
          currentState = State.IDLE;
          System.out.println("File received");
          out.close();
          sendFileList(ctx);
          break;
        }
      }
    }
  }

  private void sendFileList(ChannelHandlerContext ctx) throws IOException {
    StringBuilder listFiles = new StringBuilder();
    Files.list(Paths.get("server_repository")).map(FileInfo::new).forEach(p -> listFiles.append(p).append("::"));
    System.out.println(listFiles);

    ByteBuf outBuff = null;
    outBuff = ByteBufAllocator.DEFAULT.directBuffer(1);
    outBuff.writeByte((byte) 16);
    ctx.write(outBuff);

    byte[] fileListBytes = listFiles.toString().getBytes(StandardCharsets.UTF_8);
    outBuff = ByteBufAllocator.DEFAULT.directBuffer(4);
    outBuff.writeInt(fileListBytes.length);
    ctx.write(outBuff);

    outBuff = ByteBufAllocator.DEFAULT.directBuffer(fileListBytes.length);
    outBuff.writeBytes(fileListBytes);
    ctx.writeAndFlush(outBuff);

    currentState = State.IDLE;
    type = DataType.EMPTY;
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }
}
