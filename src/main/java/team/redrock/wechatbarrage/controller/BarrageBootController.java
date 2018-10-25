package team.redrock.wechatbarrage.controller;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import team.redrock.wechatbarrage.service.BarrageCoreService;
import team.redrock.wechatbarrage.service.handle.HttpRequestHandle;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

/**
 * @Author 余歌
 * @Date 2018/8/19
 * @Description netty的启动引导
 **/
@Slf4j
@Component
public class BarrageBootController {
    @Value("${barrage.port}")
    private int port;  //netty的运行端口

    @Autowired
    private BarrageCoreService coreService;

    private EventLoopGroup group = null;

    /***
     * netty 的启动引导
     */
    @PostConstruct
    public void run() throws InterruptedException {
        group = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(group)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(this.port))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new HttpServerCodec())
                                .addLast(new HttpObjectAggregator(64 * 1024))
                                .addLast(new WebSocketServerCompressionHandler())
                                .addLast(new HttpRequestHandle(coreService));
                    }
                });
        log.info("netty启动");
        Channel f = bootstrap.bind().sync().channel();
    }

    @PreDestroy
    public void close() throws InterruptedException {
        group.shutdownGracefully().sync();
    }
}
