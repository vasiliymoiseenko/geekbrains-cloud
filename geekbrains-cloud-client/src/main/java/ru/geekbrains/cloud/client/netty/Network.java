package ru.geekbrains.cloud.client.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.nio.charset.StandardCharsets;
import ru.geekbrains.cloud.client.javafx.Controller;

public class Network {

  private Channel channel;
  private Controller controller;

  public Network(Controller controller) {
    this.controller = controller;
    new Thread(() -> {
      EventLoopGroup workerGroup = new NioEventLoopGroup();

      try {
        Bootstrap b = new Bootstrap();
        b.group(workerGroup)
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<SocketChannel>() {
              @Override
              protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new ClientInHandler(controller));
                channel = socketChannel;
              }
            });
        ChannelFuture f = b.connect("localhost", 45001).sync();
        updateFileList();
        f.channel().closeFuture().sync();
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        workerGroup.shutdownGracefully();
      }
    }).start();
  }

  public Channel getChannel() {
    return channel;
  }

  public void updateFileList() {
    ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer(1);
    buf.writeByte((byte) 16);
    channel.writeAndFlush(buf);
  }

  public void sendDownloadRequest(String fileName) {
    byte[] fileNameBytes = fileName.getBytes(StandardCharsets.UTF_8);
    ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer(1 + 4 + fileNameBytes.length);
    buf.writeByte((byte) 4);
    buf.writeInt(fileNameBytes.length);
    buf.writeBytes(fileNameBytes);
    channel.writeAndFlush(buf);
  }
}
