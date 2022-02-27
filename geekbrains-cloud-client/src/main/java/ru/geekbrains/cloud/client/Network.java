package ru.geekbrains.cloud.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

public class Network {

  private SocketChannel channel;

  public Network() {
    new Thread(() -> {
      EventLoopGroup workerGroup = new NioEventLoopGroup();

      try {
        Bootstrap b = new Bootstrap();
        b.group(workerGroup)
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<SocketChannel>() {
              @Override
              protected void initChannel(SocketChannel ch) throws Exception {
                channel = ch;
              }
            });
        ChannelFuture f = b.connect("localhost", 45001).sync();
        f.channel().closeFuture().sync();
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        workerGroup.shutdownGracefully();
      }
    }).start();
  }

  public void updateFileList() {
    ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer(1);
    buf.writeByte((byte) 2);
    channel.writeAndFlush(buf);
  }
}
