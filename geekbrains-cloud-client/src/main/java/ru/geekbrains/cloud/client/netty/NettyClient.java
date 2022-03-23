package ru.geekbrains.cloud.client.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import java.util.concurrent.CountDownLatch;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import ru.geekbrains.cloud.client.javafx.Controller;
import ru.geekbrains.cloud.common.messages.abs.AbstractMessage;

@Log4j2
public class NettyClient implements Runnable{

  private static final int MAXIMUM_OBJECT_SIZE = 1024 * 1024 * 10;

  @Getter
  private ChannelFuture channelFuture;

  private Controller controller;
  private CountDownLatch countDownLatch;

  public NettyClient(Controller controller, CountDownLatch countDownLatch) {
    this.controller = controller;
    this.countDownLatch = countDownLatch;
  }

  @Override
  public void run() {
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
            }
          });
      channelFuture = b.connect("localhost", 45001).sync();
      countDownLatch.countDown();
      channelFuture.channel().closeFuture().sync();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      workerGroup.shutdownGracefully();
    }
  }

  public void send(AbstractMessage message) {
    channelFuture.channel().writeAndFlush(message).addListener(future -> {
      if (!future.isSuccess()) {
        log.info(future.cause().getMessage());
      }
    });
  }
}
