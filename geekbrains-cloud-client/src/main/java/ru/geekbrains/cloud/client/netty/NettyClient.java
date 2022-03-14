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
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import java.nio.charset.StandardCharsets;
import ru.geekbrains.cloud.client.javafx.Controller;
import ru.geekbrains.cloud.common.messages.ListRequest;

public class NettyClient {

  private static final int MAXIMUM_OBJECT_SIZE = 1024 * 1024 * 10;

  private volatile ChannelFuture channelFuture;

  private Channel channel;
  private Controller controller;

  public NettyClient(Controller controller) {
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
                socketChannel.pipeline().addLast(
                    new ObjectDecoder(MAXIMUM_OBJECT_SIZE, ClassResolvers.cacheDisabled(null)),
                    new ObjectEncoder(),
                    new NettyClientHandler(controller));
                channel = socketChannel;
              }
            });
        channelFuture = b.connect("localhost", 45001).sync();
        updateFileList();
        channelFuture.channel().closeFuture().sync();
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
    channel.writeAndFlush(new ListRequest());
  }

  public void sendDownloadRequest(String fileName) {
    //channel.writeAndFlush(buf);
  }
}
